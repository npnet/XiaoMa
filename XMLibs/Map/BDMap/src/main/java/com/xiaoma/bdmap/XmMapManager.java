package com.xiaoma.bdmap;

import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.manager.GeoFenceClient;
import com.xiaoma.mapadapter.manager.GeocodeSearch;
import com.xiaoma.mapadapter.manager.LocationClient;
import com.xiaoma.mapadapter.manager.MapSdkManager;
import com.xiaoma.mapadapter.manager.PoiSearch;
import com.xiaoma.mapadapter.view.MapView;
import com.xiaoma.mapadapter.view.SupportMapFragment;

/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class XmMapManager {
    private static final XmMapManager ourInstance = new XmMapManager();

    public static XmMapManager getInstance() {
        return ourInstance;
    }

    private XmMapManager() {
    }

    public void init() {
        MapConverter.registerMapConverter(new BDConverter());
        GeocodeSearch.register(new BDGeocodeSearchControl());
        GeoFenceClient.registerGeoFenceClient(new BDGeoFenceClientControl());
        LocationClient.registerLocationClient(new BDLocationClientControl());
        MapSdkManager.registerMapSdkManager(new BDSdkManager());
        PoiSearch.registerPoiSearch(new BDPoiSearchControl());
        MapView.registerMapView(new BDMapViewControl());
        SupportMapFragment.registerSupportMapFragment(new BDSupportMapFragment());
    }
}
