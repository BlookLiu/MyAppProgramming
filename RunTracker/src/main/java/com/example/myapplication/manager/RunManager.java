package com.example.myapplication.manager;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.myapplication.db.RunDatabaseHelper;
import com.example.myapplication.model.Run;

/**
 * Created by liuxi on 2015/12/15.
 */
public class RunManager {
    private static final String TAG = "RunManager";
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    public static final String ACTION_LOCATION = "com.example.myapplication.runtracker" +
            ".ACTION_LOCATION";
    public static final int REQ_CHECK_LOCATION_PERMISSION = 0x01;
    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mSharedPreferences;
    private long mCurrentId;

    private RunManager(Context context) {
        mAppContext = context;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(context);
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentId = mSharedPreferences.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    public static RunManager getInstance(Context context) {
        if (sRunManager == null) {
            sRunManager = new RunManager(context);
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent intent = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, intent, flags);
    }

    @SuppressWarnings("checkPermission")
    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && mLocationManager
                .isProviderEnabled(TEST_PROVIDER)) {
            Log.d(TAG, "using test provider");
            provider = TEST_PROVIDER;
        }
        // check permission
        if (ActivityCompat.checkSelfPermission(mAppContext, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(mAppContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (mAppContext instanceof Activity) {
                Activity activity = (Activity) mAppContext;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
                                .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQ_CHECK_LOCATION_PERMISSION);
            }
            return;
        }

        Location lastLoc = mLocationManager.getLastKnownLocation(provider);
        if (lastLoc != null) {
            Log.d(TAG, "get last location: " + lastLoc.toString());
            lastLoc.setTime(System.currentTimeMillis());
            broadcastLocation(lastLoc);
        }
        PendingIntent pendingIntent = getLocationPendingIntent(true);
        /**/
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

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == mCurrentId;
    }

    public Run startNewRun() {
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void stopRun() {
        stopLocationUpdates();
        mCurrentId = -1;
        mSharedPreferences.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    public void insertLocation(Location location) {
        if (mCurrentId != -1) {
            mHelper.insertLocation(mCurrentId, location);
        } else {
            Log.e(TAG, "location received with no tracking run; ignore.");
        }
    }

    public void startTrackingRun(Run run) {
        mCurrentId = run.getId();
        mSharedPreferences.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentId).commit();
        startLocationUpdates();
    }

    private Run insertRun() {
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public RunDatabaseHelper.RunCursor queryRuns() {
        return mHelper.queryRuns();
    }

    public Run getRun(long runId) {
        Run run = null;
        RunDatabaseHelper.RunCursor runCursor = mHelper.queryRun(runId);
        runCursor.moveToFirst();
        if (!runCursor.isAfterLast()) {
            run = runCursor.getRun();
        }
        runCursor.close();
        return run;
    }

    public Location getLastLocationForRun(long runId) {
        Location location = null;
        RunDatabaseHelper.LocationCursor locationCursor = mHelper.queryLastLocationForRun(runId);
        locationCursor.moveToFirst();
        if (!locationCursor.isAfterLast()) {
            location = locationCursor.getLocation();
        }
        locationCursor.close();
        return location;
    }

    public RunDatabaseHelper.LocationCursor queryLocationsForRun(long runId) {
        return mHelper.queryLocationsForRun(runId);
    }

    public long getCurrentId() {
        return mCurrentId;
    }
}
