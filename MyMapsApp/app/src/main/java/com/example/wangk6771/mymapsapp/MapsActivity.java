package com.example.wangk6771.mymapsapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private EditText searchBarText;
    private LocationManager locationManager;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BETWEEN_UPDATES = 3000;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 5;
    private static final int MY_LOC_ZOOM_FACTOR = 17;
    private Location myLocation;
    private boolean tracking = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng birthPlace = new LatLng(38.5449, -121.7405);
        mMap.addMarker(new MarkerOptions().position(birthPlace).title("Marker in birthplace"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(birthPlace));
    }

    public void changeMapType(View v) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public void pointsOfInterest(View v) {
        searchBarText = (EditText) findViewById(R.id.searchText);
        String loc = searchBarText.getText().toString().toLowerCase();
        if (loc.equals("torrey pines beach")) {
            LatLng beach = new LatLng(32.9362, -117.2614);
            mMap.addMarker(new MarkerOptions().position(beach).title("Torrey Pines Beach"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(beach));
        }
        if (loc.equals("airport")) {
            LatLng airport = new LatLng(32.7338006, -117.193303792);
            mMap.addMarker(new MarkerOptions().position(airport).title("San Diego Airport"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(airport));
        }
        if (loc.equals("seaworld")) {
            LatLng seaworld = new LatLng(32.7648, -117.2266);
            mMap.addMarker(new MarkerOptions().position(seaworld).title("Seaworld"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(seaworld));
        }
        if (loc.equals("zoo")) {
            LatLng zoo = new LatLng(32.7347483943, -117.150943196);
            mMap.addMarker(new MarkerOptions().position(zoo).title("San Diego Zoo"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(zoo));
        }
    }

    public void dropNetworkMarker(String provider) {
        LatLng userLocation = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);
        }
        if (myLocation == null) {
            //display a message via Log.d
            Log.d("dropMarker", "myLocation was null");

        } else {
            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            //display a message with the Lat/Long
            Log.d("dropMarker", "Lat: " + myLocation.getLatitude() + " Long: " + myLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);
            //drops the marker on the map
            if(isNetworkEnabled) {
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(userLocation)
                        .radius(1)
                        .strokeColor(Color.RED)
                        .strokeWidth(2)
                        .fillColor(Color.RED));
                mMap.animateCamera(update);
            }
        }
    }

    public void dropGPSMarker(String provider) {
        LatLng userLocation = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);
        }
        if (myLocation == null) {
            //display a message via Log.d
            Log.d("dropMarker", "myLocation was null");

        } else {
            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            //display a message with the Lat/Long
            Log.d("dropMarker", "Lat: " + myLocation.getLatitude() + " Long: " + myLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);
            //drops the marker on the map
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(userLocation)
                        .radius(1)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(2)
                        .fillColor(Color.BLUE));
                mMap.animateCamera(update);

        }
    }

    public void getLocation(View v) {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                //get GPS status
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSenabled){
                Log.d("MyMaps", "getLocation: GPS is enabled");
                Toast.makeText(this, "GPS is enabled", Toast.LENGTH_LONG);
            }

            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled){
                Log.d("MyMaps", "getLocation: Network is enabled");
                Toast.makeText(this, "Network is enabled", Toast.LENGTH_LONG);

            }

            if (!isGPSenabled && !isNetworkEnabled) {
                Log.d("MyMaps", "getLocation: No provider is enabled");
            } else {

                this.canGetLocation = true;
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                if (isNetworkEnabled) {
                    Log.d("MyMaps", "getLocation: Network enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BETWEEN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATE,
                            locationListenerNetwork);

                    Log.d("MyMaps", "getLocation: NetworkLoc update request successful");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }
                if (isGPSenabled) {
                    Log.d("MyMaps", "getLocation: Network enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BETWEEN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATE,
                            locationListenerGPS);

                    Log.d("MyMaps", "getLocation: NetworkLoc update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
            }


        } catch (Exception e) {
            Log.d("MyMaps", "Caught exception in getLocation");
            e.printStackTrace();
        }
    }


    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output is Log.d and Toast that GPS is enabled
            Log.d(TAG, "locationListenerGPS: onLocationChanged: enabled");
            //Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT);

            //Drop a marker on map - create a method called dropMarker
            dropGPSMarker(LocationManager.GPS_PROVIDER);

            //Remove the network location updates
            //locationManager.removeUpdates(locationListenerNetwork);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output is Log.d and Toast that GPS is enabled and working
            Log.d(TAG, "locationListenerGPS: onLocationChanged: enabled");
            //Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT);

            //setup a switch statement to check the status input parameter
            //case LocationProvider.AVAILABLE --> output message to Log.d and Toast
            if (status == LocationProvider.AVAILABLE) {
                Log.d(TAG, "locationListenerGPS: Location Provider is availabe");
                //Toast.makeText(this, "Location provider availabe", Toast.LENGTH_SHORT);
                isNetworkEnabled = false;
                isGPSenabled = true;
            }
            //case LocationProvider.OUT_OF_SERVICE --> request updates from NETWORK_PROVIDER
            //case LocationProvider.TEMPORARILY_UNAVAILABLE --> request updates from NETWORK_PROVIDER
            else if(status== LocationProvider.OUT_OF_SERVICE|| status == LocationProvider.TEMPORARILY_UNAVAILABLE){
                isNetworkEnabled=true;
                isGPSenabled=false;
            }
            //case default --> request updates from NETWORK_PROVIDER
            else{
                isNetworkEnabled=true;
                isGPSenabled=false;
            }

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            //output is Log.d and Toast that GPS is enabled
            Log.d(TAG, "locationListenerNetwork: onLocationChanged: enabled");
            //Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT);

            //Drop a marker on map - create a method called dropMarker
            dropNetworkMarker(LocationManager.NETWORK_PROVIDER);

            //Remove the network location updates
            //locationManager.removeUpdates(locationListenerNetwork);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output is Log.d and Toast that GPS is enabled and working
            Log.d(TAG, "locationListenerGPS: onLocationChanged: enabled");
            //Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT);

            //setup a switch statement to check the status input parameter
            //case LocationProvider.AVAILABLE --> output message to Log.d and Toast
//            if (status == LocationProvider.AVAILABLE) {
//                Log.d(TAG, "locationListenerGPS: Location Provider is availabe");
//                //Toast.makeText(this, "Location provider availabe", Toast.LENGTH_SHORT);
//            }
//            //case LocationProvider.OUT_OF_SERVICE --> request updates from NETWORK_PROVIDER
//            //case LocationProvider.TEMPORARILY_UNAVAILABLE --> request updates from NETWORK_PROVIDER
//            else if(status== LocationProvider.OUT_OF_SERVICE|| status == LocationProvider.TEMPORARILY_UNAVAILABLE){
//                isNetworkEnabled=true;
//                isGPSEnabled=false;
//            }
//            //case default --> request updates from NETWORK_PROVIDER
//            else{
//                isNetworkEnabled=true;
//                isGPSEnabled=false;
//            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    public void clearMap(){
        mMap.clear();
        Log.d("clearMap", "Map cleared");
    }

}
