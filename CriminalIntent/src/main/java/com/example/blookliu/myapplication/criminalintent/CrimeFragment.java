package com.example.blookliu.myapplication.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blookliu.myapplication.criminalintent.model.Crime;
import com.example.blookliu.myapplication.criminalintent.model.CrimeLab;
import com.example.blookliu.myapplication.criminalintent.model.Photo;
import com.example.blookliu.myapplication.criminalintent.util.PictureUtil;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by BlookLiu on 2015/9/30.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_DATE = "dialog_date";
    private static final String DIALOG_TIME = "dialog_time";
    private static final String DIALOG_IMAGE = "dialog_image";
    private static final int PHOTO_VIEW_HEIGHT = 80;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoBtn;
    private ImageView mPhotoView;
    private OnFragmentInteractionListener mOnFragmentInteractionListener;
    private int photoWidth;
    private int photoHeight;
    private boolean hasUpdateImage;

    public static final String EXTRA_CRIME_ID = "com.example.blookliu.criminalintent.extra_crime_id";
    public static final int REQUEST_CRIME_DATETIME = 0x01;
    public static final int REQUEST_CRIME_TIME = 0x02;
    public static final int REQUEST_CRIME_PHOTO = 0x03;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            try {
                mOnFragmentInteractionListener = (OnFragmentInteractionListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(CrimeFragment.EXTRA_CRIME_ID);
        Log.d(TAG, "UUID " + crimeId);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        Log.d(TAG, "mCrime " + mCrime);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title_et);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                mOnFragmentInteractionListener.updateTitle(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton = (Button) v.findViewById(R.id.crime_date_btn);
        mDateButton.setText(formatDate(mCrime.getDate()));
//        mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DateTimePickerFragment dialog = DateTimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CRIME_DATETIME);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved_cb);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        mPhotoBtn = (ImageButton) v.findViewById(R.id.crime_camera_ib);
        mPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoBtn.showContextMenu();
                /*Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_CRIME_PHOTO);*/
            }
        });
        registerForContextMenu(mPhotoBtn);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_camera_iv);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p != null) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                    int orientation = p.getOrientation();
                    ImageFragment.newInstance(path, orientation).show(fm, DIALOG_IMAGE);
                }
            }
        });
        /*mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Photo photo = mCrime.getPhoto();
                if (photo != null) {
                    File file = getActivity().getFileStreamPath(photo.getFilename());
                    if (file.exists()) {
                        Log.i(TAG, "delete the file " + file.getAbsolutePath());
                        file.delete();
                    }
                    mCrime.setPhoto(null);
                }
                updatePhotoView();
                return false;
            }
        });*/
        registerForContextMenu(mPhotoView);
        photoWidth = photoHeight = PictureUtil.dp2px(getActivity(), PHOTO_VIEW_HEIGHT);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        return v;
    }

    @NonNull
    private CharSequence formatDate(Date date) {
        CharSequence dateSeq = null;
        if (DateFormat.is24HourFormat(this.getActivity())) {
            dateSeq = DateFormat.format("yyyy-MM-dd kk:mm", date);
        } else {
            dateSeq = DateFormat.format("yyyy-MM-dd a hh:mm", date);
        }
        return dateSeq;
    }

    private void updatePhotoView() {
        Photo p = mCrime.getPhoto();
        Bitmap bitmap = null;
        if (p != null) {
            if (!hasUpdateImage) {
                Log.i(TAG, "do not need update image view");
                return;
            }
            final String filepath = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            final int orientation = p.getOrientation();
//            bitmap = PictureUtil.getScaledBitmap(filepath, mPhotoView.getWidth(), mPhotoView.getHeight());
            AsyncTask<Integer, Integer, Bitmap> asyncLoadBitmap = new AsyncTask<Integer, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Integer... params) {
                    Bitmap scaledBitmap = PictureUtil.getScaledBitmap(filepath, photoWidth, photoHeight);
                    if (scaledBitmap == null) {
                        Log.w(TAG, String.format("bitmap %s is not exist", filepath));
                        return null;
                    }
                    return scaledBitmap;
                    /*Log.d(TAG, "orientation " + orientation);
                    if (orientation == 90 || orientation == 270) {
                        return PictureUtil.rotateBitmap(scaledBitmap, (270 - orientation), true);
                    } else {
                        return PictureUtil.rotateBitmap(scaledBitmap, (orientation + 90), true);
                    }*/
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    mPhotoView.setImageBitmap(bitmap);
                    hasUpdateImage = false;
                }


            };
            asyncLoadBitmap.execute();
        } else {
            mPhotoView.setImageBitmap(null);
        }
//        mPhotoView.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CRIME_DATETIME) {
            Date date = (Date) data.getSerializableExtra(DateTimePickerFragment.EXTRA_CRIME_DATETIME);
            mCrime.setDate(date);
            mDateButton.setText(formatDate(mCrime.getDate()));
        } /*else if (requestCode == REQUEST_CRIME_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_CRIME_TIME);
            mCrime.setDate(date);
            mDateButton.setText(formatDate(mCrime.getDate()));
        }*/ else if (requestCode == REQUEST_CRIME_PHOTO) {
            Photo oldPhoto = mCrime.getPhoto();
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            int orientation = data.getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ORIENTATION, 0);
            if (filename != null) {
//                Log.i(TAG, "photo filename " + filename);
                Toast.makeText(getActivity(), "photo filename " + filename, Toast.LENGTH_SHORT).show();
                Photo p = new Photo(filename, orientation);
                mCrime.setPhoto(p);
                hasUpdateImage = true;
                updatePhotoView();
            }
            //delete the old photo if it exists
            if (oldPhoto != null) {
                File oldFile = getActivity().getFileStreamPath(oldPhoto.getFilename());
                if (oldFile.exists()) {
                    Log.i(TAG, "delete the file " + oldFile.getAbsolutePath());
                    oldFile.delete();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_crime, menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "view id " + v.getId());
        if (v.getId() == R.id.crime_camera_iv)
            getActivity().getMenuInflater().inflate(R.menu.menu_crime_pager_context, menu);
        else if (v.getId() == R.id.crime_camera_ib) {
            getActivity().getMenuInflater().inflate(R.menu.menu_crime_select_camera_activity_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "is delete " + (item.getItemId() == R.id.crime_camera_delete));
        int id = item.getItemId();
        switch (id) {
            case R.id.crime_camera_delete:
                Photo photo = mCrime.getPhoto();
                if (photo != null) {
                    File file = getActivity().getFileStreamPath(photo.getFilename());
                    if (file.exists()) {
                        Log.i(TAG, "delete the file " + file.getAbsolutePath());
                        file.delete();
                    }
                    mCrime.setPhoto(null);
                }
                updatePhotoView();
                break;
            case R.id.crime_camera_system: {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /*String fileUri = getActivity().
                i.putExtra(MediaStore.EXTRA_OUTPUT, )*/
                startActivity(i);
                break;
            }
            case R.id.crime_camera_customer: {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_CRIME_PHOTO);
                break;
            }
            default:
                Log.w(TAG, "undefined " + id);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        hasUpdateImage = true;
        updatePhotoView();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        PictureUtil.cleanImageView(mPhotoView);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
        mOnFragmentInteractionListener = null;
    }

    public interface OnFragmentInteractionListener {
        void updateTitle(CharSequence title);
    }
}
