package com.example.blookliu.myapplication.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CrimeCameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CrimeCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrimeCameraFragment extends Fragment implements PreviewView.IPreviewInternal {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "CrimeCameraFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String EXTRA_PHOTO_FILENAME = "com.example.blookliu.criminalintent.photo_filename";
    public static final String EXTRA_PHOTO_ORIENTATION = "com.example.blookliu.criminalintent.photo_orientation";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private PreviewView mPreviewView;
    private FrameLayout mProgressBarFl;
    private OrientationEventListener mOrientationEventListener;
    private int mDegree = 0;
    /**
     * surfaceview.setVisibility()之后会调用PreviewView的RequestLayout(),添加标志位判断fragment状态是resume还是pause
     */
    private boolean mIsActive = false;
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressBarFl.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.d(TAG, String.format("test save bitmap width %d, height %d", bm.getWidth(), bm.getHeight()));
//            Bitmap rotateBm = PictureUtil.rotateBitmap(bm, 90, true);
            String filename = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream fileOutputStream = null;
            boolean isSuccess = true;
            try {
                fileOutputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
//                rotateBm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.write(data);
                //存入外部
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(path, filename);
                Log.d(TAG, "external path " + file.getAbsolutePath());
                FileOutputStream fileOutputStream1 = new FileOutputStream(file);
                fileOutputStream1.write(data);
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file " + filename, e);
                isSuccess = false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing file " + filename, e);
                        isSuccess = false;
                    }
                }
            }
            if (isSuccess) {
                Log.i(TAG, "JPEG saved at " + filename);
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                i.putExtra(EXTRA_PHOTO_ORIENTATION, mDegree);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };
//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CrimeCameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CrimeCameraFragment newInstance(String param1, String param2) {
        CrimeCameraFragment fragment = new CrimeCameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CrimeCameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "on create view");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);
        Button takePicBtn = (Button) v.findViewById(R.id.crime_camera_take_pic_btn);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mPictureCallback);
                }
            }
        });

//        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_sv);
        mPreviewView = (PreviewView) v.findViewById(R.id.crime_camera_sv);
        mSurfaceView = mPreviewView.mSurfaceView;
        mPreviewView.setPreviewInternal(this);
        mProgressBarFl = (FrameLayout) v.findViewById(R.id.crime_camera_progressBar_fl);
        mProgressBarFl.setVisibility(View.INVISIBLE);
        mOrientationEventListener = new OrientationEventListener(getActivity()) {
            @Override
            public void onOrientationChanged(int orientation) {
                Log.v(TAG, "orientation: " + orientation);

                if (ORIENTATION_UNKNOWN == orientation) {
                    return;
                }
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(0, info);
                orientation = (orientation + 45) / 90 * 90;
                int rotation = 0;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    rotation = (info.orientation - orientation + 360) % 360;
                } else {
                    rotation = (info.orientation + orientation) % 360;
                }
                if (null != mCamera) {
                    Log.v(TAG, "rotation " + rotation);
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setRotation(rotation);
                    mCamera.setParameters(parameters);
                }
            }
        };

        return v;
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
/*        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, String.format("on resume %s", mSurfaceView.getHolder().getSurface() != null));
        /**
         * 由于异步开启camera，错过了在previewView初始化时measure和layout之前设置camera的时机，
         */
        AsyncTask<Integer, Integer, Integer> asyncOpenCamera = new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer[] params) {
                Log.i(TAG, "start open camera");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    mCamera = Camera.open(0);
                } else {
                    mCamera = Camera.open();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                Log.d(TAG, "finish open camera");
                mPreviewView.setCamera(mCamera);
                setSurfaceCallback();
            }
        };
        asyncOpenCamera.execute();
        mIsActive = true;
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        } else {
            Log.w(TAG, "cannot detect orientation");
        }
    }

    private void setSurfaceCallback() {
        Log.i(TAG, "set surface callback");
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surface created");
//                mCamera = Camera.open();
                if (mCamera != null) {
                    try {
                        mCamera.setPreviewDisplay(holder);
                        mCamera.startPreview();
                    } catch (IOException e) {
                        Log.e(TAG, "Error setting up preview display", e);
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, String.format("width %s, height %s creating %b", width, height, holder.getSurface() != null));
                if (mCamera == null) {
                    Log.w(TAG, "camera is null");
                    return;
                }
                mCamera.stopPreview();
                Camera.Parameters parameters = mCamera.getParameters();
//                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes());
                if (mPreviewView.mPreviewSize != null) {
                    Camera.Size s = mPreviewView.mPreviewSize;
                    Log.d(TAG, String.format("best preview size height: %d, width %d", s.height, s.width));
                    parameters.setPreviewSize(s.width, s.height);
                    s = getBestSupportedSize(parameters.getSupportedPictureSizes());
                    Log.d(TAG, String.format("best picture size height: %d, width %d", s.height, s.width));
                    parameters.setPictureSize(s.width, s.height);
                }
                parameters.setJpegQuality(100);
                if (getActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "portrait");
                    mCamera.setDisplayOrientation(90);
                } else {
                    parameters.set("orientation", "landscape");
                    mCamera.setDisplayOrientation(0);
                }
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                Log.d(TAG, String.format("focus mode %s", parameters.getFocusMode()));
                if (parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO) || parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_MACRO)) {
                    mCamera.autoFocus(null);
                }
                mCamera.cancelAutoFocus();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surface destroyed");
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            Log.d(TAG, "on pause camera release");
            mCamera.release();
            mCamera = null;
        }
//        mPreviewView.setVisibility(View.GONE);
        mSurfaceView.setVisibility(View.GONE);
        mIsActive = false;
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
            mOrientationEventListener = null;
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "on destroy view");
        super.onDestroyView();
    }

    @Override
    public void afterMeasure() {
        Log.d(TAG, "after measure " + mPreviewView.mPreviewSize);
    }

    @Override
    public void afterLayout() {

    }

    @Override
    public void beforeLayout() {
        Log.d(TAG, "before layout " + mPreviewView.mPreviewSize);
        if (mPreviewView.mPreviewSize != null && mIsActive == true) {
            mSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
