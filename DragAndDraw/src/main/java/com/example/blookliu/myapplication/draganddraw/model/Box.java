package com.example.blookliu.myapplication.draganddraw.model;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by liuxi on 2015/11/26.
 */
public class Box implements Parcelable{
    private PointF mOrigin;
    private PointF mCurrent;

    public Box(PointF origin) {
        mOrigin = mCurrent = origin;
    }

    public Box(Parcel in) {
        mOrigin = in.readParcelable(getClass().getClassLoader());
        mCurrent = in.readParcelable(getClass().getClassLoader());
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("Box", "flags: "+flags);
        dest.writeParcelable(mOrigin,flags);
        dest.writeParcelable(mCurrent, flags);
    }

    public static final Parcelable.Creator<Box> CREATOR
            = new Parcelable.Creator<Box>() {

        public Box createFromParcel(Parcel in) {
            return new Box(in);
        }

        public Box[] newArray(int size) {
            return new Box[size];
        }

    };
}
