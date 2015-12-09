package com.example.blookliu.myapplication.photogallery.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.blookliu.myapplication.photogallery.PhotoGalleryActivity;
import com.example.blookliu.myapplication.photogallery.R;
import com.example.blookliu.myapplication.photogallery.model.GalleryItem;
import com.example.blookliu.myapplication.photogallery.util.FlickrFetchr;

import java.util.ArrayList;

public class PollService extends IntentService {
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";
    public static final String ACTION_SHOW_NOTIFICATION = "com.example.blookliu.photogallery.show_notification";
    public static final String PREF_PRIVATE = "com.example.blookliu.photogallery.PRIVATE";
    public static final String REQUEST_NOTIFY_CODE = "request_notify_code";
    public static final String NOTIFICATION_PARCELABLE = "notification_parcelable";
    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 15;
    private Notification notification;
    private static int count = 1;

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Receive an intent " + intent);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Log.d(TAG, String.format("networkinfo %s, connection %s", networkInfo.toString(), networkInfo.isConnected()));
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() && networkInfo != null && networkInfo.isConnected();
        if (!isNetworkAvailable) {
            Log.w(TAG, "network is not available currently");
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String query = preferences.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
        String lastResultId = preferences.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);
        ArrayList<GalleryItem> items;
        if (query != null) {
            items = new FlickrFetchr().search(query);
        } else {
            items = new FlickrFetchr().fetchItems();
        }
        if (items.size() == 0) {
            Log.i(TAG, "no items");
            return;
        }
        String resultId = items.get(0).getId();
        if (lastResultId == null || !lastResultId.equals(resultId)) {
            Log.i(TAG, "Got a new result " + resultId);
            Intent i = new Intent(this, PhotoGalleryActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            /*if (notification == null) {
                notification = new NotificationCompat.Builder(this)
                        .setTicker(getResources().getString(R.string.new_pictures_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(getResources().getString(R.string.new_pictures_title))
                        .setContentText(getResources().getString(R.string.new_pictures_text))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setNumber(1)
                        .setOngoing(true)
                        .build();
            } else {
                notification.number += notification.number;
                Log.d(TAG, "number " + notification.number);
            }*/
            Notification newNotification = new NotificationCompat.Builder(this)
                    .setTicker(getResources().getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(getResources().getString(R.string.new_pictures_title))
                    .setContentText(getResources().getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
//                    .setOngoing(true)
                    .setNumber(count)
                    .build();
            /**
             * notification.number无效，未知原因。。。
             */
            newNotification.number = count++;
            Log.d(TAG, "number " + newNotification.number + "\tthis: " + this);
//            NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//            nm.notify(0, newNotification);
//            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PREF_PRIVATE);
            showBackgroundNotification(0, newNotification);
            preferences.edit().putString(FlickrFetchr.PREF_LAST_RESULT_ID, resultId).commit();
        } else {
            Log.i(TAG, "Got a old result " + resultId);
        }
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_NOTIFY_CODE, requestCode);
        i.putExtra(NOTIFICATION_PARCELABLE, notification);
        sendOrderedBroadcast(i, PREF_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PollService.PREF_IS_ALARM_ON, isOn).commit();
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent();
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }
}
