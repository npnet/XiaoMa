package com.xiaoma.mapadapter.constant;

import com.xiaoma.mapadapter.model.MapType;

/**
 * Created by minxiwen on 2017/12/11 0011.
 */

public class MapConstant {
    public static final MapType mapType = MapType.MAP_GD;
    public static final String GPS = "gps";
    public static final String AMAP = "autonavi";
    public static final int SEARCH_SUCCESS = 1000;
    public static final int SEARCH_FAIL = 1001;
    public static final int SEARCH_NO_NETWORK = 1002;
    public static final int CREATE_GEOFENCE_SUCCESS = 1;
    public static final int CREATE_GEOFENCE_FAIL = -1;
    public static final String BUNDLE_KEY_CUSTOMID = "customId";
    public static final String BUNDLE_KEY_FENCESTATUS = "event";
    public static final int GEO_FENCE_IN = 1;
    public static final int GEO_FENCE_OUT = 2;
    public static final String BUNDLE_KEY_MARKER_EXTRA_INFO = "extra_info";
    public static final int EACH_UPDATE_SIZE = 20;
}
