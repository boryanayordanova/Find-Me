package com.boryana.android.thesis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.boryana.android.thesis.Models.UsersItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowUserCurrentLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private String userName;
    private String recordDate;
    private Double coordinate_x;
    private Double coordinate_y;

    private TextView currentFriendName;
    private TextView currentFriendDate;
    private TextView currentFriendDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_current_location);

        getSupportActionBar().setTitle("Местоположение на приятел");

        Bundle bundle = getIntent().getExtras();
        if (bundle.size() == 0) finish();

        userName = bundle.getString("NAME");
        recordDate = bundle.getString("REC_DATE");
        coordinate_x = bundle.getDouble("COOR_X");
        coordinate_y = bundle.getDouble("COOR_Y");

        currentFriendName = (TextView) findViewById(R.id.chosenFriendName);
        currentFriendDistance = (TextView) findViewById(R.id.chosenFriendDistance);
        currentFriendDate = (TextView) findViewById(R.id.chosenFriendDate);

        currentFriendName.setText(userName);
        currentFriendDate.setText(recordDate);
        if(MainScreenActivity.LatitudeCoords == 0 || MainScreenActivity.LotitudeCoords == 0){
            currentFriendDistance.setText("неуточнени километри разтрояние");
        } else {
            UsersItem currentUser = new UsersItem(0,"",String.valueOf(coordinate_x),String.valueOf(coordinate_y),"");
            double distanceToMe = currentUser.distanceToMe(MainScreenActivity.LatitudeCoords, MainScreenActivity.LotitudeCoords);
            currentFriendDistance.setText("~ "+ String.valueOf(distanceToMe)+ " км. разстояние");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_chosen_friend);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onInfoWindowClick(Marker marker) {

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

            mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(coordinate_x, coordinate_y))
                            .title(userName)
            );

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinate_x, coordinate_y),13));
        }
    }
}
