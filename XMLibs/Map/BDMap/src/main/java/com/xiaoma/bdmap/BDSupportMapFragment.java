package com.xiaoma.bdmap;

import android.support.v4.app.Fragment;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.IFragment;
import com.xiaoma.mapadapter.model.MapOption;
import com.xiaoma.mapadapter.view.Map;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDSupportMapFragment implements IFragment {
    private com.baidu.mapapi.map.SupportMapFragment supportMapFragment;

    public BDSupportMapFragment() {

    }

    @Override
    public Fragment newInstance() {
        supportMapFragment = SupportMapFragment.newInstance();
        return supportMapFragment;
    }

    @Override
    public Fragment newInstance(MapOption option) {
        supportMapFragment = SupportMapFragment.newInstance((BaiduMapOptions) MapConverter.getInstance().convertMapOption(option));
        return supportMapFragment;
    }

    @Override
    public Map getMap() {
        return new BDMap(supportMapFragment.getBaiduMap());
    }

    @Override
    public Fragment getFragment() {
        return supportMapFragment;
    }
}
