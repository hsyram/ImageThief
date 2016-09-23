package com.idtmessaging.imagethief.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import com.idtmessaging.imagethief.ImageThiefApp;

/**
 * Created by mary on 23/09/16.
 */

public class ImageModel {
    public static final String SP_NAME = "ImageThief";

    public ImageModel() {
    }

    public ImageModel(Context context, String mUrl) {
        this.mUrl = mUrl;
        setImageUriFromUrl(context);
        this.mBitmap = ((ImageThiefApp) context).getBitmapFromMemCache(mUrl);
    }

    private Uri mUri;
    private String mUrl;
    private Bitmap mBitmap;
    private boolean mSuccess = true;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Context context,Bitmap bitmap) {
        this.mBitmap = bitmap;

        if(mUrl != null) {
            ((ImageThiefApp) context).addBitmapToMemoryCache(mUrl, mBitmap);
        }
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        this.mSuccess = success;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Context context, String filePath) {
        this.mUri = Util.getImageContentUri(context, filePath);

        if(getUrl() != null) {
            SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getUrl(), filePath);
            editor.apply();
        }

    }

    public void setImageUriFromUrl(Context context){
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        String filePath = preferences.getString(mUrl,"");
        if(!filePath.equals("")){
            mUri = Util.getImageContentUri(context,filePath);
        }
    }
}
