package com.example.blookliu.myapplication.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.blookliu.myapplication.photogallery.util.FlickrFetchr;

/**
 * Created by BlookLiu on 2015/11/9.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity {
    private static final String TAG = "PhotoGalleryActivity";

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment fragment = (PhotoGalleryFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle bundle = intent.getBundleExtra(SearchManager.APP_DATA);
            Log.i(TAG, "Received a new search query " + query + "extra: " + bundle.getString("data"));
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(FlickrFetchr.PREF_SEARCH_QUERY, query).commit();
        }
        fragment.updateItems();
    }
}
