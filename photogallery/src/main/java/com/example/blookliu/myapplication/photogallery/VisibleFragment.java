package com.example.blookliu.myapplication.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.blookliu.myapplication.photogallery.service.PollService;

/**
 * Created by liuxi on 2015/11/23.
 */
public class VisibleFragment extends Fragment {
    public static final String TAG = "VisibleFragment";

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Got a broadcast: " + intent.getAction());
//            Toast.makeText(getActivity(), "Got a broadcast: " + intent.getAction(), Toast.LENGTH_SHORT).show();
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mOnShowNotification, new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION), PollService.PREF_PRIVATE, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}
