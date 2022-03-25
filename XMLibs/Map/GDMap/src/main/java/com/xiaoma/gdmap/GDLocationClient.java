package com.xiaoma.gdmap;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.ILocation;
import com.xiaoma.mapadapter.listener.OnLocationChangeListener;
import com.xiaoma.mapadapter.model.LocationClientOption;

/**
 * Created by minxiwen on 2017/12/11 0011.
 */

public class GDLocationClient implements ILocation {
    private AMapLocationClient client;

    public GDLocationClient(Context context) {
        client = new AMapLocationClient(context);
    }

    @Override
    public void start() {
        client.startLocation();
    }

    @Override
    public void stop() {
        client.stopLocation();
    }

    @Override
    public void destroy() {
        client.onDestroy();
    }

    @Override
    public void setLocOption(LocationClientOption option) {
        client.setLocationOption((AMapLocationClientOption) MapConverter.getInstance().convertLocationOption(option));
    }

    @Override
    public void setLocationListener(final OnLocationChangeListener listener) {
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                listener.onLocationChange(MapConverter.getInstance().convertLocationInfo(aMapLocation));
            }
        });
    }
}
