package com.stefanlippl.hangover.taxi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stefanlippl.hangover.R;

public class TaxiDetail extends AppCompatActivity implements View.OnClickListener{

    TextView companyName, address, phoneNumber;
    ImageView taxiLogo;
    Button call;
    CardView info, priceList, agb;
    Toolbar toolbar;
    ImageView arrowInfo, arrowPrice, arrowAGB;

    TaxiCompany current;
    String selectedFragment = "info";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_detail);
        initialiseViews();
        setButtonOnClickListeners();

        Intent intent = getIntent();
        current = intent.getParcelableExtra("taxi");

        setUpInfoFragment();
        setUpToolBar();
        setUpViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        //reset color of selected fragment
        this.info.setCardBackgroundColor(getColor(R.color.card_color));
        this.priceList.setCardBackgroundColor(getColor(R.color.card_color));
        this.agb.setCardBackgroundColor(getColor(R.color.card_color));

        Bundle bundle = new Bundle();
        //reset arrow rotation of selected fragment
        if(selectedFragment.equals("info")) arrowInfo.setRotation(arrowInfo.getRotation() + 180);
        else if(selectedFragment.equals("price")) arrowPrice.setRotation(arrowPrice.getRotation() + 180);
        else if(selectedFragment.equals("agb")) arrowAGB.setRotation(arrowAGB.getRotation() + 180);

        switch (v.getId()){
            //if info card selected change color, rotate arrow and replace fragment
            case R.id.info_card:
                selectedFragment = "info";
                arrowInfo.setRotation(arrowInfo.getRotation() + 180);
                bundle.putString("text",current.getInfo());
                TaxiDetailFragment info = new TaxiDetailFragment();
                info.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.taxi_frag_container, info).commit();
                this.info.setCardBackgroundColor(getColor(android.R.color.darker_gray));
                break;
            //if price list card selected change color, rotate arrow and replace fragment
            case R.id.pricelist:
                selectedFragment = "price";
                arrowPrice.setRotation(arrowPrice.getRotation() + 180);
                bundle.putString("text",current.getPrices());
                TaxiDetailFragment prices = new TaxiDetailFragment();
                prices.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.taxi_frag_container, prices).commit();
                this.priceList.setCardBackgroundColor(getColor(android.R.color.darker_gray));
                break;
            //if agb card selected change color, rotate arrow and replace fragment
            case R.id.agb_taxi:
                selectedFragment = "agb";
                arrowAGB.setRotation(arrowAGB.getRotation() + 180);
                bundle.putString("text",current.getAgb());
                TaxiDetailFragment agb = new TaxiDetailFragment();
                agb.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.taxi_frag_container, agb).commit();
                this.agb.setCardBackgroundColor(getColor(android.R.color.darker_gray));
                break;
            //if call button clicked send implicit intent with phone number
            case R.id.share_button:
                String uri = "tel:" + current.getTelNum();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
                break;
        }
    }

    private void initialiseViews(){
        companyName = findViewById(R.id.headlinedetail);
        address = findViewById(R.id.datedetail);
        phoneNumber = findViewById(R.id.locationdetail);
        taxiLogo = findViewById(R.id.locationlogodetail);
        call = findViewById(R.id.share_button);
        priceList = findViewById(R.id.pricelist);
        toolbar = findViewById(R.id.toolbar4);
        agb = findViewById(R.id.agb_taxi);
        info = findViewById(R.id.info_card);
        arrowInfo = findViewById(R.id.arrow_info);
        arrowPrice = findViewById(R.id.arrow_price);
        arrowAGB = findViewById(R.id.arrow_agb);
    }

    private void setButtonOnClickListeners(){
        info.setOnClickListener(this);
        priceList.setOnClickListener(this);
        agb.setOnClickListener(this);
        call.setOnClickListener(this);
    }

    private void setUpInfoFragment(){
        arrowInfo.setRotation(arrowInfo.getRotation() + 180);
        Bundle bundle = new Bundle();
        bundle.putString("text",current.getInfo());
        TaxiDetailFragment info = new TaxiDetailFragment();
        info.setArguments(bundle);
        this.info.setCardBackgroundColor(getColor(android.R.color.darker_gray));
        getSupportFragmentManager().beginTransaction().replace(R.id.taxi_frag_container, info).commit();
    }

    private void setUpToolBar(){
        toolbar.setTitle(current.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpViews(){
        companyName.setText(current.getName());
        address.setText(current.getFullAdress());
        phoneNumber.setText(current.getTelNum());
        taxiLogo.setImageResource(R.drawable.call_a_taxi_white);
    }
}
