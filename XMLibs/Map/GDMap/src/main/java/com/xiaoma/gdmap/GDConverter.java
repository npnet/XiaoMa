package com.xiaoma.gdmap;

import com.amap.api.fence.GeoFence;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiSearch;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 针对高德地图的数据转换器
 * Created by minxiwen on 2017/12/11 0011.
 */

public class GDConverter implements IConverter {

    @Override
    public LatLng convertLatLng(Object target) {
        if (target != null) {
            com.amap.api.maps.model.LatLng latLng = (com.amap.api.maps.model.LatLng) target;
            return new LatLng(latLng.latitude, latLng.longitude);
        }
        return null;
    }

    @Override
    public LocationInfo convertLocationInfo(Object target) {
        if (target != null) {
            LocationInfo locationInfo = new LocationInfo();
            AMapLocation location = (AMapLocation) target;
            locationInfo.setLatitude(location.getLatitude());
            locationInfo.setLongitude(location.getLongitude());
            locationInfo.setAccuracy(location.getAccuracy());
            locationInfo.setAltitude(location.getAltitude());
            locationInfo.setAddress(location.getAddress());
            locationInfo.setBearing(location.getBearing());
            locationInfo.setCity(location.getCity());
            locationInfo.setDistrict(location.getDistrict());
            locationInfo.setProvince(location.getProvince());
            locationInfo.setGpsAccuracyStatus(location.getGpsAccuracyStatus());
            locationInfo.setErrorCode(location.getErrorCode());
            locationInfo.setLocationType(location.getLocationType());
            locationInfo.setSatellites(location.getSatellites());
            locationInfo.setSpeed(location.getSpeed());
            locationInfo.setErrorInfo(location.getErrorInfo());
            return locationInfo;
        } else {
            return null;
        }
    }

    @Override
    public Object convertLocationOption(LocationClientOption option) {
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        locationClientOption.setNeedAddress(option.isNeedAddress());
        locationClientOption.setGpsFirst(option.isGpsFirst());
        locationClientOption.setInterval(option.getLocationInterval());
        locationClientOption.setMockEnable(option.isMockEnable());
        if (option.getLocationMode() == LocationMode.Battery_Saving) {
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        } else if (option.getLocationMode() == LocationMode.Device_Sensors) {
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        } else {
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        }
        return locationClientOption;
    }

    @Override
    public PoiResult convertPoiResult(Object target) {
        if (target != null) {
            PoiResult poiResult = new PoiResult();
            com.amap.api.services.poisearch.PoiResult result = (com.amap.api.services.poisearch.PoiResult) target;
            List<PoiInfo> poiInfoList = new ArrayList<>();
            for (PoiItem item : result.getPois()) {
                poiInfoList.add(convertPoiInfo(item));
            }
            poiResult.setPoiInfoList(poiInfoList);
            poiResult.setPageNum(result.getQuery().getPageNum());
            return poiResult;
        }
        return null;
    }

    @Override
    public PoiInfo convertPoiInfo(Object target) {
        if (target != null) {
            PoiInfo poiInfo = new PoiInfo();
            PoiItem poiItem = (PoiItem) target;
            poiInfo.setTitle(poiItem.getTitle());
            poiInfo.setProvinceName(poiItem.getProvinceName());
            poiInfo.setCityName(poiItem.getCityName());
            poiInfo.setAdName(poiItem.getAdName());
            poiInfo.setSnippet(poiItem.getSnippet());
            poiInfo.setAddress("");
            LatLonPoint point = new LatLonPoint();
            point.setLatitude(poiItem.getLatLonPoint().getLatitude());
            point.setLongitude(poiItem.getLatLonPoint().getLongitude());
            poiInfo.setLatLonPoint(point);
            return poiInfo;
        }
        return null;
    }

    @Override
    public Object convertQueryOption(QueryOption queryOption) {
        if (queryOption != null) {
            PoiSearch.Query query = new PoiSearch.Query(queryOption.getQueryContent(), queryOption.getCategory(), queryOption.getCity());
            query.setPageNum(queryOption.getPageNum());
            query.setPageSize(queryOption.getPageSize());
            return query;
        }
        return null;
    }

    @Override
    public Object convertQueryBound(QueryBound queryBound) {
        if (queryBound != null) {
            com.amap.api.services.core.LatLonPoint point = new com.amap.api.services.core.LatLonPoint(queryBound.getCenterPoint().getLatitude(), queryBound.getCenterPoint().getLongitude());
            PoiSearch.SearchBound bound = new PoiSearch.SearchBound(point, queryBound.getRadius(), queryBound.isDistanceSort());
            return bound;
        }
        return null;
    }

