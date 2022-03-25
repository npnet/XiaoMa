package com.xiaoma.dualscreen.presentation;

import android.car.hardware.vendor.PowerMode;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.callback.IMapAidlConnectListen;
import com.mapbar.xiaoma.callback.XmMapNaviManagerCallBack;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.CarServiceConnManager;
import com.xiaoma.carlib.manager.CarServiceListener;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.constant.DataType;
import com.xiaoma.dualscreen.constant.TabState;
import com.xiaoma.dualscreen.eol.EOLScreenClient;
import com.xiaoma.dualscreen.eol.EOLView;
import com.xiaoma.dualscreen.eol.IDualScreenEOLEventListener;
import com.xiaoma.dualscreen.listener.PhoneListener;
import com.xiaoma.dualscreen.manager.DualScreenMapManager;
import com.xiaoma.dualscreen.manager.DualViewManager;
import com.xiaoma.dualscreen.manager.MediaViewManager;
import com.xiaoma.dualscreen.manager.PlayerConnectHelper;
import com.xiaoma.dualscreen.manager.WheelOperateDualManager;
import com.xiaoma.dualscreen.views.LeftNaviView;
import com.xiaoma.dualscreen.views.MediaView;
import com.xiaoma.dualscreen.views.NaviView;
import com.xiaoma.dualscreen.views.PhoneView;
import com.xiaoma.dualscreen.views.SimpleView;
import com.xiaoma.dualscreen.views.ViewCache;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.List;


