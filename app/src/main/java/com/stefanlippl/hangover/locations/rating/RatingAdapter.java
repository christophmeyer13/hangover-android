package com.stefanlippl.hangover.locations.rating;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.stefanlippl.hangover.R;

import java.util.ArrayList;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private ArrayList<RatingCard> ratingCards;

    public static class RatingViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView date;
        TextView text;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating_stars);
            date = itemView.findViewById(R.id.rating_date);
            text = itemView.findViewById(R.id.rating_text);
        }
    }

    public RatingAdapter(ArrayList<RatingCard> ratingCards) {
        this.ratingCards = ratingCards;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rating_card, viewGroup, false);
        return new RatingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder ratingViewHolder, int i) {
        RatingCard current = ratingCards.get(i);

        if (current != null) {
            ratingViewHolder.ratingBar.setNumStars(5);
            ratingViewHolder.ratingBar.setRating(current.getRating());
            ratingViewHolder.date.setText(current.getDate());
            ratingViewHolder.text.setText(current.getText());
        }
    }

    @Override
    public int getItemCount() {
        return ratingCards.size();
    }
}
