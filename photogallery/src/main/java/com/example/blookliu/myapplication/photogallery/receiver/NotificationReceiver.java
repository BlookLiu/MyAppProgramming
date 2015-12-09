package com.example.blookliu.myapplication.photogallery.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.blookliu.myapplication.photogallery.service.PollService;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received result: " + getResultCode());
        if (getResultCode() != Activity.RESULT_CANCELED) {
            int requestCode = intent.getIntExtra(PollService.REQUEST_NOTIFY_CODE, 0);
            Notification notification = intent.getParcelableExtra(PollService.NOTIFICATION_PARCELABLE);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(requestCode, notification);
        }
    }
}
