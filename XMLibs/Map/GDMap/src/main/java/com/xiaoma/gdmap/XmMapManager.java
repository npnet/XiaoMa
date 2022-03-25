package com.xiaoma.gdmap;

import android.content.Context;

import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.manager.GeoFenceClient;
import com.xiaoma.mapadapter.manager.GeocodeSearch;
import com.xiaoma.mapadapter.manager.LocationClient;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.manager.MapSdkManager;
import com.xiaoma.mapadapter.manager.PoiSearch;
import com.xiaoma.mapadapter.view.MapView;
import com.xiaoma.mapadapter.view.TextrueMapView;
import com.xiaoma.mapadapter.view.SupportMapFragment;

/**
 * Created by minxiwen on 2017/12/15 0015.
 */

public class XmMapManager {

    private static final XmMapManager ourInstance = new XmMapManager();

    public static XmMapManager getInstance() {
        return ourInstance;
    }

    private XmMapManager() {
    }

    /**
     * 需登录成功后初始化定位功能
     * AndroidManifest.xml根据自己包名配置高德或者百度开发key，和需要的相关权限，参考相关App集成
     * @param context
     * @param loginUserId
     */
    public void init(Context context, String loginUserId) {
        MapConverter.registerMapConverter(new GDConverter());
        GeocodeSearch.register(new GDGeocodeSearchControl());
        GeoFenceClient.registerGeoFenceClient(new GDGeoFenceClientControl());
        LocationClient.registerLocationClient(new GDLocationClientControl());
        MapSdkManager.registerMapSdkManager(new GDSdkManager());
        PoiSearch.registerPoiSearch(new GDPoiSearchControl());
        MapView.registerMapView(new GDMapViewControl());
        TextrueMapView.registerMapView(new GDMapViewControl());
        SupportMapFragment.registerSupportMapFragment(new GDSupportMapFragment());
        LocationManager.getInstance().init(context, loginUserId);
    }

}
