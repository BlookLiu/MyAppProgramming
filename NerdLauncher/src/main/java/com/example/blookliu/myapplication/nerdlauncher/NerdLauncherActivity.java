package com.example.blookliu.myapplication.nerdlauncher;

import android.support.v4.app.Fragment;

/**
 * Created by BlookLiu on 2015/11/7.
 */
public class NerdLauncherActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new NerdLauncherFragment();
    }
}
