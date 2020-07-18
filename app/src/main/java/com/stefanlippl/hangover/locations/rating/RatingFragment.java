package com.stefanlippl.hangover.locations.rating;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.locations.LocationItem;

import java.util.ArrayList;
import java.util.Collections;

public class RatingFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<RatingAdapter.RatingViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RatingCard> ratingCards = new ArrayList<>();
    LocationItem item;

    private RatingBar ratingBar;
    private TextView count;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rating_fragment, container, false);
        initialiseViews();

        item = getArguments().getParcelable("location");
        ratingBar.setRating(item.getRating());
        count.setText(String.format("%s wurde von %d User bewertet.", item.getLocationName(), item.getRatingCount()));

        downloadRatingsFromFirebase();

        return view;
    }

    private void initialiseViews() {
        ratingBar = view.findViewById(R.id.rating_avg);
        count = view.findViewById(R.id.rating_count);
        recyclerView = view.findViewById(R.id.rating_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new RatingAdapter(ratingCards);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void downloadRatingsFromFirebase() {
        String directory = String.format("Locations/%s/Rating", item.getId());
        db.collection(directory).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        float rating = Float.parseFloat(document.getString("ratingStars"));
                        String text = document.getString("ratingText");
                        String date = document.getString("ratingDate");
                        ratingCards.add(new RatingCard(rating, text, date));
                    }
                    Collections.sort(ratingCards);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
