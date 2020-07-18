package com.stefanlippl.hangover.law;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefanlippl.hangover.R;

public class ImpressumFragment extends Fragment {

    private View view;
    private TextView agbDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_agb, container, false);
        agbDetail = view.findViewById(R.id.agbdetail);
        agbDetail.setText("Seitenbetreiber\n" +
                "Angaben gemäß § 5 TMG\n" +
                "\n" +
                "Hangover Application UG (haftungsbeschränkt) \n" +
                "93053 - Regensburg\n" +
                "\n" +
                "Kontakt\n" +
                "E-Mail: hangover-regensburg@outlook.de\n" +
                "Telefon: 0151 23472175\n" +
                "\n" +
                "Vertreten durch:\n" +
                "Stefan Lippl\n" +
                "Christoph Meyer\n" +
                "\n" +
                "Verantwortlich für den Inhalt nach § 55 Abs. 2 RStV & Verantwortlicher im Sinne des Presserechts (V.i.S.d.P.)\n" +
                "Hangover Application UG (haftungsbeschränkt)");
        return view;
    }
}
