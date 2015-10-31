package com.example.blookliu.myapplication.criminalintent.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by BlookLiu on 2015/10/19.
 */
public class PictureUtil {
    private static final String TAG = "PictureUtil";

    public static Bitmap getScaledBitmap(String srcPath, int destWidth, int destHeight) {
        if (srcPath == null) {
            Log.w(TAG, "srcPath is null!");
            return null;
        }
        Log.d(TAG, String.format("destWidth %d, destHeight %d", destWidth, destHeight));
        if (destWidth < 1 || destHeight < 1) {
            Log.w(TAG, String.format("destWidth %d, destHeight %d is illegal!", destWidth, destHeight));
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        Log.d(TAG, String.format("srcWidth %d, srcHeight %d", srcWidth, srcHeight));
        int inSampleSize = 1;
        /*if (srcWidth > destWidth || srcHeight > destHeight) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }*/
        while (srcWidth > destWidth || srcHeight > destHeight) {
            srcWidth = srcWidth >> 1;
            srcHeight = srcHeight >> 1;
            inSampleSize++;
        }
        if (inSampleSize < 1) {
            inSampleSize = 1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
        if (bitmap == null) {
            Log.w(TAG, "bitmap cannot be decoded!");
            return bitmap;
        }
        //rotation
        return rotateBitmap(bitmap, getRotationDegree(srcPath), true);
    }

    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            Log.i(TAG, "is not BitmapDrawable");
            return;
        }
        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        if (b != null && b.getBitmap() != null)
            b.getBitmap().recycle();
        imageView.setImageBitmap(null);
    }

    public static int dp2px(Context c, int dp) {
        float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int getRotationDegree(String filepath) {
        int degree = 0;
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(filepath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation == -1) {
                Log.w(TAG, "error orientation!");
                return 0;
            }
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, "error to read exif info " + filepath, e);
        }
        Log.i(TAG, String.format("orientation %d, degree %d", orientation, degree));
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree, boolean recycle) {
        Log.d(TAG, "rotate degree " + degree);
        if (degree % 360 == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (rotateBitmap == null) {
            rotateBitmap = bitmap;
        }
        if (recycle && rotateBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotateBitmap;
    }
}
