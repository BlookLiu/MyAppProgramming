package com.example.blookliu.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

/**
 * Created by BlookLiu on 2015/10/10.
 */
public class HelloMoonVideoFragment extends Fragment {
    private static final String TAG = "HelloMoonVideoFragment";
    private MediaController mMediaController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hello_moon_video, container, false);
        VideoView videoView = (VideoView) v.findViewById(R.id.hello_moon_vv);
        mMediaController = new MediaController(getActivity());
        videoView.setMediaController(mMediaController);
//        Uri myUri = Uri.parse("android.resource://com.example.blookliu.myapplication/raw/apollo_17_stroll");
//        videoView.setVideoURI(myUri);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test1.mp4";
        Log.d(TAG, "path: " + path);
        videoView.setVideoPath(path);
//        videoView.start();
        return v;
    }
}
