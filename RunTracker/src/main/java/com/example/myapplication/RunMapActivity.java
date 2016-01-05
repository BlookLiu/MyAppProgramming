package com.example.myapplication;

import android.support.v4.app.Fragment;

/**
 * Created by liuxi on 2015/12/26.
 */
public class RunMapActivity extends SingleFragmentActivity {
    public static final String EXTRA_RUN_ID = "com.example.myapplication.runtracker.run_id";
    public static final String EXTRA_MAP_TYPE = "com.example.myapplication.runtracker.map_type";
    public static final int MAP_BAIDU = 0x01;
    public static final int MAP_GOOGLE = 0x02;
    public static final int MAP_UNKNOWN = 0x00;

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        int mapType = getIntent().getIntExtra(EXTRA_MAP_TYPE, -1);
        if (runId != -1) {
            return MapFragment.newInstance(runId);
        } else {
            return new MapFragment();
        }

    }

}
