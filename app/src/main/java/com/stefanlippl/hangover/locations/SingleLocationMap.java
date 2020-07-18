package com.stefanlippl.hangover.locations;

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

public class SingleLocationMap extends Fragment implements OnMapReadyCallback {

    private View view;
    private GoogleMap googleMap;
    private MapView mapView;
    private LocationItem item;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_single_location_layout, container, false);

        item = getArguments().getParcelable("location");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map_single_location);
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
        addMapMarker();
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(MainActivity.areLocationsGranted);
    }

    private void addMapMarker() {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(item.getLat(), item.getLng())).title(item.getLocationName()).snippet(item.getLocationStreet() + " " + item.getHouseNumber()).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_flag)));
        CameraPosition currentSelectedLocation = CameraPosition.builder().target(new LatLng(item.getLat(), item.getLng())).zoom(14).bearing(0).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentSelectedLocation));
    }

    private void setUpMapStyle() {
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.styles);
        googleMap.setMapStyle(styleOptions);
    }
}
