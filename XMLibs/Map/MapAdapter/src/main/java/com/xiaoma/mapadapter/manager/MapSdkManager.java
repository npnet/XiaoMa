package com.xiaoma.mapadapter.manager;

import android.content.Context;

import com.xiaoma.mapadapter.interfaces.ISdkInitializer;


/**
 * 地图初始化管理器
 * Created by minxiwen on 2017/12/13 0013.
 */

public class MapSdkManager implements ISdkInitializer {
    private static ISdkInitializer realSdkInitializer;

    public MapSdkManager() {
//        if (MapConstant.mapType == MapType.MAP_GD){
//            realSdkInitializer = new GDSdkManager();
//        }else if (MapConstant.mapType == MapType.MAP_BD){
////            realSdkInitializer = new BDSdkManager();
//        }
    }


    @Override
    public void init(Context context) {
        realSdkInitializer.init(context);
    }

    public static void registerMapSdkManager(ISdkInitializer iSdkInitializer) {
        realSdkInitializer = iSdkInitializer;
    }
}
