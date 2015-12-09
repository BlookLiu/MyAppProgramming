package com.example.blookliu.myapplication.remotecontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

/**
 * Created by BlookLiu on 2015/11/8.
 */
public class RemoteControlActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RemoteControlFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }
}
