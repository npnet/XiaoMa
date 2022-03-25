package com.xiaoma.bdmap;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.ILocation;
import com.xiaoma.mapadapter.listener.OnLocationChangeListener;
import com.xiaoma.mapadapter.model.LocationClientOption;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDLocationClient implements ILocation {
    private com.baidu.location.LocationClient locationClient;

    public BDLocationClient(Context context) {
        locationClient = new LocationClient(context);
    }

    @Override
    public void start() {
        locationClient.start();
    }

    @Override
    public void stop() {
        locationClient.stop();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setLocOption(LocationClientOption option) {
        locationClient.setLocOption((com.baidu.location.LocationClientOption) MapConverter.getInstance().convertLocationOption(option));
    }

    @Override
    public void setLocationListener(final OnLocationChangeListener listener) {
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                listener.onLocationChange(MapConverter.getInstance().convertLocationInfo(bdLocation));
            }
        });
    }
}
