package com.xiaoma.gdmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.xiaoma.mapadapter.constant.MapConstant;
import com.xiaoma.mapadapter.interfaces.IGeoFence;
import com.xiaoma.mapadapter.listener.OnFenceStatusChangeListener;
import com.xiaoma.mapadapter.listener.OnGeoFenceListener;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * Created by minxiwen on 2017/12/13 0013.
 */

public class GDGeoFenceClient implements IGeoFence {
    //定义接收广播的action字符串
    private static final String GEO_FENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
    private com.amap.api.fence.GeoFenceClient geoFenceClient;
    private OnFenceStatusChangeListener onFenceStatusChangeListener;
    private Context context;
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GEO_FENCE_BROADCAST_ACTION)) {
                //解析广播内容
                Bundle bundle = intent.getExtras();
                //获取自定义的围栏标识：
                String customId = bundle.getString(MapConstant.BUNDLE_KEY_CUSTOMID);
                //获取围栏行为：
                int status = bundle.getInt(MapConstant.BUNDLE_KEY_FENCESTATUS);
                KLog.d("customId = " + customId + "status = " + status);
                if (onFenceStatusChangeListener != null) {
                    onFenceStatusChangeListener.onFenceStatusChange(status, customId);
                }
            }
        }
    };

    public GDGeoFenceClient(Context context) {
        this.context = context;
        geoFenceClient = new GeoFenceClient(context);
    }

    @Override
    public void start() {
        geoFenceClient.setActivateAction(MapConstant.GEO_FENCE_IN | MapConstant.GEO_FENCE_OUT);
        //创建并设置PendingIntent
        geoFenceClient.createPendingIntent(GEO_FENCE_BROADCAST_ACTION);
        IntentFilter filter = new IntentFilter();
        filter.addAction(GEO_FENCE_BROADCAST_ACTION);
        context.registerReceiver(mGeoFenceReceiver, filter);
    }

    @Override
    public void stop() {
        context.unregisterReceiver(mGeoFenceReceiver);
        geoFenceClient.removeGeoFence();
    }

    @Override
    public void setOnFenceStatusChangeListener(OnFenceStatusChangeListener onFenceStatusChangeListener) {
        this.onFenceStatusChangeListener = onFenceStatusChangeListener;
    }

    @Override
    public void setGeoFenceListener(final OnGeoFenceListener listener) {
        geoFenceClient.setGeoFenceListener(new GeoFenceListener() {
            @Override
            public void onGeoFenceCreateFinished(List<GeoFence> list, int code, String msg) {
                int resultCode;
                if (code == GeoFence.ADDGEOFENCE_SUCCESS) {
                    resultCode = MapConstant.CREATE_GEOFENCE_SUCCESS;
                } else {
                    resultCode = MapConstant.CREATE_GEOFENCE_FAIL;
                }
                listener.onGeoFenceCreateFinished(resultCode, msg);
            }
        });
    }

    @Override
    public boolean removeGeoFence(String messageId) {
        List<GeoFence> geoFences = geoFenceClient.getAllGeoFence();
        if (ListUtils.isEmpty(geoFences)) {
            return false;
        }
        GeoFence target = null;
        for (GeoFence geoFence : geoFences) {
            if (messageId.equals(geoFence.getCustomId())) {
                target = geoFence;
                break;
            }
        }
        return geoFenceClient.removeGeoFence(target);
    }

    @Override
    public void addGeoFence(LatLng latLng, float radius, String customId) {
        geoFenceClient.addGeoFence(new DPoint(latLng.getLatitude(), latLng.getLongitude()), radius, customId);
    }
}
