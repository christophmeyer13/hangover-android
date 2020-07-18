package com.stefanlippl.hangover.locations.lettercards;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.locations.LocationDetail;
import com.stefanlippl.hangover.locations.LocationsAdapter;
import com.stefanlippl.hangover.locations.RecyclerViewClickListener;

import java.util.ArrayList;

public class LetterCardAdapter extends RecyclerView.Adapter<LetterCardAdapter.LetterCardViewHolder> {

    private ArrayList<LetterCard> letterCards;

    public static class LetterCardViewHolder extends RecyclerView.ViewHolder{

        TextView letter;
        RecyclerView locations;

        LetterCardViewHolder(@NonNull View itemView){
            super(itemView);
            this.letter = itemView.findViewById(R.id.letter);
            this.locations = itemView.findViewById(R.id.location_cards);
        }

    }

    public LetterCardAdapter(ArrayList<LetterCard> letterCards){
        this.letterCards = letterCards;
    }

    @NonNull
    @Override
    public LetterCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.letter_cards,viewGroup,false);
        return new LetterCardViewHolder(v);
    }

    /**
     * Initialise new Adapter for LocationItems in OnBindViewHolder to display all Locations of a LetterCard in the RecyclerView of the LetterCard
     * @param letterCardViewHolder instance of LetterCardViewHolder
     * @param i position of element in letterCards
     */
    @Override
    public void onBindViewHolder(@NonNull final LetterCardViewHolder letterCardViewHolder, int i){

        final LetterCard currentLetter = letterCards.get(i);

        if(currentLetter != null){
            letterCardViewHolder.letter.setText("" + currentLetter.getStartingLetter());
            RecyclerView recyclerView = letterCardViewHolder.itemView.findViewById(R.id.location_cards);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(letterCardViewHolder.itemView.getContext());
            LocationsAdapter adapter = new LocationsAdapter(currentLetter.getLocations(), new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent = new Intent(letterCardViewHolder.itemView.getContext(), LocationDetail.class);
                    intent.putExtra("location",currentLetter.getLocations().get(position));
                    letterCardViewHolder.itemView.getContext().startActivity(intent);
                }
            });
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

    }

    public int getItemCount(){
        return letterCards.size();
    }

}
