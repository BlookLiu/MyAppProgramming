package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.manager.RunManager;
import com.example.myapplication.model.Run;
import com.example.myapplication.receiver.LocationReceiver;

/**
 * Created by liuxi on 2015/12/15.
 */
public class RunFragment extends Fragment {
    private static final String TAG = "RunFragment";
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int NOTIFY_TRACKING_ID = "notify_tracking_id".hashCode();

    private Button mStartBtn, mStopBtn;
    private TextView mStartedTv, mLatitudeTv, mLongitudeTv, mAltitudeTv, mDurationTv;
    private RunManager mRunManager;
    private Location mLastLocation;
    private Run mRun;
    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceive(Context context, Location location) {
            super.onLocationReceive(context, location);
//            Toast.makeText(getActivity(), String.format("location: %s", location.toString()), Toast.LENGTH_SHORT).show();
            if (!mRunManager.isTrackingRun(mRun)) {
                return;
            }
            mLastLocation = location;
//            Log.d(TAG, "visibility: " + isVisible());
            if (isVisible()) {
                updateUI();
            }
            //do notify
            /**
             * PendingIntent传参为空，
             * 方法1：intent.setAction(String.valueOf(System.currentTimeMillis()))。
             * 方法2：PendingIntent.getActivity()方法的flags参数设置为FLAG_UPDATE_CURRENT
             */
            Intent intent = new Intent();
            intent.setClass(getActivity(), RunActivity.class);
            intent.putExtra(RunActivity.EXTRA_RUN_ID, mRun.getId());
//                intent.setAction(String.valueOf(System.currentTimeMillis()));
            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //custom view
            RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R
                    .layout.notify_content_view_run);
            remoteViews.setTextViewText(R.id.notify_altitude_tv, String.valueOf(mLastLocation
                    .getAltitude()));
            remoteViews.setTextViewText(R.id.notify_longitude_tv, String.valueOf(mLastLocation
                    .getLongitude()));
            remoteViews.setTextViewText(R.id.notify_latitude_tv, String.valueOf(mLastLocation
                    .getLatitude()));

            Notification notification = new NotificationCompat.Builder(getActivity())
                    .setTicker(getString(R.string.notify_track_ticker)).setContent(remoteViews)
                    .setContentIntent(pendingIntent).setSmallIcon(android.R.drawable
                            .ic_menu_mylocation).setAutoCancel(true).setOngoing(true).build();
            NotificationManager notificationManager = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFY_TRACKING_ID, notification);
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            super.onProviderEnabledChanged(enabled);
            Toast.makeText(getActivity(), enabled ? R.string.gps_enabled : R.string.gps_disabled, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.getInstance(getActivity());
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                mRun = mRunManager.getRun(runId);
                mLastLocation = mRunManager.getLastLocationForRun(runId);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, container, false);
        mStartedTv = (TextView) v.findViewById(R.id.start_tv);
        mLatitudeTv = (TextView) v.findViewById(R.id.latitude_tv);
        mLongitudeTv = (TextView) v.findViewById(R.id.longitude_tv);
        mAltitudeTv = (TextView) v.findViewById(R.id.altitude_tv);
        mDurationTv = (TextView) v.findViewById(R.id.elapsed_tv);
        mStartBtn = (Button) v.findViewById(R.id.start_btn);
        mStopBtn = (Button) v.findViewById(R.id.stop_btn);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRun == null) {
                    mRun = mRunManager.startNewRun();
                } else {
                    mRunManager.startTrackingRun(mRun);
                }
                updateUI();

            }
        });
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopRun();
                updateUI();
                NotificationManager notificationManager = (NotificationManager) getActivity()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFY_TRACKING_ID);

            }
        });
        updateUI();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    public static RunFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment runFragment = new RunFragment();
        runFragment.setArguments(args);
        return runFragment;
    }

    private void updateUI() {
        boolean started = mRunManager.isTrackingRun();
        boolean trackingThisRun = mRunManager.isTrackingRun(mRun);
        if (mRun != null)
            mStartedTv.setText(mRun.getStartDate().toString());
        int durationSeconds = 0;
        if (mLastLocation != null && mRun != null) {
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            mAltitudeTv.setText(Double.toString(mLastLocation.getAltitude()));
            mLatitudeTv.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTv.setText(Double.toString(mLastLocation.getLongitude()));
        }
        mDurationTv.setText(String.valueOf(durationSeconds));
        mStartBtn.setEnabled(!started);
        mStopBtn.setEnabled(started && trackingThisRun);
    }
}
