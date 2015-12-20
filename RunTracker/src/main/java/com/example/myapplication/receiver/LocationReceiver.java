package com.example.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by liuxi on 2015/12/15.
 */
public class LocationReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "receive location event");
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            onLocationReceive(context, location);
            return;
        }
        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            onProviderEnabledChanged(enabled);
        }
    }

    protected void onProviderEnabledChanged(boolean enabled) {
        Log.i(TAG, String.format("Provider %B", enabled));
    }

    protected void onLocationReceive(Context context, Location location) {
        Log.i(TAG, String.format("get location from %s, latitude: %f, longitude: %f", location.getProvider(), location.getLatitude(), location.getLongitude()));
    }
}
