package com.moto.googlemaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mapa;
    private FusedLocationProviderClient fusedLocationClient;
    Polyline ruta;

    // https://maps.googleapis.com/maps/api/directions/json?origin=20.140396,%20-101.150549&destination=20.169192,%20-101.182198&key=AIzaSyBHtYD_i3eqYqdCroUTQDwzb5FtqD323oc


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        if (location != null) {
                            mapa.clear();
                            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            mapa.addMarker(new MarkerOptions()
                                    .position(currentPosition)
                                    .title("Marker in Sydney"));
                            CameraPosition cameraPosition = CameraPosition.builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(20)
                                    .build();

                            mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            Polyline polyline1 = mapa.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .add(
                                            new LatLng(location.getLatitude(), location.getLongitude())
                                    ));
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
    }
}