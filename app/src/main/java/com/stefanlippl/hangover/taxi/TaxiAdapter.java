package com.stefanlippl.hangover.taxi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stefanlippl.hangover.R;

import java.util.ArrayList;

public class TaxiAdapter extends ArrayAdapter <TaxiCompany> {

    private ArrayList<TaxiCompany> companies;

    private TextView companyName, address, telNum;
    private ImageView taxi;


    TaxiAdapter(Context context, ArrayList<TaxiCompany> companies){
        super(context, R.layout.taxi_item,companies);
        this.companies = companies;
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent) {
        View v = convertView;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.taxi_item,null);
        }
        TaxiCompany company = companies.get(position);

        if(company != null){
            initialiseViews(v);
            setUpViews(company);
        }
        return v;
    }

    private void initialiseViews(View v){
        companyName = v.findViewById(R.id.company_name);
        address = v.findViewById(R.id.company_address);
        telNum = v.findViewById(R.id.phone_number);
        taxi = v.findViewById(R.id.taxi_icon);
    }

    private void setUpViews(TaxiCompany company){
        companyName.setText(company.getName());
        address.setText(company.getFullAdress());
        telNum.setText(company.getTelNum());
        taxi.setImageResource(R.drawable.call_a_taxi);
    }
}
