package com.example.blookliu.myapplication.photogallery.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.blookliu.myapplication.photogallery.service.PollService;

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";
    /**
     * 如果接收不到intent，可能是应用被禁止自启动了。
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, String.format("Received broadcast intent: %s", intent.getAction()));
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("开机测试")
                .setContentTitle("test")
                .setContentText("content")
                .setAutoCancel(true)
                .setNumber(2)
                .build();
//        Log.i(TAG, "1");
        Toast.makeText(context, "开机", Toast.LENGTH_SHORT).show();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notification.hashCode(), notification);
//        Log.i(TAG, "2");
        boolean isOn = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PollService.PREF_IS_ALARM_ON, false);
        PollService.setServiceAlarm(context, isOn);
//        Log.i(TAG, "3");
    }
}
