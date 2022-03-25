package com.xiaoma.mapadapter.convert;

import com.xiaoma.mapadapter.interfaces.IConverter;
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
 * 对外提供统一的数据转换器
 * Created by minxiwen on 2017/12/11 0011.
 */

public class MapConverter implements IConverter {
    private static IConverter iConverter;
    private static MapConverter instance;

    public static MapConverter getInstance() {
        if (instance == null) {
            instance = new MapConverter();
        }
        return instance;
    }

    private MapConverter() {

    }

    @Override
    public LatLng convertLatLng(Object target) {
        return iConverter.convertLatLng(target);
    }

    @Override
    public LocationInfo convertLocationInfo(Object target) {
        return iConverter.convertLocationInfo(target);
    }

    @Override
    public Object convertLocationOption(LocationClientOption option) {
        return iConverter.convertLocationOption(option);
    }

    @Override
    public PoiResult convertPoiResult(Object target) {
        return iConverter.convertPoiResult(target);
    }

    @Override
    public PoiInfo convertPoiInfo(Object target) {
        return iConverter.convertPoiInfo(target);
    }

    @Override
    public Object convertQueryOption(QueryOption queryOption) {
        return iConverter.convertQueryOption(queryOption);
    }

    @Override
    public Object convertQueryBound(QueryBound queryBound) {
        return iConverter.convertQueryBound(queryBound);
    }

    @Override
    public Object convertRegeocodeQueryOption(RegeocodeQueryOption option) {
        return iConverter.convertRegeocodeQueryOption(option);
    }

    @Override
    public RegeocodeResultInfo convertRegeocodeResult(Object target) {
        return iConverter.convertRegeocodeResult(target);
    }

    @Override
    public Object convertMarkerOption(MarkerOption option) {
        return iConverter.convertMarkerOption(option);
    }

    @Override
    public Object convertMapOption(MapOption mapOption) {
        return iConverter.convertMapOption(mapOption);
    }

    @Override
    public Object convertCameraUpdateInfo(CameraUpdateInfo cameraUpdateInfo) {
        return iConverter.convertCameraUpdateInfo(cameraUpdateInfo);
    }

    @Override
    public Object convertCircleOption(CircleOption circleOption) {
        return iConverter.convertCircleOption(circleOption);
    }

    @Override
    public Marker convertMarker(Object target) {
        return iConverter.convertMarker(target);
    }

    @Override
    public GeoFenceInfo convertGeoFenceInfo(Object target) {
        return iConverter.convertGeoFenceInfo(target);
    }

    public static void registerMapConverter(IConverter iConverter) {
        MapConverter.iConverter = iConverter;
    }
}
