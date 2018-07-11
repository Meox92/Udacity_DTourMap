package com.example.maola.dtourmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.maola.dtourmap.Model.Report;
import com.example.maola.dtourmap.NewReportActivity.NewReportActivity;
import com.example.maola.dtourmap.Utility.Constants;
import com.example.maola.dtourmap.Utility.FirebaseUtils;
import com.example.maola.dtourmap.Utility.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastKnownLocation;
    private boolean mPermissionDenied = false;
    private boolean mLocationPermissionGranted;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };
    private DatabaseReference myRef;
    private Object reportIdObj;
    private List<Object> listaChiavi;
    private Marker markerID;
    private HashMap<String, Object> result;
    private List<Report> lSegnalazioni;
    private Report report1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLastKnownLocation != null) {
                    double dLat = mLastKnownLocation.getLatitude();
                    double dLng = mLastKnownLocation.getLongitude();
                    Intent i = new Intent(getApplicationContext(), NewReportActivity.class);
                    i.putExtra(Constants.vLat, dLat);
                    i.putExtra(Constants.vLng, dLng);

                    startActivity(i);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        myRef = FirebaseUtils.getReportRef();
        reportListener();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_rate) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Toast.makeText(MapActivity.this, "Clicked on snippet" + marker.getTitle() + marker.getId(), Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(MapActivity.this, ReportDetailActivity.class);
//                i.putExtra("varMarkerId", marker.getId());
//                i.putExtra("varIdMarkerDB", result);
//                startActivity(i);
//                Log.i("MarkerID", result.get(marker.getId()) + "");
//
//            }
//        });

//        mMap.setOnMyLocationChangeListener(myLocationChangeListener);


        mMap.setOnMyLocationButtonClickListener(this);
        checkLocationPermission();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(getApplicationContext(), "Long press", Toast.LENGTH_LONG).show();
                double dLat = latLng.latitude;
                double dLng = latLng.longitude;
                Intent i = new Intent(getApplicationContext(), NewReportActivity.class);
                i.putExtra(Constants.vLat, dLat);
                i.putExtra(Constants.vLng, dLng);

                startActivity(i);
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
            return;
        }

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLastKnownLocation = location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 16.00f));                        }
                    }
                });

    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mLocationPermissionGranted = true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            checkLocationPermission();
            mLocationPermissionGranted = true;
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                checkLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void reportListener(){
        lSegnalazioni = new ArrayList<Report>();
        listaChiavi = new ArrayList<>();
//        final List<String> stringList = new ArrayList<>();
        result = new HashMap<>();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapSegnalazioni : dataSnapshot.getChildren()) {
                    lSegnalazioni.add(snapSegnalazioni.getValue(Report.class));
                    reportIdObj = snapSegnalazioni.getKey();
                    listaChiavi.add(reportIdObj);

                    //TODO creare modalità lista
                }
                for(int i = 0; i< lSegnalazioni.size(); i++){
                    report1 = lSegnalazioni.get(i);
                    String typology = report1.getTypology();
                    String description = report1.getDescription();
                    String time = report1.getPostingDate();

//                    mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
//                    ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                    setMarkerColor(report1, typology);
                    markerID = setMarkerColor(report1, typology);

                    result.put(markerID.getId(), listaChiavi.get(i));
                    Log.i("TAG", result.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public Marker setMarkerColor(Report report1, String typology){
        Marker mRenderedMarker;


        if(typology.equals(getString(R.string.spaccio))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(report1.getPoints() + "_" + report1.getPostingDate()+ "_" + report1.getDescription()));
        }
        else if(typology.equals(getString(R.string.furto))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getPostingDate()+ "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        } else if(typology.equals(getString(R.string.vandalismo))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getPostingDate()+ "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        } else if(typology.equals(getString(R.string.altro))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getPostingDate()+ "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }else{
            Log.i("MapsActivity", "Errore");
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet("non disp1_non disp2_non disp3"));
        }

        return mRenderedMarker;
    }


}
