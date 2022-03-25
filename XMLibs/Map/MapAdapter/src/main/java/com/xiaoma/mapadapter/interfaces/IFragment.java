package com.xiaoma.mapadapter.interfaces;

import android.support.v4.app.Fragment;

import com.xiaoma.mapadapter.model.MapOption;
import com.xiaoma.mapadapter.view.Map;


/**
 * 封装MapView生命周期的SupportMapFragment的行为抽象
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface IFragment {
    Fragment newInstance();

    Fragment newInstance(MapOption option);

    Map getMap();

    Fragment getFragment();
}
