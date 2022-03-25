//package com.xiaoma.dualscreen.test;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import com.xiaoma.config.ConfigManager;
//import com.xiaoma.dualscreen.manager.WheelOperateDualManager;
//import com.xiaoma.dualscreen.views.LeftNaviView;
//import com.xiaoma.dualscreen.views.NaviView;
//import com.xiaoma.ui.toast.XMToast;
//
///**
// * @author: iSun
// * @date: 2019/7/11 0011
// */
//public class SimulateTest {
//
//    private static SimulateTest instance;
//    private WheelOperateDualManager.OnWheelOperatePhoneListener keyListener;
//    private Context mContext;
//    private LeftNaviView leftNaviView;
//    private NaviView naviView;
//
//    public static SimulateTest getInstance() {
//        if (instance == null) {
//            synchronized (SimulateTest.class) {
//                if (instance == null) {
//                    instance = new SimulateTest();
//                }
//            }
//        }
//        return instance;
//    }
//
//
//    private SimulateTest() {
//
//    }
//
//    public void init(Context context) {
//        this.mContext = context;
//        if (ConfigManager.ApkConfig.isDebug()) {
//            mContext.registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    if (keyListener != null) {
//                        int key = intent.getIntExtra("key", -1);
//                        switch (key) {
//                            case 0:
//                                keyListener.keyCancel();
//                                break;
//                            case 1:
//                                keyListener.keyUp();
//                                break;
//                            case 2:
//                                keyListener.keyDown();
//                                break;
//                            case 3:
//                                keyListener.keyOk();
//                                break;
//                            //界面测试
//                            case 4:
//                                if (leftNaviView != null && leftNaviView.getParent() != null) {
//                                    ViewGroup parent = (ViewGroup) leftNaviView.getParent();
//                                    boolean result = leftNaviView != null && leftNaviView.getVisibility() == View.VISIBLE && parent.getVisibility() == View.VISIBLE;
//                                    XMToast.showToast(mContext, "左侧导航是否显示:" + result);
//                                } else {
//                                    XMToast.showToast(mContext, "左侧导航没有显示");
//                                }
//                                break;
//                            case 5:
//                                if (naviView != null && naviView.getParent() != null) {
//                                    ViewGroup parent = (ViewGroup) naviView.getParent();
//                                    boolean result = naviView != null && naviView.getVisibility() == View.VISIBLE && parent.getVisibility() == View.VISIBLE;
//                                    XMToast.showToast(mContext, "全屏导航是否显示:" + result);
//                                } else {
//                                    XMToast.showToast(mContext, "全屏导航没有显示");
//                                }
//                                break;
//                            case 6:
//                                keyListener.keyOk();
//                                break;
//
//                        }
//                    }
//                }
//            }, new IntentFilter("com.xiaoma.test"));
//        }
//    }
//
//
//    public void setKeyListener(WheelOperateDualManager.OnWheelOperatePhoneListener listener) {
//        this.keyListener = listener;
//    }
//
//    public void initLeftView(LeftNaviView leftNaviView) {
//        this.leftNaviView = leftNaviView;
//    }
//
//    public void initNaviView(NaviView naviView) {
//        this.naviView = naviView;
//    }
//}
