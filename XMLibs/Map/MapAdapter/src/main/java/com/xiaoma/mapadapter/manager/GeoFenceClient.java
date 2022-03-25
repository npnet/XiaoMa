package com.xiaoma.mapadapter.manager;

import android.content.Context;

import com.xiaoma.mapadapter.control.GeoFenceClientControl;
import com.xiaoma.mapadapter.interfaces.IGeoFence;
import com.xiaoma.mapadapter.listener.OnFenceStatusChangeListener;
import com.xiaoma.mapadapter.listener.OnGeoFenceListener;
import com.xiaoma.mapadapter.model.LatLng;


/**
 * 对上层提供的地理围栏客户端
 * Created by minxiwen on 2017/12/13 0013.
 */

public class GeoFenceClient implements IGeoFence {
    private static GeoFenceClientControl geoFenceClientControl;
    private IGeoFence realGeoFenceClient;

    public GeoFenceClient(Context context) {
        realGeoFenceClient = geoFenceClientControl.getGeoFenceClient(context);
    }

    @Override
    public void start() {
        realGeoFenceClient.start();
    }

    @Override
    public void stop() {
        realGeoFenceClient.stop();
    }

    @Override
    public void setOnFenceStatusChangeListener(OnFenceStatusChangeListener onFenceStatusChangeListener) {
        realGeoFenceClient.setOnFenceStatusChangeListener(onFenceStatusChangeListener);
    }

    @Override
    public void setGeoFenceListener(OnGeoFenceListener listener) {
        realGeoFenceClient.setGeoFenceListener(listener);
    }

    @Override
    public boolean removeGeoFence(String messageId) {
        return realGeoFenceClient.removeGeoFence(messageId);
    }

    @Override
    public void addGeoFence(LatLng latLng, float radius, String customId) {
        realGeoFenceClient.addGeoFence(latLng, radius, customId);
    }

    public static void registerGeoFenceClient(GeoFenceClientControl geoFenceClientControl1) {
        geoFenceClientControl = geoFenceClientControl1;
    }

}
