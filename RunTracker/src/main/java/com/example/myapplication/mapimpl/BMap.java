package com.example.myapplication.mapimpl;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.myapplication.basemap.IMap;

/**
 * Created by liuxi on 2016/1/3.
 */
public class BMap implements IMap<BaiduMap> {
    private MapView mMapView;
    private BaiduMap mMap;
//    private IMapView.IOnReadyMapCallback mCallback;

    public BMap(MapView mapView) {
        mMapView = mapView;
//        mCallback = callback;
    }

    @Override
    public void enableMyLocation(boolean b) {
        mMap.setMyLocationEnabled(b);
    }

    public void loadMap() {
        mMap = mMapView.getMap();
    }

    @Override
    public BaiduMap getMap() {
        if (mMap == null) {
            mMap = mMapView.getMap();
        }
        return mMap;
    }
}
