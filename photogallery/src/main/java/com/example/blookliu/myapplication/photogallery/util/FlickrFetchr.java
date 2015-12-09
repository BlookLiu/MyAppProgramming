package com.example.blookliu.myapplication.photogallery.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.blookliu.myapplication.photogallery.model.GalleryItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by BlookLiu on 2015/11/9.
 */
public class FlickrFetchr {

    public static final String PREF_SEARCH_QUERY = "search_query";
    public static final String PREF_LAST_RESULT_ID = "last_result_id";

    private static final String TAG = "FlickrFetchr";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "284661f0d5f6e4e870a89c9c55265fff";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String PARAM_EXTRAS = "extras";
    private static final String PARAM_TEXT = "text";
    private static final String XML_PHOTO = "photo";

    public ArrayList<GalleryItem> downloadGalleryItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<>();
        try {
            String xmlString = new HttpUtil().getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);
            XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));
            parseItems(items, xmlPullParser);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Failed to parse items", e);
        }
        return items;
    }

    public ArrayList<GalleryItem> fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .build().toString();
        Log.i(TAG, "fetch url: " + url);
        return downloadGalleryItems(url);
    }

    public ArrayList<GalleryItem> search(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, query)
                .build().toString();
        Log.i(TAG, "search url: " + url);
        return downloadGalleryItems(url);
    }

    private void parseItems(ArrayList<GalleryItem> items, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int eventType = xmlPullParser.next();
        while (eventType != xmlPullParser.END_DOCUMENT) {
            if (eventType == xmlPullParser.START_TAG && XML_PHOTO.equals(xmlPullParser.getName())) {
                String id = xmlPullParser.getAttributeValue(null, "id");
                String caption = xmlPullParser.getAttributeValue(null, "title");
                String smallUrl = xmlPullParser.getAttributeValue(null, EXTRA_SMALL_URL);
                String owner = xmlPullParser.getAttributeValue(null, "owner");
                GalleryItem item = new GalleryItem(caption, id, smallUrl, owner);
                Log.v(TAG, "item: " + item.toString());
                items.add(item);
            }
            eventType = xmlPullParser.next();
        }
    }
}
