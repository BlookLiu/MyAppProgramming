package com.example.myapplication;

import android.support.v4.app.Fragment;

/**
 * Created by liuxi on 2015/12/20.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
