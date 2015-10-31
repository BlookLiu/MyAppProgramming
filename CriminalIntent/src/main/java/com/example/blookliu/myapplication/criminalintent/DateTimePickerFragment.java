package com.example.blookliu.myapplication.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by BlookLiu on 2015/10/6.
 */
public class DateTimePickerFragment extends DialogFragment {
    private static final String TAG = "DateTimePickerFragment";
    private Date mDate;
    public static final String EXTRA_CRIME_DATETIME = "com.example.blookliu.criminalintent.DateTimePickerFragment.crime_datetime";

    public static DateTimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_DATETIME, date);
        DateTimePickerFragment fragment = new DateTimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(EXTRA_CRIME_DATETIME);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View v = View.inflate(this.getActivity(), R.layout.dialog_crime_datetime, null);
        DatePicker dp = (DatePicker) v.findViewById(R.id.crime_date_dp);
        dp.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Log.d()
                calendar.set(year, monthOfYear, dayOfMonth);
                mDate = calendar.getTime();
                getArguments().putSerializable(EXTRA_CRIME_DATETIME, mDate);
            }
        });
        TimePicker tp = (TimePicker) v.findViewById(R.id.crime_time_tp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp.setHour(hour);
            tp.setMinute(minute);
        } else {
            tp.setCurrentHour(hour);
            tp.setCurrentMinute(minute);
        }
        if (DateFormat.is24HourFormat(getActivity())) {
            tp.setIs24HourView(true);
        } else {
            tp.setIs24HourView(false);
        }
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                mDate = calendar.getTime();
                getArguments().putSerializable(EXTRA_CRIME_DATETIME, mDate);
            }
        });
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_picker_fragment_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(Activity.RESULT_OK);
            }
        }).create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_CRIME_DATETIME, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
