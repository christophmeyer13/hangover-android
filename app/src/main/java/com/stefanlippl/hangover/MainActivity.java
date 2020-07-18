package com.stefanlippl.hangover;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.events.EventsFragment;
import com.stefanlippl.hangover.law.*;
import com.stefanlippl.hangover.locations.LocationFragment;
import com.stefanlippl.hangover.locations.LocationItem;
import com.stefanlippl.hangover.map.MapFragment;
import com.stefanlippl.hangover.taxi.TaxiFragment;
import com.stefanlippl.hangover.user.UserArea;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int LOCATION_PERMISSION_CODE = 2;
    public static boolean areLocationsGranted = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolBar();
        setUpBurgerMenue();
        setUpEventsFragment();

        checkIfLocationPermissionsAlreadyGranted();
        getAverageRatingForAllLocations();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Makes sure the User can close the Drawer with Back-Button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (getSupportFragmentManager().findFragmentByTag("Events") != null && getSupportFragmentManager().findFragmentByTag("Events").isVisible()) {
            toolbar.setTitle(R.string.events);
            navigationView.setCheckedItem(R.id.nav_events);
        } else if (getSupportFragmentManager().findFragmentByTag("Locations") != null && getSupportFragmentManager().findFragmentByTag("Locations").isVisible()) {
            toolbar.setTitle(R.string.locations);
            navigationView.setCheckedItem(R.id.nav_locations);
        } else if (getSupportFragmentManager().findFragmentByTag("Map") != null && getSupportFragmentManager().findFragmentByTag("Map").isVisible()) {
            toolbar.setTitle(R.string.map);
            navigationView.setCheckedItem(R.id.nav_map);
        } else if (getSupportFragmentManager().findFragmentByTag("Persönlicher Bereich") != null && getSupportFragmentManager().findFragmentByTag("Persönlicher Bereich").isVisible()) {
            toolbar.setTitle(R.string.pers_nlicher_bereich);
            navigationView.setCheckedItem(R.id.nav_persönlicherBereich);
        } else if (getSupportFragmentManager().findFragmentByTag("Call a Taxi") != null && getSupportFragmentManager().findFragmentByTag("Call a Taxi").isVisible()) {
            toolbar.setTitle(R.string.call_a_taxi);
            navigationView.setCheckedItem(R.id.nav_taxi);
        } else if (getSupportFragmentManager().findFragmentByTag("AGB's / Datenschutz") != null && getSupportFragmentManager().findFragmentByTag("AGB's / Datenschutz").isVisible()) {
            toolbar.setTitle(R.string.agb_s);
            navigationView.setCheckedItem(R.id.nav_agb_datenschutz);
        } else if (getSupportFragmentManager().findFragmentByTag("Impressum") != null && getSupportFragmentManager().findFragmentByTag("Impressum").isVisible()) {
            toolbar.setTitle(R.string.impressum);
            navigationView.setCheckedItem(R.id.nav_impressum);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * This method is for selecting the Fragments which will be replaced when navigating through the drawer menue
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_container, new EventsFragment(), "Events");
            transaction.commit();
            toolbar.setTitle(R.string.events);

        } else if (id == R.id.nav_locations) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocationFragment(), "Locations");
            transaction.addToBackStack(null);
            transaction.commit();
            toolbar.setTitle(R.string.locations);

        } else if (id == R.id.nav_map) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment(), "Map");
            transaction.addToBackStack(null);
            transaction.commit();
            toolbar.setTitle(R.string.map);

        } else if (id == R.id.nav_persönlicherBereich) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserArea(), "Persönlicher Bereich");
            transaction.addToBackStack(null);
            transaction.commit();
            toolbar.setTitle(R.string.pers_area);

        } else if (id == R.id.nav_agb_datenschutz) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AgbFragment(), "AGB/Datenschutz").addToBackStack("AGB's / Datenschutz").commit();
            toolbar.setTitle(R.string.law);

        } else if (id == R.id.nav_impressum) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ImpressumFragment(), "Impressum").addToBackStack("Impressum").commit();
            toolbar.setTitle(R.string.impressum);

        } else if (id == R.id.nav_taxi) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TaxiFragment(), "Call a Taxi");
            transaction.addToBackStack(null);
            transaction.commit();
            toolbar.setTitle(R.string.taxi);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkIfLocationPermissionsAlreadyGranted() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            areLocationsGranted = true;
        } else {
            requestLocationPermissions();
        }
    }


    private void requestLocationPermissions() {
        // if permission was denied before show alert dialog
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Standort benötigt!")
                    .setMessage("Um dir passende Locations und Events in deiner Nähe anzeigen zu können, brauchen wir die Erlaubnis deinen Standort abfragen zu dürfen.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            // else request permission
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            areLocationsGranted = grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * This method is called after the LoadingScreen to make sure all Locations are already in offline Database
     */
    private void getAverageRatingForAllLocations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (final LocationItem current : Database.getAppDatabase(getApplicationContext()).accessDao().getAllLocations()) {
                    String directory = String.format("Locations/%s/Rating", current.getId());
                    db.collection(directory).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        float rating = 0;
                        int count = 0;

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    rating += Float.parseFloat(document.getString("ratingStars"));
                                    count++;
                                }
                                if (count != 0) rating = rating / count;
                                else rating = 0;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Database.getAppDatabase(getApplicationContext()).accessDao().updateRating(current.getId(), rating);
                                        Database.getAppDatabase(getApplicationContext()).accessDao().updateRatingCount(current.getId(), count);
                                    }
                                }).start();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void setUpToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.events);
        setSupportActionBar(toolbar);
    }

    private void setUpBurgerMenue() {
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpEventsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EventsFragment(),"Events").commit();
        navigationView.setCheckedItem(R.id.nav_events);
        navigationView.setNavigationItemSelectedListener(this);
    }
}