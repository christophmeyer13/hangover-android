package com.stefanlippl.hangover.loadingscreen;

import com.stefanlippl.hangover.locations.LocationItem;

public interface OnLocationDownloadFinishedListener {

    void onLocationUpdate();

    void onLocationDownloadFinished(LocationItem locationItem);

}
