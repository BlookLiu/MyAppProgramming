package com.example.myapplication.model;

import java.util.Date;

/**
 * Created by liuxi on 2015/12/16.
 */
public class Run {
    private Date mStartDate;

    public Run() {
        mStartDate = new Date();
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public int getDurationSeconds(long endMillions) {
        return (int) ((endMillions - mStartDate.getTime()) / 1000);
    }

    public static String formatDuration(int durationSeconds) {
        int s = durationSeconds % 60;
        int m = ((durationSeconds - s) / 60) % 60;
        int h = (durationSeconds - (m * 60) - s) / 3600;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
