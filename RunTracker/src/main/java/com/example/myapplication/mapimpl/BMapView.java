package com.example.myapplication.mapimpl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.Display;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.myapplication.R;
import com.example.myapplication.basemap.IMap;
import com.example.myapplication.basemap.IMapView;
import com.example.myapplication.db.RunDatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 百度MapView
 * Created by liuxi on 2016/1/3.
 */
public class BMapView implements IMapView, BaiduMap.OnMapLoadedCallback {
    private static final String TAG = "BMapView";
    private MapView mMapView;
    private Activity mActivity;
    private IMap mMap;
    private BaiduMap mBaiduMap;
    private boolean mIsMapLoaded = false;
    //    private RunDatabaseHelper.LocationCursor mCursor;
    private IOnReadyMapCallback mCallback;

    public BMapView(Activity activity) {
        mActivity = activity;
        BaiduMapOptions options = new BaiduMapOptions();
//        MapStatus ms = new MapStatus.Builder().zoom(20).build();
        options.compassEnabled(true).zoomControlsEnabled(false).overlookingGesturesEnabled(false);
        mMapView = new MapView(activity, options);
        mMap = new BMap(mMapView);
    }

    public BMapView(Activity activity, BaiduMapOptions options) {
        mActivity = activity;
        mMapView = new MapView(activity, options);
        mMap = new BMap(mMapView);
    }

    public BMapView(Context context, MapView mapView) {
        mMapView = mapView;
    }

    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public void getAsyncMap(IOnReadyMapCallback callback) {
        if (callback != null) {
            // baidu地图没有异步方法，不需要回调
            mBaiduMap = (BaiduMap) mMap.getMap();
            mBaiduMap.setOnMapLoadedCallback(this);
            mCallback = callback;
//            callback.onReadyMap(mMap);
        }

    }

    @Override
    public void enableMyLocation(boolean enabled) {
        Log.d(TAG, "my location " + enabled);
        mBaiduMap.setMyLocationEnabled(enabled);
    }

    @Override
    public void updateUI(RunDatabaseHelper.LocationCursor cursor) {
        if (!mIsMapLoaded || mBaiduMap == null || cursor == null || cursor.isClosed()) {
            Log.w(TAG, String.format("can not update UI, map: %s, cursor: %s, isClosed: %s", mBaiduMap,
                    cursor, cursor != null && cursor.isClosed()));
            return;
        }
        mBaiduMap.clear();
        PolylineOptions polylineOptions = new PolylineOptions();

        List<LatLng> points = new ArrayList<>();
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        Log.d(TAG, "cursor count: " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Location location = cursor.getLocation();
            LatLng p = new LatLng(location.getLatitude(), location.getLongitude());
            if (cursor.isFirst()) {
                String startDate = new Date(location.getTime()).toString();
                MarkerOptions startMarkerOptions = new MarkerOptions().position(p).title
                        (mActivity.getString(R.string.run_start)).icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_gcoding));
                mBaiduMap.addOverlay(startMarkerOptions);
            } else if (cursor.isLast()) {
                String endDate = new Date(location.getTime()).toString();
                MarkerOptions endMarkerOptions = new MarkerOptions().position(p).title(mActivity
                        .getString(R.string.run_finish)).icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_gcoding));
                mBaiduMap.addOverlay(endMarkerOptions);
            }
            points.add(p);
            latLngBuilder.include(p);
            cursor.moveToNext();
        }
        /*LatLng t = new LatLng(0, 0);
        points.add(t);
        latLngBuilder.include(t);*/
        if (points.size() > 1) {
            polylineOptions.points(points).width(10).color(0xAAFF0000);
            mBaiduMap.addOverlay(polylineOptions);
        }
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Log.d(TAG, String.format("width: %d, height: %d", point.x, point.y));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBuilder.build(),
                point.x, point.y));
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(20));
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "on map loaded");
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(20));
        mIsMapLoaded = true;
        mCallback.onReadyMap(mMap);
    }
}
