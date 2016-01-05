package com.example.myapplication;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.basemap.IMap;
import com.example.myapplication.basemap.IMapView;
import com.example.myapplication.db.RunDatabaseHelper;
import com.example.myapplication.loader.LocationListCursorLoader;
import com.example.myapplication.manager.RunManager;
import com.example.myapplication.mapimpl.BMapView;
import com.example.myapplication.receiver.LocationReceiver;

/**
 * Created by liuxi on 2016/1/3.
 */
public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IMapView.IOnReadyMapCallback {
    private static final String TAG = "MapFragment";
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int REQ_LOCATION_PERMISSION = 0x01;
    private static final int LOAD_LOCATIONS = "load_location".hashCode();
    private RunDatabaseHelper.LocationCursor mLocationCursor;
    private IMapView mFackMapView;
    private IMap mFackMap;
    private LocationReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceive(Context context, Location location) {
            super.onLocationReceive(context, location);
            Log.d(TAG, String.format("receive: %s", location.toString()));
            Bundle args = getArguments();
            if (args != null) {
                long runId = args.getLong(ARG_RUN_ID, -1);
                if (runId != -1) {
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(LOAD_LOCATIONS, args, MapFragment.this);
                }
            }
        }
    };

    public static MapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // baidu地图
        mFackMapView = new BMapView(getActivity());
        View mapView = mFackMapView.getMapView();
        mFackMapView.getAsyncMap(this);
        return mapView;
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

    private void updateUI() {
        if (mLocationCursor == null || mLocationCursor.isClosed()) {
            Log.w(TAG, String.format("cursor: %s, closed: %B", mLocationCursor, mLocationCursor != null && mLocationCursor.isClosed()));
            return;
        }
        mFackMapView.updateUI(mLocationCursor);
    }

    @Override
    public void onReadyMap(IMap map) {
        mFackMap = map;
        updateUI();
        mFackMap.enableMyLocation(true);
    }
}
