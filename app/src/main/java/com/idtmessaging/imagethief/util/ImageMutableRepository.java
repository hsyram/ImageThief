package com.idtmessaging.imagethief.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.idtmessaging.imagethief.reactive.Observable;
import com.idtmessaging.imagethief.reactive.Receiver;
import com.idtmessaging.imagethief.reactive.Supplier;
import com.idtmessaging.imagethief.reactive.Updatable;

import java.util.HashSet;


/**
 * Created by mary on 23/09/16.
 */

public class ImageMutableRepository implements Observable, Supplier<ImageModel>, Receiver<ImageModel> {
    private static final String TAG = "ImageMutableRepository";
    private ImageModel mImageModel;
    private HashSet<Updatable> mUpdatables = new HashSet<>();

    private static ImageMutableRepository sRepository;

    public static ImageMutableRepository getInstance(){
        if(sRepository == null){
            sRepository = new ImageMutableRepository();
        }
        return sRepository;
    }

    private ImageMutableRepository(){}


    @Override
    public void addUpdatable(@NonNull Updatable updatable) {
        Log.e(TAG,"addUpdatable");
        mUpdatables.add(updatable);
    }

    @Override
    public void removeUpdatable(@NonNull Updatable updatable) {
        Log.e(TAG,"removeUpdatable");
        if(mUpdatables.contains(updatable)) {
            mUpdatables.remove(updatable);
        }
    }

    @Override
    public void accept(@NonNull ImageModel value) {
        Log.e(TAG,"accept");
        mImageModel = value;
        for (Updatable updatable: mUpdatables){
            updatable.update();
        }

    }

    @NonNull
    @Override
    public ImageModel get() {
        return mImageModel;
    }

}
