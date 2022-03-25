package com.xiaoma.mapadapter.interfaces;


import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.CircleOption;
import com.xiaoma.mapadapter.model.GeoFenceInfo;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationClientOption;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.MapOption;
import com.xiaoma.mapadapter.model.MarkerOption;
import com.xiaoma.mapadapter.model.PoiInfo;
import com.xiaoma.mapadapter.model.PoiResult;
import com.xiaoma.mapadapter.model.QueryBound;
import com.xiaoma.mapadapter.model.QueryOption;
import com.xiaoma.mapadapter.model.RegeocodeQueryOption;
import com.xiaoma.mapadapter.model.RegeocodeResultInfo;
import com.xiaoma.mapadapter.view.Marker;

/**
 * 地图定位搜索数据转换接口
 * Created by minxiwen on 2017/12/11 0011.
 */

public interface IConverter<T> {

    LatLng convertLatLng(T target);

    LocationInfo convertLocationInfo(T target);

    T convertLocationOption(LocationClientOption option);

    PoiResult convertPoiResult(T target);

    PoiInfo convertPoiInfo(T target);

    T convertQueryOption(QueryOption queryOption);

    T convertQueryBound(QueryBound queryBound);

    T convertRegeocodeQueryOption(RegeocodeQueryOption option);

    RegeocodeResultInfo convertRegeocodeResult(T target);

    T convertMarkerOption(MarkerOption option);

    T convertMapOption(MapOption mapOption);

    T convertCameraUpdateInfo(CameraUpdateInfo cameraUpdateInfo);

    T convertCircleOption(CircleOption circleOption);

    Marker convertMarker(T target);

    GeoFenceInfo convertGeoFenceInfo(T target);
}
