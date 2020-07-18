package com.stefanlippl.hangover.locations;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.utils.ImageHelper;

import java.util.ArrayList;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder> {

    private ArrayList<LocationItem> locationItems;
    private RecyclerViewClickListener listener;

    public static class LocationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView locationLogo;
        TextView locationName;
        TextView locationAdress;
        TextView locationType;
        TextView rating;
        private RecyclerViewClickListener listener;

        LocationsViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            this.locationLogo = itemView.findViewById(R.id.locationlogo);
            this.locationName = itemView.findViewById(R.id.headlinelocation);
            this.locationAdress = itemView.findViewById(R.id.datelocation);
            this.locationType = itemView.findViewById(R.id.art);
            this.rating = itemView.findViewById(R.id.full_rating);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    public LocationsAdapter(ArrayList<LocationItem> locationItems, RecyclerViewClickListener listener) {
        this.locationItems = locationItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.location_item, viewGroup, false);
        return new LocationsViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsViewHolder locationsViewHolder, int i) {
        LocationItem currentLocation = locationItems.get(i);
        if (currentLocation != null && currentLocation.getLocationLogo() != null) {
            locationsViewHolder.locationLogo.setImageBitmap(ImageHelper.getRoundedCornerBitmap(currentLocation.getLocationLogo(), 10));
        } else {
            locationsViewHolder.locationLogo.setImageResource(currentLocation.getImageResource());
        }
        locationsViewHolder.locationName.setText(currentLocation.getLocationName());
        locationsViewHolder.locationAdress.setText(String.format("%s %s, %s %s", currentLocation.getLocationStreet(), currentLocation.getHouseNumber(), currentLocation.getLocationZip(), currentLocation.getLocationCity()));
        locationsViewHolder.locationType.setText(currentLocation.getLocationType());
        locationsViewHolder.rating.setText(String.format("%.1f", currentLocation.getRating()));
    }

    @Override
    public int getItemCount() {
        return locationItems.size();
    }
}