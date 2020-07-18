package com.stefanlippl.hangover.locations;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.*;
import com.stefanlippl.hangover.locations.rating.OnRatingSuccessListener;
import com.stefanlippl.hangover.locations.rating.RateDialog;
import com.stefanlippl.hangover.locations.rating.RatingFragment;
import com.stefanlippl.hangover.utils.ImageHelper;

public class LocationDetail extends AppCompatActivity implements View.OnClickListener, OnRatingSuccessListener {

    private ImageView locationLogo;
    private TextView locationName, locationAddress;
    private Toolbar toolbar;
    private ImageView mapIcon;
    private ImageView arrowInfo, arrowEvents, arrowRating;
    private CardView infoloc, events, rating;
    private LinearLayout fragmentContainer;
    private Button likeButton, shareButton, rateButton;
    private LinearLayout layout;
    private ConstraintLayout bottom_bar;

    private LocationItem locationItem;
    private String selectedFragment = "info";
    private boolean isMapsEnabled = false;
    private boolean isSaved = false;
    private boolean isRated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        Intent intent = getIntent();
        final LocationItem item = intent.getParcelableExtra("location");
        locationItem = item;

        initialiseViews();
        setOnClickListeners();
        setupBottomBar(item);
        setUpInfoFragment();
        setUpToolbar();
        setUpViews();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isMapsEnabled) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.BELOW, R.id.panel);
                fragmentContainer.setLayoutParams(params);
                infoloc.setVisibility(View.VISIBLE);
                events.setVisibility(View.VISIBLE);
                infoloc.setActivated(true);
                events.setActivated(true);
                isMapsEnabled = false;
                bottom_bar.setVisibility(View.VISIBLE);
                bottom_bar.setActivated(true);

                Bundle bundle = new Bundle();
                bundle.putParcelable("location", locationItem);
                LocationInfoFragment info = new LocationInfoFragment();
                info.setArguments(bundle);
                infoloc.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, info).commit();
                if (selectedFragment.equals("info")) arrowInfo.setRotation(arrowInfo.getRotation() + 180);
                else if (selectedFragment.equals("events")) {
                    arrowEvents.setRotation(arrowEvents.getRotation() + 180);
                } else if (selectedFragment.equals("rating"))
                    arrowRating.setRotation(arrowRating.getRotation() + 180);
                selectedFragment = "info";
                arrowInfo.setRotation(arrowInfo.getRotation() + 180);
                infoloc.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                events.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                rating.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", locationItem);

        //set fragment container below card panel
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.panel);

        switch (v.getId()) {
            // if info card is pressed, fragment will be replaced with info fragment, make selected card darker, set arrow rotation, set arguments for fragment
            case R.id.location_info_card:
                if (selectedFragment.equals("info")) arrowInfo.setRotation(arrowInfo.getRotation() + 180);
                else if (selectedFragment.equals("events")) {
                    arrowEvents.setRotation(arrowEvents.getRotation() + 180);
                } else if (selectedFragment.equals("rating"))
                    arrowRating.setRotation(arrowRating.getRotation() + 180);
                fragmentContainer.setLayoutParams(params);
                isMapsEnabled = false;
                selectedFragment = "info";
                layout.setBackgroundResource(android.R.color.white);
                infoloc.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                events.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                rating.setCardBackgroundColor(getResources().getColor(android.R.color.white));

                arrowInfo.setRotation(arrowInfo.getRotation() + 180);
                LocationInfoFragment info = new LocationInfoFragment();
                info.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, info).commit();
                break;
            // if events card is pressed, fragment will be replaced with events fragment, make selected card darker, set arrow rotation, set arguments for fragment
            case R.id.location_events_card:
                if (selectedFragment.equals("info")) arrowInfo.setRotation(arrowInfo.getRotation() + 180);
                else if (selectedFragment.equals("events")) {
                    arrowEvents.setRotation(arrowEvents.getRotation() + 180);
                } else if (selectedFragment.equals("rating"))
                    arrowRating.setRotation(arrowRating.getRotation() + 180);
                fragmentContainer.setLayoutParams(params);
                isMapsEnabled = false;
                selectedFragment = "events";
                layout.setBackgroundResource(android.R.color.darker_gray);
                events.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                infoloc.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                rating.setCardBackgroundColor(getResources().getColor(android.R.color.white));

                arrowEvents.setRotation(arrowEvents.getRotation() + 180);
                LocationEventsFragment eventsFrag = new LocationEventsFragment();
                eventsFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, eventsFrag).commit();
                break;
            // if rating card is pressed, fragment will be replaced with rating fragment, make selected card darker, set arrow rotation, set arguments for fragment
            case R.id.location_rating_card:
                if (selectedFragment.equals("info")) arrowInfo.setRotation(arrowInfo.getRotation() + 180);
                else if (selectedFragment.equals("events")) {
                    arrowEvents.setRotation(arrowEvents.getRotation() + 180);
                } else if (selectedFragment.equals("rating"))
                    arrowRating.setRotation(arrowRating.getRotation() + 180);
                fragmentContainer.setLayoutParams(params);
                isMapsEnabled = false;
                selectedFragment = "rating";
                layout.setBackgroundResource(android.R.color.darker_gray);
                rating.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                infoloc.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                events.setCardBackgroundColor(getResources().getColor(android.R.color.white));

                arrowRating.setRotation(arrowRating.getRotation() + 180);
                RatingFragment rating = new RatingFragment();
                rating.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, rating).commit();
                break;
            // if map icon is pressed, fragment will be replaced with single location map fragment, fragment container size is increased, bottom bar is invisible, set arguments for fragment
            case R.id.mapIcon:
                if (!isMapsEnabled) {
                    isMapsEnabled = true;
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    params1.addRule(RelativeLayout.BELOW, R.id.container);
                    fragmentContainer.setLayoutParams(params1);
                    infoloc.setVisibility(View.INVISIBLE);
                    events.setVisibility(View.INVISIBLE);
                    infoloc.setActivated(false);
                    events.setActivated(false);
                    bottom_bar.setVisibility(View.INVISIBLE);
                    bottom_bar.setActivated(false);

                    Bundle bundle1 = new Bundle();
                    bundle1.putParcelable("location", locationItem);
                    SingleLocationMap map = new SingleLocationMap();
                    map.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, map).commit();
                }
                break;
            // save location to personal area, update location in offline database, set button color
            case R.id.like_button:
                if (!isSaved) {
                    isSaved = true;
                    likeButton.setBackgroundResource(R.drawable.like_black);
                    Toast.makeText(getApplicationContext(), "Location gespeichert!", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            locationItem.setSaved(true);
                            Database.getAppDatabase(getApplicationContext()).accessDao().saveLocation(locationItem.getId(), true);
                            Log.e("LOCATION"," " + locationItem.getLocationName());
                        }
                    }).start();
                } else {
                    isSaved = false;
                    likeButton.setBackgroundResource(R.drawable.like_white);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            locationItem.setSaved(false);
                            Database.getAppDatabase(getApplicationContext()).accessDao().saveLocation(locationItem.getId(), false);
                        }
                    }).start();
                }
                break;
            // show rate dialog
            case R.id.rate_button:
                if (!isRated) {
                    isRated = true;
                    RateDialog rateDialog = new RateDialog(LocationDetail.this, locationItem, rateButton, LocationDetail.this);
                    rateDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    Window window = rateDialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.BOTTOM;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);
                    rateDialog.show();
                }
                break;
            //share location
            case R.id.share_button:
                ShareCompat.IntentBuilder.from(LocationDetail.this)
                        .setType("text/plain/")
                        .setText(locationItem.getLocationName() + ",\nin " + locationItem.getLocationCity() + "\n Geteilt aus Hangover App\n --> Hangover URL <--")
                        .startChooser();
                break;
        }
    }

    private void setupBottomBar(final LocationItem location) {
        if (location.isSaved()) {
            isSaved = true;
            likeButton.setBackgroundResource(R.drawable.like_black);
        }
        if (location.isRated()) {
            isRated = true;
            rateButton.setBackgroundResource(R.drawable.rating_black);
            rateButton.setActivated(false);
        }
    }

    @Override
    public void onRatingSuccess(LocationItem locationItem) {
        isRated = true;
        rateButton.setActivated(false);
        this.locationItem = locationItem;
        if (selectedFragment.equals("rating")) {
            RatingFragment rating = new RatingFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("location", locationItem);
            rating.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, rating).commit();
        }
    }

    private void initialiseViews() {
        toolbar = findViewById(R.id.toolbar3);
        locationLogo = findViewById(R.id.locationlogo_locations);
        locationName = findViewById(R.id.headlinedetaillocation);
        locationAddress = findViewById(R.id.adressdetail);
        mapIcon = findViewById(R.id.mapIcon);
        arrowInfo = findViewById(R.id.arrow_location_info);
        arrowEvents = findViewById(R.id.arrow_location_events);
        infoloc = findViewById(R.id.location_info_card);
        events = findViewById(R.id.location_events_card);
        fragmentContainer = findViewById(R.id.frag_with_bottom);
        layout = findViewById(R.id.panel);
        bottom_bar = findViewById(R.id.bottim_bar);
        arrowRating = findViewById(R.id.arrow_location_rating);
        rating = findViewById(R.id.location_rating_card);
        likeButton = findViewById(R.id.like_button);
        shareButton = findViewById(R.id.share_button);
        rateButton = findViewById(R.id.rate_button);
    }

    private void setOnClickListeners() {
        infoloc.setOnClickListener(this);
        events.setOnClickListener(this);
        rating.setOnClickListener(this);
        mapIcon.setOnClickListener(this);
        likeButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        rateButton.setOnClickListener(this);
    }

    private void setUpInfoFragment() {
        arrowInfo.setRotation(arrowInfo.getRotation() + 180);
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", locationItem);
        final LocationInfoFragment info = new LocationInfoFragment();
        info.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, info).commit();
        layout.setBackgroundResource(android.R.color.white);
        infoloc.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpViews(){
        locationName.setText(locationItem.getLocationName());
        locationAddress.setText(String.format("%s %s, %s %s", locationItem.getLocationStreet(), locationItem.getHouseNumber(), locationItem.getLocationZip(), locationItem.getLocationCity()));
        if (locationItem.getLocationLogo() != null) {
            locationLogo.setImageBitmap(ImageHelper.getRoundedCornerBitmap(locationItem.getLocationLogo(), 10));
        } else {
            locationLogo.setImageResource(locationItem.getImageResource());
        }
    }
}
