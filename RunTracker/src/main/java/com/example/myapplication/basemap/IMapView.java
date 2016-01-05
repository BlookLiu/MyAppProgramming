package com.example.myapplication.basemap;

import android.view.View;

import com.example.myapplication.db.RunDatabaseHelper;

/**
 * Created by liuxi on 2015/12/30.
 */
public interface IMapView {

    View getMapView();

    void getAsyncMap(IOnReadyMapCallback callback);

    void enableMyLocation(boolean enabled);

    void updateUI(RunDatabaseHelper.LocationCursor cursor);

    interface IOnReadyMapCallback {
        void onReadyMap(IMap map);
    }

}
