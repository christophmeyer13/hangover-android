package com.stefanlippl.hangover.locations.rating;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.locations.LocationItem;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RateDialog extends Dialog {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LocationItem location;
    private Button ratingButton;
    private OnRatingSuccessListener listener;
    private Button send, cancel;
    private EditText ratingText;
    private RatingBar ratingBar;

    public RateDialog(Activity activity, LocationItem location, Button ratingButton, OnRatingSuccessListener listener){
        super(activity);
        this.location = location;
        this.ratingButton = ratingButton;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rate_dialog);
        initialiseViews();
        ratingBar.setNumStars(5);
        setUpButtonOnClickListeners();
    }


    private String getDate(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setCalendar(calendar);
        return df.format(calendar.getTime());
    }

    private void initialiseViews(){
        send = findViewById(R.id.ok_button);
        cancel = findViewById(R.id.cancel_button);
        ratingBar = findViewById(R.id.ratingBar2);
        ratingText = findViewById(R.id.user_input);
    }

    private void setUpButtonOnClickListeners(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float rating = ratingBar.getRating();
                String directory = String.format("Locations/%s/Rating",location.getId());
                Map<String,String> map = new HashMap<>();
                map.put("ratingStars","" + rating);
                map.put("ratingText",ratingText.getText().toString());
                map.put("ratingDate",getDate());
                db.collection(directory).document()
                        .set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(),"Vielen Dank!",Toast.LENGTH_SHORT).show();
                                ratingButton.setBackgroundResource(R.drawable.rating_black);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Database.getAppDatabase(getContext()).accessDao().rateLocation(location.getId(),true);
                                        Database.getAppDatabase(getContext()).accessDao().updateRating(location.getId(),((location.getRating()*location.getRatingCount())+rating)/(location.getRatingCount()+1));
                                        Database.getAppDatabase(getContext()).accessDao().updateRatingCount(location.getId(),location.getRatingCount()+1);
                                        listener.onRatingSuccess(Database.getAppDatabase(getContext()).accessDao().getSingleLocation(location.getId()));
                                    }
                                }).start();
                                dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Etwas ist schief gelaufen...",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
