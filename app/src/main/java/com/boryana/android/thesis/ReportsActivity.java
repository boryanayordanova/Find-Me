package com.boryana.android.thesis;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boryana.android.thesis.Models.PointCoordinates;
import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.Models.UsersItem;
import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ReportsActivity extends AppCompatActivity implements OnMapReadyCallback, OnInfoWindowClickListener, View.OnClickListener {

    private GoogleMap mMap;

    private Calendar calendar;
    private Button changeDate;
    private int year, month, day;

    private ProgressBar loadReport;

    private AutoCompleteTextView searchUserName;
    private static ArrayAdapter<String> mUserFriendsNamesAdapter;
    private ArrayList<UsersItem> allFriends = new ArrayList<UsersItem>();

    private Button executeReport;
    private TextView showDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        loadReport = (ProgressBar) findViewById(R.id.progressBarLoadReport);


        // add ourselves
        User user = new User(getBaseContext());
        getSupportActionBar().setTitle("Справка изминато разстояние");
        allFriends.add(new UsersItem(Integer.parseInt(user.getUserId()), user.getUserName(), "", "", ""));

        if(allFriends.size() == 1) findAllUserFriends(getBaseContext());

        changeDate = (Button) findViewById(R.id.setDateRequest);
        changeDate.setOnClickListener(this);

        executeReport = (Button) findViewById(R.id.executeReport);
        executeReport.setOnClickListener(this);

        showDistance = (TextView) findViewById(R.id.textDistanceInfo);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_report);
        mapFragment.getMapAsync(this);





        searchUserName = (AutoCompleteTextView) findViewById(R.id.searchUserName);
        mUserFriendsNamesAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line);
        searchUserName.setAdapter(mUserFriendsNamesAdapter);

        fillUserFriendsAdapter();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("onMapReady", "INSIDE");
        if (mMap == null) {
            mMap = googleMap;
            mMap.setOnInfoWindowClickListener(this);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);


        }
    }


    public void fillUserFriendsAdapter() {
        mUserFriendsNamesAdapter.clear();

        for(UsersItem item : allFriends){
            mUserFriendsNamesAdapter.add(item.getUserName());
        }
        mUserFriendsNamesAdapter.notifyDataSetChanged();
    }

    // load data for friends (user name and ids)
    private void findAllUserFriends(Context cntx) {
        String method = "get_all_friends_url";

        ServerApiRequests apiRequests = new ServerApiRequests(
                this,
                new ServerApiRequests.AsyncStart() {
                    @Override
                    public void onProcessStart() {
                        loadReport.setVisibility(View.VISIBLE);
                    }
                },
                new ServerApiRequests.AsyncResponse() {
                    @Override
                    public void onProcessFinish(String responce) {

                        Log.e("report acr - fr search", responce);

                        if(!responce.equals("Server error")){

                            try {
                                JSONObject result = new JSONObject(responce);

                                String state = result.getString("success");

                                if(state.equals("true")){




                                    JSONArray jsonUsers = result.getJSONArray("users");

                                    if (allFriends.size() !=  (jsonUsers.length()+1)){

                                        allFriends.clear();

                                        for (int i = 0; i < jsonUsers.length(); i++) {

                                            UsersItem tmpItem = new UsersItem(
                                                    jsonUsers.getJSONObject(i).getInt("ID")
                                                    , jsonUsers.getJSONObject(i).getString("NAME")
                                                    , jsonUsers.getJSONObject(i).getString("COOR_X")
                                                    , jsonUsers.getJSONObject(i).getString("COOR_Y")
                                                    , jsonUsers.getJSONObject(i).getString("REC_DATE"));

                                            allFriends.add(tmpItem);
                                        }

                                        // adding ourselves
                                        User user = new User(getBaseContext());
                                        allFriends.add(new UsersItem(Integer.parseInt(user.getUserId()), user.getUserName(), "", "", ""));

                                        fillUserFriendsAdapter();
                                    }


                                }


                                loadReport.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                PersonalToastDesign tst = new PersonalToastDesign(getBaseContext());
                                tst.showToast("Проблем с интернета/сървъра");
                                loadReport.setVisibility(View.GONE);
                            }
                        }  else {// if server OKAY
                            loadReport.setVisibility(View.GONE);
                            PersonalToastDesign tst = new PersonalToastDesign(getBaseContext());
                            tst.showToast("Проблем с интернета/сървъра");
                        }

                    }
                }
        );
        User user = new User(getApplicationContext());
        apiRequests.execute(method, user.getUserId());
    }

        @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setDateRequest){
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            showDialog(999);

        } else if (v.getId() == R.id.executeReport){
            if(searchUserName.getText().length() == 0){
                new PersonalToastDesign(getApplicationContext()).showToast("Моля въведете име за търсене");
                searchUserName.requestFocus();
            } else {
                executeReport.setEnabled(false);

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String userId = "";
                String userName = "";
                Boolean foundId = false;
                for(UsersItem tmpItem: allFriends){
                    if(tmpItem.getUserName().equals(searchUserName.getText().toString())){
                        userId = String.valueOf(tmpItem.getUserId());
                        userName = tmpItem.getUserName();
                        foundId = true;
                    }
                }

                if (foundId == true){
                    executeReportRequest(userId, changeDate.getText().toString(), userName);
                } else {
                    new PersonalToastDesign(getBaseContext()).showToast("Потребителя не е ваш приятел !");
                    executeReport.setEnabled(true);
                    showDistance.setVisibility(View.GONE);
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(1));
                }



                searchUserName.setText("");
                searchUserName.clearFocus();

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                showDate(year, month + 1, day);


            }
        }
    }

    private void executeReportRequest(String user_id, String search_date, final String searchedUser){

//        Log.e("DATA:",search_date);
//        Log.e("Removing fiend","From database and updating the adapter");
        new ServerApiRequests(this,

                new ServerApiRequests.AsyncStart(){

                    @Override
                    public void onProcessStart(){
                        loadReport.setVisibility(View.VISIBLE);
                    }
                },

                new ServerApiRequests.AsyncResponse() {

                    @Override
                    public void onProcessFinish(String output) {
                        Log.e("response", output);

                        try {
                            JSONObject result = new JSONObject(output);

                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            String state = result.getString("success");
                            if(state.equals("true")) {
//                                tst.showToast("User unfriended.");

                                mMap.clear();
                                ArrayList<Marker> markersList = new ArrayList<Marker>();
                                double distance = 0;
                                double prev_x = 0;
                                double prev_y = 0;

                                PolylineOptions rectOptions = new PolylineOptions();
                                JSONArray jsonUsers = result.getJSONArray("user");


                                for (int i = 0; i < jsonUsers.length(); i++) {


                                    PointCoordinates tempPoint = new PointCoordinates(
                                              jsonUsers.getJSONObject(i).getDouble("COOR_X")
                                            , jsonUsers.getJSONObject(i).getDouble("COOR_Y")
                                            ,jsonUsers.getJSONObject(i).getString("REC_DATE"));

                                    Marker marker =  mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(tempPoint.getCoord_x(), tempPoint.getCoord_y()))
                                                    .title(tempPoint.getRec_date())
//                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_house))
                                            );
//                                    marker.showInfoWindow();
                                    markersList.add(marker);

                                    rectOptions.add(new LatLng(tempPoint.getCoord_x(), tempPoint.getCoord_y()));

                                    // accumulate distance

                                    if (i > 0){
                                        distance += tempPoint.distanceToMe(prev_x,prev_y);
                                    } else {
                                        prev_x = tempPoint.getCoord_x();
                                        prev_y = tempPoint.getCoord_y();
                                    }
                                }

                                showDistance.setVisibility(View.VISIBLE);
                                double finalValue = Math.round( distance * 1000.0 ) / 1000.0;
                                showDistance.setText("Изминатo разстояние от "+ searchedUser + " ~ " + String.valueOf(finalValue)+ " км.");


                                mMap.addPolyline(rectOptions).setColor(Color.GREEN);;

                                // zooming
                                final int padding = 30;//initialize the padding for map boundary
                                /**create the bounds from latlngBuilder to set into map camera*/
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (Marker m : markersList) {
                                    builder.include(m.getPosition());
                                }

                                final LatLngBounds bounds = builder.build();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                            } else {
                                //tst.showToast(result.getString("error"));
                                String r = result.getString("error");
                                if(r.equals("Day or user not found")) {
                                    tst.showToast("Денят, или потребителят не са намерени");
                                }
                                if(r.equals("Please provide user name and search date")) {
                                    tst.showToast("Моля въведете име и търсена дата");
                                }

                                mMap.clear();
                                showDistance.setVisibility(View.GONE);
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(1));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            tst.showToast("Възникна проблем със сървъра");
                            mMap.clear();
                            executeReport.setEnabled(true);
                            showDistance.setVisibility(View.GONE);
                            loadReport.setVisibility(View.GONE);
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(1));
                        }

                        loadReport.setVisibility(View.GONE);
                        executeReport.setEnabled(true);
                    }
                }

        ).execute("get_report_coords_url", user_id, search_date);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }




//    @SuppressWarnings("deprecation")
//    public void setDate(View view) {
//        showDialog(999);
//        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
//                .show();
//    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        changeDate.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));
    }



}
