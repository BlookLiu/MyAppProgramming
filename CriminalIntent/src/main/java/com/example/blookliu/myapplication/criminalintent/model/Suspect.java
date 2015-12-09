package com.example.blookliu.myapplication.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BlookLiu on 2015/11/4.
 */
public class Suspect {
    private static final String JSON_NAME = "name";
    private static final String JSON_NUMBER = "number";
    private String mName;
    private String mNumber;

    public Suspect(String name) {
        this(name, "");
    }

    public Suspect(String name, String number) {
        mName = name;
        mNumber = number;
    }

    public Suspect(JSONObject json) throws JSONException {
        mName = json.getString(JSON_NAME);
        mNumber = json.getString(JSON_NUMBER);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_NAME, mName);
        jsonObject.put(JSON_NUMBER, mNumber);
        return jsonObject;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }
}
