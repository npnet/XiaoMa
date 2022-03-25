package com.xiaoma.mapadapter.utils;

import com.xiaoma.mapadapter.model.LatLng;

/**
 * 地图相关工具类
 * Created by minxiwen on 2017/12/11 0011.
 */

public class MapUtil {

    private static final double EARTH_RADIUS = 6378137.0;

    /**
     * 计算两点之间距离的
     *
     * @param firstPoint
     * @param secondPoint
     * @return
     */
    public static double calculateLineDistance(LatLng firstPoint, LatLng secondPoint) {
        return getPointsDistance(firstPoint.longitude, firstPoint.latitude, secondPoint.longitude, secondPoint.latitude);
//        if (MapConstant.mapType == MapType.MAP_GD) {
//            com.amap.api.maps2d.model.LatLng latLng1 = new com.amap.api.maps2d.model.LatLng(firstPoint.getLatitude(), firstPoint.getLongitude());
//            com.amap.api.maps2d.model.LatLng latLng2 = new com.amap.api.maps2d.model.LatLng(secondPoint.getLatitude(), secondPoint.getLongitude());
//            return AMapUtils.calculateLineDistance(latLng1, latLng2);
//        } else if (MapConstant.mapType == MapType.MAP_BD) {
//            com.baidu.mapapi.model.LatLng latLng1 = new com.baidu.mapapi.model.LatLng(firstPoint.latitude, firstPoint.longitude);
//            com.baidu.mapapi.model.LatLng latLng2 = new com.baidu.mapapi.model.LatLng(secondPoint.latitude, secondPoint.longitude);
//            return DistanceUtil.getDistance(latLng1, latLng2);
//        }
//        return 0;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getPointsDistance(double longitude1, double latitude1,
                                           double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Math.abs(Lat1 - Lat2);
        double b = Math.abs(rad(longitude1) - rad(longitude2));
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