    @Override
    public Object convertRegeocodeQueryOption(RegeocodeQueryOption option) {
        if (option != null) {
            com.amap.api.services.core.LatLonPoint point = new com.amap.api.services.core.LatLonPoint(option.getCenterPoint().getLatitude(), option.getCenterPoint().getLongitude());
            RegeocodeQuery regeocodeQuery = new RegeocodeQuery(point, option.getRadius(), option.getType().name());
            return regeocodeQuery;
        }
        return null;
    }

    @Override
    public RegeocodeResultInfo convertRegeocodeResult(Object target) {
        if (target != null) {
            RegeocodeResult regeocodeResult = (RegeocodeResult) target;
            RegeocodeResultInfo regeocodeResultInfo = new RegeocodeResultInfo();
            regeocodeResultInfo.setProvince(regeocodeResult.getRegeocodeAddress().getProvince());
            regeocodeResultInfo.setCity(regeocodeResult.getRegeocodeAddress().getCity());
            regeocodeResultInfo.setTownship(regeocodeResult.getRegeocodeAddress().getTownship());
            regeocodeResultInfo.setDistrict(regeocodeResult.getRegeocodeAddress().getDistrict());
            regeocodeResultInfo.setStreet(regeocodeResult.getRegeocodeAddress().getStreetNumber().getStreet());
            regeocodeResultInfo.setNumber(regeocodeResult.getRegeocodeAddress().getStreetNumber().getNumber());
            regeocodeResultInfo.setFormatAddress(regeocodeResult.getRegeocodeAddress().getFormatAddress());
            regeocodeResultInfo.setBuilding(regeocodeResult.getRegeocodeAddress().getBuilding());
//            if (!Utils.isListEmpty(regeocodeResult.getRegeocodeAddress().getPois())){
//                List<PoiInfo> poiInfoList = new ArrayList<>();
//                for (PoiItem poiItem : regeocodeResult.getRegeocodeAddress().getPois()){
//                    poiInfoList.add(convertPoiInfo(poiItem));
//                }
//                regeocodeResultInfo.setPoiInfoList(poiInfoList);
//            }
            return regeocodeResultInfo;
        }
        return null;
    }

    @Override
    public Object convertMarkerOption(MarkerOption option) {
        if (option != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new com.amap.api.maps.model.LatLng(option.getLatLng().getLatitude(), option.getLatLng().getLongitude()))
                    .title(option.getTitle())
                    .anchor(option.getAnchor1(), option.getAnchor2())
                    .icon(BitmapDescriptorFactory.fromBitmap(option.getBitmap()));
            return markerOptions;
        }
        return null;
    }

    @Override
    public Object convertMapOption(MapOption mapOption) {
        if (mapOption != null) {
            AMapOptions aMapOptions = new AMapOptions();
            CameraPosition position = new CameraPosition.Builder()
                    .target(new com.amap.api.maps.model.LatLng(mapOption.getLatitude(), mapOption.getLongitude()))
                    .zoom(mapOption.getFactor()).build();
            aMapOptions.camera(position);
            return aMapOptions;
        }
        return null;
    }

    @Override
    public Object convertCameraUpdateInfo(CameraUpdateInfo cameraUpdateInfo) {
        if (cameraUpdateInfo != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new com.amap.api.maps.model.LatLng(cameraUpdateInfo.getLatLng().getLatitude(), cameraUpdateInfo.getLatLng().getLongitude()), cameraUpdateInfo.getFactor());
            return cameraUpdate;
        }
        return null;
    }

    @Override
    public Object convertCircleOption(CircleOption circleOption) {
        if (circleOption != null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(new com.amap.api.maps.model.LatLng(circleOption.getLatLng().getLatitude(), circleOption.getLatLng().getLongitude()))
                    .radius(circleOption.getRadius())
                    .fillColor(circleOption.getFillColor())
                    .strokeColor(circleOption.getStrokeColor())
                    .strokeWidth(circleOption.getStrokeWidth());
            return circleOptions;
        }
        return null;
    }

    @Override
    public Marker convertMarker(Object target) {
        if (target != null) {
            com.amap.api.maps.model.Marker srcMarker = (com.amap.api.maps.model.Marker) target;
            Marker desMarker = new GDMarker(srcMarker);
            return desMarker;
        }
        return null;
    }

    @Override
    public GeoFenceInfo convertGeoFenceInfo(Object target) {
        if (target != null) {
            GeoFence geoFence = (GeoFence) target;
            GeoFenceInfo geoFenceInfo = new GeoFenceInfo();
            geoFenceInfo.setLatLng(new LatLng(geoFence.getCenter().getLatitude(), geoFence.getCenter().getLongitude()));
            geoFenceInfo.setCustomId(geoFence.getCustomId());
            geoFenceInfo.setRadius(geoFence.getRadius());
            return geoFenceInfo;
        }
        return null;
    }
}
