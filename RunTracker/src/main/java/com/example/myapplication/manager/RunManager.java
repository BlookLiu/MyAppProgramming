package com.example.myapplication.manager;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by liuxi on 2015/12/15.
 */
public class RunManager {
    private static final String TAG = "RunManager";
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    public static final String ACTION_LOCATION = "com.example.myapplication.runtracker.ACTION_LOCATION";
    public static final int REQ_CHECK_LOCATION_PERMISSION = "check_location_permission".hashCode();
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;

    private RunManager(Context context) {
        mAppContext = context;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static RunManager getInstance(Context context) {
        if (sRunManager == null) {
            sRunManager = new RunManager(context.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent intent = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, intent, flags);
    }

    public void startLocationUpdates(Activity activity) {
        String provider = LocationManager.GPS_PROVIDER;
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            Log.d(TAG, "using test provider");
            provider = TEST_PROVIDER;
        }
        Location lastLoc = mLocationManager.getLastKnownLocation(provider);
        if (lastLoc != null) {
            Log.d(TAG, "get last location: " + lastLoc.toString());
            lastLoc.setTime(System.currentTimeMillis());
            broadcastLocation(lastLoc);
        }
        PendingIntent pendingIntent = getLocationPendingIntent(true);
        if (ActivityCompat.checkSelfPermission(mAppContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mAppContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_CHECK_LOCATION_PERMISSION);
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 0, 0, pendingIntent);
    }

    private void broadcastLocation(Location lastLoc) {
        Intent intent = new Intent(ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, lastLoc);
        mAppContext.sendBroadcast(intent);
    }

    public void stopLocationUpdates() {
        PendingIntent pendingIntent = getLocationPendingIntent(false);
        if (pendingIntent != null) {
            mLocationManager.removeUpdates(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }
}
