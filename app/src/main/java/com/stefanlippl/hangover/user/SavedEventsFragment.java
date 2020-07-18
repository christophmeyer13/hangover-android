package com.stefanlippl.hangover.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.events.EventAdapter;
import com.stefanlippl.hangover.events.EventDetail;
import com.stefanlippl.hangover.events.EventItem;
import com.stefanlippl.hangover.events.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Collections;

public class SavedEventsFragment extends Fragment {

    View view;

    private ArrayList<EventItem> savedEvents = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<EventAdapter.EventViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.saved_cards_fragment,container,false);

        setUpRecyclerView();
        getSavedEventsFromOfflineDatabase();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedEvents.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        savedEvents.clear();
    }

    private void setUpRecyclerView(){
        recyclerView = view.findViewById(R.id.saved_items);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), EventDetail.class);
                intent.putExtra("event", savedEvents.get(position));
                startActivity(intent);
            }
        };
        adapter = new EventAdapter(savedEvents,listener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getSavedEventsFromOfflineDatabase(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedEvents.addAll(Database.getAppDatabase(getContext()).accessDao().getSavedEvents(true));
                Collections.sort(savedEvents);
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
