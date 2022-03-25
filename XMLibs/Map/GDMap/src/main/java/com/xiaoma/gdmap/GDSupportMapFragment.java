package com.xiaoma.gdmap;

import android.support.v4.app.Fragment;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.SupportMapFragment;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.IFragment;
import com.xiaoma.mapadapter.model.MapOption;
import com.xiaoma.mapadapter.view.Map;

/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class GDSupportMapFragment implements IFragment {
    private com.amap.api.maps.SupportMapFragment supportMapFragment;

    public GDSupportMapFragment() {

    }

    @Override
    public Fragment newInstance() {
        supportMapFragment = SupportMapFragment.newInstance();
        return supportMapFragment;
    }

    @Override
    public Fragment newInstance(MapOption option) {
        supportMapFragment = SupportMapFragment.newInstance((AMapOptions) MapConverter.getInstance().convertMapOption(option));
        return supportMapFragment;
    }

    @Override
    public Map getMap() {
        AMap aMap = supportMapFragment.getMap();
        return new GDMap(aMap);
    }

    @Override
    public Fragment getFragment() {
        return supportMapFragment;
    }
}
