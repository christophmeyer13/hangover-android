package com.stefanlippl.hangover.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stefanlippl.hangover.R;

public class UserArea extends Fragment {

    private View view;
    private CardView events, locations;
    private ImageView arrowEvents, arrowLocations;
    private String currentTab = "events";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_fragment, container, false);

        initialiseViews();
        setUpSavedEventsFragment();
        setUpButtonOnClickListeners();

        return view;
    }

    private void initialiseViews(){
        events = view.findViewById(R.id.location_info_card);
        locations = view.findViewById(R.id.location_events_card);
        arrowEvents = view.findViewById(R.id.arrow_location_info);
        arrowLocations = view.findViewById(R.id.arrow_location_events);
    }

    private void setUpSavedEventsFragment(){
        events.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        arrowEvents.setRotation(arrowEvents.getRotation() + 180);
        getChildFragmentManager().beginTransaction().add(R.id.saved_things_container, new SavedEventsFragment()).commit();
    }

    private void setUpButtonOnClickListeners(){
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentTab.equals("events")) {
                    currentTab = "events";
                    arrowEvents.setRotation(arrowEvents.getRotation() + 180);
                    arrowLocations.setRotation(arrowLocations.getRotation() + 180);
                    getChildFragmentManager().beginTransaction().replace(R.id.saved_things_container, new SavedEventsFragment()).commit();
                    events.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    locations.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                }

            }
        });

        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentTab.equals("locations")) {
                    currentTab = "locations";
                    arrowLocations.setRotation(arrowLocations.getRotation() + 180);
                    arrowEvents.setRotation(arrowEvents.getRotation() + 180);
                    getChildFragmentManager().beginTransaction().replace(R.id.saved_things_container, new SavedLocationsFragment()).commit();
                    locations.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    events.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                }
            }
        });
    }
}
