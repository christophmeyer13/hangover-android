package com.stefanlippl.hangover.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.events.datecards.DateCard;
import com.stefanlippl.hangover.events.datecards.DateCardAdapter;
import com.stefanlippl.hangover.events.filter.FilterDialog;
import com.stefanlippl.hangover.events.filter.OnFilterChangedListener;
import com.stefanlippl.hangover.locations.LocationItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class EventsFragment extends Fragment implements OnFilterChangedListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<EventItem> eventItems = new ArrayList<>();
    private ArrayList<DateCard> dateCards = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private RecyclerView.Adapter<DateCardAdapter.DateCardViewHolder> adapter;
    private FloatingActionButton fab;

    private String[] locationTypes;
    private boolean[] checkedLocationTypes;
    private String[] musicTypes;
    private boolean[] checkedMusicTypes;
    private int maxPrice;

    private View view;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.events_layout, container, false);

        initialiseViews();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventItems.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllEventsAndDatesFromOfflineDatabase();
    }

    private ArrayList<GregorianCalendar> calendarFromString(ArrayList<String> dates) {
        ArrayList<GregorianCalendar> result = new ArrayList<>();
        for (String date : dates) {
            GregorianCalendar calendar = new GregorianCalendar();
            char[] date1 = date.toCharArray();
            int year = Integer.parseInt(String.format("%c%c%c%c", date1[6], date1[7], date1[8], date1[9]));
            int month = Integer.parseInt(String.format("%c%c", date1[3], date1[4]));
            int day = Integer.parseInt(String.format("%c%c", date1[0], date1[1]));
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            result.add(calendar);
        }
        return result;
    }

    private ArrayList<String> getFilteredDates() {
        ArrayList<String> result = new ArrayList<>();
        for (EventItem current : eventItems) {
            if (!result.contains(current.getDate())) {
                result.add(current.getDate());
            }
        }
        return result;
    }

    /**
     * 1. set instance memeber arrays
     * 2. clear array lists
     * 3. get events where entry fee < maxPrice
     * 4. get events where locationtype matches selected locationtypes
     * 5. get events where musictype matches selected musictypes
     *
     * @param locationTypes        array of all available location types
     * @param checkedLocationTypes boolean array of checked location types
     * @param musicTypes           array of all available music types
     * @param checkedMusicTypes    boolean array of checked music types
     * @param maxPrice             maxium entry fee selected from user
     */
    @Override
    public void applyFilters(final String[] locationTypes, final boolean[] checkedLocationTypes, final String[] musicTypes, final boolean[] checkedMusicTypes, final int maxPrice) {
        this.locationTypes = locationTypes;
        this.checkedLocationTypes = checkedLocationTypes;
        this.musicTypes = musicTypes;
        this.checkedMusicTypes = checkedMusicTypes;
        this.maxPrice = maxPrice;
        eventItems.clear();
        dateCards.clear();
        dates.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<EventItem> temp = new ArrayList<>(Database.getAppDatabase(getContext()).accessDao().getFilteredEventsByPrice(maxPrice));
                for (EventItem current : temp) {
                    LocationItem location = Database.getAppDatabase(getContext()).accessDao().getSingleLocation(current.getLocationId());
                    String locationtype = location.getLocationType();
                    for (int i = 1; i < locationTypes.length; i++) {
                        if (checkedLocationTypes[i] && (locationtype.equals(locationTypes[i]))) {
                            for (int j = 1; j < checkedMusicTypes.length; j++) {
                                if (checkedMusicTypes[j] && (current.getMusic().contains(musicTypes[j]))) {
                                    if (!eventItems.contains(current)) eventItems.add(current);
                                }
                            }
                        }
                    }
                }
                dates = getFilteredDates();
                ArrayList<GregorianCalendar> calendars = calendarFromString(dates);
                Collections.sort(calendars);
                for (GregorianCalendar date : calendars) {
                    dateCards.add(new DateCard(date, eventItems));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public void resetFilters(String[] locationTypes, boolean[] checkedLocationsTypes, String[] musicTypes, boolean[] checkedMusicTypes) {
        this.locationTypes = locationTypes;
        this.checkedLocationTypes = checkedLocationsTypes;
        this.musicTypes = musicTypes;
        this.checkedMusicTypes = checkedMusicTypes;
        eventItems.clear();
        dates.clear();
        dateCards.clear();
        Arrays.fill(checkedLocationTypes, true);
        Arrays.fill(checkedMusicTypes, true);
        this.maxPrice = 20;
        getAllEventsAndDatesFromOfflineDatabase();
    }

    private void initialiseViews() {
        fab = view.findViewById(R.id.events_filter);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new DateCardAdapter(dateCards);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog dialog = new FilterDialog(getContext(), EventsFragment.this, locationTypes, checkedLocationTypes, musicTypes, checkedMusicTypes, maxPrice);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                dialog.show();
            }
        });
    }

    private void getAllEventsAndDatesFromOfflineDatabase() {
        eventItems.clear();
        dates.clear();
        dateCards.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                eventItems.addAll(Database.getAppDatabase(getContext()).accessDao().getAllEvents());
                dates.addAll(Database.getAppDatabase(getContext()).accessDao().getAllEventDates());
                ArrayList<GregorianCalendar> calendars = calendarFromString(dates);
                Collections.sort(calendars);

                for (GregorianCalendar c : calendars) {
                    dateCards.add(new DateCard(c, eventItems));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}