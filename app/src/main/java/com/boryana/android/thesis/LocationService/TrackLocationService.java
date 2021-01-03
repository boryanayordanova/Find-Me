package com.boryana.android.thesis.LocationService;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.boryana.android.thesis.MainScreenActivity;
import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Boryana on 16.7.2016 г..
 */
public class TrackLocationService extends Service {

    private GoogleApiClient mGoogleApiClient;
    private String userID;
    private DBHelper mydb;
    private boolean serverAvailable = true;

    public TrackLocationService() { super(); }

    @Override
    public void onCreate() {
        mydb = new DBHelper(this);
        getClient().connect();

        Intent notificationIntent = new Intent(this, MainScreenActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_location)
                .setContentTitle("Find me")
                .setContentText("Приложението функционира...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        if(extras == null) {
            Log.d("Service", "null");
        }else
        {
            userID = (String) extras.get("USER_ID");
            Log.d("Service fo user", userID);

        }

       //return START_STICKY;
        //return Service.START_NOT_STICKY;
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        if (getClient().isConnected()) {
            getClient().disconnect();

        }
        Intent intent = new Intent(this,MainScreenActivity.class);
        User user = new User(getApplicationContext());
        intent.putExtra("USER_ID", user.getUserId());
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private GoogleApiClient getClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (ActivityCompat.checkSelfPermission(TrackLocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackLocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            startLocationRequest();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }

        return mGoogleApiClient;
    }

    private void startLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30 * 60 * 1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("Current cords", location.toString());
                saveLocationLocal(location); // save to local database
            }
        });
    }


    private void saveLocationLocal(Location location){
        // here we put this record in our local DB
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendar.getTime());

        mydb.insertCoordinate(userID, formattedDate, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        // after its saved we start upload of all records of DB
        if (serverAvailable == true){
            serverAvailable = false;
            uploadToServer();
        }
    }


    private void uploadToServer(){
        // create array of cordinates to be sent to server
        ArrayList<String> allCoordinates = mydb.getAllCoordinates();
        uploadData(getBaseContext(), allCoordinates);
    }


    private void removeLocationsLocal(ArrayList<Integer> recordIDsArray){
        // remove records that are uploaded into server
        for(Integer recordID : recordIDsArray){
            mydb.deleteCoordinate(recordID);
        }
    }



    private void uploadData(Context cntx, final ArrayList<String> arrayOfCoordinates){

        // get a RequestQueue
        RequestQueue singletonQueue = InternetRequestsQueue.getInstance(cntx).getRequestQueue();
        Log.e("Sending to server",String.valueOf(arrayOfCoordinates.size()) + " " + arrayOfCoordinates.toString());

        // send the request to the server
//        String BASE_SERVER_URL = "http://192.168.0.101:8080/server_bori_app/";
        String BASE_SERVER_URL = "http://boryana.comxa.com/android/";
        String UPLOAD_COORDINATES_URL = BASE_SERVER_URL + "add_coordinates.php";

        // add a request
        InternetRequestsQueue.getInstance(cntx).addToRequestQueue(
                new StringRequest(Request.Method.POST, UPLOAD_COORDINATES_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.e("SERVICE RESPONCE",response);
                                try {
                                    JSONObject mainObject = new JSONObject(response.toString());
                                    Log.e("JSON", mainObject.toString());

                                    String success = mainObject.getString("success");
                                    if (success == "true") {

                                        ArrayList<Integer> coordinateToBeRemoved = new ArrayList<Integer>();

                                        JSONArray recordIDs = mainObject.getJSONArray("inserted_coordinates");
                                        for(int i = 0; i<recordIDs.length(); i++){
                                            coordinateToBeRemoved.add(recordIDs.getJSONObject(i).getInt("ID"));
                                        }

                                        removeLocationsLocal(coordinateToBeRemoved);


                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    serverAvailable = true;
                                }

                                serverAvailable = true;

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.e("Volley error server", error.getMessage());
                                serverAvailable = true;
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();
                        for (int i = 0; i < arrayOfCoordinates.size(); i++) {
                            params.put("insert_coordinate_rec[" + (i) + "]", arrayOfCoordinates.get(i));
                            // you first send both data with same param name as friendnr[] ....  now send with params friendnr[0],friendnr[1] ..and so on
                        }

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                }
        );
    }
}
