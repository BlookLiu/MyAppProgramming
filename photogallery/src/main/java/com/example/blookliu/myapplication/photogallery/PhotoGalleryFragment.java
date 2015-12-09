package com.example.blookliu.myapplication.photogallery;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.blookliu.myapplication.photogallery.model.GalleryItem;
import com.example.blookliu.myapplication.photogallery.service.PollService;
import com.example.blookliu.myapplication.photogallery.util.FlickrFetchr;
import com.example.blookliu.myapplication.photogallery.util.ThumbnailDownloader;

import java.util.ArrayList;

/**
 * Created by BlookLiu on 2015/11/9.
 */
public class PhotoGalleryFragment extends VisibleFragment implements OnScrollListener {
    private static final String TAG = "PhotoGalleryFragment";
    private GridView mGridView;
    private ArrayList<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<ImageView> mThumbnailDownloader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
        /*Intent i = new Intent(getActivity(), PollService.class);
        getActivity().startService(i);*/
//        PollService.setServiceAlarm(getActivity(), true);
        mThumbnailDownloader = new ThumbnailDownloader<ImageView>(new Handler());
        mThumbnailDownloader.setListener(new ThumbnailDownloader.downloaderListener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()) {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        setupAdapter();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem item = mItems.get(position);
                Uri photoPageUrl = Uri.parse(item.getPhotoPageUrl());
                Uri baiduUrl = Uri.parse("http://www.baidu.com");
                Intent i = new Intent(Intent.ACTION_VIEW, baiduUrl);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem item = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQuery("test", false);
            Bundle bundle = new Bundle();
            bundle.putString("data", "text");
            searchView.setAppSearchData(bundle);
//            searchView.setIconifiedByDefault(true);
//            searchView.setIconified(true);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
            searchView.setSearchableInfo(searchableInfo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search: {
//                getActivity().onSearchRequested();
                Log.d(TAG, "click search");
                Bundle data = new Bundle();
                data.putString("data", "search");
                getActivity().startSearch("test", true, data, false);
                return true;
            }
            case R.id.menu_item_clear: {
                Log.d(TAG, "click clear");
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(FlickrFetchr.PREF_SEARCH_QUERY, null).commit();
                updateItems();
                ((ArrayAdapter) mGridView.getAdapter()).notifyDataSetChanged();
                return true;
            }
            case R.id.menu_item_toggle_polling: {
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            }
            default:
                Log.d(TAG, "click default");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "prepare options menu");
        super.onPrepareOptionsMenu(menu);
        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            Log.d(TAG, "alarm on");
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            Log.d(TAG, "alarm off");
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
/*
            case SCROLL_STATE_IDLE: {
                Log.i(TAG, "scroll idle");
                // download the visiable item
                int firstPos = view.getFirstVisiblePosition();
                int LastPos = view.getLastVisiblePosition();
            }
*/
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void setupAdapter() {
        if (getActivity() == null || mGridView == null) {
            Log.w(TAG, "unattach!");
            return;
        }
        /*if (mItems != null) {
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }*/
        mGridView.setAdapter(new GalleryItemAdapter(mItems));
        mGridView.setEmptyView(View.inflate(getActivity(), R.layout.gallery_empty_view, null));
    }

    public void updateItems() {
        new FetchItemsTask().execute();
    }

    class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            /*try {
                String result = new HttpUtil().getUrl("http://www.baidu.com");
                Log.i(TAG, "fetched contents of URL: " + result);
            } catch (IOException e) {
                Log.e(TAG, "Failed to fetch url: ", e);
            }*/
            mItems.clear();
            Activity activity = getActivity();
            if (activity == null) {
                return new ArrayList<>();
            }
            String query = PreferenceManager.getDefaultSharedPreferences(activity).getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
            if (query != null) {
                return new FlickrFetchr().search(query);
            } else {
                return new FlickrFetchr().fetchItems();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mItems.addAll(galleryItems);
            ((ArrayAdapter) mGridView.getAdapter()).notifyDataSetChanged();
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
        private ArrayList<String> preloadList = new ArrayList<>(20);

        public GalleryItemAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.v(TAG, "get view");
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_iv);
            imageView.setImageResource(R.drawable.brian_up_close);
            // preload previous 10 items and next 10 items
            preloadList.clear();
            if (position != 0) {
                int count = 0;
                for (int i = position - 1; i >= 0 && count < 10; i--) {
                    preloadList.add(getItem(i).getUrl());
                    count++;
                }
            }
            if (position != getCount() - 1) {
                int count = 0;
                for (int i = position + 1; i < getCount() && count < 10; i++) {
                    preloadList.add(getItem(i).getUrl());
                    count++;
                }
            }
            mThumbnailDownloader.preload(preloadList);
            GalleryItem item = getItem(position);
            mThumbnailDownloader.queueThumbnail(imageView, item.getUrl());
            return convertView;
        }
    }
}
