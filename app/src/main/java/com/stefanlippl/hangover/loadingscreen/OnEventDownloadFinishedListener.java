package com.stefanlippl.hangover.loadingscreen;

import com.stefanlippl.hangover.events.EventItem;

public interface OnEventDownloadFinishedListener {

    void onEventUpdate();

    void onEventDownloadFinished(EventItem eventItem);

}
