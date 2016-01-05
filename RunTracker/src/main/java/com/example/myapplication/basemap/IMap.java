package com.example.myapplication.basemap;

/**
 * Created by liuxi on 2016/1/3.
 */
public interface IMap<T> {
    void enableMyLocation(boolean b);

    void loadMap();

    T getMap();
}
