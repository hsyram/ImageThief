package com.idtmessaging.imagethief.util;

import android.graphics.Bitmap;

/**
 * Created by mary on 23/09/16.
 */

public class ImageModel {

    private String mName;
    private String mUrl;
    private Bitmap mBitmap;
    private boolean mSuccess = true;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        this.mSuccess = success;
    }
}
