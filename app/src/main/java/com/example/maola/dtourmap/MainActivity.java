package com.example.maola.dtourmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.maola.dtourmap.Model.InfoWindowData;
import com.example.maola.dtourmap.Model.Report;
import com.example.maola.dtourmap.reportActivities.NewReportActivity;
import com.example.maola.dtourmap.UserActivity.LoginActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.maola.dtourmap.Utility.StringUtils.getDate;
import static java.text.DateFormat.getDateTimeInstance;

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
    private String description, time;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = "2MainActivity";
    private FirebaseUser currentUser;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check user status and eventually redirect to loginActivity
        getUserStatus();

        // Setup UI
        setupUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Firebase DB
        myRef = FirebaseUtils.getReportRef();
        reportListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            firebaseAuth.removeAuthStateListener(mAuthListener);
//        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getUserStatus();
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

        } else if (id == R.id.logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getUserStatus(){
        // Get User instance
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null) {
            Toast.makeText(getApplicationContext(), "User null", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else if(currentUser.isAnonymous()) {
            Toast.makeText(getApplicationContext(), "User isAnonymous", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), currentUser.getEmail(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupUI() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    if(currentUser.isAnonymous()) {
                        logout();
                    } else if(mLastKnownLocation != null) {
                        double dLat = mLastKnownLocation.getLatitude();
                        double dLng = mLastKnownLocation.getLongitude();
                        Intent i = new Intent(getApplicationContext(), NewReportActivity.class);
                        i.putExtra(Constants.vLat, dLat);
                        i.putExtra(Constants.vLng, dLng);

                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Impossibile trovare la posizione corrente, premi un paio di secondi sulla " +
                                        "mappa per aggiungere un nuovo report", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null && currentUser.isAnonymous()){
            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.putExtra(Constants.show_anonymous, false);
                        startActivity(i);
                    } else {
                        Log.w(TAG,"Something is wrong!");
                    }
                }
            });
        } else {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "Clicked on snippet" + marker.getTitle() + marker.getId(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), NewReportActivity.class);
                i.putExtra("varMarkerId", marker.getId());
                i.putExtra("varIdMarkerDB", result);
                startActivity(i);
                Log.i("MarkerID", result.get(marker.getId()) + "");

            }
        });

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);


        mMap.setOnMyLocationButtonClickListener(this);
        checkLocationPermission();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

//                Toast.makeText(getApplicationContext(), "User null", Toast.LENGTH_LONG).show();
//                    double dLat = latLng.latitude;
//                    double dLng = latLng.longitude;
//                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                    i.putExtra(Constants.vLat, dLat);
//                    i.putExtra(Constants.vLng, dLng);
//
//                    startActivity(i);
                double dLat = latLng.latitude;
                double dLng = latLng.longitude;
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser.isAnonymous()) {
                    Toast.makeText(getApplicationContext(), "Anonymous" + currentUser, Toast.LENGTH_LONG).show();
                    logout();
                } else {
                    Toast.makeText(getApplicationContext(), "User not null " + currentUser, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), NewReportActivity.class);
                    intent.putExtra(Constants.vLat, dLat);
                    intent.putExtra(Constants.vLng, dLng);
                    startActivity(intent);
                    finish();
                }

//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null) {
//                    Toast.makeText(getApplicationContext(), "User null", Toast.LENGTH_LONG).show();
//
//                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                    i.putExtra(Constants.vLat, dLat);
//                    i.putExtra(Constants.vLng, dLng);
//
//                    startActivity(i);
//                } else {
//                    Toast.makeText(getApplicationContext(), "User not null " + user, Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(getApplicationContext(), NewReportActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            }});


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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapSegnalazioni : dataSnapshot.getChildren()) {
                    Report report2 = snapSegnalazioni.getValue(Report.class);
                    markerID = setMarkerColor(report2, report2.getTypology());
                    //TODO creare modalità lista
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }

        });
    }



    public Marker setMarkerColor(Report report1, String category){
        Marker mRenderedMarker;
        String snippetString = "snippet1_snippet2_snippet3_snippet4";
        long postingDate = report1.getPostingDate();
//        String snippetString = getDate(postingDate, "dd/MM/yyyy hh:mm");
        InfoWindowData info = new InfoWindowData();
        info.setAddress(report1.getAddress());
        info.setPostingDate(getDate(postingDate, "dd/MM/yyyy hh:mm"));
        info.setReportDate(getDate(report1.getReportDate(), "dd/MM/yyyy"));
        info.setUserName(report1.getUserName());
        if(report1.getDescription() != null) {
            info.setDescription(report1.getDescription());
        }


        if(category.equals(getString(R.string.spaccio))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(snippetString));
        }
        else if(category.equals(getString(R.string.furto))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getAddress())
                    .snippet(snippetString)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        } else if(category.equals(getString(R.string.vandalismo))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getAddress())
                    .snippet(snippetString)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        } else if(category.equals(getString(R.string.altro))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getAddress())
                    .snippet(snippetString)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }else {
            Log.i(TAG, "Errore");
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title("errore")
                    .snippet("non disp1_non disp2_non disp3"));
        }
        mRenderedMarker.setTag(info);
        return mRenderedMarker;
    }




class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


    private final View mContents;

    CustomInfoWindowAdapter() {
        mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mContents);
        return mContents;
    }


    private void render(Marker marker, View view) {

        String str = marker.getSnippet();
        Log.i("Marker snippet", str);
//        final String[] str2 = str.split("_");

        String title = marker.getTitle();

        TextView titleUi = (TextView) view.findViewById(R.id.info_address);
        TextView postedBy = (TextView)view.findViewById(R.id.info_posted_by);
        TextView info_date = (TextView) view.findViewById(R.id.info_date);
        TextView info_detail = (TextView) view.findViewById(R.id.info_detail);

        if (title != null) {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }
        info_date.setText(str);

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        postedBy.setText("Segnalato il " + infoWindowData.getPostingDate() + "da " + infoWindowData.getUserName());
        info_date.setText(infoWindowData.getReportDate().toString());
        info_detail.setText(infoWindowData.getDescription());
    }


}

}



