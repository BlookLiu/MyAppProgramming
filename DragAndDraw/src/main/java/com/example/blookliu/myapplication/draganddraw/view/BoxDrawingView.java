package com.example.blookliu.myapplication.draganddraw.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.blookliu.myapplication.draganddraw.R;
import com.example.blookliu.myapplication.draganddraw.model.Box;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";
    private static final String RESTORE_KEY = "restore_key";
    private Box mCurrentBox;
    private ArrayList<Box> mBoxes = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF curr = new PointF(event.getX(), event.getY());
        Log.v(TAG, String.format("Received event at x=%f, y=%f", curr.x, curr.y));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, " action down");
                mCurrentBox = new Box(curr);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.v(TAG, "action move");
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(curr);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "action up");
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.v(TAG, "action cancel");
                mCurrentBox = null;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        for (Box box : mBoxes) {
            float l = Math.min(box.getOrigin().x, box.getCurrent().x);
            float r = Math.max(box.getOrigin().x, box.getCurrent().x);
            float t = Math.min(box.getOrigin().y, box.getCurrent().y);
            float b = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(l, t, r, b, mBoxPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.i(TAG, "on save instance state");
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESTORE_KEY, mBoxes);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.i(TAG, "on restore instance state");
        Bundle bundle = (Bundle) state;
        mBoxes = (ArrayList<Box>) bundle.getSerializable(RESTORE_KEY);
        invalidate();
    }

    private class ShapeSaveState extends BaseSavedState {

        public ShapeSaveState(Parcel source) {
            super(source);
        }

        public ShapeSaveState(Parcelable superState) {
            super(superState);
        }
    }
}
