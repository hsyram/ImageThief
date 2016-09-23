package com.idtmessaging.imagethief.reactive;

/**
 * Created by mary on 23/09/16.
 */

public interface Observable {
    void addUpdatable(Updatable updatable);

    void removeUpdatable(Updatable updatable);
}
