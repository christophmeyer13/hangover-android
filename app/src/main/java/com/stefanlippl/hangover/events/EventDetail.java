package com.stefanlippl.hangover.events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.locations.*;
import com.stefanlippl.hangover.locations.LocationItem;
import com.stefanlippl.hangover.utils.ImageHelper;

public class EventDetail extends AppCompatActivity {

    Toolbar toolbar;
    CardView header;
    Button likeButton, shareButton;
    ImageView imageheadline, locationlogo;
    TextView headline, date, location, eventinfodetails, musicdetails, pricedetails;

    private boolean isSaved = false;
    LocationItem locationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        initiateViews();

        Intent intent = getIntent();
        final EventItem item = intent.getParcelableExtra("event");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Database database = Database.getAppDatabase(getApplicationContext());
                locationItem = database.accessDao().getSingleLocation(item.getLocationId());
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
        }

        setUpToolbar();
        setUpViews(item, locationItem);
        setupBottomBar(item, locationItem);

    }

    private void setupBottomBar(final EventItem event, final LocationItem location) {
        likeButton = findViewById(R.id.like_button);
        shareButton = findViewById(R.id.share_button);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSaved) {
                    isSaved = true;
                    likeButton.setBackgroundResource(R.drawable.like_black);
                    Toast.makeText(getApplicationContext(), "Event gespeichert!", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            event.setSaved(true);
                            Database.getAppDatabase(getApplicationContext()).accessDao().saveEvent(event.getId(), true);
                        }
                    }).start();
                } else {
                    isSaved = false;
                    likeButton.setBackgroundResource(R.drawable.like_white);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            event.setSaved(false);
                            Database.getAppDatabase(getApplicationContext()).accessDao().saveEvent(event.getId(), false);
                        }
                    }).start();
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(EventDetail.this)
                        .setType("text/plain/")
                        .setText(event.getHeadline() + "\n" + event.getDate() + ", " + event.getTime() + "\n@" + location.getLocationName() + "\n Geteilt aus Hangover App\n --> Hangover URL <--")
                        .startChooser();
            }
        });

        if (event.isSaved()) {
            isSaved = true;
            likeButton.setBackgroundResource(R.drawable.like_black);
        }
    }

    private void initiateViews() {
        toolbar = findViewById(R.id.toolbar2);
        imageheadline = findViewById(R.id.imageheadline);
        locationlogo = findViewById(R.id.locationlogodetail);
        headline = findViewById(R.id.headlinedetail);
        date = findViewById(R.id.datedetail);
        location = findViewById(R.id.locationdetail);
        eventinfodetails = findViewById(R.id.eventinfodetails);
        musicdetails = findViewById(R.id.musicdetails);
        pricedetails = findViewById(R.id.pricedetails);
        header = findViewById(R.id.header);
    }

    private void setUpViews(EventItem item, final LocationItem locationItem) {
        headline.setText(item.getHeadline());
        date.setText(String.format("%s ab %s Uhr", item.getDate(), item.getTime()));
        location.setText(String.format("@ %s", locationItem.getLocationName()));
        eventinfodetails.setText(item.getInfo());
        musicdetails.setText(item.getMusic());
        if (item.getPrice() == 0) pricedetails.setText(R.string.kostenlos);
        else pricedetails.setText(String.format("%.2f€", item.getPrice()));

        if (item.getUri() != null)
            try {
                Uri imageUri = Uri.parse("file://" + item.getUri());
                Bitmap bitmap = ImageHelper.getThumbnail(imageUri, this.getApplicationContext());

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = (int) (size.y * 0.4);
                if (height < bitmap.getHeight()) {
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.height = height;
                    imageheadline.setLayoutParams(layoutParams);
                }
                imageheadline.setImageBitmap(bitmap);
            } catch (Exception e) {
            }
        else imageheadline.setImageResource(R.drawable.ho_logo_full);
        if (locationItem.getLocationLogo() != null)
            locationlogo.setImageBitmap(ImageHelper.getRoundedCornerBitmap(locationItem.getLocationLogo(), 10));
        else {
            locationlogo.setImageResource(R.drawable.ho_logo);
        }

        locationlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetail.this, LocationDetail.class);
                intent.putExtra("location", locationItem);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(){
        toolbar.setTitle("Event @ " + locationItem.getLocationName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
