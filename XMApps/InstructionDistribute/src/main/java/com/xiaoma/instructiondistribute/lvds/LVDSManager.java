//package com.xiaoma.instructiondistribute.lvds;
//
//import android.annotation.DrawableRes;
//import android.content.Context;
//import android.hardware.display.DisplayManager;
//import android.util.Log;
//import android.view.Display;
//import android.widget.Toast;
//
///**
// * <des>
// *
// * @author YangGang
// * @date 2019/7/11
// */
//public class LVDSManager {
//
//    //是否高配
//    private boolean mIsShowF = true;
//    private Context mAppContext;
//    private LVDSShowScreen mLvdsShowScreen;
//
//    private LVDSManager() {
//    }
//
//    public static LVDSManager newSingleton() {
//        return Holder.sINSTANCE;
//    }
//
//    public void init(Context context) {
//        if (context == null) {
//            throw new IllegalArgumentException("context should not be null!");
//        }
//        mAppContext = context.getApplicationContext();
//    }
//
//    public void showDualScreen() {
//        if (mAppContext == null) {
//            throw new IllegalArgumentException("Pls call LVDSManager#init() first!");
//        }
//        DisplayManager displayManager = (DisplayManager) mAppContext.getSystemService(Context.DISPLAY_SERVICE);
//        Display[] displays = displayManager.getDisplays();
//        if (displays == null || displays.length == 0) {
//            toast("未检测到屏幕信息");
//            return;
//        }
//        Display showHDMIDisplay = null;
//        int screenSize = displays.length;
//        for (int i = 0; i < screenSize; i++) {
//            Display display = displays[i];
//            String name = display.getName();
//            log(String.format("Screen Info %1$s -> %2$s", String.valueOf(i), name));
//            if (name == null) {
//                continue;
//            }
//            if (name.contains("HDMI")) {
//                log(String.format("HDMI Screen Info %1$s -> %2$s", String.valueOf(i), name));
//                showHDMIDisplay = display;
//                break;
//            }
//        }
//        if (showHDMIDisplay == null) {
//            toast("未检测到HDMI屏幕");
//        } else {
//            if (mLvdsShowScreen == null) {
//                mLvdsShowScreen = new LVDSShowScreen(mAppContext, showHDMIDisplay);
//            }
//            mLvdsShowScreen.show();
//            log("HDMI Screen Build Success!");
//        }
//    }
//
//    /**
//     * @param resId 显示的本地图片id,如果要隐藏
//     */
//    public void setShowLVDS(@DrawableRes int resId) {
////        if (mLvdsShowScreen != null) {
////            mIsShowF = (resId != R.drawable.transparent_resource);
////            mLvdsShowScreen.showLVDS(resId);
////        } else {
////            toast("请打开投屏!");
////        }
//    }
//
//    private void toast(String toastMsg) {
//        Toast.makeText(mAppContext, toastMsg, Toast.LENGTH_SHORT).show();
//    }
//
//    private void log(String content) {
//        Log.d("LVDS", content);
//    }
//
//    interface Holder {
//        LVDSManager sINSTANCE = new LVDSManager();
//    }
//}
