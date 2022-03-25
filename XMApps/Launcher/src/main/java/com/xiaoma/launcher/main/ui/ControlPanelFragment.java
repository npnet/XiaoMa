package com.xiaoma.launcher.main.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.views.WidthProgressBar;
import com.xiaoma.launcher.main.adapter.PluginAdapter;
import com.xiaoma.launcher.main.listener.ControlPanelListener;
import com.xiaoma.launcher.main.model.PluginModel;
import com.xiaoma.launcher.main.vm.ControlVM;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: iSun
 * @date: 2018/12/11 0011
 */
@PageDescComponent(EventConstants.PageDescribe.ControlPanelFragmentPagePathDesc)
public class ControlPanelFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "ControlPanelFragment";
    private RecyclerView rv;
    private PluginAdapter adapter;
    private ControlPanelListener listener;
    private int minDistance = 100;
    private int minVelocity = 30;
    private GestureDetector mGestureDetector;
    private final List<PluginModel> pluginBeanList = new CopyOnWriteArrayList<>();
    private ImageView mBluetooth;
    private ImageView mInternet;
    private ImageView mHotspot;
    private ImageView mWifi;
    private ImageView mPower;
    private int countToCarLauncher;
    protected static final int KEY_PERMISSION = 0x001;
    private ControlVM mControlVM;
    private WidthProgressBar mVolumeBar;
    private WidthProgressBar mBrightnessBar;
    private View turnOn;
    private User mUser;

    //TODO 由于目前无法获取移动数据是否打开，暂时由自己处理默认为移动数据打开
    //是否打开移动数据 默认为true
    private boolean isOpenData;

    private int wifiWorkMode = -1;
    private boolean isShow;

    public static ControlPanelFragment newInstance() {
        return new ControlPanelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        bindView(view);
        initEvent(view);
        initTestData();
    }

    public void bindView(View view) {
        launcherPluginFromAssert();
        rv = view.findViewById(R.id.rv);
        turnOn = view.findViewById(R.id.turnOn);
        mBluetooth = view.findViewById(R.id.iv_bluetooth);
        mInternet = view.findViewById(R.id.iv_internet);
        mHotspot = view.findViewById(R.id.iv_hotspot);
        mWifi = view.findViewById(R.id.iv_wifi);
        mPower = view.findViewById(R.id.iv_power);
        mBrightnessBar = view.findViewById(R.id.brightness);
        mVolumeBar = view.findViewById(R.id.volume);
        LinearLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        rv.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        adapter = new PluginAdapter(getContext());
        rv.setAdapter(adapter);
        view.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countToCarLauncher++;
                if (countToCarLauncher >= 5) {
                    countToCarLauncher = 0;
                    LaunchUtils.launchApp(getContext(), LauncherConstants.LauncherApp.LAUNCHER_CARLAUNCHER_PACKAGE, LauncherConstants.LauncherApp.LAUNCHER_CARLAUNCHER_CLASS);
                }
            }
        });
        turnOn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent(View view) {
        mControlVM = ViewModelProviders.of(this).get(ControlVM.class);

        //TODO 先去请求一次当前wifi工作模式和数据流量是否打开
        mControlVM.getWorkPattern();
        mControlVM.getInternet();

        mBluetooth.setOnClickListener(this);
        mInternet.setOnClickListener(this);
        mHotspot.setOnClickListener(this);
        mWifi.setOnClickListener(this);
        mPower.setOnClickListener(this);

        mBrightnessBar.setProgress(mControlVM.getBrightness() <= 0 ? 1 : mControlVM.getBrightness());
        mVolumeBar.setProgress(mControlVM.getVolume());
        mBluetooth.setImageResource(mControlVM.getBtState() ? R.drawable.bg_bluetooth_on : R.drawable.bg_bluetooth_off);

        mBrightnessBar.setChangedListener(new WidthProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int currentValue, int percent) {
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.CONTROL_BRIGHTNESS,
                        currentValue + "",
                        EventConstants.PagePath.ControlPanelFragment,
                        EventConstants.PageDescribe.ControlPanelFragmentPagePathDesc);
                mControlVM.setBrightness(currentValue);
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
                mControlVM.setVolume(currentValue);
            }

            @Override
            public void onProgressCancel() {

            }
        });


        adapter.setItemClickListener(new PluginAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PluginModel pluginModel = pluginBeanList.get(position);

                if (AppUtils.isAppInstalled(getContext(), pluginModel.getPackageName())) {
                    if (!mContext.getPackageName().equals(pluginModel.getPackageName())) {
                        if (!canUse(pluginModel.getPackageName())) return;
                        LaunchUtils.openApp(getContext(), pluginModel.getPackageName());
                    } else {
                        try {
                            Class activityClass = Class.forName(pluginModel.getClassName());
                            if (!canUse(activityClass)) return;
                            startActivity(new Intent(mContext, activityClass));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    XMToast.showToast(mContext, mContext.getString(R.string.app_no_install_tip));
                }

            }
        });
        mGestureDetector = new GestureDetector(this.getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (turnOn != null && turnOn.getVisibility() == View.VISIBLE) {
                    KLog.d("ControlPanelFragment onDoubleTap");
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) {
                    return false;
                }
                //右
                if (e1.getX() - e2.getX() > minDistance
                        && Math.abs(velocityX) > minVelocity
                        && Math.abs(velocityX) > Math.abs(velocityY)) {
                    //左
                    if (listener != null) {
                        listener.onHideControlPanel();
                    }
                    return true;
                } else return e2.getX() - e1.getX() > minDistance
                        && Math.abs(velocityX) > minVelocity;
            }
        });
        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private void isShow(boolean show) {
        isShow = show;
    }

    // 功能是否能使用
    private boolean canUse(Class activityClass) {
        if (activityClass == null) return true;
        String clzName = activityClass.getName();
        if (clzName.equals(com.xiaoma.smarthome.login.ui.MainActivity.class.getName())) {
            if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.XIAOMA_SMART_HOME)) {
                XMToast.showToast(mActivity, LoginTypeManager.getPrompt(mActivity));
                return false;
            }
        }

        if (clzName.equals(MarkMainActivity.class.getName())) {
            return LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.MARK, new OnBlockCallback() {
                @Override
                public boolean onKeyVerification(LoginType loginType) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(LoginConstants.KeyBind.BundleKey.USER, mUser);
                    LoginTypeManager.getInstance().keyVerificationAndStartAct(mActivity, bundle, activityClass.getName());
                    return true;
                }
            });
        }
        return true;
    }

    // 功能是否能使用
    private boolean canUse(String packageName) {
        if (TextUtils.isEmpty(packageName) || !"com.xiaoma.motorcade".equals(packageName))
            return true;
        return LoginTypeManager.getInstance().judgeUse(packageName, new OnBlockCallback() {
            @Override
            public boolean onKeyVerification(LoginType loginType) {
                if (mUser == null) return false;
                Bundle bundle = new Bundle();
                bundle.putParcelable(LoginConstants.KeyBind.BundleKey.USER, mUser);
                LoginTypeManager.getInstance().keyVerificationAndStartApp(mActivity, bundle, packageName);
                return true;
            }
        });
    }

    public void setControlPanelListener(ControlPanelListener listener) {
        this.listener = listener;
    }

    public void initTestData() {
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
        if (itemBeans != null) {
            for (int i = 0; i < itemBeans.size(); i++) {
                //没有全息时隐藏途记功能
                if (LauncherConstants.MARK_TEXT.equals(itemBeans.get(i).getItemName())) {
                    if (XmCarConfigManager.hasJourneyRecord()) {
                        pluginBeanList.add(itemBeans.get(i));
                    }
                    continue;
                }
                pluginBeanList.add(itemBeans.get(i));
            }
        }
    }

    @Override
    //按钮对应的名称
    @NormalOnClick({EventConstants.NormalClick.CONTROL_BLUETOOTH, EventConstants.NormalClick.CONTROL_INTERNET, EventConstants.NormalClick.CONTROL_HOTSPOT, EventConstants.NormalClick.CONTROL_WIFI, EventConstants.NormalClick.CONTROL_POWER})
    //按钮对应的R文件id
    @ResId({R.id.iv_bluetooth, R.id.iv_internet, R.id.iv_hotspot, R.id.iv_wifi, R.id.iv_power})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bluetooth:
                mControlVM.toggleBt();
                break;
            case R.id.iv_internet:
                handleDataSwitch();
                break;
            case R.id.iv_hotspot:
                handleHotSpot();
                break;
            case R.id.iv_wifi:
                handleWifi();
                break;
            case R.id.iv_power:
                ScreenControlUtil.sendTurnOffScreenBroadCast(mContext);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KLog.d("ControlPanelFragment", "onHiddenChanged" + hidden);

        isShow = !hidden;

        if (!isHidden()) {
            mBrightnessBar.setProgress(mControlVM.getBrightness() <= 0 ? 1 : mControlVM.getBrightness());
            mVolumeBar.setProgress(mControlVM.getVolume());
            mBluetooth.setImageResource(mControlVM.getBtState() ? R.drawable.bg_bluetooth_on : R.drawable.bg_bluetooth_off);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mBrightnessBar.setProgress(mControlVM.getBrightness() <= 0 ? 1 : mControlVM.getBrightness());
        mVolumeBar.setProgress(mControlVM.getVolume());
        mBluetooth.setImageResource(mControlVM.getBtState() ? R.drawable.bg_bluetooth_on : R.drawable.bg_bluetooth_off);
        if (LoginManager.getInstance().isUserLogin()) {
            mUser = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        }

        if (getUserVisibleHint() && !isHidden()) {
            isShow = true;
        }
    }

    private void handleWifi() {
        int result;
        if (wifiWorkMode != SDKConstants.WifiMode.STA) {
            result = mControlVM.toggleWifi(true);
        } else {
            result = mControlVM.toggleWifi(false);
        }
        if (result == SDKConstants.RequestAck.RESULT_ERROR || result == SDKConstants.RequestAck.RESULT_FAILED) {
            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        }
    }

    private void handleHotSpot() {
        int result;
        if (wifiWorkMode != SDKConstants.WifiMode.AP) {
            result = mControlVM.toggleHotspot(true);
        } else {
            result = mControlVM.toggleHotspot(false);
        }
        if (result == SDKConstants.RequestAck.RESULT_ERROR || result == SDKConstants.RequestAck.RESULT_FAILED) {
            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        }
    }

    private void handleDataSwitch() {
        boolean isDataSwitch = !isOpenData;
        int dataSwitchResult = mControlVM.toggleInternet(isDataSwitch);
        KLog.d("ControlPanelFragment", "handleDataSwitch" + dataSwitchResult + "isDataSwitch" + isDataSwitch);
        //        if (dataSwitchResult == SDKConstants.RequestAck.RESULT_ERROR || dataSwitchResult == SDKConstants.RequestAck.RESULT_FAILED) {
        //            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        //        }
        //
        //        else if (dataSwitchResult == SDKConstants.RequestAck.RESULT_SUCCESS) {
        //            if (isDataSwitch) {
        //                XMToast.showToast(mContext, getString(R.string.data_switch_open), mContext.getDrawable(R.drawable.icon_refresh_n));
        //                mInternet.setImageResource(R.drawable.bg_data_switch_on);
        //            } else {
        //                XMToast.showToast(mContext, getString(R.string.data_switch_close), mContext.getDrawable(R.drawable.icon_refresh_n));
        //                mInternet.setImageResource(R.drawable.bg_data_switch_off);
        //            }
        //        }
        //操作完了之后，在主动调用一次获取当前数据开关状态是否打开，用来通知systemui那边去更新图标
        mControlVM.getInternet();
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_ON_SUCCESS)
    public void bluetoothTurnOnSuccess(String event) {
        if (isShow) {
            XMToast.showToast(mContext, getString(R.string.bluetooth_open), mContext.getDrawable(R.drawable.icon_bluetooth_n));
        }
        mBluetooth.setImageResource(R.drawable.bg_bluetooth_on);
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_ON_FAILED)
    public void bluetoothTurnOnFailed(String event) {
        if (isShow) {
            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        }
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_OFF_SUCCESS)
    public void bluetoothTurnOffSuccess(String event) {
        if (isShow) {
            XMToast.showToast(mContext, getString(R.string.bluetooth_close), mContext.getDrawable(R.drawable.icon_bluetooth_dis));
        }
        mBluetooth.setImageResource(R.drawable.bg_bluetooth_off);
    }

    @Subscriber(tag = LauncherConstants.BlUETOOTH_TURN_OFF_FAILED)
    public void bluetoothTurnOffFailed(String event) {
        if (isShow) {
            XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        }
    }

    @Subscriber(tag = LauncherConstants.IS_DATA_SWITCH)
    public void isDataSwicth(boolean isDataSwitch) {
        KLog.d("ControlPanelFragment", "isDataSwicth" + isDataSwitch);
        isOpenData = isDataSwitch;
        if (isDataSwitch) {
            if (isShow) {
                XMToast.showToast(mContext, getString(R.string.data_switch_open), mContext.getDrawable(R.drawable.icon_refresh_n));
            }
            mInternet.setImageResource(R.drawable.bg_data_switch_on);
        } else {
            if (isShow) {
                XMToast.showToast(mContext, getString(R.string.data_switch_close), mContext.getDrawable(R.drawable.icon_refresh_n));
            }
            mInternet.setImageResource(R.drawable.bg_data_switch_off);
        }
        //        mInternet.setImageResource(isDataSwitch ? R.drawable.bg_data_switch_on : R.drawable.bg_data_switch_off);
    }


    @Subscriber(tag = LauncherConstants.WIFI_WORK_PATTERN)
    public void wifiWorkPattern(int wp) {
        if (wp == SDKConstants.WifiMode.AP) {
            if (isShow) {
                XMToast.showToast(mContext, getString(R.string.hotspot_open), mContext.getDrawable(R.drawable.icon_hotspot_n));
            }
            mHotspot.setImageResource(R.drawable.bg_hotspot_on);
            mWifi.setImageResource(R.drawable.bg_wifi_off);
        } else if (wp == SDKConstants.WifiMode.STA) {
            if (isShow) {
                XMToast.showToast(mContext, getString(R.string.wifi_open), mContext.getDrawable(R.drawable.icon_wifi_n));
            }
            mWifi.setImageResource(R.drawable.bg_wifi_on);
            mHotspot.setImageResource(R.drawable.bg_hotspot_off);
        } else if (wp == SDKConstants.WifiMode.OFF) {

            if (wifiWorkMode == -1) {
                mHotspot.setImageResource(R.drawable.bg_hotspot_off);
                mWifi.setImageResource(R.drawable.bg_wifi_off);
            }

            if (wifiWorkMode == SDKConstants.WifiMode.AP) {
                if (isShow) {
                    XMToast.showToast(mContext, getString(R.string.hotspot_close), mContext.getDrawable(R.drawable.icon_hotspot_dis));
                }
                mHotspot.setImageResource(R.drawable.bg_hotspot_off);
            } else if (wifiWorkMode == SDKConstants.WifiMode.STA) {
                if (isShow) {
                    XMToast.showToast(mContext, getString(R.string.wifi_close), mContext.getDrawable(R.drawable.icon_wifi_dis));
                }
                mWifi.setImageResource(R.drawable.bg_wifi_off);
            }
        }
        wifiWorkMode = wp;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
