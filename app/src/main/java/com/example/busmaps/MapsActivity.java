package com.example.busmaps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
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

        // Show labels on the markers
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(@NonNull Marker marker) {
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
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }

        });

        // Get road route between MGBS and JBS points
        String key = "AIzaSyAtrfs_lzcQMX0fRUUjkwsGk5fx3WXeZvw";
        String directionURL = getDirectionURL(mgbsHyderabad, jbsHyderabad, key);

        Log.e("url output", directionURL.toString());
        // Draw road route on map
        drawRoute(directionURL);
    }

    // Get URL for getting road route between origin and destination points
    private String getDirectionURL(LatLng origin, LatLng dest, String key) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + dest.latitude + "," + dest.longitude
                + "&sensor=false"
                + "&mode=driving"
                + "&key=" + key;
    }

    private void drawRoute(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    JSONArray routeArray = response.optJSONArray("routes");
                    try {
                        assert routeArray != null;
                        JSONObject routes = routeArray.getJSONObject(0);
                        JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                        String encodedString = overviewPolylines.getString("points");
                        List<LatLng> list = decodePoly(encodedString);
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .addAll(list)
                                .width(10)
                                .color(Color.RED)
                                .geodesic(true);
                        mMap.addPolyline(polylineOptions);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Error occurred while drawing route", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, error.toString());
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private List<LatLng> decodePoly(String encodedString) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encodedString.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }


}