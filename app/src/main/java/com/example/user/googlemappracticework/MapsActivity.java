package com.example.user.googlemappracticework;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.googlemappracticework.Model.ModelLocationData;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private EditText etSearch;

    private Location currentLocation;
    private LocationManager locationManager;
    private static final long MIN_TIME = 0;
    private static final float MIN_DISTANCE = 1000;

    private Marker myLocationMarker;
    private Marker marker;

    View mapView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbReferenceLocation;

    ArrayList<ModelLocationData> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //below VIEW is used for moving location button to specific position
        mapView = mapFragment.getView();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbReferenceLocation = firebaseDatabase.getReference("LOCATIONS");

        listData = new ArrayList<>();

        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    geoLocate();
                    return true;
                } else {
                    return false;
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Grant the Permission", Toast.LENGTH_LONG).show();
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is disabled", Toast.LENGTH_SHORT).show();
            return;
        }

        //after checking required permissions
        //getCurrent location without  pressing any button
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

    }

    public void saveToFirebase(final Location location) {

        final String id = dbReferenceLocation.push().getKey();

        final String name = "ali@gmail.com";
        final Double latitude = location.getLatitude();
        final Double longitude = location.getLongitude();

        dbReferenceLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;

                //first time there will be no data. So checked it here
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        i++;
                        ModelLocationData data = snapshot.getValue(ModelLocationData.class);

                        if (data.getLatitude().equals(latitude) && data.getLongitude().equals(longitude) && data.getName().equals(name)) {
                            Toast.makeText(MapsActivity.this, "nothing changed", Toast.LENGTH_SHORT).show();
                            break;

                        } else if ((!data.getLatitude().equals(latitude) && !data.getLongitude().equals(longitude)) && data.getName().equals(name)) {
                            Toast.makeText(MapsActivity.this, "LatLng changed", Toast.LENGTH_SHORT).show();

                            String id = snapshot.getKey();
                            ModelLocationData updatedData = new ModelLocationData(name, latitude, longitude);
                            dbReferenceLocation.child(id).setValue(updatedData);
                            break;
                        }

                        if (i == dataSnapshot.getChildrenCount()) {
                            Toast.makeText(MapsActivity.this, "At the end of table i.e. New Entry", Toast.LENGTH_SHORT).show();

                            String id = dbReferenceLocation.getKey();
                            ModelLocationData value = new ModelLocationData(name, latitude, longitude);
                            dbReferenceLocation.child(id).setValue(value);
                        }
                    }//end of loop

                } else {
                    //this else work  only once when there is no data at very beginning

                    ModelLocationData modelLocationData = new ModelLocationData(name, latitude, longitude);
                    dbReferenceLocation.child(id).setValue(modelLocationData);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add (first way) a marker in Gujranwala and move/animate the camera
 /*      LatLng latLng = new LatLng(32.073419, 74.210114);
        MarkerOptions options = new MarkerOptions()
                .title("This is title")
                .position(latLng);
        mMap.addMarker(options);

        //animate camera here
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(update);*/

        // Add (second way) a marker in Gujranwala and move/animate the camera
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(32.112910, 74.163596)).title("Just for testing"));

        locationBtnClick();
        moveLocationIconBelowEditText();
        showMarkers();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Grant the Location Permission", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    public void showMarkers() {

        //mMap.clear();

        dbReferenceLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelLocationData data = snapshot.getValue(ModelLocationData.class);
                    listData.add(data);
                }

                for (int i = 0; i < listData.size(); i++) {

                    String name = listData.get(i).getName();
                    Double latitude = listData.get(i).getLatitude();
                    Double longitude = listData.get(i).getLongitude();

                    marker = mMap.addMarker(new MarkerOptions().title(name).position(new LatLng(latitude, longitude)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void moveLocationIconBelowEditText() {
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 250, 100, 30);
    }

    public void locationBtnClick() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MapsActivity.this, "GPS is disabled.", Toast.LENGTH_LONG).show();
                    return true;

                } else
                    return false;
            }
        });
    }

    public void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(update);
    }

    public void geoLocate() {
        String search = etSearch.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = null;
        try {
            list = gc.getFromLocationName(search, 1);

            Address address = list.get(0);
            String countryName = address.getCountryName();
            String locality = address.getLocality();

            double lat = address.getLatitude();
            double lng = address.getLongitude();
            goToLocationZoom(lat, lng, 15);

        } catch (IOException e) {
            Toast.makeText(this, "in catch block", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onLocationChanged(Location location) {
/*
        //mMap.clear();
        if (myLocationMarker.isVisible()) {
            myLocationMarker.remove();
        }
        myLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("My Location"));
*/

        goToLocationZoom(location.getLatitude(), location.getLongitude(), 15);

        saveToFirebase(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_None) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        } else if (id == R.id.menu_Normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        } else if (id == R.id.menu_Terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        } else if (id == R.id.menu_Satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        } else if (id == R.id.menu_Hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
