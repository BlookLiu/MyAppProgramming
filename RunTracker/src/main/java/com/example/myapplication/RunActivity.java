package com.example.myapplication;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by liuxi on 2015/12/15.
 */
public class RunActivity extends SingleFragmentActivity {
    private static final String TAG = "RunActivity";
    public static final String EXTRA_RUN_ID = "com.example.myapplication.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        Log.d(TAG, "runId: " + runId);
        if (runId != -1) {
            return RunFragment.newInstance(runId);
        } else {
            return new RunFragment();
        }
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "new intent "+intent.getLongExtra(EXTRA_RUN_ID, -1));
        super.onNewIntent(intent);
        setIntent(intent);
    }*/
}
