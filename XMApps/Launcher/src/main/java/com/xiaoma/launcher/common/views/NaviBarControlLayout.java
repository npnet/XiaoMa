package com.xiaoma.launcher.common.views;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.listener.SlideListener;
import com.xiaoma.launcher.common.manager.LauncherIBCallManager;
import com.xiaoma.launcher.main.adapter.PluginAdapter;
import com.xiaoma.launcher.main.model.PluginModel;
import com.xiaoma.launcher.main.vm.ControlManager;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.launcher.wheel.WheelManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.screentool.ScreenControlUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Thomas on 2019/6/28 0028
 */
@SuppressLint("LogNotTimber")
public class NaviBarControlLayout extends RelativeLayout implements View.OnClickListener {
    private static final String ENGINEER_MODE_PACKAGE = "com.citos.d058engineermode";
    private static final String ENGINEER_MODE_MAIN_ACTIVITY = "com.citos.d058engineermode.ui.EngineerModeActivity";
    private static final String TAG = "[NaviBarControlLayout]";
    private RecyclerView rv;
    private PluginAdapter adapter;
    private int minDistance = 100;
    private int minVelocity = 30;
    private final List<PluginModel> pluginBeanList = new CopyOnWriteArrayList<>();
    private ImageView mBluetooth;
    private ImageView mInternet;
    private ImageView mHotspot;
    private ImageView mWifi;
    private ImageView mPower;
    private TextView tvSetting, tvPlugin;
    private int countToCarLauncher;
    private ControlManager controlManager;
    private WidthProgressBar mVolumeBar;
    private WidthProgressBar mBrightnessBar;
    private User mUser;
    private SlideListener slideListener;
    private int tts_volume_max = 8;
    private int media_volume_max = 40;

    //TODO 由于目前无法获取移动数据是否打开，暂时由自己处理默认为移动数据打开
    //是否打开移动数据 默认为true
    private Boolean isOpenData;
    private int wifiWorkMode = -1;
    private AnimationDrawable bluetoothAnim;
    private AnimationDrawable wifiAnim;
    private AnimationDrawable spotAnim;
    private AnimationDrawable dataAnim;

    private static boolean touchBar = false;
    private int currentMediaChannel = -1;

