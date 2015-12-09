package com.example.blookliu.myapplication.draganddraw;

import android.support.v4.app.Fragment;

public class DragAndDrawActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DragAndDrawFragment();
    }
}
