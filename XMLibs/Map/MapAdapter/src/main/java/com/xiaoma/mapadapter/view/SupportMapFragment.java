package com.xiaoma.mapadapter.view;

import android.support.v4.app.Fragment;

import com.xiaoma.mapadapter.interfaces.IFragment;
import com.xiaoma.mapadapter.model.MapOption;


/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class SupportMapFragment implements IFragment {
    private static IFragment realMapFragment;

    public SupportMapFragment() {
//        if (MapConstant.mapType == MapType.MAP_GD) {
//            realMapFragment = new GDSupportMapFragment();
//        }else if (MapConstant.mapType == MapType.MAP_BD){
//            realMapFragment = new BDSupportMapFragment();
//        }
    }

    @Override
    public Fragment newInstance() {
        return realMapFragment.newInstance();
    }

    @Override
    public Fragment newInstance(MapOption option) {
        return realMapFragment.newInstance(option);
    }

    @Override
    public Map getMap() {
        return realMapFragment.getMap();
    }

    @Override
    public Fragment getFragment() {
        return realMapFragment.getFragment();
    }

    public static void registerSupportMapFragment(IFragment iFragment) {
        realMapFragment = iFragment;
    }
}
