package com.stefanlippl.hangover.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.locations.*;
import com.stefanlippl.hangover.locations.LocationItem;
import com.stefanlippl.hangover.locations.LocationsAdapter;
import com.stefanlippl.hangover.locations.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Collections;

public class SavedLocationsFragment extends Fragment {

    private View view;

    private ArrayList<LocationItem> savedLocations = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.saved_cards_fragment,container,false);

        setUpRecyclerView();
        getSavedLocationsFromOfflineDatabase();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedLocations.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        savedLocations.clear();
    }

    private void setUpRecyclerView(){
        recyclerView = view.findViewById(R.id.saved_items);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), LocationDetail.class);
                intent.putExtra("location",savedLocations.get(position));
                startActivity(intent);
            }
        };
        adapter = new LocationsAdapter(savedLocations,listener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getSavedLocationsFromOfflineDatabase(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedLocations.addAll(Database.getAppDatabase(getContext()).accessDao().getSavedLocations(true));
                Collections.sort(savedLocations);
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
