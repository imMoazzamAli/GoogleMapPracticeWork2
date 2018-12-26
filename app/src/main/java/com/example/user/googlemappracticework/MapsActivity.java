package com.example.user.googlemappracticework;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;

    private EditText etSearch;

    View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();

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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Gujranwala and move/animate the camera
        LatLng latLng = new LatLng(32.073419, 74.210114);
        MarkerOptions options = new MarkerOptions()
                .title("This is title")
                .position(latLng);
        mMap.addMarker(options);

        //animate camera here
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(update);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Grant the Permission", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        moveLocationIconBelowEditText();
        locationBtnClick();

    }

    public void moveLocationIconBelowEditText() {
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 280, 100, 30);
    }

    public void locationBtnClick() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
            goToLocationZoom(lat, lng, 10);

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

    @Override
    public void onClick(View v) {

    }

}
