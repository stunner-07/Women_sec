package com.example.winter;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "Your Location!");
            }
        }
    }

    public void centerMapOnLocation (Location location, String titlee)
    {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.clear();
        if(titlee == "Your Location!")
            mMap.addMarker(new MarkerOptions().position(userLocation).title(titlee));
        else
            mMap.addMarker(new MarkerOptions().position(userLocation).title(titlee).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        if(intent.getIntExtra("placeNumber", 0)==0)
        {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //centerMapOnLocation(location, "Your Location!");
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

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation!=null) {
                    centerMapOnLocation(lastKnownLocation, "Your Location!");
                }
                else{
//                    LatLng sydney = new LatLng(-34, 151);
//                    centerMapOnLocation(sydney,"your location");
//                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    Location loc = new Location(LocationManager.GPS_PROVIDER);
                    loc.setLatitude(26.2183);
                    loc.setLongitude(78.1828);
                    centerMapOnLocation(loc,"Your Location");
                }
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }
        else
        {
            //mMap.clear();
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MarkUnsafe.locations.get(intent.getIntExtra("placeNumber", 0)).latitude);
            placeLocation.setLongitude(MarkUnsafe.locations.get(intent.getIntExtra("placeNumber", 0)).longitude);

            centerMapOnLocation(placeLocation, MarkUnsafe.places.get(intent.getIntExtra("placeNumber", 0)));
            mMap.addMarker(new MarkerOptions().position(MarkUnsafe.locations.get(intent.getIntExtra("placeNumber", 0))).title(MarkUnsafe.places.get(intent.getIntExtra("placeNumber", 0))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            LatLng userLocation = new LatLng(placeLocation.getLatitude(), placeLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(listAddresses!=null && listAddresses.size()>0)
            {
                if(listAddresses.get(0).getThoroughfare()!=null)
                {
                    if(listAddresses.get(0).getSubThoroughfare()!=null)
                    {
                        address += listAddresses.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAddresses.get(0).getThoroughfare();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(address == "")
        {
            address = "Latitude:" + Double.toString(latLng.latitude) + " Longitide:" + Double.toString(latLng.longitude);
        }
        Log.i("Location: ", address);
        mMap.addMarker(new MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        MarkUnsafe.places.add(address);
        MarkUnsafe.locations.add(latLng);
        MarkUnsafe.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(this,  address + " : Marked Unsafe!", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(intent);
    }
}
