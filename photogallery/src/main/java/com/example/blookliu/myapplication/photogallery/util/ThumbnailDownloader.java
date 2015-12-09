package com.example.blookliu.myapplication.photogallery.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by BlookLiu on 2015/11/12.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int DOWNLOAD_MSG = 0x01;
    private static final int PRELOAD = 0x02;
    private Handler mHandler, mResponseHandler;
    private final Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    private downloaderListener mListener;
    private final LruCache<String, Bitmap> cache = new LruCache<>(50);
    private final Set<String> downloadingSet = Collections.synchronizedSet(new HashSet<String>());

    public downloaderListener getListener() {
        return mListener;
    }

    public void setListener(downloaderListener listener) {
        mListener = listener;
    }

    public ThumbnailDownloader() {
        super(TAG);
    }

    public ThumbnailDownloader(Handler responseHandler) {
        this();
        mResponseHandler = responseHandler;
    }

    public void queueThumbnail(final Token token, String url) {
        Log.i(TAG, "got an url: " + url);
        if (url != null && cache.get(url) != null) {
            Log.i(TAG, "get from cache " + url);
            final Bitmap bitmap = cache.get(url);
            mResponseHandler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    mListener.onThumbnailDownloaded(token, bitmap);
                    requestMap.remove(token);
                }
            });
        } else {
            requestMap.put(token, url);
            downloadingSet.add(url);
            mHandler.obtainMessage(DOWNLOAD_MSG, token).sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(this.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == DOWNLOAD_MSG) {
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "got a request url: " + requestMap.get(token));
                    handleRequest(token);
                } else if (msg.what == PRELOAD) {
                    String url = (String) msg.obj;
                    Log.i(TAG, "preload a url " + url);
                    handlePreload(url);
                }
                return false;
            }
        });
    }

    private void handleRequest(final Token token) {
        final String url = requestMap.get(token);
        if (url == null) {
            Log.w(TAG, "no url!");
            return;
        }
        Log.i(TAG, "request url: " + url);
        try {
            byte[] bitmapBytes = new HttpUtil().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "bitmap created");
            cache.put(url, bitmap);
            mResponseHandler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) == null || !requestMap.get(token).equals(url)) {
                        Log.i(TAG, "thumbnail has out of date");
                        return;
                    }
                    mListener.onThumbnailDownloaded(token, bitmap);
                    requestMap.remove(token);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "error downloading image", e);
        } finally {
            downloadingSet.remove(url);
        }
    }

    public void preload(ArrayList<String> preloadList) {
        for (String url : preloadList) {
            if (url != null) {
                preload(url);
            } else {
                Log.w(TAG, "url is null!");
            }
        }
    }

    public void preload(String url) {
        if (cache.get(url) == null && !downloadingSet.contains(url)) {
            downloadingSet.add(url);
            mHandler.obtainMessage(PRELOAD, url).sendToTarget();
        } else {
            Log.v(TAG, String.format("don't need to preload, in cache %s, downloading %s", cache.get(url), downloadingSet.contains(url)));
        }
    }

    private void handlePreload(String url) {
        Log.i(TAG, "preload url " + url);
        try {
            byte[] bitmapBytes = new HttpUtil().getUrlBytes(url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            cache.put(url, bitmap);
        } catch (IOException e) {
            Log.e(TAG, "preload failed: ", e);
        } finally {
            downloadingSet.remove(url);
        }
    }

    private boolean isDownloading(String url) {
        Iterator<String> iterator = downloadingSet.iterator();
        while (iterator.hasNext()) {
            String oldUrl = iterator.next();
            Log.v(TAG, String.format("old url %s, new url %s", url, oldUrl));
            if (url.equals(oldUrl)) {
                return true;
            }
        }
        return false;
    }

    private boolean removeDownloading(String url) {
        Iterator<String> iterator = downloadingSet.iterator();
        while (iterator.hasNext()) {
            if (url.equals(iterator.next())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public interface downloaderListener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }
}
