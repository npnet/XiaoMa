package com.xiaoma.mapadapter.model;

/**
 * Created by minxiwen on 2017/12/13 0013.
 */

public enum GeoFenceAction {
    ACTION_IN(1),
    ACTION_OUT(2);

    int value;

    GeoFenceAction(int value) {
        this.value = value;
    }
}
