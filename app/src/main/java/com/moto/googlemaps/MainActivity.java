package com.moto.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mapa;
    private FusedLocationProviderClient fusedLocationClient;
    RequestQueue requestQueue;
    Polyline polyadd;
    HashMap<String, String> hm = new HashMap<String, String>();

    // https://maps.googleapis.com/maps/api/directions/json?origin=20.140396,%20-101.150549&destination=20.169192,%20-101.182198&key=AIzaSyBHtYD_i3eqYqdCroUTQDwzb5FtqD323oc


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            objectRequest(location.getLatitude(),location.getLongitude(),20.140396,-101.150549);
                            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            mapa.addMarker(new MarkerOptions()
                                    .position(currentPosition)
                                    .title("Current Position"));
                            CameraPosition cameraPosition = CameraPosition.builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15)
                                    .build();

                            mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                });
    }

    private void objectRequest (double lat1, double lo1, double lat2, double lo2) {
        List<LatLng> listaPuntos = new ArrayList<>();
        Gson gson = new Gson();
        Log.d("HOLI",lat1 + " " + lo1 + " " + lat2 + " " +lo2);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+lat1+",%20"+lo1+"&destination="+lat2+",%20"+lo2+"&key=AIzaSyBHtYD_i3eqYqdCroUTQDwzb5FtqD323oc";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {
                    jRoutes = response.getJSONArray("routes");
                    for(int i = 0; i < jRoutes.length(); i++) {
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        for(int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                            for(int k = 0; k <jSteps.length(); j++){
                                double startLocationLat = 0;
                                double startLocationLong = 0;
                                double endLocationLat = 0;
                                double endLocationLong = 0;
                                startLocationLat = (double)((JSONObject)((JSONObject)jSteps.get(k)).get("start_location")).get("lat");
                                startLocationLong = (double)((JSONObject)((JSONObject)jSteps.get(k)).get("start_location")).get("lng");
                                endLocationLat = (double)((JSONObject)((JSONObject)jSteps.get(k)).get("end_location")).get("lat");
                                endLocationLong = (double)((JSONObject)((JSONObject)jSteps.get(k)).get("end_location")).get("lng");
                                LatLng start = new LatLng(startLocationLat,startLocationLong);
                                LatLng end = new LatLng(endLocationLat,endLocationLong);
                                listaPuntos.add(start);
                                listaPuntos.add(end);
                            }
                        }
                    }
                    polyadd = mapa.addPolyline(new PolylineOptions().clickable(true)
                            .addAll(listaPuntos));
                }catch (Exception ex){
                    Log.d("HOLI",ex.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}