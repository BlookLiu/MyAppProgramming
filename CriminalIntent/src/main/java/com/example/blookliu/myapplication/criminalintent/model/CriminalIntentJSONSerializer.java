package com.example.blookliu.myapplication.criminalintent.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by BlookLiu on 2015/10/12.
 */
public class CriminalIntentJSONSerializer {
    private static final String TAG = "JSONSerializer";
    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context context, String filename) {
        mContext = context;
        mFilename = filename;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (Crime c : crimes) {
            array.put(c.toJSON());
        }
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public void saveCrimesToExternal(ArrayList<Crime> crimes) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (Crime c : crimes) {
            array.put(c.toJSON());
        }
        FileOutputStream fileOutputStream = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File f = new File(mContext.getExternalFilesDir(null), mFilename);
                Log.d(TAG, String.format("external file path %s", f.getAbsolutePath()));
                fileOutputStream = new FileOutputStream(f, false);
                fileOutputStream.write(array.toString().getBytes());
            } else {
                Log.w(TAG, "external storage can not write");
            }
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
            InputStream inputStream = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return crimes;
    }

    public ArrayList<Crime> loadCrimesFromExternal() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        FileInputStream fileInputStream = null;
        BufferedReader reader = null;
        try {
            File f = new File(mContext.getExternalFilesDir(null), mFilename);
            Log.d(TAG, String.format("load external file path %s", f.getAbsolutePath()));
            if (f.exists()) {
                fileInputStream = new FileInputStream(f);
                reader = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                for (int i = 0; i < array.length(); i++) {
                    crimes.add(new Crime(array.getJSONObject(i)));
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return crimes;
    }
}
