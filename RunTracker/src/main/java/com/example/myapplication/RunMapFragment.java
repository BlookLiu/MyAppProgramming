package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.db.RunDatabaseHelper;
import com.example.myapplication.loader.LocationListCursorLoader;
import com.example.myapplication.manager.RunManager;
import com.example.myapplication.receiver.LocationReceiver;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxi on 2015/12/26.
 */
public class RunMapFragment extends SupportMapFragment implements LoaderManager
        .LoaderCallbacks<Cursor>, OnMapReadyCallback {
    private static final String TAG = "RunMapFragment";
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int REQ_LOCATION_PERMISSION = 0x01;
    private static final int LOAD_LOCATIONS = "load_location".hashCode();
    private GoogleMap mGoogleMap;
    private RunDatabaseHelper.LocationCursor mLocationCursor;
    private LocationReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceive(Context context, Location location) {
            super.onLocationReceive(context, location);
            Log.d(TAG, String.format("receive: %s", location.toString()));
//            mGoogleMap.clear();
//            updateUI();
            Bundle args = getArguments();
            if (args != null) {
                long runId = args.getLong(ARG_RUN_ID, -1);
                if (runId != -1) {
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(LOAD_LOCATIONS, args, RunMapFragment.this);
                }
            }
        }
    };

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment runMapFragment = new RunMapFragment();
        runMapFragment.setArguments(args);
        return runMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
        // init loader
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(LOAD_LOCATIONS, args, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "create view");
        View v = super.onCreateView(inflater, container, savedInstanceState);
//        mGoogleMap = getMap();
        return v;
    }

    private void enableLocation(boolean enabled) {
        Log.d(TAG, "enable my location " + enabled);
        mGoogleMap.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "request permission result code: " + requestCode);
        Map<String, Integer> perms = new HashMap<>();
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }
        switch (requestCode) {
            case REQ_LOCATION_PERMISSION: {
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                        .PERMISSION_GRANTED && perms.get(Manifest.permission
                        .ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "permissions have granted");
                    enableLocation(true);
                } else {
                    Log.w(TAG, "permission have been denied");
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager
                .ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    private void updateUI() {
        Log.d(TAG, "do update UI");
        if (mGoogleMap == null || mLocationCursor == null) {
            Log.w(TAG, String.format("update error, googleMap: %s, locationCursor: %s",
                    mGoogleMap, mLocationCursor));
            return;
        }
        mGoogleMap.clear();
        PolylineOptions polylineOptions = new PolylineOptions();
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        mLocationCursor.moveToFirst();
        Log.d(TAG, "cursor count: " + mLocationCursor.getCount());
        while (!mLocationCursor.isAfterLast()) {
            Location location = mLocationCursor.getLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //add marker
            if (mLocationCursor.isFirst()) {
                String startDate = new Date(location.getTime()).toString();
                MarkerOptions startMarkerOptions = new MarkerOptions().position(latLng).title
                        (getString(R.string.run_start)).snippet(getString(R.string
                        .run_start_at_format, startDate));
                mGoogleMap.addMarker(startMarkerOptions);
            } else if (mLocationCursor.isLast()) {
                String endDate = new Date(location.getTime()).toString();
                MarkerOptions endMarkerOptions = new MarkerOptions().position(latLng).title
                        (getString(R.string.run_finish)).snippet(getString(R.string
                        .run_finish_at_format, endDate));
                mGoogleMap.addMarker(endMarkerOptions);
            }
            polylineOptions.add(latLng);
            latLngBuilder.include(latLng);
            mLocationCursor.moveToNext();
        }
        mGoogleMap.addPolyline(polylineOptions);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        LatLngBounds latLngBounds = latLngBuilder.build();
        Point point = new Point();
        display.getSize(point);
        CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBounds, point.x, point
                .y, 50);
        mGoogleMap.moveCamera(movement);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        Log.d(TAG, "create loader: " + runId);
        return new LocationListCursorLoader(getActivity(), runId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "finish load");
        mLocationCursor = (RunDatabaseHelper.LocationCursor) data;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "loader reset");
        mLocationCursor.close();
        mLocationCursor = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "map ready");
        mGoogleMap = googleMap;
        updateUI();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "req code: " + REQ_LOCATION_PERMISSION);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_LOCATION_PERMISSION);
            return;
        }
        enableLocation(true);
    }
}
