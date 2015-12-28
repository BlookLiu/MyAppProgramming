package com.example.myapplication.loader;

import android.content.Context;
import android.database.Cursor;

import com.example.myapplication.manager.RunManager;

/**
 * Created by liuxi on 2015/12/27.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mRunId;

    public LocationListCursorLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.getInstance(getContext()).queryLocationsForRun(mRunId);
    }
}
