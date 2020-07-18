package com.stefanlippl.hangover.locations;

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

public class LocationEventsFragment extends Fragment {

    private ArrayList<EventItem> events = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<EventAdapter.EventViewHolder> adapter;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.location_detail_events_fragment, container, false);

        LocationItem current = getArguments().getParcelable("location");
        getEvents(current.getId());
        setUpRecyclerView();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        events.clear();
    }

    private void getEvents(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                events.addAll(Database.getAppDatabase(getContext()).accessDao().getAllEventsFromSingleLocation(id));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort(events);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void setUpRecyclerView(){
        recyclerView = view.findViewById(R.id.location_events);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new EventAdapter(events, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), EventDetail.class);
                intent.putExtra("event", events.get(position));
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
