package com.example.blookliu.myapplication.draganddraw.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.AbsSavedState;
import android.view.MotionEvent;
import android.view.View;

import com.example.blookliu.myapplication.draganddraw.model.Box;

import java.util.ArrayList;
import java.util.Arrays;

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
        int actionIndex = MotionEventCompat.getActionIndex(event);
        int pointerId = MotionEventCompat.getPointerId(event, actionIndex);
        int findIndex = MotionEventCompat.findPointerIndex(event, pointerId);
        Log.v(TAG, String.format("Received event at x=%f, y=%f, action_index: %d, pointer_id: %d, find_index: %d", curr.x, curr.y, actionIndex, pointerId, findIndex));
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, " action down");
                /**
                 * 如果所按区域已经有图形则选中，否则就新建
                 */
                for (Box b : mBoxes) {

                }
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
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.v(TAG, "pointer down");

                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.v(TAG, "pointer up!");
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
        AbsSavedState savedState = (AbsSavedState) super.onSaveInstanceState();
        ShapeSaveState shapeSaveState = new ShapeSaveState(savedState);
        shapeSaveState.mBoxes = mBoxes.toArray(new Box[mBoxes.size()]);
        return shapeSaveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.i(TAG, "on restore instance state");
        ShapeSaveState shapeSaveState = (ShapeSaveState) state;
        super.onRestoreInstanceState(shapeSaveState.getSuperState());
        mBoxes = new ArrayList<>(Arrays.asList(shapeSaveState.mBoxes));

    }

    @SuppressWarnings("unchecked")
    private static class ShapeSaveState extends BaseSavedState {
        Box[] mBoxes;
        public ShapeSaveState(Parcel source) {
            super(source);
            mBoxes = (Box[]) source.readParcelableArray(Box.class.getClassLoader());
        }

        public ShapeSaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            if (mBoxes != null && mBoxes.length > 0)
                Log.d(TAG, "write to parcel " + flags);
            out.writeParcelableArray(mBoxes, flags);
        }


        public static final Parcelable.Creator<ShapeSaveState> CREATOR
                = new Parcelable.Creator<ShapeSaveState>() {

            public ShapeSaveState createFromParcel(Parcel in) {
                return new ShapeSaveState(in);
            }

            public ShapeSaveState[] newArray(int size) {
                return new ShapeSaveState[size];
            }

        };
    }
}
