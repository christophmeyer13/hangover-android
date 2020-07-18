package com.stefanlippl.hangover.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanlippl.hangover.R;

import java.util.ArrayList;

public class TaxiFragment extends Fragment {

    View view;
    private FirebaseFirestore onlineDatabase = FirebaseFirestore.getInstance();

    ListView taxiCompanies;
    ArrayList<TaxiCompany> companies = new ArrayList<>();
    TaxiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.taxi_layout,container,false);

        setUpListView();
        downloadAllTaxiCompanies();

        return view;
    }

    private void downloadAllTaxiCompanies(){
        companies.clear();
        onlineDatabase.collection("Taxis").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String name = document.getString("name");
                                String telNum = document.getString("telNum");
                                String info = document.getString("info");
                                String street = document.getString("street");
                                String zip = document.getString("zip");
                                String city = document.getString("city");
                                String prices = document.getString("prices");
                                String agb = document.getString("agb");
                                String houseNumber = document.getString("housenumber");

                                TaxiCompany company = new TaxiCompany(name,telNum,info,street,houseNumber,zip,city,prices,agb);
                                companies.add(company);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void setUpListView(){
        adapter = new TaxiAdapter(getActivity(),companies);
        taxiCompanies = view.findViewById(R.id.taxis_list);
        taxiCompanies.setAdapter(adapter);
        taxiCompanies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaxiCompany taxiCompany = (TaxiCompany) taxiCompanies.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(),TaxiDetail.class);
                intent.putExtra("taxi",taxiCompany);
                startActivity(intent);
            }
        });
    }
}
