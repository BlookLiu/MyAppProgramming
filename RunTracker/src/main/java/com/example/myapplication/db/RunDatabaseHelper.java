package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.example.myapplication.model.Run;

import java.util.Date;

/**
 * Created by liuxi on 2015/12/20.
 */
public class RunDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "RunDatabaseHelper";
    private static final String DB_NAME = "run.sqlite";
    private static final int DB_VERSION = 1;

    private static final String TABLE_RUN = "run";
    private static final String COL_RUN_ID = "_id";
    private static final String COL_RUN_START_DATE = "start_date";

    private static final String TABLE_LOCATION = "location";
    private static final String COL_LOCATION_TIMESTAMP = "timestamp";
    private static final String COL_LOCATION_LATITUDE = "latitude";
    private static final String COL_LOCATION_LONGITUDE = "longitude";
    private static final String COL_LOCATION_ALTITUDE = "altitude";
    private static final String COL_LOCATION_PROVIDER = "provider";
    private static final String COL_LOCATION_RUN_ID = "run_id";


    public RunDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "create db");
        /**
         * execSQL（String, Object[]）方法不管用
         */
        db.execSQL(String.format("create table %s(_id integer primary key autoincrement, %s " +
                "integer)", TABLE_RUN, COL_RUN_START_DATE));
        db.execSQL(String.format("create table %s(%s integer, %s real, %s real, %s real, %s " +
                        "varchar (100), %s integer references run(_id))", TABLE_LOCATION,
                COL_LOCATION_TIMESTAMP, COL_LOCATION_LATITUDE, COL_LOCATION_LONGITUDE,
                COL_LOCATION_ALTITUDE, COL_LOCATION_PROVIDER, COL_LOCATION_RUN_ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("upgrade db, newVersion: %d, oldVersion: %d", newVersion,
                oldVersion));

    }

    public long insertRun(Run run) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RUN_START_DATE, run.getStartDate().getTime());
        return getWritableDatabase().insert(TABLE_RUN, null, cv);
    }

    public long insertLocation(long runId, Location location) {
        ContentValues cv = new ContentValues();
        cv.put(COL_LOCATION_ALTITUDE, location.getAltitude());
        cv.put(COL_LOCATION_LATITUDE, location.getLatitude());
        cv.put(COL_LOCATION_LONGITUDE, location.getLongitude());
        cv.put(COL_LOCATION_PROVIDER, location.getProvider());
        cv.put(COL_LOCATION_TIMESTAMP, location.getTime());
        cv.put(COL_LOCATION_RUN_ID, runId);
        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    public RunCursor queryRuns() {
        Cursor wrapper = getReadableDatabase().query(TABLE_RUN, null, null, null, null, null,
                COL_RUN_START_DATE + " asc");
        return new RunCursor(wrapper);
    }

    public RunCursor queryRun(long runId) {
        Cursor wrapper = getReadableDatabase().query(TABLE_RUN, null, COL_RUN_ID + " = ?", new
                String[]{String.valueOf(runId)}, null, null, null, "1");
        return new RunCursor(wrapper);
    }

    public LocationCursor queryLastLocationForRun(long runId) {
        Cursor wrapper = getReadableDatabase().query(TABLE_LOCATION, null, COL_LOCATION_RUN_ID +
                " = ? ", new String[]{String.valueOf(runId)}, null, null, COL_LOCATION_TIMESTAMP
                + " desc", "1");
        return new LocationCursor(wrapper);
    }

    public LocationCursor queryLocationsForRun(long runId) {
        Cursor wrapper = getReadableDatabase().query(TABLE_LOCATION, null, COL_LOCATION_RUN_ID +
                " = ?", new String[]{String.valueOf(runId)}, null, null, COL_LOCATION_TIMESTAMP +
                " asc");
        return new LocationCursor(wrapper);
    }

    public static class RunCursor extends CursorWrapper {
        private static final String TAG = "RunCursor";

        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public RunCursor(Cursor cursor) {
            super(cursor);
        }

        public Run getRun() {
            if (isBeforeFirst() || isAfterLast()) {
                Log.w(TAG, "cursor out of bound");
                return null;
            }
            Run run = new Run();
            long runId = getLong(getColumnIndex(COL_RUN_ID));
            run.setId(runId);
            long startDate = getLong(getColumnIndex(COL_RUN_START_DATE));
            run.setStartDate(new Date(startDate));
            return run;
        }
    }

    public static class LocationCursor extends CursorWrapper {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public LocationCursor(Cursor cursor) {
            super(cursor);
        }

        public Location getLocation() {
            if (isBeforeFirst() || isAfterLast()) {
                Log.w(TAG, "cursor out of bound");
                return null;
            }
            String provider = getString(getColumnIndex(COL_LOCATION_PROVIDER));
            Location location = new Location(provider);
            location.setLatitude(getDouble(getColumnIndex(COL_LOCATION_LATITUDE)));
            location.setLongitude(getDouble(getColumnIndex(COL_LOCATION_LONGITUDE)));
            location.setAltitude(getDouble(getColumnIndex(COL_LOCATION_ALTITUDE)));
            location.setTime(getLong(getColumnIndex(COL_LOCATION_TIMESTAMP)));
            return location;
        }
    }

}
