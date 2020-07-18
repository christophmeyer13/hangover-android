package com.stefanlippl.hangover.locations.lettercards;

import com.stefanlippl.hangover.locations.LocationItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LetterCard {

    private ArrayList<LocationItem> locations = new ArrayList<>();
    private String startingLetter;

    public LetterCard(String startingLetter, List<LocationItem> allLocations) {
        this.startingLetter = startingLetter;
        locations.addAll(allLocations);
        Collections.sort(locations);
    }

    public ArrayList<LocationItem> getLocations() {
        return locations;
    }

    String getStartingLetter() {
        return startingLetter;
    }
}
