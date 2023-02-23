package com.example.busmaps;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set default location to Hyderabad, India
        LatLng hyderabad = new LatLng(17.3850, 78.4867);
        float zoomLevel = 12f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hyderabad, zoomLevel));

        // Add a marker to MGBS Hyderabad bus stand
        LatLng mgbsHyderabad = new LatLng(17.3799, 78.4860);
        mMap.addMarker(new MarkerOptions().position(mgbsHyderabad).title("MGBS"));

        // Add a marker to JBS Hyderabad bus stand
        LatLng jbsHyderabad = new LatLng(17.4477, 78.4980);
        mMap.addMarker(new MarkerOptions().position(jbsHyderabad).title("JBS"));

// Create a list to store the waypoints
        List<LatLng> waypoints = new ArrayList<>();

// Create a polyline options object and set its properties
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        polylineOptions.geodesic(true);

// Add the origin and destination to the waypoints list
        waypoints.add(mgbsHyderabad);
        waypoints.add(jbsHyderabad);

// Draw the road route between the origin and destination
        polylineOptions.addAll(waypoints);
        mMap.addPolyline(polylineOptions);

        // Show labels on the markers
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                info.addView(title);

                return info;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

        });
    }
}