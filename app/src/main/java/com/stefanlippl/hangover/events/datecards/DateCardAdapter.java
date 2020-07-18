package com.stefanlippl.hangover.events.datecards;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.events.EventAdapter;
import com.stefanlippl.hangover.events.EventDetail;
import com.stefanlippl.hangover.events.RecyclerViewClickListener;

import java.util.ArrayList;

public class DateCardAdapter extends RecyclerView.Adapter<DateCardAdapter.DateCardViewHolder> {

    private ArrayList<DateCard> dateCards;

    public static class DateCardViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        RecyclerView events;

        public DateCardViewHolder(@NonNull View itemView) {
            super(itemView);
            this.date = itemView.findViewById(R.id.letter);
            this.events = itemView.findViewById(R.id.location_cards);
        }
    }

    public DateCardAdapter(ArrayList<DateCard> dateCards) {
        this.dateCards = dateCards;
    }

    @NonNull
    @Override
    public DateCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.letter_cards, viewGroup, false);
        DateCardViewHolder viewHolder = new DateCardViewHolder(v);
        return viewHolder;
    }

    /**
     * Initialise new Adapter for LocationItems in OnBindViewHolder to display all Events of a DateCard in the RecyclerView of the DateCard
     * @param dateCardViewHolder instance of DateCardViewHolder
     * @param i position of element in letterCards
     */
    @Override
    public void onBindViewHolder(@NonNull final DateCardViewHolder dateCardViewHolder, final int i) {

        final DateCard currentDate = dateCards.get(i);

        if (currentDate != null) {
            dateCardViewHolder.date.setText(currentDate.getWeekDay() + " - " + currentDate.getDateString());
            RecyclerView recyclerView = dateCardViewHolder.itemView.findViewById(R.id.location_cards);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(dateCardViewHolder.itemView.getContext());
            EventAdapter adapter = new EventAdapter(currentDate.getEvents(), new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                        Intent intent = new Intent(dateCardViewHolder.itemView.getContext(), EventDetail.class);
                        intent.putExtra("event", currentDate.getEvents().get(position));
                        dateCardViewHolder.itemView.getContext().startActivity(intent);
                }
            });
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    public int getItemCount() {
        return dateCards.size();
    }
}
