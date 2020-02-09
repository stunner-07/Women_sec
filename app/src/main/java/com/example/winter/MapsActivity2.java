package com.example.winter;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import com.google.android.gms.location.LocationListener;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.StrictMath.abs;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap, m2Map;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng predestination =null;
    CountDownTimer t;
    String address = "";
    int i = 0, l=1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng latLng = centerMapOnLocation(lastKnownLocation, "Your Location!");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        }
    }

    public void sendMessage(LatLng userLocation, String message) {
        String phoneNumber = "6265300149";
        SmsManager smsManager = SmsManager.getDefault();

        if (message == "I'm at")
            message += " Latitude:" + Double.toString(userLocation.latitude) + " and Longitude:" + Double.toString(userLocation.longitude);
        else
            message = "Destination Reached!";

        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public LatLng centerMapOnLocation(Location location, String titlee) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(titlee));
        return userLocation;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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
        };

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        m2Map = googleMap;
        mMap.setOnMapLongClickListener(this);
        m2Map.setOnMapLongClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.i("gfd", "dsgs");
            LatLng latLng = centerMapOnLocation(lastKnownLocation, "Your Location!");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @Override
    public void onMapLongClick(final LatLng destination) {
        if(destination!=predestination) {

            l = 0;
            if (i == 1) {
                m2Map.clear();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapOnLocation(lastKnownLocation, "Your Location!");
                    Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), destination).width(5).color(Color.GREEN));
                    //mMap.clear();
                    t.cancel();
                }
                i = 0;
            }
            i++;
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            address = "";
            try {
                List<Address> listAddresses = geocoder.getFromLocation(destination.latitude, destination.longitude, 1);
                if (listAddresses != null && listAddresses.size() > 0) {
                    if (listAddresses.get(0).getThoroughfare() != null) {
                        if (listAddresses.get(0).getSubThoroughfare() != null) {
                            address += listAddresses.get(0).getSubThoroughfare() + " ";
                        }
                        address += listAddresses.get(0).getThoroughfare();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (address == "") {
                address = "Latitude:" + Double.toString(destination.latitude) + " Longitude:" + Double.toString(destination.longitude);
            }
            Log.i("Location: ", address);
            Toast.makeText(this, "Destination set to : " + address, Toast.LENGTH_SHORT).show();
            m2Map.addMarker(new MarkerOptions().position(destination).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "Your Location!");
                Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), destination).width(5).color(Color.GREEN));
            }

            t = new CountDownTimer(Long.MAX_VALUE, 15000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    final Handler handler = new Handler();
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {

                            if (l == 1) {
                                t.cancel();
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
                                }
                            }
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            LatLng currentlatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            mMap.clear();
                            centerMapOnLocation(currentLocation, "Your Location!");

                            m2Map.addMarker(new MarkerOptions().position(destination).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true).add(currentlatlng, destination).width(5).color(Color.GREEN));

                            if (abs(currentLocation.getLatitude() - destination.latitude) > 0.0001 || abs(currentLocation.getLongitude() - destination.longitude) > 0.0001) {

                                //sendMessage(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), "I'm at");
                                Log.i("I'm at", Double.toString(currentLocation.getLatitude()) + " ," + Double.toString(currentLocation.getLongitude()));
                                handler.postDelayed(this, 15000);
                            } else {
                                onFinish();
                            }

                        }

                    };
                    if (l == 0)
                        handler.post(run);

                }

                @Override
                public void onFinish() {

                    //sendMessage(new LatLng(0,0), "Reached!");
                    Log.i("Destination", " Reached!");
                    l = 1;
                    cancel();
                }
            }.start();
            predestination = destination;
        }
    }
}
