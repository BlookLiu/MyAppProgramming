package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button mStartBtn, mStopBtn;
    private TextView mStartedTv, mLatitudeTv, mLongtitudeTv, mAltitudeTv, mDurationTv;
    private RunManager mRunManager;
    private Location mLastLocation;
    private Run mRun;
    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceive(Context context, Location location) {
            super.onLocationReceive(context, location);
//            Toast.makeText(getActivity(), String.format("location: %s", location.toString()), Toast.LENGTH_SHORT).show();
            mLastLocation = location;
            if (isVisible()) {
                updateUI();
            }
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, container, false);
        mStartedTv = (TextView) v.findViewById(R.id.start_tv);
        mLatitudeTv = (TextView) v.findViewById(R.id.latitude_tv);
        mLongtitudeTv = (TextView) v.findViewById(R.id.longitude_tv);
        mAltitudeTv = (TextView) v.findViewById(R.id.altitude_tv);
        mDurationTv = (TextView) v.findViewById(R.id.elapsed_tv);
        mStartBtn = (Button) v.findViewById(R.id.start_btn);
        mStopBtn = (Button) v.findViewById(R.id.stop_btn);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.startLocationUpdates(getActivity());
                mRun = new Run();
                updateUI();
            }
        });
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopLocationUpdates();
                updateUI();
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

    private void updateUI() {
        boolean started = mRunManager.isTrackingRun();
        if (mRun != null)
            mStartedTv.setText(mRun.getStartDate().toString());
        int durationSeconds = 0;
        if (mLastLocation != null && mRun != null) {
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            mAltitudeTv.setText(Double.toString(mLastLocation.getAltitude()));
            mLatitudeTv.setText(Double.toString(mLastLocation.getLatitude()));
            mLongtitudeTv.setText(Double.toString(mLastLocation.getLongitude()));
        }
        mDurationTv.setText(String.valueOf(durationSeconds));
        mStartBtn.setEnabled(!started);
        mStopBtn.setEnabled(started);
    }
}
