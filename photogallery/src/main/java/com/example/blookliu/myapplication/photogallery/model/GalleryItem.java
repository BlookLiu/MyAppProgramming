package com.example.blookliu.myapplication.photogallery.model;

/**
 * Created by BlookLiu on 2015/11/10.
 */
public class GalleryItem {
    private String mCatption;
    private String mId;
    private String mUrl;
    private String mOwner;
    public GalleryItem(String catption, String id, String url, String owner) {
        mCatption = catption;
        mId = id;
        mUrl = url;
        mOwner = owner;
    }

    public String getCatption() {
        return mCatption;
    }

    public void setCatption(String catption) {
        mCatption = catption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getPhotoPageUrl(){
        return String.format("http://www.flickr.com/photos/%s/%s",mOwner, mId);
    }
    @Override
    public String toString() {
        return mCatption + "\t" + mId + "\t" + mUrl;
    }
}
