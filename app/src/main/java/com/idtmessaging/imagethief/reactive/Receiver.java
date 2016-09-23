package com.idtmessaging.imagethief.reactive;

import android.support.annotation.NonNull;

/**
 * Created by mary on 23/09/16.
 */

public interface Receiver<T> {
    void accept(@NonNull T value);
}
