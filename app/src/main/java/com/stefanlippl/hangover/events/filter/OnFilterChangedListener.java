package com.stefanlippl.hangover.events.filter;

public interface OnFilterChangedListener {

    void applyFilters(String[] locationTypes, boolean[] checkedLocationTypes, String[] musicTypes, boolean[] checkedMusicTypes, int maxPrice);

    void resetFilters(String[] locationTypes, boolean[] checkedLocationsTypes, String[] musicTypes, boolean[] checkedMusicTypes);

}
