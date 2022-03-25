//package com.xiaoma.dualscreen.manager;
//
//import android.content.Context;
//
//import com.xiaoma.dualscreen.constant.TabState;
//import com.xiaoma.dualscreen.listener.TabChangeListener;
//
///**
// * @author: iSun
// * @date: 2019/3/7 0007
// * 屏幕显示状态 废弃
// */
//public class ScreenStateManager implements TabChangeListener {
//    private boolean isShowSimple;
//    private boolean isShowLeftNavi;
//    private TabState isShowCenter;
//    private boolean isShowCenterBoolean;
//    private boolean isShowFullScrean;
//    private boolean isShowPhoneWindow;
//
//
//    private static ScreenStateManager instance;
//    private Context mContext;
//
//    public static ScreenStateManager getInstance() {
//        if (instance == null) {
//            synchronized (ScreenStateManager.class) {
//                if (instance == null) {
//                    instance = new ScreenStateManager();
//                }
//            }
//        }
//        return instance;
//    }
//
//    public void init(Context context) {
//        this.mContext = context;
//        KeyEventDispatcher.getInstance().registerTabChangeListener(this);
//        // TODO: 2019/3/7 0007 状态初始化代码
////        isShowSimple = false;
////        isShowLeftNavi = false;
////        isShowFullScrean = false;
////        isShowCenter = null;
//    }
//
//    public boolean isShowSimple() {
//        return isShowSimple;
//    }
//
//    public boolean isShowLeftNavi() {
//        return isShowLeftNavi;
//    }
//
//    public boolean isShowPhoneWindow() {
//        return isShowPhoneWindow;
//    }
//
//    public TabState isShowCenterT() {
//        return isShowCenter;
//    }
//
//    public boolean isShowCenterB() {
//        return isShowCenterBoolean;
//    }
//
//    public boolean isShowFullScrean() {
//        return isShowFullScrean;
//    }
//
//    @Override
//    public void onTabChange(TabState state) {
//        if (state == TabState.MEDIA || state == TabState.NAVI || state == TabState.PHONE) {
//            isShowCenter = state;
//            isShowCenterBoolean = true;
//        } else {
//            isShowCenter = null;
//            isShowCenterBoolean = false;
//        }
//    }
//}
