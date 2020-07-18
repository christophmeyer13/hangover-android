package com.stefanlippl.hangover.loadingscreen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanlippl.hangover.MainActivity;
import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.events.EventItem;
import com.stefanlippl.hangover.locations.LocationItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class LoadingScreen extends AppCompatActivity implements OnLocationDownloadFinishedListener, OnEventDownloadFinishedListener {

    private static final int INTERNET_PERMISSION_CODE = 1;
    private FirebaseFirestore onlineDatabase = FirebaseFirestore.getInstance();
    private Database database;

    //Lists for onlineDatabase comparison
    private ArrayList<String> existingDatabaseLocationIDs = new ArrayList<>();
    private ArrayList<String> existingDatabaseEventIDs = new ArrayList<>();
    private ArrayList<String> firebaseLocationIDs = new ArrayList<>();
    private ArrayList<String> firebaseEventIDs = new ArrayList<>();

    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        database = Database.getAppDatabase(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);

        checkIfInternetPermissionAlreadyGranted();
    }

    private void startDownloads() {
        existingDatabaseLocationIDs = getExistingLocationIDs();
        existingDatabaseEventIDs = getExistingEventIDs();
        deleteOldEvents();
        firebaseLocationIDs = getFirebaseLocationIDs();
    }

    private ArrayList<String> getExistingLocationIDs() {
        final ArrayList<String> result = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<LocationItem> locations = database.accessDao().getAllLocations();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LocationItem item : locations) {
                            existingDatabaseLocationIDs.add(item.getId());
                            result.add(item.getId());
                        }
                    }
                });
            }
        }).start();
        return result;
    }

    private ArrayList<String> getExistingEventIDs() {
        final ArrayList<String> result = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<EventItem> events = database.accessDao().getAllEvents();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (EventItem item : events) {
                            existingDatabaseEventIDs.add(item.getId());
                            result.add(item.getId());
                        }
                    }
                });
            }
        }).start();
        return result;
    }

    private ArrayList<String> getFirebaseLocationIDs() {

        onlineDatabase.collection("Locations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        firebaseLocationIDs.add(document.getId());
                    }
                }
                ArrayList<String> toDownload = compareLocationIDs(firebaseLocationIDs, existingDatabaseLocationIDs);
                for (int i = 0; i < toDownload.size(); i++) {
                    downloadSingleLocationFromFirebase(toDownload, i);
                }
                getFirebaseEventIDs(toDownload.size());
            }
        });
        return firebaseLocationIDs;
    }

    private ArrayList<String> getFirebaseEventIDs(final int numLocations) {

        onlineDatabase.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        firebaseEventIDs.add(document.getId());
                    }
                }
                ArrayList<String> toDownload = compareEventIDs(firebaseEventIDs, existingDatabaseEventIDs, numLocations);
                for (int i = 0; i < toDownload.size(); i++) {
                    downloadSingleEventFromFirebase(toDownload, i);
                }
            }
        });
        return firebaseEventIDs;
    }

    /**
     * @param firebaseEntries online database locationIDs
     * @param existingDatabaseEntries offline database locationIDs
     * @return arrayList with new locationIDs from firebase
     */
    private ArrayList<String> compareLocationIDs(ArrayList<String> firebaseEntries, ArrayList<String> existingDatabaseEntries) {
        ArrayList<String> result = new ArrayList<>();
        boolean isAlreadyInDatabase = false;
        for (String currentElement : firebaseEntries) {
            for (int i = 0; i < existingDatabaseEntries.size(); i++) {
                if (currentElement.equals(existingDatabaseEntries.get(i))) {
                    isAlreadyInDatabase = true;
                }
            }
            if (!isAlreadyInDatabase) result.add(currentElement);
            isAlreadyInDatabase = false;
        }
        return result;
    }

    /**
     * @param firebaseEntries online database eventIDs
     * @param existingDatabaseEntries offline database eventIDs
     * @return arrayList with new eventIDs from firebase
     */
    private ArrayList<String> compareEventIDs(ArrayList<String> firebaseEntries, ArrayList<String> existingDatabaseEntries, int numLocations) {
        ArrayList<String> result = new ArrayList<>();
        boolean isAlreadyInDatabase = false;
        for (String currentElement : firebaseEntries) {
            for (int i = 0; i < existingDatabaseEntries.size(); i++) {
                if (currentElement.equals(existingDatabaseEntries.get(i))) {
                    isAlreadyInDatabase = true;
                }
            }
            if (!isAlreadyInDatabase) result.add(currentElement);
            isAlreadyInDatabase = false;
        }
        if (result.size() == 0 && numLocations == 0) {
            Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
            startActivity(intent);
        } else progressBar.setMax(result.size() + numLocations);
        return result;
    }

    /**
     * @param ids list of locationIDs
     * @param position position of ID to download
     */
    private void downloadSingleLocationFromFirebase(ArrayList<String> ids, int position) {
        onlineDatabase.collection("Locations").document(ids.get(position)).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String id = document.getId();
                                String name = document.getString("name");
                                String street = document.getString("street");
                                String houseNumber = document.getString("housenumber");
                                String zip = document.getString("zip");
                                String city = document.getString("city");
                                String type = document.getString("art");
                                String ima = document.getString("ima");
                                String info = document.getString("info");

                                String mondayFrom = document.getString("monday_from");
                                String tuesdayFrom = document.getString("tuesday_from");
                                String wednesdayFrom = document.getString("wednesday_from");
                                String thursdayFrom = document.getString("thursday_from");
                                String fridayFrom = document.getString("friday_from");
                                String saturdayFrom = document.getString("saturday_from");
                                String sundayFrom = document.getString("sunday_from");
                                String[] openFrom = new String[]{mondayFrom, tuesdayFrom, wednesdayFrom, thursdayFrom, fridayFrom, saturdayFrom, sundayFrom};

                                String mondayUntil = document.getString("monday_till");
                                String tuesdayUntil = document.getString("tuesday_till");
                                String wednesdayUntil = document.getString("wednesday_till");
                                String thursdayUntil = document.getString("thursday_till");
                                String fridayUntil = document.getString("friday_till");
                                String saturdayUntil = document.getString("saturday_till");
                                String sundayUntil = document.getString("sunday_till");
                                String[] openUntil = new String[]{mondayUntil, tuesdayUntil, wednesdayUntil, thursdayUntil, fridayUntil, saturdayUntil, sundayUntil};

                                if (!ima.equals(""))
                                    new DownloadLocationImageTask(LoadingScreen.this, id, name, street, houseNumber, zip, city, type, ima, info, openFrom, openUntil, getApplicationContext()).execute(ima);
                                //if no imagge on db, set image to hangover logo
                                else {
                                    onLocationDownloadFinished(new LocationItem(id, R.drawable.ho_logo, name, street, houseNumber, zip, city, type, ima, info, openFrom, openUntil, getApplicationContext(), null));
                                    onLocationUpdate();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * @param ids list of eventDs
     * @param position position of ID to download
     */
    private void downloadSingleEventFromFirebase(ArrayList<String> ids, int position) {
        onlineDatabase.collection("Events").document(ids.get(position)).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String id = document.getId();
                                String headline = document.getString("headline");
                                String date = document.getString("date");
                                String ima = document.getString("ima");
                                String info = document.getString("info");
                                String locationId = document.getString("locationid");
                                String music = document.getString("music");
                                String price = document.getString("price");
                                String time = document.getString("time");

                                if (price.equals("")) {
                                    price = "0";
                                }

                                if (!ima.equals(""))
                                    new DownloadEventImageTask(LoadingScreen.this, id, headline, date, ima, info, locationId, music, price, time, getApplicationContext()).execute(ima);
                                //if no image, use hangover logo
                                else {
                                    onEventDownloadFinished(new EventItem(getApplicationContext(), id, R.drawable.ho_logo, headline, date, time, locationId, ima, info, Double.parseDouble(price), music, null));
                                    onLocationUpdate();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Interface Methoden
     */

    @Override
    public void onLocationUpdate() {
        progressBar.setProgress(progressBar.getProgress() + 1, true);
        if (progressBar.getProgress() == progressBar.getMax()) {
            Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * @param locationItem location to add to database
     */
    @Override
    public void onLocationDownloadFinished(final LocationItem locationItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.accessDao().insertSingleLocation(locationItem);
            }
        }).start();
    }

    @Override
    public void onEventUpdate() {
        progressBar.setProgress(progressBar.getProgress() + 1, true);
        if (progressBar.getProgress() == progressBar.getMax()) {
            Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * @param eventItem event to add to database
     */
    @Override
    public void onEventDownloadFinished(final EventItem eventItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.accessDao().insertSingleEvent(eventItem);
            }
        }).start();
    }

    private void checkIfInternetPermissionAlreadyGranted() {
        if (ContextCompat.checkSelfPermission(LoadingScreen.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            startDownloads();
        } else {
            requestInternetPermission();
        }
    }

    private void requestInternetPermission() {
        // if permission was denied before show alert dialog
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
            new AlertDialog.Builder(this)
                    .setTitle("Internet benötigt!")
                    .setMessage("Um dir passende Locations und Events von der Datenbank herunterzuladen, brauchen wir die Erlaubnis dein Internet benutzen zu dürfen.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoadingScreen.this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownloads();
            } else {
                finish();
            }
        }
    }

    /**
     * delete events where date < today
     */
    private void deleteOldEvents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<EventItem> allEvents = new ArrayList<>();
                allEvents.addAll(Database.getAppDatabase(getApplicationContext()).accessDao().getAllEvents());
                GregorianCalendar today = new GregorianCalendar();
                today.set(Calendar.HOUR_OF_DAY, 5);
                today.set(Calendar.MINUTE, 0);
                for (EventItem item : allEvents) {
                    if (item.getCalendar().compareTo(today) < 0) {
                        Database.getAppDatabase(getApplicationContext()).accessDao().deleteSingleEvent(item.getId());
                    }
                }
            }
        }).start();
    }
}
