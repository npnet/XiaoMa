package com.xiaoma.mapadapter.interfaces;


import com.xiaoma.mapadapter.listener.OnFenceStatusChangeListener;
import com.xiaoma.mapadapter.listener.OnGeoFenceListener;
import com.xiaoma.mapadapter.model.LatLng;

/**
 * 地理围栏客户端的行为抽象
 * Created by minxiwen on 2017/12/13 0013.
 */

public interface IGeoFence {

    void start();

    void stop();

    void setOnFenceStatusChangeListener(OnFenceStatusChangeListener onFenceStatusChangeListener);

    void setGeoFenceListener(OnGeoFenceListener listener);

    boolean removeGeoFence(String messageId);

    void addGeoFence(LatLng latLng, float radius, String customId);
}
