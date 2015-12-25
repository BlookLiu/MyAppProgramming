package com.example.myapplication.loader;

import android.content.Context;
import android.location.Location;

import com.example.myapplication.manager.RunManager;

/**
 * Created by liuxi on 2015/12/25.
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;

    public LastLocationLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.getInstance(getContext()).getLastLocationForRun(mRunId);
    }
}
