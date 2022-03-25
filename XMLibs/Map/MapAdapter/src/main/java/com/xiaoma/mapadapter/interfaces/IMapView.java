package com.xiaoma.mapadapter.interfaces;

import android.os.Bundle;
import android.view.View;

import com.xiaoma.mapadapter.view.Map;


/**
 * 对MapView行为的抽象
 * Created by minxiwen on 2017/12/14 0014.
 */

public interface IMapView {
    Map getMap();

    void onCreate(Bundle bundle);

    void onSaveInstanceState(Bundle outState);

    void onResume();

    void onPause();

    void onDestroy();

    View getMapView();
}
