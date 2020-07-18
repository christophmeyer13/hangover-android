package com.stefanlippl.hangover.locations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefanlippl.hangover.R;

public class LocationInfoFragment extends Fragment {

    TextView info, openingTimes;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.location_detail_info_fragment,container,false);

        info = view.findViewById(R.id.locationinfodetails);
        openingTimes = view.findViewById(R.id.openingtimesdetails);

        LocationItem current = getArguments().getParcelable("location");
        info.setText(String.format(current.getLocationInfo()));
        openingTimes.setText(current.getLocationOpening());

        return view;
    }
}
