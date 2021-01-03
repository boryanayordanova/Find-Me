package com.boryana.android.thesis;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.boryana.android.thesis.LocationService.TrackLocationService;
import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.Models.UsersItem;
import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class MainScreenActivity extends AppCompatActivity implements OnMapReadyCallback, OnInfoWindowClickListener, View.OnClickListener {

    private static final int REFRESH_TIME = 10 * 60 * 1000;

    private static int RESULT_ACTIVITY_CODE = 1254;
    private static int RESULT_CLOSE_MYSELF = 25865;

    public static Double LatitudeCoords = 0.0;
    public static Double LotitudeCoords = 0.0;
    public static String friendRequestsJSON = "";


    private User user;
    RelativeLayout headerLayour;
    Button showFriendRequestActivity;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //sdf
            loadLocations();

            mHandler.postDelayed(mRunnable, REFRESH_TIME);
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    final ArrayList<Marker> markerList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        user = new User(getApplicationContext());

        getSupportActionBar().setTitle(user.getUserName().toString());

        headerLayour = (RelativeLayout) findViewById(R.id.layoutHeaderRequests);

        showFriendRequestActivity = (Button) findViewById(R.id.buttonShowRequestActivity);
        showFriendRequestActivity.setOnClickListener(this);

        Button showUsersSubMenu = (Button) findViewById(R.id.btnUsersSubMenu);
        showUsersSubMenu.setOnClickListener(this);

        Button showReportsSubMenu = (Button) findViewById(R.id.btnRepostsSubMenu);
        showReportsSubMenu.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initGoogleClient();

//       stopService(new Intent(this, TrackLocationService.class));

        if (!isMyServiceRunning(TrackLocationService.class)) {
            Intent intent = new Intent(this, TrackLocationService.class);
            intent.putExtra("USER_ID", user.getUserId());
            startService(intent);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void initGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(MainScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainScreenActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (location != null) {
                            LatitudeCoords = location.getLatitude();
                            LotitudeCoords = location.getLongitude();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));


                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonShowRequestActivity) {
            Intent friendRequestsIntent = new Intent(this, FriendRequestsActivity.class);
            startActivity(friendRequestsIntent);
        } else if (v.getId() == R.id.btnUsersSubMenu) {
            Intent usersMenuIntent = new Intent(this, UserFriendsActivity.class);
            startActivity(usersMenuIntent);
        } else if (v.getId() == R.id.btnRepostsSubMenu) {
            Intent reportsMenuIntent = new Intent(this, ReportsActivity.class);
            startActivity(reportsMenuIntent);
        }
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        // here you say what to do if you click on a marker
        Toast.makeText(this, "Info window clicked", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mRunnable);
        checkForFriendRequests(user.getUserId());
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mRunnable);
        super.onPause();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.boryana.android.thesis/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.boryana.android.thesis/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_all_friends) {
                showAllFriendsZoom();

            }
            if (id == R.id.action_settings) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivityForResult(intent, RESULT_CLOSE_MYSELF);
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onActivityResult ( int requestCode, int resultCode, Intent data){
            if (requestCode == RESULT_ACTIVITY_CODE) {
                onMapReady(mMap);

            } else if (requestCode == RESULT_CLOSE_MYSELF) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }


    private void loadLocations() {
        //here we call server request to get new coordinates for visualization
        String method = "get_all_friends_url";


        ServerApiRequests apiRequests = new ServerApiRequests(
                this,
                new ServerApiRequests.AsyncStart() {
                    @Override
                    public void onProcessStart() {

                    }
                },
                new ServerApiRequests.AsyncResponse() {
                    @Override
                    public void onProcessFinish(String responce) {

                        Log.e("friend locations", responce);


                        try {
                            JSONObject result = new JSONObject(responce);

                            String state = result.getString("success");

                            if (state.equals("true")) {

//                                Marker Burgas = mMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(42.510578, 27.461014))
//                                        .title("Burgas")
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_house)));
//                                Burgas.showInfoWindow();

                                mMap.clear();

                                JSONArray jsonUsers = result.getJSONArray("users");
                                for (int i = 0; i < jsonUsers.length(); i++) {

                                    UsersItem tmpItem = new UsersItem(
                                            jsonUsers.getJSONObject(i).getInt("ID")
                                            , jsonUsers.getJSONObject(i).getString("NAME")
                                            , jsonUsers.getJSONObject(i).getString("COOR_X")
                                            , jsonUsers.getJSONObject(i).getString("COOR_Y")
                                            , jsonUsers.getJSONObject(i).getString("REC_DATE"));

                                    if (tmpItem.getCoor_x() != null && tmpItem.getCoor_y() != null) {
                                        Marker friends = mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(tmpItem.getCoor_x(), tmpItem.getCoor_y()))
                                                        .title(tmpItem.getUserName())
//                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_house))
                                        );
//                                    Burgas.showInfoWindow();

                                        markerList.add(friends);


                                    }
                                }

                            } else {
                                PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                                String r = result.getString("error");
                                if (r.equals("No friends found")) {
                                    tst.showToast("Няма намерени приятели");
                                    //tst.showToast(result.getString("error"));
                                }
                                if (r.equals("Please provide user id.")) {
                                    tst.showToast("Моля задайте потребител");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getBaseContext());
                            tst.showToast("Възникна грешка със сървъра");
                        }
                    }
                }
        );
        apiRequests.execute(method, user.getUserId());

    }

    public void showAllFriendsZoom(){

        if(markerList.isEmpty()){
            PersonalToastDesign tst = new PersonalToastDesign(this);
            tst.showToast("Няма намерени приятели");
        }else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Marker m : markerList) {
                builder.include(m.getPosition());
            }

            final int padding = 200;
            final LatLngBounds bounds = builder.build();

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                }
            });
        }
    }

    public void checkForFriendRequests(String userId) {
        new ServerApiRequests(this,

                new ServerApiRequests.AsyncStart() {

                    @Override
                    public void onProcessStart() {
//                        loadingLogin.setVisibility(View.VISIBLE);
                    }
                },

                new ServerApiRequests.AsyncResponse() {

                    @Override
                    public void onProcessFinish(String output) {
                        Log.e("friend requests", output);


                        try {
                            JSONObject result = new JSONObject(output);

                            String state = result.getString("success");
                            if (state.equals("true")) {
//
                                friendRequestsJSON = output;

                                int countRequests = result.getJSONArray("users").length();
                                headerLayour.setVisibility(View.VISIBLE);
                                showFriendRequestActivity.setText("Имате " + String.valueOf(countRequests) + " заявки за приятелство.");


                            } else {
                                headerLayour.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

        ).execute("find_friend_requests_count_url", userId);
    }


//    public void addMarkersToMap(){
//        Marker Burgas = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(42.510578, 27.461014))
//                .title("Burgas")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_house)));
//        Burgas.showInfoWindow();
//        markersList.add(Burgas);
//
//        Marker Sofia = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(42.697578, 23.32252))
//                .title("Sofia"));
//        //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_house)));
//        Sofia.showInfoWindow();
//        markersList.add(Sofia);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        Marker markerSydner = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        markerSydner.showInfoWindow();
//        markersList.add(markerSydner);
//
//
////                Marker updatedPos = mMap.addMarker(new MarkerOptions()
////                        .position(currentPositionPoint)
////                        .title("new Point"));
//        // .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_house)));
////                updatedPos.showInfoWindow();
//
//
//        //positioning
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//    }

//    private  void showMapMarkers(LatLng location) {
//
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker m : markersList) {
//            builder.include(m.getPosition());
//        }
//        //initialize the padding for map boundary
//        final int padding = 200;
//        /**create the bounds from latlngBuilder to set into map camera*/
//        final LatLngBounds bounds = builder.build();
//
//
//        // positioning animation
//
//        /**create the camera with bounds and padding to set into map*/
////        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        /**call the map call back to know map is loaded or not*/
////        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
////            @Override
////            public void onMapLoaded() {
////                /**set animated zoom camera into map*/
////                Toast.makeText(getBaseContext(), "animating", Toast.LENGTH_SHORT).show();
////                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
////            }
////        });
//
//        if(location != null){
//            mMap.moveCamera((CameraUpdateFactory.newLatLng(location)));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
//        }
//    }


//    // dialog to turn on GPS
//    private void buildAlertMessageNoGps() {
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setMessage("Your location is disable to use. Do you want to enable it?")
//                .setCancelable(true)
//                .setPositiveButton("Yes.", new DialogInterface.OnClickListener() {
//
//                            public void onClick(final DialogInterface dialog, final int id) {
//                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RESULT_ACTIVITY_CODE);
//                            }
//
//                        }
//
//                )
//                .setNegativeButton("No",
//
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(final DialogInterface dialog, final int id) {
//                                dialog.cancel();
//                                finish();
//                            }
//                        }
//                );
//        final AlertDialog alert = builder.create();
//        alert.setCancelable(false);
//        alert.show();
//    }


//    //This is for Lat lng which is determine by your device GPS
//    public class MyLocationListenerGPS implements LocationListener {
//        @Override
//        public void onLocationChanged(Location loc) {
//            glat = loc.getLatitude();
//            glng = loc.getLongitude();
//
//            //Setting the GPS Lat, Lng into the textView
//            //thats works
//            /*
//            textViewGpsLat.setText("GPS Latitude:  " + glat);
//            textViewGpsLng.setText("GPS Longitude:  " + glng);
//*/
//            Log.d("LAT & LNG GPS:", glat + " " + glng);
//            Toast.makeText(getApplicationContext(), "LAT & LNG GPS:" + nlat + " " + nlng, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            Log.d("LOG", "GPS is OFF!");
//            Toast.makeText(getApplicationContext(), "GPS is OFF!", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            Log.d("LOG", "Thanks for enabling GPS !");
//            Toast.makeText(getApplicationContext(), "Thanks for enabling GPS !", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//    }


}
