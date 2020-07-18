package com.stefanlippl.hangover.taxi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefanlippl.hangover.R;

public class TaxiDetailFragment extends Fragment {

    View view;
    TextView text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.taxi_detail_fragment,container,false);

        String inputText = getArguments().getString("text");
        text = view.findViewById(R.id.taxi_frag_text);
        text.setText(inputText);

        return view;
    }
}
