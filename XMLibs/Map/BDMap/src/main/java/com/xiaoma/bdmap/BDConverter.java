package com.xiaoma.bdmap;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.xiaoma.mapadapter.interfaces.IConverter;
import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.CircleOption;
import com.xiaoma.mapadapter.model.GeoFenceInfo;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LatLonPoint;
import com.xiaoma.mapadapter.model.LocationClientOption;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.LocationMode;
import com.xiaoma.mapadapter.model.MapOption;
import com.xiaoma.mapadapter.model.MarkerOption;
import com.xiaoma.mapadapter.model.PoiInfo;
import com.xiaoma.mapadapter.model.PoiResult;
import com.xiaoma.mapadapter.model.QueryBound;
import com.xiaoma.mapadapter.model.QueryOption;
import com.xiaoma.mapadapter.model.RegeocodeQueryOption;
import com.xiaoma.mapadapter.model.RegeocodeResultInfo;
import com.xiaoma.mapadapter.view.Marker;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDConverter implements IConverter {
    @Override
    public LatLng convertLatLng(Object target) {
        if (target == null) {
            return null;
        }
        com.baidu.mapapi.model.LatLng srcLatLng = (com.baidu.mapapi.model.LatLng) target;
        LatLng latLng = new LatLng(srcLatLng.latitude, srcLatLng.longitude);
        return latLng;
    }

    @Override
    public LocationInfo convertLocationInfo(Object target) {
        if (target == null) {
            return null;
        }
        BDLocation location = (BDLocation) target;
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLatitude(location.getLatitude());
        locationInfo.setLongitude(location.getLongitude());
        locationInfo.setAccuracy(location.getRadius());
        locationInfo.setAltitude(location.getAltitude());
        locationInfo.setAddress(location.getAddrStr());
        locationInfo.setBearing(location.getDirection());
        locationInfo.setCity(location.getCity());
        locationInfo.setDistrict(location.getDistrict());
        locationInfo.setProvince(location.getProvince());
        locationInfo.setGpsAccuracyStatus(location.getGpsAccuracyStatus());
        locationInfo.setErrorCode(0);
        locationInfo.setLocationType(location.getLocType());
        locationInfo.setSatellites(location.getSatelliteNumber());
        locationInfo.setSpeed(location.getSpeed());
        locationInfo.setErrorInfo("");
        return locationInfo;
    }

    @Override
    public Object convertLocationOption(LocationClientOption option) {
        com.baidu.location.LocationClientOption locationClientOption = new com.baidu.location.LocationClientOption();
        locationClientOption.setIsNeedAddress(option.isNeedAddress());
        locationClientOption.setOpenGps(option.isGpsFirst());
        locationClientOption.setScanSpan((int) option.getLocationInterval());
        locationClientOption.setEnableSimulateGps(option.isMockEnable());
        if (option.getLocationMode() == LocationMode.Hight_Accuracy) {
            locationClientOption.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);
        } else if (option.getLocationMode() == LocationMode.Battery_Saving) {
            locationClientOption.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Battery_Saving);
        } else if (option.getLocationMode() == LocationMode.Device_Sensors) {
            locationClientOption.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Device_Sensors);
        }
        return locationClientOption;
    }

    @Override
    public PoiResult convertPoiResult(Object target) {
        if (target == null) {
            return null;
        }
        PoiResult poiResult = new PoiResult();
        com.baidu.mapapi.search.poi.PoiResult result = (com.baidu.mapapi.search.poi.PoiResult) target;
        if (!ListUtils.isEmpty(result.getAllPoi())) {
            List<PoiInfo> poiInfoList = new ArrayList<>();
            for (com.baidu.mapapi.search.core.PoiInfo item : result.getAllPoi()) {
                poiInfoList.add(convertPoiInfo(item));
            }
            poiResult.setPoiInfoList(poiInfoList);
        }
        poiResult.setPageNum(result.getCurrentPageNum());
        return poiResult;
    }

    @Override
    public PoiInfo convertPoiInfo(Object target) {
        if (target == null) {
            return null;
        }
        PoiInfo poiInfo = new PoiInfo();
        com.baidu.mapapi.search.core.PoiInfo poiItem = (com.baidu.mapapi.search.core.PoiInfo) target;
        poiInfo.setTitle(poiItem.name);
        poiInfo.setProvinceName("");
        poiInfo.setCityName(poiItem.city);
        poiInfo.setAdName("");
        poiInfo.setSnippet("");
        poiInfo.setAddress(poiItem.address);
        LatLonPoint point = new LatLonPoint();
        point.setLatitude(poiItem.location.latitude);
        point.setLongitude(poiItem.location.longitude);
        poiInfo.setLatLonPoint(point);
        return poiInfo;
    }

    @Override
    public Object convertQueryOption(QueryOption queryOption) {
        if (queryOption == null) {
            return null;
        }
        PoiCitySearchOption option = new PoiCitySearchOption();
        option.keyword(queryOption.getQueryContent())
                .city(queryOption.getCity())
                .pageCapacity(queryOption.getPageSize())
                .pageNum(queryOption.getPageNum());
        return option;
    }

    @Override
    public Object convertQueryBound(QueryBound queryBound) {
        return null;
    }

    @Override
    public Object convertRegeocodeQueryOption(RegeocodeQueryOption option) {
        if (option == null) {
            return null;
        }
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(new com.baidu.mapapi.model.LatLng(option.getCenterPoint().getLatitude(), option.getCenterPoint().getLongitude()));
        return reverseGeoCodeOption;
    }

    @Override
    public RegeocodeResultInfo convertRegeocodeResult(Object target) {
        if (target == null) {
            return null;
        }
        ReverseGeoCodeResult reverseGeoCodeResult = (ReverseGeoCodeResult) target;
        RegeocodeResultInfo regeocodeResultInfo = new RegeocodeResultInfo();
        if (reverseGeoCodeResult.getAddressDetail() != null) {
            regeocodeResultInfo.setProvince(reverseGeoCodeResult.getAddressDetail().province);
            regeocodeResultInfo.setCity(reverseGeoCodeResult.getAddressDetail().city);
            regeocodeResultInfo.setTownship(reverseGeoCodeResult.getAddressDetail().countryName);
            regeocodeResultInfo.setDistrict(reverseGeoCodeResult.getAddressDetail().district);
            regeocodeResultInfo.setStreet(reverseGeoCodeResult.getAddressDetail().street);
            regeocodeResultInfo.setNumber(reverseGeoCodeResult.getAddressDetail().streetNumber);
        }
        regeocodeResultInfo.setFormatAddress(reverseGeoCodeResult.getAddress());
        if (!ListUtils.isEmpty(reverseGeoCodeResult.getPoiList())) {
            List<PoiInfo> poiInfoList = new ArrayList<>();
            for (com.baidu.mapapi.search.core.PoiInfo poiItem : reverseGeoCodeResult.getPoiList()) {
                poiInfoList.add(convertPoiInfo(poiItem));
            }
            regeocodeResultInfo.setPoiInfoList(poiInfoList);
        }
        return regeocodeResultInfo;
    }

    @Override
    public Object convertMarkerOption(MarkerOption option) {
        if (option == null) {
            return null;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new com.baidu.mapapi.model.LatLng(option.getLatLng().getLatitude(), option.getLatLng().getLongitude()))
                .title(option.getTitle())
                .anchor(option.getAnchor1(), option.getAnchor2())
                .icon(BitmapDescriptorFactory.fromBitmap(option.getBitmap()));
        return markerOptions;
    }

    @Override
    public Object convertMapOption(MapOption mapOption) {
        if (mapOption == null) {
            return null;
        }
        BaiduMapOptions baiduMapOptions = new BaiduMapOptions();
        MapStatus mapStatus = new MapStatus.Builder()
                .target(new com.baidu.mapapi.model.LatLng(mapOption.getLatitude(), mapOption.getLongitude()))
                .zoom(mapOption.getFactor()).build();
        baiduMapOptions.mapStatus(mapStatus);
        return baiduMapOptions;
    }


    @Override
    public Object convertCameraUpdateInfo(CameraUpdateInfo cameraUpdateInfo) {
        if (cameraUpdateInfo == null) {
            return null;
        }
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(
                new com.baidu.mapapi.model.LatLng(cameraUpdateInfo.getLatLng().latitude, cameraUpdateInfo.getLatLng().longitude), cameraUpdateInfo.getFactor());
        return mapStatusUpdate;
    }

    @Override
    public Object convertCircleOption(CircleOption circleOption) {
        if (circleOption == null) {
            return null;
        }
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(new com.baidu.mapapi.model.LatLng(circleOption.getLatLng().getLatitude(), circleOption.getLatLng().getLongitude()))
                .radius(circleOption.getRadius())
                .fillColor(circleOption.getFillColor())
                .stroke(new Stroke((int) circleOption.getStrokeWidth(), circleOption.getStrokeColor()));
        return circleOptions;
    }

    @Override
    public Marker convertMarker(Object target) {
        if (target == null) {
            return null;
        }
        com.baidu.mapapi.map.Marker srcMarker = (com.baidu.mapapi.map.Marker) target;
        Marker desMarker = new BDMarker(srcMarker);
        return desMarker;
    }

    @Override
    public GeoFenceInfo convertGeoFenceInfo(Object target) {
        return null;
    }
}
