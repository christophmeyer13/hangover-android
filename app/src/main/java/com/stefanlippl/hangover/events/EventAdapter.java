package com.stefanlippl.hangover.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.locations.LocationItem;
import com.stefanlippl.hangover.utils.ImageHelper;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<EventItem> eventItems;
    private RecyclerViewClickListener listener;
    private LocationItem locationItem;

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView eventLogo;
        TextView eventTitle;
        TextView eventDate;
        TextView eventDetails;
        private RecyclerViewClickListener listener;

        EventViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            this.eventLogo = itemView.findViewById(R.id.eventlogo);
            this.eventTitle = itemView.findViewById(R.id.headline);
            this.eventDate = itemView.findViewById(R.id.date);
            this.eventDetails = itemView.findViewById(R.id.locationId);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    public EventAdapter(ArrayList<EventItem> eventItems, RecyclerViewClickListener listener) {
        this.eventItems = eventItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.event_item, viewGroup, false);
        return new EventViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder eventViewHolder, int i) {

        final EventItem currentEvent = eventItems.get(i);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Database database = Database.getAppDatabase(eventViewHolder.itemView.getContext());
                locationItem = database.accessDao().getSingleLocation(currentEvent.getLocationId());
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {}

        if (currentEvent != null && currentEvent.getUri() != null) {
            try {
                Uri imageUri = Uri.parse("file://" + currentEvent.getUri());
                Bitmap bitmap = ImageHelper.getThumbnail(imageUri, eventViewHolder.itemView.getContext().getApplicationContext());
                eventViewHolder.eventLogo.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 5));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            eventViewHolder.eventLogo.setImageResource(currentEvent.getImageResource());
        }
        eventViewHolder.eventTitle.setText(currentEvent.getHeadline());
        eventViewHolder.eventDate.setText(String.format("ab %s Uhr", currentEvent.getTime()));
        eventViewHolder.eventDetails.setText(String.format("@%s", locationItem.getLocationName()));
    }

    @Override
    public int getItemCount() {
        return eventItems.size();
    }


}