package com.stefanlippl.hangover.locations;

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
import com.stefanlippl.hangover.locations.filter.FilterDialog;
import com.stefanlippl.hangover.locations.filter.OnFilterChangedListener;
import com.stefanlippl.hangover.locations.lettercards.LetterCard;
import com.stefanlippl.hangover.locations.lettercards.LetterCardAdapter;

import java.util.ArrayList;

public class LocationFragment extends Fragment implements OnFilterChangedListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<LetterCardAdapter.LetterCardViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;
    static ArrayList<LetterCard> letterCards = new ArrayList<>();
    private ArrayList<LocationItem> specialChars = new ArrayList<>();
    private FloatingActionButton fab;

    private String[] types;
    private boolean[] checkedTypes;

    private View view;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.locations_layout, container, false);

        initialiseView();
        setUpRecyclerView();
        setUpFloatingActionButtonOnClickListener();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        letterCards.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllLetterCards();
    }

    /**
     * 1. Save filters and checkedFilters to instance members
     * 2. clear array lists
     * 3. get all locations where locationtype matches filtered types and put them into letter cards
     * @param filters array of all filters
     * @param checkedFilters boolean array of filters
     */
    public void onFilterApplied(final String[] filters, final boolean[] checkedFilters) {
        types = filters;
        checkedTypes = checkedFilters;
        letterCards.clear();
        specialChars.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = '!'; i <= 'Z'; i++) {
                    if (i == '%') continue;
                    if (i < 'A') {
                        if (Database.getAppDatabase(getContext()).accessDao().getLocationsWithSameStartingLetter("" + (char) (i)).size() == 0)
                            continue;
                        for (int j = 0; j < checkedFilters.length; j++) {
                            if (checkedFilters[j]) {
                                specialChars.addAll(Database.getAppDatabase(getContext()).accessDao().getAllLocationsFromAType(filters[j], "" + (char) (i)));
                            }
                        }
                    } else {
                        if (Database.getAppDatabase(getContext()).accessDao().getLocationsWithSameStartingLetter("" + (char) (i)).size() == 0)
                            continue;
                        ArrayList<LocationItem> tmp = new ArrayList<>();
                        for (int j = 0; j < checkedFilters.length; j++) {
                            if (checkedFilters[j]) {
                                tmp.addAll(Database.getAppDatabase(getContext()).accessDao().getAllLocationsFromAType(filters[j], "" + (char) (i)));
                            }
                        }
                        if (tmp.size() > 0) {
                            letterCards.add(new LetterCard("" + (char) (i), tmp));
                        }
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (specialChars.size() > 0)
                            letterCards.add(new LetterCard("#", specialChars));
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void resetFilters(String[] filters, boolean[] checkedFilters) {
        types = filters;
        checkedTypes = checkedFilters;
        getAllLetterCards();
    }

    private void initialiseView() {
        fab = view.findViewById(R.id.locations_filter);
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new LetterCardAdapter(letterCards);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setUpFloatingActionButtonOnClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog dialog = new FilterDialog(getContext(), LocationFragment.this, types, checkedTypes);
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

    /**
     * TODO
     */
    private void getAllLetterCards() {
        letterCards.clear();
        specialChars.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = '!'; i <= 'Z'; i++) {
                    if (i == '%') continue;
                    if (i < 'A') {
                        if (Database.getAppDatabase(getContext()).accessDao().getLocationsWithSameStartingLetter("" + (char) (i)).size() == 0)
                            continue;
                        specialChars.addAll(Database.getAppDatabase(getContext()).accessDao().getLocationsWithSameStartingLetter("" + (char) (i)));
                    } else {
                        if (Database.getAppDatabase(getContext()).accessDao().getLocationsWithSameStartingLetter("" + (char) (i)).size() == 0)
                            continue;
                        letterCards.add(new LetterCard("" + (char) (i), Database.getAppDatabase(getContext()).accessDao().getLocationsWithSameStartingLetter("" + (char) (i))));
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (specialChars.size() > 0)
                            letterCards.add(new LetterCard("#", specialChars));
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}