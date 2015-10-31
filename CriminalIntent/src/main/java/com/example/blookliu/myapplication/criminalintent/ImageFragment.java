package com.example.blookliu.myapplication.criminalintent;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.blookliu.myapplication.criminalintent.util.PictureUtil;

/**
 * Created by BlookLiu on 2015/10/19.
 */
public class ImageFragment extends DialogFragment {
    public static final String TAG = "ImageFragment";
    public static final String EXTRA_IMAGE_PATH = "com.example.blookliu.criminalintent.image_path";
    public static final String EXTRA_IMAGE_ORIENTATION = "com.example.blookliu.criminalintent.image_orientation";
    private ImageView mImageView;
    private ProgressBar mImagePb;

    public static ImageFragment newInstance(String imagePath, int orientation) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        args.putSerializable(EXTRA_IMAGE_ORIENTATION, orientation);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_crime_iamge, container, false);
        mImagePb = (ProgressBar) v.findViewById(R.id.crime_image_pb);
        mImageView = (ImageView) v.findViewById(R.id.crime_image_iv);
        mImageView.setBackgroundResource(android.R.color.darker_gray);
        final String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        final int orientation = (int) getArguments().getSerializable(EXTRA_IMAGE_ORIENTATION);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        //异步加载
        AsyncTask<Integer, Integer, Bitmap> asyncLoadBitmap = new AsyncTask<Integer, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Integer... params) {
                Bitmap bm = PictureUtil.getScaledBitmap(path, metrics.widthPixels, metrics.heightPixels);
                Bitmap rotatedBitmap = null;
                Log.i(TAG, String.format("bm width %d, height %d", bm.getWidth(), bm.getHeight()));
                // 引起oom
                /*if (orientation == 90 || orientation == 270) {
                    rotatedBitmap = PictureUtil.rotateBitmap(bm, (270 - orientation), true);
                } else {
                    rotatedBitmap = PictureUtil.rotateBitmap(bm, (orientation + 90), true);
                }*/
                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
                mImagePb.setVisibility(View.GONE);
            }
        };
        asyncLoadBitmap.execute();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtil.cleanImageView(mImageView);
    }

}
