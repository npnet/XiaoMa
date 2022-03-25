package com.xiaoma.mapadapter.manager;

import android.content.Context;

import com.xiaoma.mapadapter.control.LocationClientControl;
import com.xiaoma.mapadapter.interfaces.ILocation;
import com.xiaoma.mapadapter.listener.OnLocationChangeListener;
import com.xiaoma.mapadapter.model.LocationClientOption;


/**
 * 提供给上层使用的定位客户端
 * Created by minxiwen on 2017/12/11 0011.
 */

public class LocationClient implements ILocation {
    private static LocationClientControl locationClientControl;
    private ILocation realLocationClient;

    public LocationClient(Context context) {
        realLocationClient = locationClientControl.getLocationClient(context);
//        if (MapConstant.mapType == MapType.MAP_GD) {
//            realLocationClient = new GDLocationClient(context);
//        } else if (MapConstant.mapType == MapType.MAP_BD){
////            realLocationClient = new BDLocationClient(context);
//        }
    }

    @Override
    public void start() {
        realLocationClient.start();
    }

    @Override
    public void stop() {
        realLocationClient.stop();
    }

    @Override
    public void destroy() {
        realLocationClient.destroy();
    }

    @Override
    public void setLocOption(LocationClientOption option) {
        realLocationClient.setLocOption(option);
    }

    @Override
    public void setLocationListener(OnLocationChangeListener listener) {
        realLocationClient.setLocationListener(listener);
    }

    public static void registerLocationClient(LocationClientControl locationClientControl1) {
        locationClientControl = locationClientControl1;
    }
}
