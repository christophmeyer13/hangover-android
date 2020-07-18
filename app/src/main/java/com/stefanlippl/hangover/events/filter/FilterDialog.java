package com.stefanlippl.hangover.events.filter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.util.ArrayUtils;
import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterDialog extends Dialog {

    private OnFilterChangedListener listener;

    private String[] locationTypes;
    private boolean[] checkedLocationTypes;
    private String[] musicTypes;
    private boolean[] checkedMusicTypes;
    private int maxPrice;

    private ListView locationTypeList;
    private ListView musicTypeList;
    private SeekBar priceFilter;
    private Button save, reset;
    private TextView maxPriceText;

    public FilterDialog(@NonNull Context context, OnFilterChangedListener listener, String[] locationTypes1, boolean[] checkedLocationTypes1, String[] musicTypes1, boolean[] checkedMusicTypes1, int maxPrice) {
        super(context);
        this.listener = listener;
        this.locationTypes = locationTypes1;
        this.checkedLocationTypes = checkedLocationTypes1;
        this.musicTypes = musicTypes1;
        this.checkedMusicTypes = checkedMusicTypes1;
        this.maxPrice = maxPrice;

        setUpLocationTypes();
        setUpMusicTypes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.events_filter_layout);

        setUpLocationTypeList();
        setUpMusicTypeList();
        setUpSeekBar();
        setUpButtons();

        setTitle("Filter");
        for (int i = 0; i < checkedLocationTypes.length; i++) {
            locationTypeList.setItemChecked(i, checkedLocationTypes[i]);
        }
        for (int i = 0; i < checkedMusicTypes.length; i++) {
            musicTypeList.setItemChecked(i, checkedMusicTypes[i]);
        }
        if(maxPrice != 0) priceFilter.setProgress(maxPrice);
        else priceFilter.setProgress(20);
        maxPriceText.setText(String.format("%d€", priceFilter.getProgress()));

    }

    private void setUpLocationTypeList() {
        ArrayAdapter<String> locationTypeAdapter = new ArrayAdapter<>(getContext(), R.layout.filter_list_item, locationTypes);
        locationTypeList = findViewById(R.id.art_d_loc_list);
        locationTypeList.setAdapter(locationTypeAdapter);
        locationTypeList.setItemsCanFocus(false);
        locationTypeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        locationTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedLocationTypes[position] = !checkedLocationTypes[position];
                if (position != 0) {
                    for (int i = 0; i < checkedLocationTypes.length; i++) {
                        if (!checkedLocationTypes[i]) {
                            checkedLocationTypes[0] = false;
                            locationTypeList.setItemChecked(0, false);
                            break;
                        } else {
                            checkedLocationTypes[0] = true;
                            locationTypeList.setItemChecked(0, true);
                        }
                    }
                }
                if (position == 0) {
                    Arrays.fill(checkedLocationTypes, checkedLocationTypes[0]);
                    for (int i = 0; i < locationTypes.length; i++) {
                        locationTypeList.setItemChecked(i, checkedLocationTypes[0]);
                    }
                }
            }
        });
    }

    private void setUpMusicTypeList() {
        ArrayAdapter<String> musicTypeAdapter = new ArrayAdapter<>(getContext(), R.layout.filter_list_item, musicTypes);
        musicTypeList = findViewById(R.id.music_list);
        musicTypeList.setAdapter(musicTypeAdapter);
        musicTypeList.setItemsCanFocus(false);
        musicTypeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        musicTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedMusicTypes[position] = !checkedMusicTypes[position];
                if (position != 0) {
                    for (int i = 0; i < checkedMusicTypes.length; i++) {
                        if (!checkedMusicTypes[i]) {
                            checkedMusicTypes[0] = false;
                            musicTypeList.setItemChecked(0, false);
                            break;
                        } else {
                            checkedMusicTypes[0] = true;
                            musicTypeList.setItemChecked(0, true);
                        }
                    }
                }
                if (position == 0) {
                    Arrays.fill(checkedMusicTypes, checkedMusicTypes[0]);
                    for (int i = 0; i < musicTypes.length; i++) {
                        musicTypeList.setItemChecked(i, checkedMusicTypes[0]);
                    }
                }
            }
        });
    }

    private void setUpSeekBar() {
        priceFilter = findViewById(R.id.price_seek_bar);
        maxPriceText = findViewById(R.id.max_price);
        priceFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxPrice = priceFilter.getProgress();
                maxPriceText.setText(String.format("%d€", maxPrice));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setUpButtons() {
        save = findViewById(R.id.save_event_filters);
        reset = findViewById(R.id.reset_event_filters);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean locationsFlag = false;
                for (int i = 0; i < checkedLocationTypes.length; i++) {
                    if(checkedLocationTypes[i]) locationsFlag = true;
                }
                if(!locationsFlag) Arrays.fill(checkedLocationTypes,true);
                boolean musicFlag = false;
                for (int i = 0; i < checkedMusicTypes.length; i++) {
                    if(checkedMusicTypes[i]) musicFlag = true;
                }
                if(!musicFlag) Arrays.fill(checkedMusicTypes,true);
                listener.applyFilters(locationTypes, checkedLocationTypes, musicTypes, checkedMusicTypes, maxPrice);
                dismiss();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.resetFilters(locationTypes,checkedLocationTypes,musicTypes,checkedMusicTypes);
                dismiss();
            }
        });
    }

    private void setUpMusicTypes(){
        if (musicTypes == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> resultMusicTypes = new ArrayList<>();
                    String[] temp;
                    temp = Database.getAppDatabase(getContext()).accessDao().getMusic();
                    for (int i = 0; i < temp.length; i++) {
                        if (temp[i].contains(", ")) {
                            String[] splitStrings = temp[i].split(", ");
                            for (String current : splitStrings) {
                                if(!resultMusicTypes.contains(current)) {
                                    resultMusicTypes.add(current);
                                }
                            }
                        } else {
                            if(!resultMusicTypes.contains(temp[i])) resultMusicTypes.add(temp[i]);
                        }
                    }
                    musicTypes = resultMusicTypes.toArray(new String[0]);
                    musicTypes = ArrayUtils.concat(new String[]{"Alle"}, musicTypes);
                    checkedMusicTypes = new boolean[musicTypes.length];
                    Arrays.fill(checkedMusicTypes, true);
                }
            }).start();
        }
    }

    private void setUpLocationTypes(){
        if (this.locationTypes == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    locationTypes = Database.getAppDatabase(getContext()).accessDao().getAllLocationTypesWithEvents();
                    locationTypes = ArrayUtils.concat(new String[]{"Alle"}, locationTypes);
                    checkedLocationTypes = new boolean[locationTypes.length];
                    Arrays.fill(checkedLocationTypes, true);
                }
            }).start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean locationsFlag = false;
        for (int i = 0; i < checkedLocationTypes.length; i++) {
            if(checkedLocationTypes[i]) locationsFlag = true;
        }
        if(!locationsFlag) Arrays.fill(checkedLocationTypes,true);
        boolean musicFlag = false;
        for (int i = 0; i < checkedMusicTypes.length; i++) {
            if(checkedMusicTypes[i]) musicFlag = true;
        }
        if(!musicFlag) Arrays.fill(checkedMusicTypes,true);
        listener.applyFilters(locationTypes, checkedLocationTypes, musicTypes, checkedMusicTypes, maxPrice);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        boolean locationsFlag = false;
        for (int i = 0; i < checkedLocationTypes.length; i++) {
            if(checkedLocationTypes[i]) locationsFlag = true;
        }
        if(!locationsFlag) Arrays.fill(checkedLocationTypes,true);
        boolean musicFlag = false;
        for (int i = 0; i < checkedMusicTypes.length; i++) {
            if(checkedMusicTypes[i]) musicFlag = true;
        }
        if(!musicFlag) Arrays.fill(checkedMusicTypes,true);
        listener.applyFilters(locationTypes, checkedLocationTypes, musicTypes, checkedMusicTypes, maxPrice);
    }
}
