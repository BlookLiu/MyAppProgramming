package com.example.blookliu.myapplication.photogallery.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BlookLiu on 2015/11/9.
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        HttpURLConnection connection = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, String.format("connection %s response error %d, msg %s", urlSpec, connection.getResponseCode(), connection.getResponseMessage()));
                return null;
            }
            Log.i(TAG, "content length " + connection.getContentLength());
            byteArrayOutputStream = new ByteArrayOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024];
            while ((count = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, count);
            }
            byte[] result = byteArrayOutputStream.toByteArray();
            Log.i(TAG, "resule length " + result.length);
            return result;
        } finally {
            if (connection != null)
                connection.disconnect();
            if (byteArrayOutputStream != null)
                byteArrayOutputStream.close();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
