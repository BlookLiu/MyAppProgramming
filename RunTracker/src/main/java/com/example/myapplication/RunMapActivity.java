package com.example.myapplication;

import android.support.v4.app.Fragment;

/**
 * Created by liuxi on 2015/12/26.
 */
public class RunMapActivity extends SingleFragmentActivity {
    public static final String EXTRA_RUN_ID = "com.example.myapplication.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1) {
            return RunMapFragment.newInstance(runId);
        } else {
            return new RunMapFragment();
        }
    }
}
