package com.stefanlippl.hangover.locations.filter;

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

import com.google.android.gms.common.util.ArrayUtils;
import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;

import java.util.Arrays;

public class FilterDialog extends Dialog {

    private OnFilterChangedListener listener;

    private String[] types;
    private boolean[] checkedTypes;

    private ListView listView;
    private Button save, reset;


    public FilterDialog(@NonNull Context context, OnFilterChangedListener listener, String[] types, boolean[] checkedTypes) {
        super(context);
        this.listener = listener;
        this.types = types;
        this.checkedTypes = checkedTypes;
        setUpLocationTypes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.locations_filter_layout);
        this.setTitle("Filter");
        setUpListView();
        setUpButtons();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean flag = false;
        for (int i = 0; i < checkedTypes.length; i++) {
            if (checkedTypes[i]) flag = true;
        }
        if (!flag) Arrays.fill(checkedTypes, true);
        listener.onFilterApplied(types, checkedTypes);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        boolean flag = false;
        for (int i = 0; i < checkedTypes.length; i++) {
            if (checkedTypes[i]) flag = true;
        }
        if (!flag) Arrays.fill(checkedTypes, true);
        listener.onFilterApplied(types, checkedTypes);
    }

    private void setUpLocationTypes() {
        if (types == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    types = Database.getAppDatabase(getContext()).accessDao().getLocationTypes();
                    types = ArrayUtils.concat(new String[]{"Alle"}, types);
                    checkedTypes = new boolean[types.length];
                    Arrays.fill(checkedTypes, true);
                    Arrays.sort(types);
                }
            }).start();
        }
    }

    private void setUpListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.filter_list_item, types);
        listView = findViewById(R.id.location_art_list);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedTypes[position] = !checkedTypes[position];
                if (position != 0) {
                    for (int i = 1; i < types.length; i++) {
                        if (!checkedTypes[i]) {
                            checkedTypes[0] = false;
                            listView.setItemChecked(0, false);
                            break;
                        } else {
                            checkedTypes[0] = true;
                            listView.setItemChecked(0, true);
                        }
                    }
                }
                if (position == 0) {
                    Arrays.fill(checkedTypes, checkedTypes[0]);
                    for (int i = 0; i < types.length; i++) {
                        listView.setItemChecked(i, checkedTypes[0]);
                    }
                }
            }
        });
        for (int i = 0; i < types.length; i++) {
            listView.setItemChecked(i, checkedTypes[i]);
        }
    }

    private void setUpButtons() {
        save = findViewById(R.id.save_filter_loc);
        reset = findViewById(R.id.reset_filter_loc);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                for (int i = 0; i < checkedTypes.length; i++) {
                    if (checkedTypes[i]) flag = true;
                }
                if (!flag) Arrays.fill(checkedTypes, true);
                listener.onFilterApplied(types, checkedTypes);
                dismiss();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.resetFilters(types, checkedTypes);
                Arrays.fill(checkedTypes, true);
                dismiss();
            }
        });
    }
}
