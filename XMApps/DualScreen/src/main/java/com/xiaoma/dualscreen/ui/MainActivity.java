package com.xiaoma.dualscreen.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.constant.TabState;
import com.xiaoma.dualscreen.listener.KeyEvent;
import com.xiaoma.dualscreen.listener.KeyEventListener;
import com.xiaoma.dualscreen.listener.TabChangeListener;
import com.xiaoma.dualscreen.manager.DualScreenMapManager;
import com.xiaoma.dualscreen.manager.KeyEventDispatcher;
import com.xiaoma.dualscreen.model.ContactModel;
import com.xiaoma.dualscreen.views.LeftNaviView;
import com.xiaoma.dualscreen.views.MediaView;
import com.xiaoma.dualscreen.views.NaviView;
import com.xiaoma.dualscreen.views.PhoneView;
import com.xiaoma.dualscreen.views.SimpleView;
import com.xiaoma.dualscreen.views.ViewCache;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, TabChangeListener, KeyEventListener, PhoneView.PhoneViewListener, MediaView.OnMediaListener {

    private FrameLayout flLeftNavi;
    private FrameLayout flCenter;
    private FrameLayout flSimple;
    private FrameLayout flFullScreen;
    private TabState mCurrentTabState;

    private SimpleView mSimpleView;
    private LeftNaviView mLeftNaviView;
    private MediaView mMediaView;
    private PhoneView mPhoneView;
    private NaviView mNaviView;

    //skin
    private boolean mIsHigh = true;
    private int mSkinType = 0;
    private FrameLayout flParentLayout;
    private View layoutView;

    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 500L;

    public boolean isEnterMediaView = false;
    public boolean isEnterPhoneView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDeviceAndView();
        bindView();
        //skin
        changeSkinLayout();
        attachView();
        //skin
        changeSkinView();

        initKeyEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //AppUpdateCheck.checkAppUpdate(getPackageName(), getApplication());
    }

    //skin
    private void initDeviceAndView() {
        //mIsHigh = XmCarConfigManager.isHighEndLcd();
        if (mIsHigh) {
            XMToast.showToast(this, "我是高配");
            setContentView(R.layout.activity_main_high);
        } else {
            XMToast.showToast(this, "我是低配");
            setContentView(R.layout.activity_main_low);
        }
    }

    //skin
    protected void bindView() {
        KLog.d("bindView");
        findViewById(R.id.btn_key_up).setOnClickListener(this);
        findViewById(R.id.btn_key_down).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_tab_phone).setOnClickListener(this);
        findViewById(R.id.btn_tab_media).setOnClickListener(this);
        findViewById(R.id.btn_tab_full).setOnClickListener(this);
        findViewById(R.id.btn_left_nav).setOnClickListener(this);
        findViewById(R.id.btn_left_nav_gone).setOnClickListener(this);
        findViewById(R.id.btn_simple).setOnClickListener(this);
        findViewById(R.id.btn_phone_coming).setOnClickListener(this);
        findViewById(R.id.btn_phone_out).setOnClickListener(this);
        findViewById(R.id.btn_phone_other_coming).setOnClickListener(this);
        findViewById(R.id.btn_phone_other_calling).setOnClickListener(this);
        findViewById(R.id.btn_no_contact).setOnClickListener(this);
        findViewById(R.id.btn_get_call_history).setOnClickListener(this);
        findViewById(R.id.btn_phone_contacting).setOnClickListener(this);
        findViewById(R.id.btn_skin_0).setOnClickListener(this);
        findViewById(R.id.btn_skin_1).setOnClickListener(this);
        findViewById(R.id.btn_skin_2).setOnClickListener(this);

        //skin
        flParentLayout = findViewById(R.id.fl_parent_layout);
    }

    public void attachView() {
        ViewCache.getInstance().init(this);
        mSimpleView = (SimpleView) ViewCache.getInstance().getSimpleView();
        mLeftNaviView = (LeftNaviView) ViewCache.getInstance().getLeftNaviView();
        mMediaView = ViewCache.getInstance().getCenterMediaView();
        mPhoneView = ViewCache.getInstance().getCenterPhoneView();
        mNaviView = (NaviView) ViewCache.getInstance().getCenterNaviView();

        mMediaView.setMediaListener(this);
        mPhoneView.setPhoneViewListener(this);

    }

    //skin
    private void changeSkinLayout() {
        if (mIsHigh) {
            switch (mSkinType) {
                case 0:
                    layoutView = LayoutInflater.from(this).inflate(R.layout.view_high_wisdom, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_high_wisdom);
                    break;
                case 1:
                    layoutView = LayoutInflater.from(this).inflate(R.layout.view_high_luxury, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_high_luxury);
                    break;
                case 2:
                    layoutView = LayoutInflater.from(this).inflate(R.layout.view_high_dream, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_high_dream);
                    break;
            }
        } else {
            switch (mSkinType) {
                case 0:
                    layoutView = LayoutInflater.from(this).inflate(R.layout.view_low_wisdom, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_low_wisdom);
                    break;
                case 1:
                    layoutView = LayoutInflater.from(this).inflate(R.layout.view_low_luxury, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_low_luxury);
                    break;
                case 2:
                    layoutView = LayoutInflater.from(this).inflate(R.layout.view_low_dream, null);
                    flParentLayout.setBackgroundResource(R.drawable.bg_content_low_dream);
                    break;
            }
        }
        flParentLayout.removeAllViews();
        flParentLayout.addView(layoutView);
        flLeftNavi = layoutView.findViewById(R.id.fl_left_navi);
        flCenter = layoutView.findViewById(R.id.fl_center);
        flSimple = layoutView.findViewById(R.id.fl_simple);
        flFullScreen = layoutView.findViewById(R.id.fl_fullscreen);
    }

    //skin
    private void changeSkinView() {
        mMediaView.changeSkin(mIsHigh, mSkinType);
        mPhoneView.changeSkin(mIsHigh, mSkinType);
        mSimpleView.changeSkin(mSkinType);
    }

    //skin
    private void notifyChangeSkin() {
        changeSkinLayout();
        changeSkinView();
        reAddSimpleView();
    }

    //skin
    private void reAddSimpleView(){
        flSimple.removeAllViews();
        if (mSimpleView.getParent() != null){
            ((ViewGroup) mSimpleView.getParent()).removeAllViews();
        }
        flSimple.addView(mSimpleView);
    }

    private void initKeyEvent() {
        KeyEventDispatcher.getInstance().init(this);
        KeyEventDispatcher.getInstance().registerTabChangeListener(this);
        KeyEventDispatcher.getInstance().registerKeyEventListener(this);
    }

    @Override
    public void onClick(View v) {
        if (true) {
            switch (v.getId()) {
                case R.id.btn_key_up:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_KEY_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.KEY_UP_EVENT));
                    break;
                case R.id.btn_key_down:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_KEY_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.KEY_DOWN_EVENT));
                    break;
                case R.id.btn_ok:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_KEY_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.KEY_OK_EVENT));
                    break;
                case R.id.btn_cancel:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_KEY_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.KEY_CANCEL_EVENT));
                    break;
                case R.id.btn_tab_phone:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_TAB_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.PHONE_TAB_EVENT));
                    break;
                case R.id.btn_tab_media:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_TAB_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.MEDIA_TAB_EVENT));
                    break;
                case R.id.btn_tab_full:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_TAB_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.NAV_TAB_EVENT));
                    break;
                case R.id.btn_left_nav:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_TAB_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.LEFT_TAB_EVENT));
                    break;
                case R.id.btn_left_nav_gone:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_TAB_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.LEFT_TAB_GONE_EVENT));
                    break;
                case R.id.btn_simple:
                    sendBroadcast(new Intent(KeyEventDispatcher.CHANGE_TAB_ACTION).putExtra(KeyEventDispatcher.EVENT_KEY, KeyEventDispatcher.SIMPLE_TAB_EVENT));
                    break;
                case R.id.btn_phone_coming:
                    mPhoneView.showInComing(new ContactBean("未知联系人", "18905461989"));
                    break;
                case R.id.btn_phone_out:
                    mPhoneView.showComeOut(new ContactBean("周杰伦", "18905461989"));
                    break;
                case R.id.btn_phone_contacting:
                    mPhoneView.showContacting(new ContactBean("周杰伦", "18905461989"));
                    break;
                case R.id.btn_phone_other_coming:
                    ContactBean contactBean1 = new ContactBean();
                    contactBean1.setName("周杰伦");
                    contactBean1.setPhoneNum("18905461989");
                    ContactModel contactModel1 = new ContactModel(contactBean1, State.INCOMING);
                    ContactBean contactBean2 = new ContactBean();
                    contactBean2.setName("周杰伦");
                    contactBean2.setPhoneNum("18905461989");
                    ContactModel contactModel2 = new ContactModel(contactBean2, State.ACTIVE);
                    List<ContactModel> contactModelList = new ArrayList<>();
                    contactModelList.add(contactModel1);
                    contactModelList.add(contactModel2);
                    mPhoneView.showOtherComing(contactModelList);
                    break;
                case R.id.btn_phone_other_calling:
                    ContactBean contactBean3 = new ContactBean();
                    contactBean3.setName("周杰伦");
                    contactBean3.setPhoneNum("18905461989");
                    ContactModel contactModel3 = new ContactModel(contactBean3, State.KEEP);
                    ContactBean contactBean4 = new ContactBean();
                    contactBean4.setName("周杰伦");
                    contactBean4.setPhoneNum("18905461989");
                    ContactModel contactModel4 = new ContactModel(contactBean4, State.ACTIVE);
                    List<ContactModel> contactModelList2 = new ArrayList<>();
                    contactModelList2.add(contactModel3);
                    contactModelList2.add(contactModel4);
                    mPhoneView.showOtherComing(contactModelList2);
                    break;
                case R.id.btn_no_contact:
                    mCurrentTabState = TabState.PHONE;
                    showPhoneTab();
                    mPhoneView.showNoContact();
                    break;
                case R.id.btn_skin_0:
                    mSkinType = 0;
                    notifyChangeSkin();
                    break;
                case R.id.btn_skin_1:
                    mSkinType = 1;
                    notifyChangeSkin();
                    break;
                case R.id.btn_skin_2:
                    mSkinType = 2;
                    notifyChangeSkin();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    //接收广播请求
    @Override
    public void onTabChange(TabState state) {
        KLog.d("MainActivity onTabChange:" + state);
        mCurrentTabState = state;
        if (state == TabState.MEDIA) {
            showMediaTab();
        } else if (state == TabState.PHONE) {
            showPhoneTab();
        } else if (state == TabState.NAVI) {
            showNavTab();
        } else if (state == TabState.LEFT) {
            showLeftTab(1);
        } else if (state == TabState.SIMPLE) {
            setSimpleViewText("test");
        }
    }

    @Override
    public void onKeyEvent(KeyEvent keyEvent) {
        KLog.d("MainActivity onKeyEvent:" + keyEvent);
        if (keyEvent == KeyEvent.UP) {
            doKeyUp();
        } else if (keyEvent == KeyEvent.DOWN) {
            doKeyDown();
        } else if (keyEvent == KeyEvent.OK) {
            doKeyOK();
        } else if (keyEvent == KeyEvent.CANCEL) {
            doKeyCancel();
        }
    }

    private void showMediaTab() {
        KLog.e("XmPresentation showMediaTab");
        if (ViewCache.getInstance().getCenterMediaView().isShown()) {
            return;
        }
        flCenter.removeAllViews();
        flCenter.setVisibility(View.VISIBLE);
        flFullScreen.setVisibility(View.INVISIBLE);
        flFullScreen.removeAllViews();
        if (mMediaView.getParent() != null) {
            ((ViewGroup) mMediaView.getParent()).removeAllViews();
        }
        flCenter.addView(mMediaView);
        mMediaView.initMenuLevel();
    }

    private void showPhoneTab() {
        KLog.e("XmPresentation showPhoneTab");
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
    }

    private void showNavTab() {
        KLog.e("XmPresentation showNavTab");
        if (flFullScreen.isShown()) {
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
        KLog.e("XmPresentation showNavTab done,getChildCount:" + flFullScreen.getChildCount());
    }

    private void showLeftTab(int state) {
        KLog.e("flLeftNavi.isShown()=" + flLeftNavi.isShown() + ";state=" + state + ";flFullScreen.isShown()=" + flFullScreen.isShown());
        if (state == 0) {
            //20190624 test
            flLeftNavi.setVisibility(View.INVISIBLE);
            flLeftNavi.removeAllViews();
        } else {
            if (flLeftNavi.isShown()) {
                KLog.e("flLeftNavi isShown");
            } else {
                flLeftNavi.setVisibility(View.VISIBLE);
                flFullScreen.removeAllViews();
                flFullScreen.setVisibility(View.INVISIBLE);
                if (mLeftNaviView.getParent() != null) {
                    ((ViewGroup) mLeftNaviView.getParent()).removeAllViews();
                }
                flLeftNavi.addView(mLeftNaviView);
                DualScreenMapManager.getInstance().setICMapMode(1);
            }
        }
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
            DualScreenMapManager.getInstance().setMapZoomIn();
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
            DualScreenMapManager.getInstance().setMapZoomOut();
        }
    }

    private void doKeyOK() {
        if (!canDoKeyEvent()) {
            return;
        }
        KLog.e("doKeyOK getCenterMediaView().isShown()=" + ViewCache.getInstance().getCenterMediaView().isShown() + ";getCenterPhoneView().isShown()=" + ViewCache.getInstance().getCenterPhoneView().isShown());
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
            isEnterMediaView = false;
            ViewCache.getInstance().getCenterMediaView().setMediaViewReturn();
        } else if (ViewCache.getInstance().getCenterPhoneView().isShown()) {
            isEnterPhoneView = false;
            ViewCache.getInstance().getCenterPhoneView().setPhoneCancel();
        }
    }

    private boolean canDoKeyEvent() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > TIME_INTERVAL) {
            mLastClickTime = nowTime;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCalling(String name, String time) {
        KLog.d("onCalling mCurrentTabState=" + mCurrentTabState);
        if (mCurrentTabState == TabState.PHONE) {
            setSimpleViewText("正在通话");
        } else {
            setSimplePhoneText("通话中", name, time);
        }
    }

    @Override
    public void onContactList() {
        setSimpleViewYellowText("最近通话");
        showPhoneTab();
    }

    @Override
    public void onImComing(String name) {
        KLog.d("onImComing mCurrentTabState=" + mCurrentTabState);
        if (mCurrentTabState == TabState.PHONE) {
            setSimpleViewText("来电显示");
        } else {
            String msg = "新来电" + "\u3000" + name;
            setSimpleViewYellowText(msg);
        }
    }

    @Override
    public void onComeOut(String name) {
        KLog.d("onComeOut mCurrentTabState=" + mCurrentTabState);
        if (mCurrentTabState == TabState.PHONE) {
            setSimpleViewText("呼叫中");
        } else {
            String msg = "正在呼叫" + "\u3000" + name;
            setSimpleViewYellowText(msg);
        }
    }

    @Override
    public void onOtherInComing(String name) {
        KLog.d("onOtherInComing mCurrentTabState=" + mCurrentTabState);
        if (mCurrentTabState == TabState.PHONE) {
            setSimpleViewText("来电显示");
        } else {
            String msg = "新来电" + "\u3000" + name;
            setSimpleViewYellowText(msg);
        }
    }

    @Override
    public void clearPhoneState() {

    }

    @Override
    public void onOtherCalling(String name, String time) {
        KLog.d("onOtherCalling mCurrentTabState=" + mCurrentTabState);
        if (mCurrentTabState == TabState.PHONE) {
            setSimpleViewText("正在通话");
        } else {
            setSimplePhoneText("通话中", name, time);
        }
    }

    @Override
    public void onNoContact() {
        setSimpleViewText(getString(R.string.no_call_record));
        showPhoneTab();
    }

    @Override
    public void onNoBlue() {
    }

    //音乐回调概要信息
    @Override
    public void onConferMediaSimpleText(String text) {
        setSimpleViewText(text);
    }

    private void setSimpleViewText(String text) {
//        if (!flSimple.isShown()) {
//            flSimple.setVisibility(View.VISIBLE);
//        }
//        mSimpleView.setSimpleText(text);
    }

    private void setSimpleViewYellowText(String text) {
//        if (!flSimple.isShown()) {
//            flSimple.setVisibility(View.VISIBLE);
//        }
//        mSimpleView.setSimpleText(text);
    }

    private void setSimplePhoneText(String status, String name, String time) {
        if (!flSimple.isShown()) {
            flSimple.setVisibility(View.VISIBLE);
        }
        mSimpleView.setSimplePhoneText(status, name, time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
