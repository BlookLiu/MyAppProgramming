package com.example.blookliu.myapplication.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BlookLiu on 2015/10/19.
 */
public class Photo {
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_ORIENTATION = "orientation";
    private String mFilename;
    private int mOrientation;

    public Photo(String filename) {
        this(filename, 0);
    }

    public Photo(String filename, int orientation) {
        mFilename = filename;
        mOrientation = orientation;
    }

    public Photo(JSONObject json) throws JSONException {
        mFilename = json.getString(JSON_FILENAME);
        mOrientation = json.getInt(JSON_ORIENTATION);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_FILENAME, mFilename);
        jsonObject.put(JSON_ORIENTATION, mOrientation);
        return jsonObject;
    }

    public String getFilename() {
        return mFilename;
    }

    public int getOrientation() {
        return mOrientation;
    }
}