/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public class XmPresentation extends BasePresentation implements PhoneView.PhoneViewListener, MediaView.OnMediaListener, WheelOperateDualManager.OnWheelOperatePhoneListener, XmCarVendorExtensionManager.DualScreenTabChangeListener, PhoneListener, SimpleView.ISimpleViewShowCallback,
        DialogInterface.OnDismissListener, DialogInterface.OnCancelListener , IMapAidlConnectListen, CarServiceListener, XmMapNaviManagerCallBack {

    private FrameLayout flLeftNavi;
    private FrameLayout flCenter;
    private FrameLayout flSimple;
    private FrameLayout flFullScreen;
    private SimpleView mSimpleView;
    private LeftNaviView mLeftNaviView;
    private MediaView mMediaView;
    private PhoneView mPhoneView;
    private NaviView mNaviView;
    private int mediaLevel = -1;
    private FrameLayout flParentLayout;
    private View layoutView;
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 500L;
    public boolean isEnterMediaView = false;
    public boolean isEnterPhoneView = false;

    public XmPresentation(Context outerContext, final Display display) {
        super(outerContext, display);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        KLog.e("XmPresentation onCreate");
        super.onCreate(savedInstanceState);
        XmMapNaviManager.getInstance().setMapAidlConnectListen(this);
        XmMapNaviManager.getInstance().setXmMapNaviManagerCallBack(this);
        DualScreenMapManager.getInstance().init(getContext().getApplicationContext());
        ViewCache.getInstance().init(getContext());
        DualViewManager.getInstance().init();
        initDeviceAndView();
        bindView();
        changeSkinLayout();
        attachView(false);
        changeSkinView();
        mSimpleView.onResume();
        WheelOperateDualManager.getInstance(getContext()).setWheelOperatePhoneListener(this);
        WheelOperateDualManager.getInstance(getContext()).registerCarLibListener();
        XmCarVendorExtensionManager.getInstance().setDualScreenTabChangeListener(this);
        PlayerConnectHelper.getInstance().setPhoneListener(this);
        setOnDismissListener(this);
        setOnCancelListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        CarServiceConnManager.getInstance(getContext()).addCallBack(this);
        EOLScreenClient.newSingleton().setOnEOLEventListener(new IDualScreenEOLEventListener() {
            @Override
            public void showImage(ClientCallback callback) {
                if(DualViewManager.getInstance().getCurrentType() == TabState.MEDIA){
                    flCenter.removeAllViews();
                    flCenter.setVisibility(View.VISIBLE);
                    flCenter.addView(ViewCache.getInstance().getEOLView());

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(CenterConstants.EOLContract.Key.EXTRA,true);
                    EOLScreenClient.newSingleton().dispatchFeedback(bundle,callback);
                }
            }

            @Override
            public void hideImage(ClientCallback callback) {
                if(DualViewManager.getInstance().getCurrentType() == TabState.MEDIA){
                    flCenter.removeAllViews();

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(CenterConstants.EOLContract.Key.EXTRA,true);
                    EOLScreenClient.newSingleton().dispatchFeedback(bundle,callback);
                }
            }
        });
    }


    //skin
    private void initDeviceAndView() {
        if (DualViewManager.getInstance().isHigh()) {
            if (ConfigManager.ApkConfig.isDebug()) {
                XMToast.showToast(getContext(), "高配");
            }
            setContentView(R.layout.presentation_high);
        } else {
            if (ConfigManager.ApkConfig.isDebug()) {
                XMToast.showToast(getContext(), "低配");
            }
            setContentView(R.layout.presentation_low);
        }
    }

    //skin
    private void changeSkinLayout() {
        boolean isLeftIsShow = false;
        if(flLeftNavi != null && flLeftNavi.isShown() && flLeftNavi.getChildCount() > 0){
            isLeftIsShow = true;
        }
        final int curSkin = DualViewManager.getInstance().getCurSkin();
        if (DualViewManager.getInstance().isHigh()) {
            switch (curSkin) {
                case 1:
                    layoutView = LayoutInflater.from(getContext()).inflate(R.layout.view_high_luxury, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_high_luxury);
                    break;
                case 2:
                    layoutView = LayoutInflater.from(getContext()).inflate(R.layout.view_high_dream, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_high_dream);
                    break;
                case 0:
                default:
                    layoutView = LayoutInflater.from(getContext()).inflate(R.layout.view_high_wisdom, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_high_wisdom);
                    break;
            }
            NaviView naviView = (NaviView) ViewCache.getInstance().getCenterNaviView();
            naviView.refreshSkin();
        } else {
            switch (curSkin) {
                case 1:
                    layoutView = LayoutInflater.from(getContext()).inflate(R.layout.view_low_luxury, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_low_luxury); 
                    break;
                case 2:
                    layoutView = LayoutInflater.from(getContext()).inflate(R.layout.view_low_dream, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_low_dream);
                    break;
                case 0:
                default:
                    layoutView = LayoutInflater.from(getContext()).inflate(R.layout.view_low_wisdom, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_low_wisdom);
                    break;
            }
        }
        flParentLayout.removeAllViews();
        flParentLayout.addView(layoutView);
        flLeftNavi = layoutView.findViewById(R.id.fl_left_navi);
        flCenter = layoutView.findViewById(R.id.fl_center);
        flSimple = layoutView.findViewById(R.id.fl_simple);
        flFullScreen = layoutView.findViewById(R.id.fl_fullscreen);
        if(isLeftIsShow) onLeftTabChangeFromLib(1);
    }

    //skin
    private void changeSkinView() {
        int curSkin = DualViewManager.getInstance().getCurSkin();
        KLog.d("changeSkinView  curSkin : "+curSkin);
        boolean isHigh = DualViewManager.getInstance().isHigh();
        mMediaView.changeSkin(isHigh, curSkin);
        mPhoneView.changeSkin(isHigh, curSkin);
        mSimpleView.changeSkin(curSkin);
    }

    //skin
    private void notifyChangeSkin() {
        changeSkinLayout();
        changeSkinView();
        reAddSimpleView();
    }

    public void refreshByLauncherChange(){
        if(DualViewManager.getInstance().getCurrentType() == TabState.MEDIA){
            mediaLevel = mMediaView.currentLevel();
        }
//        changeSkinLayout();
        attachView(true);
        changeSkinView();
        onTabChangeFromLib(XmCarVendorExtensionManager.getInstance().getInteractMode());
        mediaLevel = -1;
    }

    //skin
    private void reAddSimpleView() {
        flSimple.removeAllViews();
        if (mSimpleView.getParent() != null) {
            ((ViewGroup) mSimpleView.getParent()).removeAllViews();
        }
        flSimple.addView(mSimpleView);
    }


    @Override
    protected void bindData() {
    }

    @Override
    protected void bindView() {
        flParentLayout = findViewById(R.id.fl_parent_layout);
    }

    @Override
    public void onDataChange(DataType type) {

    }

    public void attachView(boolean changeLanguage) {
        mSimpleView = (SimpleView) ViewCache.getInstance().getSimpleView();
        mMediaView = ViewCache.getInstance().getCenterMediaView();
        mPhoneView = ViewCache.getInstance().getCenterPhoneView();
        if(!changeLanguage){
            mLeftNaviView = (LeftNaviView) ViewCache.getInstance().getLeftNaviView();
            mNaviView = (NaviView) ViewCache.getInstance().getCenterNaviView();
        }
        mMediaView.setMediaListener(this);
        mPhoneView.setPhoneViewListener(this);
        flSimple.removeAllViews();
        flSimple.addView(mSimpleView);
        mSimpleView.setiSimpleViewShowCallback(this);
    }


    private void showMediaTab() {
        KLog.e("XmPresentation showMediaTab");
        if (ViewCache.getInstance().getCenterMediaView().isShown()) {
            XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(mMediaView.currentLevel());
            return;
        }
        flFullScreen.setVisibility(View.INVISIBLE);
        flFullScreen.removeAllViews();
        flCenter.removeAllViews();
        flCenter.setVisibility(View.VISIBLE);
        if (mMediaView.getParent() != null) {
            ((ViewGroup) mMediaView.getParent()).removeAllViews();
        }
        flCenter.addView(mMediaView);
        if(mediaLevel > 0){
            if(mediaLevel == 2){
                mMediaView.setMediaViewOK(true);
                XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(mMediaView.currentLevel());
            }else{
                mMediaView.initMenuLevel();
            }
        }else{
            //当仪表当前有相关音源处于播放状态，在非媒体界面进行切换至媒体选项按下方控ok键时，显示当前播放音源的二级播放页面；
            //当仪表当前有相关音源处于未播放状态或暂停后直接从二级页面跳转至其他选项一级页面，再次返回至媒体按下方控ok键时，显示一级列表页。
            //当在有媒体播放时切换回媒体tab显示原先二级页面
            if (!mMediaView.isPlaying()) {
                mMediaView.initMenuLevel();
            }else{
                mMediaView.refreshDetail();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isEnterMediaView = true;
            }
        }, 1000);
    }

    private void showPhoneTab() {
        KLog.e("XmPresentation showPhoneTab");
        XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.MEDIA_MAIN);
        if (ViewCache.getInstance().getCenterPhoneView().isShown()) {
            return;
        }
        flCenter.removeAllViews();
        flCenter.setVisibility(View.VISIBLE);
        flFullScreen.setVisibility(View.INVISIBLE);
        flFullScreen.removeAllViews();
        if (mPhoneView.getParent() != null) {
            ((ViewGroup) mPhoneView.getParent()).removeAllViews();
        }
        flCenter.addView(mPhoneView);
        mPhoneView.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isEnterPhoneView = true;
            }
        }, 1000);
    }

    private void showNavTab() {
        KLog.e("XmPresentation showNavTab");
        if (flFullScreen.isShown()) {
            XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(mNaviView.getNaviLevel());
            return;
        }
        flCenter.removeAllViews();
        flLeftNavi.removeAllViews();
        flCenter.setVisibility(View.INVISIBLE);
        flLeftNavi.setVisibility(View.INVISIBLE);
        flFullScreen.setVisibility(View.VISIBLE);
        if (mNaviView.getParent() != null) {
            ((ViewGroup) mNaviView.getParent()).removeAllViews();
        }
        flFullScreen.addView(mNaviView);
        DualScreenMapManager.getInstance().setICMapMode(2);
        mNaviView.cancelMenu();
        KLog.e("XmPresentation showNavTab done,getChildCount:" + flFullScreen.getChildCount());
    }

    private void showLeftTab(int state) {
        KLog.e("flLeftNavi.isShown()=" + flLeftNavi.isShown() + ";state=" + state + ";flFullScreen.isShown()=" + flFullScreen.isShown());
        if (state == 0) {
            XmCarVendorExtensionManager.getInstance().setNaviDisplay(SDKConstants.VALUE.CanCommon_OFF);
            flLeftNavi.setVisibility(View.INVISIBLE);
            flLeftNavi.removeAllViews();
        } else {
            if(DualScreenMapManager.getInstance().isNaviEngineenInited()){
                if (flLeftNavi.isShown()) {
                    KLog.e("flLeftNavi isShown");
                } else {
                    flFullScreen.removeAllViews();
                    flFullScreen.setVisibility(View.INVISIBLE);
                    flLeftNavi.setVisibility(View.VISIBLE);
                    if (mLeftNaviView.getParent() != null) {
                        ((ViewGroup) mLeftNaviView.getParent()).removeAllViews();
                    }
                    flLeftNavi.addView(mLeftNaviView);
                }
                DualScreenMapManager.getInstance().setICMapMode(1);
                XmCarVendorExtensionManager.getInstance().setNaviDisplay(SDKConstants.VALUE.CanCommon_ON);
            }else{
                flFullScreen.removeAllViews();
                flFullScreen.setVisibility(View.INVISIBLE);
                flLeftNavi.setVisibility(View.VISIBLE);
                if (mLeftNaviView.getParent() != null) {
                    ((ViewGroup) mLeftNaviView.getParent()).removeAllViews();
                }
                flLeftNavi.addView(mLeftNaviView);
            }
        }
    }

    private void hideAllView() {
        KLog.e("XmPresentation hideAllView");
        if (mMediaView.getParent() != null) {
            ((ViewGroup) mMediaView.getParent()).removeAllViews();
        }
        if (mNaviView.getParent() != null) {
            ((ViewGroup) mNaviView.getParent()).removeAllViews();
            mNaviView.cancelMenu();
        }
        flCenter.setVisibility(View.INVISIBLE);
        flFullScreen.setVisibility(View.INVISIBLE);
        //flSimple.setVisibility(View.INVISIBLE);
    }

    private void doKeyUp() {
        if (!canDoKeyEvent()) {
            return;
        }
        if (ViewCache.getInstance().getCenterMediaView().isShown()) {
            ViewCache.getInstance().getCenterMediaView().setSelectedMusicUp();
        } else if (ViewCache.getInstance().getCenterPhoneView().isShown()) {
            ViewCache.getInstance().getCenterPhoneView().setPhoneSelectUp();
        } else if (ViewCache.getInstance().getCenterNaviView().isShown()) {
            NaviView centerNaviView = (NaviView) ViewCache.getInstance().getCenterNaviView();
//            centerNaviView.onKeyUp();
        }
    }

    private void doKeyDown() {
        if (!canDoKeyEvent()) {
            return;
        }
        if (ViewCache.getInstance().getCenterMediaView().isShown()) {
            ViewCache.getInstance().getCenterMediaView().setSelectedMusicDown();
        } else if (ViewCache.getInstance().getCenterPhoneView().isShown()) {
            ViewCache.getInstance().getCenterPhoneView().setPhoneSelectDown();
        } else if (ViewCache.getInstance().getCenterNaviView().isShown()) {
            NaviView centerNaviView = (NaviView) ViewCache.getInstance().getCenterNaviView();
//            centerNaviView.onKeyUp();
        }
    }

    private void doKeyOK() {
        if (!canDoKeyEvent()) {
            return;
        }
        if (ViewCache.getInstance().getCenterMediaView().isShown()) {
            if (isEnterMediaView) {
                ViewCache.getInstance().getCenterMediaView().setMediaViewOK(false);
            } else {
                isEnterMediaView = true;
            }
        } else if (ViewCache.getInstance().getCenterPhoneView().isShown()) {
            if (isEnterPhoneView) {
                ViewCache.getInstance().getCenterPhoneView().setPhoneOk();
            } else {
                isEnterPhoneView = true;
            }
        }
    }

    private void doKeyCancel() {
        if (!canDoKeyEvent()) {
            return;
        }
        if (ViewCache.getInstance().getCenterMediaView().isShown()) {
            if (mMediaView.isMusicTypeLayout()) {
                isEnterMediaView = false;
            }
            ViewCache.getInstance().getCenterMediaView().setMediaViewReturn();
        } else if (ViewCache.getInstance().getCenterPhoneView().isShown()) {
            isEnterPhoneView = false;
            //ViewCache.getInstance().getCenterPhoneView().setPhoneCancel();
        }
    }

    @Override
    public void onCalling(String name, String time) {
        KLog.e("onCalling mCurrentTabState=" + DualViewManager.getInstance().getCurrentType());
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText(getContext().getString(R.string.busy_now));
        } else {
            mSimpleView.setSimplePhoneText(getContext().getString(R.string.busg_calling), name, time);
        }
    }

    @Override
    public void onContactList() {
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText(getContext().getString(R.string.recent_calls));
        }
    }

    @Override
    public void onImComing(String name) {
        KLog.e("onImComing mCurrentTabState=" + DualViewManager.getInstance().getCurrentType());
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText(getContext().getString(R.string.caller_id));
        } else {
            String msg = getContext().getString(R.string.new_caller) + "\u3000" + name;
            setSimpleViewText(msg);
        }
        XmCarVendorExtensionManager.getInstance().setInteractModeReq(SDKConstants.VALUE.HuInteractReq_TEL_REQ);
    }

    @Override
    public void onComeOut(String name) {
        KLog.e("onComeOut mCurrentTabState=" + DualViewManager.getInstance().getCurrentType());
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText(getContext().getString(R.string.calling));
        } else {
            String msg = getContext().getString(R.string.calling_now) + "\u3000" + name;
            setSimpleViewText(msg);
        }
    }

    @Override
    public void onOtherInComing(String name) {
        KLog.e("onOtherInComing mCurrentTabState=" + DualViewManager.getInstance().getCurrentType());
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText(getContext().getString(R.string.caller_id));
        } else {
            String msg = getContext().getString(R.string.new_caller) + "\u3000" + name;
            setSimpleViewText(msg);
        }
        XmCarVendorExtensionManager.getInstance().setInteractModeReq(SDKConstants.VALUE.HuInteractReq_TEL_REQ);
    }

    @Override
    public void clearPhoneState() {
        setSimpleViewText("");
    }

    @Override
    public void onOtherCalling(String name, String time) {
        KLog.e("onOtherCalling mCurrentTabState=" + DualViewManager.getInstance().getCurrentType());
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText(getContext().getString(R.string.busy_now));
        } else {
            mSimpleView.setSimplePhoneText(getContext().getString(R.string.busg_calling), name, time);
        }
    }

    @Override
    public void onNoContact() {
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText("");
        }
    }

    @Override
    public void onNoBlue() {
        if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE) {
            setSimpleViewText("");
        }
    }

    //音乐回调概要信息
    @Override
    public void onConferMediaSimpleText(String audioTitle) {
        //低配不处理概要信息
        mSimpleView.setAudioTitle(audioTitle);
        mSimpleView.onResume();
    }

    @Override
    public int getPhoneState() {
        if(mPhoneView != null) return mPhoneView.getPhoneState();
        return 0;
    }

    private void setSimpleViewText(String text) {
        mSimpleView.setPhoneTitle(text);
        mSimpleView.onResume();
    }

    @Override
    public void keyUp() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                doKeyUp();
            }
        });
    }

    @Override
    public void keyDown() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                doKeyDown();
            }
        });
    }

    @Override
    public void keyCancel() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                doKeyCancel();
            }
        });
    }

    @Override
    public void keyOk() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                doKeyOK();
            }
        });
    }

    private boolean canDoKeyEvent() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > TIME_INTERVAL) {
            mLastClickTime = nowTime;
            return true;
        }
        return false;
    }

    @Override
    public void onTabChangeFromLib(int value) {
        KLog.e("XmPresentation onTabChangeFromLib:" + value);
        switch (value) {
            case SDKConstants.VALUE.InteractMode_NAVI:
                XmCarVendorExtensionManager.getInstance().setInteractModeReq(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
                flSimple.setBackgroundColor(getResources().getColor(R.color.color_transparent));
                showNavTab();
                mSimpleView.onResume();
                if(DualScreenMapManager.getInstance().isNaviEngineenInited()){
                    XmCarVendorExtensionManager.getInstance().setInteractMode(SDKConstants.VALUE.InteractMode_NAVI_REQ);
                }
                break;
            case SDKConstants.VALUE.InteractMode_MEDIA:
                XmCarVendorExtensionManager.getInstance().setInteractModeReq(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
                flSimple.setBackgroundResource(R.drawable.bg_simple_high_dream);
                showMediaTab();
                mSimpleView.onResume();
                XmCarVendorExtensionManager.getInstance().setInteractMode(SDKConstants.VALUE.InteractMode_MEDIA_REQ);
                break;
            case SDKConstants.VALUE.InteractMode_TEL:
                XmCarVendorExtensionManager.getInstance().setInteractModeReq(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
                flSimple.setBackgroundResource(R.drawable.bg_simple_high_dream);
                showPhoneTab();
                mSimpleView.onResume();
                XmCarVendorExtensionManager.getInstance().setInteractMode(SDKConstants.VALUE.InteractMode_TEL_REQ);
                break;
            case SDKConstants.VALUE.InteractMode_INACTIVE:
            default:
                XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.INACTIVE);
                flSimple.setBackgroundResource(R.drawable.bg_simple_high_dream);
                hideAllView();
                mSimpleView.onResume();
                XmCarVendorExtensionManager.getInstance().setInteractMode(SDKConstants.VALUE.InteractMode_INACTIVE_REQ);
                break;
        }
    }

    @Override
    public void onLeftTabChangeFromLib(int state) {
        KLog.e("左侧导航显示请求 XmPresentation onLeftTabChangeFromLib:state=" + state);
        //低配不处理左侧仪表
        if (!DualViewManager.getInstance().isHigh()) {
            return;
        }
        //全屏导航在显示，左边导航的消息不处理
        if (DualViewManager.getInstance().getCurrentType() == TabState.NAVI) {
            XmCarVendorExtensionManager.getInstance().setNaviDisplay(SDKConstants.VALUE.CanCommon_OFF);
            DualScreenMapManager.getInstance().setICMapMode(2);
            return;
        }
        showLeftTab(state);
    }

    @Override
    public void onThemeChangeFromLib(final int value) {
        DualViewManager.getInstance().setCurSkin(value);
        notifyChangeSkin();
        onTabChangeFromLib(XmCarVendorExtensionManager.getInstance().getInteractMode());
    }

    @Override
    public void onIgnChange(int value) {
        if(value == PowerMode.RECENT_OFF){//IGN_OFF
            if(DualViewManager.getInstance().getCurrentType() == TabState.MEDIA){
                mediaLevel = mMediaView.currentLevel();
            }
            if(mSimpleView != null) mSimpleView.dismissSimpleText();
            XmCarVendorExtensionManager.getInstance().setInteractModeReq(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
        }else if(value == PowerMode.IG_ON){//IGN_OFF
            if(mSimpleView != null) mSimpleView.onResume();
            if(DualViewManager.getInstance().getCurrentType() != TabState.MEDIA){
                mediaLevel = -1;
            }
            onTabChangeFromLib(XmCarVendorExtensionManager.getInstance().getInteractMode());
            onLeftTabChangeFromLib(XmCarVendorExtensionManager.getInstance().getNaviDisplayInICMode());
            mediaLevel = -1;
        }
    }

    @Override
    public boolean isMediaPlaying() {
        return mMediaView == null?false:mMediaView.isPlaying();
    }

    @Override
    public int getCallState() {
        return mPhoneView == null?0:mPhoneView.getPhoneState();
    }

    @Override
    public void connected() {
        KLog.d("map aidl connected, try to show navi");
        ThreadDispatcher.getDispatcher().removeOnMain(naviShowRunnable);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(naviShowRunnable, 5000);
    }

    @Override
    public void mapInitFinish() {
        KLog.d("map aidl init, try to show navi");
        ThreadDispatcher.getDispatcher().removeOnMain(naviShowRunnable);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(naviShowRunnable, 5000);
    }

    Runnable naviShowRunnable = new Runnable() {
        @Override
        public void run() {
            if(DualViewManager.getInstance().getCurrentType() == TabState.NAVI ){
                if(DualScreenMapManager.getInstance().isNaviEngineenInited()){
                    XmCarVendorExtensionManager.getInstance().setInteractMode(SDKConstants.VALUE.InteractMode_NAVI_REQ);
                }else {
                    XmCarVendorExtensionManager.getInstance().setInteractMode(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
                }
            }else if(flLeftNavi.isShown() && DualScreenMapManager.getInstance().isNaviEngineenInited()){
                DualScreenMapManager.getInstance().setICMapMode(1);
                XmCarVendorExtensionManager.getInstance().setNaviDisplay(SDKConstants.VALUE.CanCommon_ON);
            }
        }
    };

    @Override
    public void disConnected() {
        KLog.d("map aidl disconnected, clear navi show");
        if(DualViewManager.getInstance().getCurrentType() == TabState.NAVI){
            XmCarVendorExtensionManager.getInstance().setInteractMode(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
        }
        XmCarVendorExtensionManager.getInstance().setNaviDisplay(SDKConstants.VALUE.CanCommon_OFF);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        KLog.e("presentation dialog dismiss");
        show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        KLog.e("presentation dialog cancel");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KLog.e("presentation dialog detachedFromWindow");
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {
        onTabChangeFromLib(XmCarVendorExtensionManager.getInstance().getInteractMode());
        onLeftTabChangeFromLib(XmCarVendorExtensionManager.getInstance().getNaviDisplayInICMode());
    }

    @Override
    public void onCarServiceDisconnected() {

    }

    @Override
    public void onSearchResult(String searchKey, int errorCode, List<PoiBean> searchResults) {

    }

    @Override
    public void onSearchNearResult(String searchKey, double lon, double lat, int errorCode, List<PoiBean> searchResults) {

    }

    @Override
    public void onNaviStatusChanged(int status, PoiBean startPoi, PoiBean endPoi) {
        //主机导航后需要主动切到导航tab
        if(status == 4 && DualViewManager.getInstance().getCurrentType() != TabState.NAVI){
            XmCarVendorExtensionManager.getInstance().setInteractModeReq(SDKConstants.VALUE.HuInteractReq_NAVI_REQ);
        }
    }

    @Override
    public void onCarPositionChanged(PoiBean currentPoi) {

    }

    @Override
    public void onNaviShowStateChanged(int state) {

    }

    @Override
    public void onSearchByRouteResult(String searchKey, int errorCode, List<PoiBean> searchResults) {

    }

    @Override
    public void onNaviTracking(int turnId, int distanceToTurn, int turnToStart) {

    }
}
