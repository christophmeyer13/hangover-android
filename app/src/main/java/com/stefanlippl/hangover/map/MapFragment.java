package com.stefanlippl.hangover.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stefanlippl.hangover.MainActivity;
import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.database.Database;
import com.stefanlippl.hangover.locations.LocationItem;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private GoogleMap googleMap;
    private MapView mapView;

    private static final int CLUB_COLOR = R.drawable.map_flag_club;
    private static final int BAR_COLOR = R.drawable.map_flag_bar;
    private static final int ELSE_COLOR = R.drawable.map_flag_else;
    private ArrayList<LocationItem> locationItems = new ArrayList<>();
    private Database database = Database.getAppDatabase(getContext());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_layout, container, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                locationItems.addAll(database.accessDao().getAllLocations());
            }
        }).start();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        this.googleMap = googleMap;
        setUpMapStyle();
        for (LocationItem locationItem : locationItems) {
            int resource;
            if(locationItem.getLocationType().startsWith("Club")) resource = CLUB_COLOR;
            else if (locationItem.getLocationType().startsWith("Bar")) resource = BAR_COLOR;
            else resource = ELSE_COLOR;
            addMapMarker(locationItem,resource);

        }
        setUpCameraPosition();
        checkLocationPermissions();
    }

    private void setUpMapStyle(){
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.styles);
        googleMap.setMapStyle(style);
    }

    private void addMapMarker(LocationItem locationItem, int resource ){
        MarkerOptions options = new MarkerOptions().position(new LatLng(locationItem.getLat(),locationItem.getLng()))
                .title(locationItem.getLocationName()).snippet(locationItem.getLocationStreet() + " " + locationItem.getHouseNumber()).icon(BitmapDescriptorFactory.fromResource(resource));
        googleMap.addMarker(options);
    }

    private void checkLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        googleMap.setMyLocationEnabled(MainActivity.areLocationsGranted);
    }

    private void setUpCameraPosition(){
        CameraPosition regensburg = CameraPosition.builder().target(new LatLng(49.014335, 12.094758)).zoom(14).bearing(0).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(regensburg));
    }

}