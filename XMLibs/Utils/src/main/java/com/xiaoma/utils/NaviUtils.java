package com.xiaoma.utils;

/**
 * Created by vincenthu on 2017/10/22.
 * @deprecated 此类已废弃,使用的高德导航; 项目里需要用四维导航: XmMapNaviManager
 */

@Deprecated
public class NaviUtils {

//    public static final String NAVI_PACKAGE_NAME = "com.autonavi.amapauto";
//    public static final String NAVI_ACTION = "AUTONAVI_STANDARD_BROADCAST_RECV";
//
//    public static boolean launchNavi(Context context) {
//        if (!AppUtils.isAppInstalled(context, NAVI_PACKAGE_NAME)) {
//            return false;
//        }
//        LaunchUtils.launchApp(context, NAVI_PACKAGE_NAME);
//        int KEY_TYPE = 10034;
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", KEY_TYPE);
//        intent.putExtra("SOURCE_APP", "Navi");
//        context.sendBroadcast(intent);
//        return true;
//    }
//
//    public static boolean launchNavi(Context context, double lat, double lon) {
//        if (!AppUtils.isAppInstalled(context, NAVI_PACKAGE_NAME)) {
//            return false;
//        }
//        LaunchUtils.launchApp(context, NAVI_PACKAGE_NAME);
//        int KEY_TYPE = 10013;
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", KEY_TYPE);
//        intent.putExtra("EXTRA_LAT", lat);
//        intent.putExtra("EXTRA_LON", lon);
//        intent.putExtra("EXTRA_DEV", 0);
//        intent.putExtra("SOURCE_APP", "Navi");
//        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        context.sendBroadcast(intent);
//        return true;
//    }
//
//    /**
//     * 启动导航
//     *
//     * @param context
//     * @param lat
//     * @param lon
//     */
//    public static boolean startNavi(final Context context, final double lat, final double lon) {
//        return startNavi(context, lat, lon, "");
//    }
//
//    /**
//     * 启动导航
//     *
//     * @param context
//     * @param lat
//     * @param lon
//     */
//    public static boolean startNavi(final Context context, final double lat, final double lon, final String poiName) {
//        if (!AppUtils.isAppInstalled(context, NAVI_PACKAGE_NAME)) {
//            return false;
//        }
//        //延迟发送导航信息，经测试，某些平板打开高德后立即发送导航广播不能接收到导航广播
//        LaunchUtils.launchApp(context, NAVI_PACKAGE_NAME);
//        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int KEY_TYPE = 10038;
//                Intent intent = new Intent();
//                intent.setAction(NAVI_ACTION);
//                intent.putExtra("KEY_TYPE", KEY_TYPE);
//                intent.putExtra("SOURCE_APP", "Navi");
//                intent.putExtra("POINAME", poiName);
//                intent.putExtra("LAT", lat);
//                intent.putExtra("LON", lon);
//                intent.putExtra("ENTRY_LAT", lat);
//                intent.putExtra("ENTRY_LON", lon);
//                intent.putExtra("DEV", 0);
//                intent.putExtra("STYLE", 0);
//                intent.putExtra("SOURCE_APP", "Third App");
//                context.sendBroadcast(intent);
//            }
//        }, 1000);
//        return true;
//    }
//
//    public static boolean exitNavi(Context context) {
//        if (!AppUtils.isAppInstalled(context, NAVI_PACKAGE_NAME)) {
//            return false;
//        }
//        int KEY_TYPE = 10021;
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", KEY_TYPE);
//        context.sendBroadcast(intent);
//
//        return true;
//    }
//
//    public static void searchNearbyPlace2(Context context, String voiceContent, double latitude, double longitude, String packNameGaode, String replaceStr) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10037);
//        intent.putExtra("SOURCE_APP", packNameGaode);
//        intent.putExtra("KEYWORDS", replaceStr);
//        intent.putExtra("LAT", latitude);
//        intent.putExtra("LON", longitude);
//        intent.putExtra("DEV", 0);
//        context.sendBroadcast(intent);
//    }
//
//    public static void enlargedAndNarrowMap(Context context, int extraOpera) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10027);
//        intent.putExtra("EXTRA_TYPE", 1);
//        intent.putExtra("EXTRA_OPERA", extraOpera);
//        context.sendBroadcast(intent);
//    }
//
//    public static void openAndCloseRealTimeTraffic(Context context, int extraOpera) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10027);
//        intent.putExtra("EXTRA_TYPE", 0);
//        intent.putExtra("EXTRA_OPERA", extraOpera);
//        context.sendBroadcast(intent);
//    }
//
//    public static void enterAndQuitOverview(Context context, int extraIsShow) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10006);
//        intent.putExtra("EXTRA_IS_SHOW", extraIsShow);
//        context.sendBroadcast(intent);
//    }
//
//    public static void continueNavigation(Context context) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10053);
//        intent.putExtra("SOURCE_APP", "xiaoma");
//        intent.putExtra("EXTRA_AUTO_BACK_NAVI_DATA", true);
//        context.sendBroadcast(intent);
//    }
//
//    public static void gotoDest(final Context context, final int destCode, final int isStartNavi) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10040);
//        intent.putExtra("DEST", destCode);
//        intent.putExtra("IS_START_NAVI", isStartNavi);
//        context.sendBroadcast(intent);
//    }
//
//    public static void resetNaviLine(Context context, int type) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10005);
//        intent.putExtra("NAVI_ROUTE_PREFER", type);
//        context.sendBroadcast(intent);
//    }
//
//    public static void cancelNavi(Context context) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10010);
//        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        context.sendBroadcast(intent);
//    }
//
//    public static void goBackMainAutoNavi(Context context) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10034);
//        intent.putExtra("SOURCE_APP", "Navi");
//        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        context.sendBroadcast(intent);
//    }
//
//    public static void set2Or3DMap(Context context, int extraOpera) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10027);
//        intent.putExtra("EXTRA_TYPE", 2);
//        intent.putExtra("EXTRA_OPERA", extraOpera);
//        context.sendBroadcast(intent);
//    }
//
//    public static void setDayStyle(Context context) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10048);
//        intent.putExtra("EXTRA_DAY_NIGHT_MODE", 1);
//        context.sendBroadcast(intent);
//    }
//
//    public static void setNightStyle(Context context) {
//        Intent intent = new Intent();
//        intent.setAction(NAVI_ACTION);
//        intent.putExtra("KEY_TYPE", 10048);
//        intent.putExtra("EXTRA_DAY_NIGHT_MODE", 2);
//        context.sendBroadcast(intent);
//    }
}
