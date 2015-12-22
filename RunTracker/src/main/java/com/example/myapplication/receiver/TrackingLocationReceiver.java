package com.example.myapplication.receiver;

import android.content.Context;
import android.location.Location;

import com.example.myapplication.manager.RunManager;

/**
 * Created by liuxi on 2015/12/20.
 */
public class TrackingLocationReceiver extends LocationReceiver {
    @Override
    protected void onLocationReceive(Context context, Location location) {
        super.onLocationReceive(context, location);
        RunManager.getInstance(context).insertLocation(location);
    }
}
