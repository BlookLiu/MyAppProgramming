package com.example.blookliu.myapplication.draganddraw;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

public class DragAndDrawActivity extends SingleFragmentActivity {
    private static final String TAG = "DragAndDrawActivity";

    @Override
    protected Fragment createFragment() {
        return new DragAndDrawFragment();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
