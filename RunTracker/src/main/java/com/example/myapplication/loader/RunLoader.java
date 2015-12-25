package com.example.myapplication.loader;

import android.content.Context;

import com.example.myapplication.manager.RunManager;
import com.example.myapplication.model.Run;

/**
 * Created by liuxi on 2015/12/25.
 */
public class RunLoader extends DataLoader<Run> {
    private long mRunId;

    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.getInstance(getContext()).getRun(mRunId);
    }
}