    public void setSlideListener(SlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public NaviBarControlLayout(Context context) {
        super(context);
        init();
    }

    public NaviBarControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NaviBarControlLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        KLog.e("NaviBarControlLayout init");
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_control_panel, this);
        EventBus.getDefault().register(this);
        initAnimation();
        bindView();
        initEvent();
        initData();
    }

    private void initAnimation() {
        bluetoothAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.bluetooth_anim);
        wifiAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.wifi_anim);
        spotAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.spot_anim);
        dataAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.data_anim);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bindView() {
        launcherPluginFromAssert();
        rv = findViewById(R.id.rv);
        mBluetooth = findViewById(R.id.iv_bluetooth);
        mInternet = findViewById(R.id.iv_internet);
        mHotspot = findViewById(R.id.iv_hotspot);
        mWifi = findViewById(R.id.iv_wifi);
        mPower = findViewById(R.id.iv_power);
        mBrightnessBar = findViewById(R.id.brightness);
        mVolumeBar = findViewById(R.id.volume);
        tvSetting = findViewById(R.id.setting);
        tvPlugin = findViewById(R.id.tv_plugin);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        rv.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, 100, 50);
        rv.addItemDecoration(dividerItem);
        adapter = new PluginAdapter(getContext());
        rv.setAdapter(adapter);
        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countToCarLauncher++;
                if (countToCarLauncher >= 5) {
                    countToCarLauncher = 0;
                    closeNaviWindow();
                    ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LaunchUtils.launchApp(getContext(), ENGINEER_MODE_PACKAGE,
                                    ENGINEER_MODE_MAIN_ACTIVITY, null, true);
                        }
                    }, NaviBarWindow.ANIMATION_TIME);
                }
            }
        });
        GestureDetector mGestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) {
                    return false;
                }
                if (e1.getX() - e2.getX() > minDistance
                        && Math.abs(velocityX) > minVelocity
                        && Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (slideListener != null) {
                        slideListener.onSlideToLeft();
                    }
                    return true;
                } else if (e2.getX() - e1.getX() > minDistance
                        && Math.abs(velocityX) > minVelocity) {
                    if (slideListener != null) {
                        slideListener.onSlideToRight();
                    }
                    return true;
                }
                return false;
            }
        });
        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        controlManager = new ControlManager();
        //TODO 先去请求一次当前wifi工作模式和数据流量是否打开
        controlManager.getWorkPattern();
        controlManager.getInternet();
        mBluetooth.setOnClickListener(this);
        mInternet.setOnClickListener(this);
        mHotspot.setOnClickListener(this);
        mWifi.setOnClickListener(this);
        mPower.setOnClickListener(this);
        mBrightnessBar.setProgress(controlManager.getBrightness() <= 0 ? 1 : controlManager.getBrightness());
        KLog.i(TAG, "set bar progress=" + WheelManager.getInstance().getGroupVolume(WheelManager.getInstance().getCarVolumeGroupId()));
        currentMediaChannel = WheelManager.getInstance().getCarVolumeGroupId();
        mVolumeBar.setProgress(WheelManager.getInstance().getGroupVolume(WheelManager.getInstance().getCarVolumeGroupId()));
        mBluetooth.setImageResource(controlManager.getBtState() ? R.drawable.bg_bluetooth_on : R.drawable.bg_bluetooth_off);
        mBrightnessBar.setChangedListener(new WidthProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int currentValue, int percent) {
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.CONTROL_BRIGHTNESS,
                        currentValue + "",
                        EventConstants.PagePath.ControlPanelFragment,
                        EventConstants.PageDescribe.ControlPanelFragmentPagePathDesc);
                controlManager.setBrightness(currentValue);
            }

            @Override
            public void onProgressCancel() {

            }
        });
        //媒体音量进度条监听
        mVolumeBar.setChangedListener(new WidthProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int currentValue, int percent) {
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.CONTROL_VOLUME,
                        currentValue + "",
                        EventConstants.PagePath.ControlPanelFragment,
                        EventConstants.PageDescribe.ControlPanelFragmentPagePathDesc);
                KLog.i(TAG, "get bar currentValue=" + currentValue);
                WheelManager.getInstance().setCarVolume(currentValue, 0);
                NaviBarControlLayout.setTouchBar(true);
            }

            @Override
            public void onProgressCancel() {
                NaviBarControlLayout.setTouchBar(false);
            }
        });
        XmCarFactory.getCarVendorExtensionManager().addValueChangeListener(new XmCarVendorExtensionManager.ValueChangeListener() {
            @Override
            public void onChange(int id, Object value) {
                //557846529 557846530 557863952 557846528
                if (isLightChange(id)) {
                    mBrightnessBar.setProgress(controlManager.getBrightness() <= 0 ? 1 : controlManager.getBrightness());
                }
            }
        });

        //音量、类型变化监听
        WheelManager.getInstance().registerVolumeCallback(new WheelManager.CarVolume() {
            @Override
            public void onCarVolumeChanged(int i, int i1, int i2) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (touchBar) {
                            return;
                        }

                        if (i == currentMediaChannel) {
                            KLog.i(TAG, "onCarVolumeChanged() run: i=" + i + " ,i1=" + i1 + " ,i2=" + i2);
                            return;
                        }

                        KLog.i(TAG, "onCarVolumeChanged() end: i=" + i + " ,i1=" + i1 + " ,i2=" + i2);

                        if (i == SDKConstants.TTS_VOLUME) {
                            mVolumeBar.setMax(tts_volume_max);
                            mVolumeBar.setProgress(i1);
                        } else {
                            mVolumeBar.setMax(media_volume_max);
                            mVolumeBar.setProgress(i1);
                        }

                        currentMediaChannel = i;
                    }
                });
            }
        });
        adapter.setItemClickListener(new PluginAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PluginModel pluginModel = pluginBeanList.get(position);
                if (!canUse(pluginModel)) {
                    return;
                }

                if (getContext().getString(R.string.listening_to_songs).equals(pluginModel.getItemName())) {
                    if (LauncherIBCallManager.isIBCall()) {
                        XMToast.showToast(mContext, R.string.please_hangup_phone);
                        return;
                    }
                }
                if (CenterConstants.BLUETOOTH_PHONE.equals(pluginModel.getPackageName())) {
                    //如果点击了蓝牙电话，就发广播通知录音，关闭录音
                    Intent intent = new Intent();
                    intent.setAction(CenterConstants.CLICK_BLUE_PHONE);
                    mContext.sendBroadcast(intent);
                }

                if (AppUtils.isAppInstalled(getContext(), pluginModel.getPackageName())) {
                    closeNaviWindow();
                    ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                        @Override
                        public void run() {
                            launcherApp(pluginModel.getPackageName(), pluginModel.getClassName());
                        }
                    }, NaviBarWindow.ANIMATION_TIME);
                } else {
                    XMToast.showToast(getContext(), getContext().getString(R.string.app_no_install_tip));
                }
            }
        });
    }

    public boolean isLightChange(int id) {
        return id == SDKConstants.ID_DAY_DISPLAY_LEVEL || id == SDKConstants.ID_NIGHT_DISPLAY_LEVEL || id == SDKConstants.ID_ILL_STATUS || id == SDKConstants.ID_DISPLAY_MODE;
    }

    private void launcherApp(String packageName, String className) {
        if (!getContext().getPackageName().equals(packageName)) {
            LaunchUtils.launchApp(getContext(), packageName);
        } else {
            try {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(packageName, className);
                intent.setComponent(componentName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 功能是否能使用
    private boolean canUse(PluginModel pluginModel) {
        String className = pluginModel.getClassName();
        String packageName = pluginModel.getPackageName();

        if (className.equals(com.xiaoma.smarthome.login.ui.MainActivity.class.getName())) {
            return LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.XIAOMA_SMART_HOME, new OnBlockCallback() {
                @Override
                public boolean onShowToast(LoginType loginType) {
                    XMToast.showToast(getContext(), loginType.getPrompt(getContext()));
                    return true;
                }
            });
        }
        if (className.equals(MarkMainActivity.class.getName())) {
            return LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.MARK, new OnBlockCallback() {
                @Override
                public boolean onShowToast(LoginType loginType) {
                    XMToast.showToast(getContext(), loginType.getPrompt(getContext()));
                    return true;
                }
            });
        }

        if (LoginCfgConstant.MOTORCADE.equals(packageName)) {
            return LoginTypeManager.getInstance().judgeUse(packageName, new OnBlockCallback() {
                @Override
                public boolean onShowToast(LoginType loginType) {
                    XMToast.showToast(getContext(), loginType.getPrompt(getContext()));
                    return true;
                }
            });
        } else {
            return true;
        }
    }

    public void initData() {
        adapter.setData(pluginBeanList);
    }

    /**
     * 从资源文件加载列表数据
     */
    private void launcherPluginFromAssert() {
        String textFromAsset;
        String moreAppPath = "config/LauncherPluginList.json";
        textFromAsset = AssetUtils.getTextFromAsset(getContext(), moreAppPath);
        pluginBeanList.clear();

        List<PluginModel> itemBeans = GsonHelper.fromJsonToList(textFromAsset, PluginModel[].class);
        Iterator<PluginModel> pluginModelIterator = itemBeans.iterator();

        while (pluginModelIterator.hasNext()) {
            PluginModel pluginModel = pluginModelIterator.next();
            if (LauncherConstants.MARK_TEXT.equals(pluginModel.getItemName())) {
                if (!XmCarConfigManager.hasJourneyRecord()) {
                    pluginModelIterator.remove();
                }
            }

            if (LauncherConstants.TEAM.equals(pluginModel.getItemName())) {
                if (!AppUtils.isAppInstalled(mContext, pluginModel.getPackageName())) {
                    pluginModelIterator.remove();
                }
            }
            if (LauncherConstants.DRIVE_SCORE.equals(pluginModel.getItemName())) {
                if (!AppUtils.isAppInstalled(mContext, pluginModel.getPackageName())) {
                    pluginModelIterator.remove();
                }
            }
        }

        pluginBeanList.addAll(itemBeans);
    }

    @Override
    //按钮对应的名称
    @NormalOnClick({EventConstants.NormalClick.CONTROL_BLUETOOTH, EventConstants.NormalClick.CONTROL_INTERNET, EventConstants.NormalClick.CONTROL_HOTSPOT, EventConstants.NormalClick.CONTROL_WIFI, EventConstants.NormalClick.CONTROL_POWER})
    //按钮对应的R文件id
    @ResId({R.id.iv_bluetooth, R.id.iv_internet, R.id.iv_hotspot, R.id.iv_wifi, R.id.iv_power})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bluetooth:
                if (!bluetoothAnim.isRunning()) {
                    mBluetooth.setImageDrawable(bluetoothAnim);
                    bluetoothAnim.start();
                }
                controlManager.toggleBt();
                break;
            case R.id.iv_internet:
                if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SWITCH_4G, new OnBlockCallback() {
                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(getContext(), loginType.getPrompt(getContext()));
                        return true;
                    }
                })) return;
                if (!dataAnim.isRunning()) {
                    mInternet.setImageDrawable(dataAnim);
                    dataAnim.start();
                }
                handleDataSwitch();
                break;
            case R.id.iv_hotspot:
                handleHotSpot();
                break;
            case R.id.iv_wifi:
                handleWifi();
                break;
            case R.id.iv_power:
                ScreenControlUtil.sendTurnOffScreenBroadCast(getContext());
                break;
        }
    }

    public void notifyUpdate() {
        mBrightnessBar.setProgress(controlManager.getBrightness() <= 0 ? 1 : controlManager.getBrightness());
        KLog.i(TAG, "notifyUpdate set bar progress=" + WheelManager.getInstance().getGroupVolume(WheelManager.getInstance().getCarVolumeGroupId()));
        mVolumeBar.setProgress(WheelManager.getInstance().getGroupVolume(WheelManager.getInstance().getCarVolumeGroupId()));
        currentMediaChannel = WheelManager.getInstance().getCarVolumeGroupId();
        mBluetooth.setImageResource(controlManager.getBtState() ? R.drawable.bg_bluetooth_on : R.drawable.bg_bluetooth_off);
        if (LoginManager.getInstance().isUserLogin()) {
            mUser = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        }

        tvSetting.setText(mContext.getString(R.string.fast_setting));
        tvPlugin.setText(mContext.getString(R.string.app_plugin));

        launcherPluginFromAssert();

        if (adapter != null) {
            adapter.setData(pluginBeanList);
            adapter.notifyDataSetChanged();
        }


    }

    private void handleWifi() {
        int result;

        /*if (wifiWorkMode == SDKConstants.WifiMode.OFF || wifiWorkMode == SDKConstants.WifiMode.STA) {
            if (!wifiAnim.isRunning()) {
                mWifi.setImageDrawable(wifiAnim);
                wifiAnim.start();
            }
        } else if (wifiWorkMode == SDKConstants.WifiMode.AP) {
            if (!spotAnim.isRunning()) {
                mHotspot.setImageDrawable(spotAnim);
                spotAnim.start();
            }
            if (!wifiAnim.isRunning()) {
                mWifi.setImageDrawable(wifiAnim);
                wifiAnim.start();
            }
        }*/

        if (!wifiAnim.isRunning()) {
            mWifi.setImageDrawable(wifiAnim);
            wifiAnim.start();
        }

        if (wifiWorkMode != SDKConstants.WifiMode.STA) {
            result = controlManager.toggleWifi(true);
        } else {
            result = controlManager.toggleWifi(false);
        }

        KLog.e("ControlPanelFragment", "handleWifi" + result + "wifiWorkMode" + wifiWorkMode);
        //去除对异常的判断显示toast，该异常只是对tbox有没有收到操作wifi的信息
//        if (result == SDKConstants.RequestAck.RESULT_ERROR || result == SDKConstants.RequestAck.RESULT_FAILED) {
//            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
//        }
    }

    private void handleHotSpot() {
        int result;

        /*if (wifiWorkMode == SDKConstants.WifiMode.OFF || wifiWorkMode == SDKConstants.WifiMode.AP) {
            if (!spotAnim.isRunning()) {
                mHotspot.setImageDrawable(spotAnim);
                spotAnim.start();
            }
        } else if (wifiWorkMode == SDKConstants.WifiMode.STA) {
            if (!spotAnim.isRunning()) {
                mHotspot.setImageDrawable(spotAnim);
                spotAnim.start();
            }
            if (!wifiAnim.isRunning()) {
                mWifi.setImageDrawable(wifiAnim);
                wifiAnim.start();
            }
        }*/

        if (!spotAnim.isRunning()) {
            mHotspot.setImageDrawable(spotAnim);
            spotAnim.start();
        }

        if (wifiWorkMode != SDKConstants.WifiMode.AP) {
            result = controlManager.toggleHotspot(true);
        } else {
            result = controlManager.toggleHotspot(false);
        }

        KLog.e("ControlPanelFragment", "handleHotSpot" + result + "wifiWorkMode" + wifiWorkMode);
        //去除对异常的判断，该异常只是对tbox有没有收到操作热点的信息
//        if (result == SDKConstants.RequestAck.RESULT_ERROR || result == SDKConstants.RequestAck.RESULT_FAILED) {
//            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
//        }
    }

    private void handleDataSwitch() {

        if (isOpenData == null) {
            return;
        }

        boolean isDataSwitch = !isOpenData;
        int dataSwitchResult = controlManager.toggleInternet(isDataSwitch);
        KLog.d("ControlPanelFragment", "handleDataSwitch" + dataSwitchResult + "isDataSwitch" + isDataSwitch);
        //        if (dataSwitchResult == SDKConstants.RequestAck.RESULT_ERROR || dataSwitchResult == SDKConstants.RequestAck.RESULT_FAILED) {
        //            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        //        }
        //
        //        else if (dataSwitchResult == SDKConstants.RequestAck.RESULT_SUCCESS) {
        //            if (isDataSwitch) {
        //                XMToast.showToast(getContext(), getString(R.string.data_switch_open), getContext().getDrawable(R.drawable.icon_refresh_n));
        //                mInternet.setImageResource(R.drawable.bg_data_switch_on);
        //            } else {
        //                XMToast.showToast(getContext(), getString(R.string.data_switch_close), getContext().getDrawable(R.drawable.icon_refresh_n));
        //                mInternet.setImageResource(R.drawable.bg_data_switch_off);
        //            }
        //        }
        //操作完了之后，在主动调用一次获取当前数据开关状态是否打开，用来通知systemui那边去更新图标
        controlManager.getInternet();
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_ON_SUCCESS)
    public void bluetoothTurnOnSuccess(String event) {
        if (bluetoothAnim != null) {
            bluetoothAnim.stop();
        }

        if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
            XMToast.showToast(getContext(), getContext().getString(R.string.bluetooth_open), getContext().getDrawable(R.drawable.icon_bluetooth_n));
        }
        mBluetooth.setImageResource(R.drawable.bg_bluetooth_on);
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_ON_FAILED)
    public void bluetoothTurnOnFailed(String event) {

        if (bluetoothAnim != null) {
            bluetoothAnim.stop();
        }
        if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        }
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_OFF_SUCCESS)
    public void bluetoothTurnOffSuccess(String event) {

        if (bluetoothAnim != null) {
            bluetoothAnim.stop();
        }
        if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
            XMToast.showToast(getContext(), getContext().getString(R.string.bluetooth_close), getContext().getDrawable(R.drawable.icon_bluetooth_dis));
        }
        mBluetooth.setImageResource(R.drawable.bg_bluetooth_off);
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_OFF_FAILED)
    public void bluetoothTurnOffFailed(String event) {

        if (bluetoothAnim != null) {
            bluetoothAnim.stop();
        }
        if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        }
    }

    @Subscriber(tag = LauncherConstants.IS_DATA_SWITCH)
    public void isDataSwicth(boolean isDataSwitch) {
        KLog.d("ControlPanelFragment", "isDataSwicth" + isDataSwitch);

        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataAnim != null) {
                    dataAnim.stop();
                }
                if (isOpenData == null || !Objects.equals(isOpenData, isDataSwitch)) {
                    if (isDataSwitch) {
                        if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
                            XMToast.showToast(getContext(), getContext().getString(R.string.data_switch_open), getContext().getDrawable(R.drawable.icon_reset_n));
                        }
                        mInternet.setImageResource(R.drawable.bg_data_switch_on);
                    } else {
                        if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
                            XMToast.showToast(getContext(), getContext().getString(R.string.data_switch_close), getContext().getDrawable(R.drawable.icon_reset_dis));
                        }
                        mInternet.setImageResource(R.drawable.bg_data_switch_off);
                    }
                }

                isOpenData = isDataSwitch;
            }
        }, 200);

        //        mInternet.setImageResource(isDataSwitch ? R.drawable.bg_data_switch_on : R.drawable.bg_data_switch_off);
    }

    @Subscriber(tag = LauncherConstants.WIFI_WORK_PATTERN)
    public void wifiWorkPattern(int wp) {

        if (wifiAnim != null) {
            wifiAnim.stop();
        }

        if (spotAnim != null) {
            spotAnim.stop();
        }
        Log.e(TAG, String.format("wifiWorkPattern: curWp: %s, newWp: %s", wifiWorkMode, wp));
        if (wifiWorkMode == wp) {
            return;
        }

        if (wp == SDKConstants.WifiMode.AP) {
            if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
                XMToast.showToast(getContext(), getContext().getString(R.string.hotspot_open), getContext().getDrawable(R.drawable.icon_hotspot_n));
            }
            mHotspot.setImageResource(R.drawable.bg_hotspot_on);
            mWifi.setImageResource(R.drawable.bg_wifi_off);
        } else if (wp == SDKConstants.WifiMode.STA) {
            if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
                XMToast.showToast(getContext(), getContext().getString(R.string.wifi_open), getContext().getDrawable(R.drawable.icon_wifi_n));
            }
            mWifi.setImageResource(R.drawable.bg_wifi_on);
            mHotspot.setImageResource(R.drawable.bg_hotspot_off);
        } else if (wp == SDKConstants.WifiMode.OFF) {
            if (wifiWorkMode == -1) {
                mHotspot.setImageResource(R.drawable.bg_hotspot_off);
                mWifi.setImageResource(R.drawable.bg_wifi_off);
            }
            if (wifiWorkMode == SDKConstants.WifiMode.AP) {
                if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
                    XMToast.showToast(getContext(), getContext().getString(R.string.hotspot_close), getContext().getDrawable(R.drawable.icon_hotspot_dis));
                }
                mHotspot.setImageResource(R.drawable.bg_hotspot_off);
            } else if (wifiWorkMode == SDKConstants.WifiMode.STA) {
                if (NaviBarWindow.getNaviBarWindow().isSlideIn) {
                    XMToast.showToast(getContext(), getContext().getString(R.string.wifi_close), getContext().getDrawable(R.drawable.icon_wifi_dis));
                }
                mWifi.setImageResource(R.drawable.bg_wifi_off);
            }
        }
        wifiWorkMode = wp;
    }

    private void closeNaviWindow() {
        NaviBarWindow.getNaviBarWindow().closeNaviWindow();
    }

    public static void setTouchBar(boolean data) {
        touchBar = data;
    }
}
