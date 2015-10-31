package com.example.blookliu.myapplication.model;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;

/**
 * Created by BlookLiu on 2015/10/9.
 */
public class AsyncAudioPlayer {
    private static final String TAG = "AsyncAudioPlayer";
    private AsyncPlayer mAsyncPlayer;

    public void play(Context c){
        mAsyncPlayer = new AsyncPlayer(null);
        String packageName = c.getPackageName();
        Uri myUri = Uri.parse("android.resource://" + packageName + "/raw/one_small_step");
        mAsyncPlayer.play(c, myUri, false, AudioManager.STREAM_MUSIC);
    }

    public void stop(){
        if(mAsyncPlayer != null){
            mAsyncPlayer.stop();
        }
    }
}
