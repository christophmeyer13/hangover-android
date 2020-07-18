package com.stefanlippl.hangover.law;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefanlippl.hangover.R;

public class AgbFragment extends Fragment {

    private static final String HANGOVER = "Hangover";
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_agb, container, false);

        agbDetail = view.findViewById(R.id.agbdetail);
        agbDetail.setText("Allgemeine Geschäftsbedingungen für das Online-Angebot von der Hangover Application UG (haftungsbeschränkt) (nachfolgend als " + HANGOVER + " bezeichnet) und für die Nutzung der Webseite www.hangover.de.\n" +
                "Beschreibung des Angebots von " + HANGOVER + "\n" +
                "Nutzer können auf www.hangover.de beispielsweise (Auswahl):\n" +
                "eine Übersicht über stattfindende Events in Berlin erhalten\n" +
                "sich für Gästelisten und Verlosungen für ausgewählte Events anmelden\n" +
                "Eventbilder ansehen\n" +
                "Nutzerkonten anlegen und selber Inhalte einreichen und veröffentlichen (z. B. Veranstaltungen) und Gästelistenanmeldungen verwalten.\n" +
                HANGOVER + " informiert angemeldete Nutzer in regelmäßigen Abständen per E-Mail (Newsletter) über anstehende Events.\n" +
                "Nutzung der Website\n" +
                "Stand: Mai 2019");
        return view;
    }
}
