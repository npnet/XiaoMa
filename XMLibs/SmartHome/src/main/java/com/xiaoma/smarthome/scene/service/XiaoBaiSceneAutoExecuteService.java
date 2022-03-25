package com.xiaoma.smarthome.scene.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;

import com.xiaoma.mapadapter.constant.MapConstant;
import com.xiaoma.mapadapter.listener.OnFenceStatusChangeListener;
import com.xiaoma.mapadapter.listener.OnGeoFenceListener;
import com.xiaoma.mapadapter.manager.GeoFenceClient;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.SmartHomeManager;
import com.xiaoma.smarthome.common.model.ExecuteCondition;
import com.xiaoma.smarthome.scene.model.AutoExecuteData;
import com.xiaoma.systemuilib.NotificationUtil;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.tts.EventTtsManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zy 2018/8/1 22:14
 * <p>
 * 场景自动执行
 */
public class XiaoBaiSceneAutoExecuteService extends Service {

    private static final int DELAYED_MILLIES = 2000;

    private GeoFenceClient mGeoFenceClient;
    //添加的所有围栏
    private HashMap<String, AutoExecuteData> mFences = new HashMap<>();

    /**
     * 创建围栏回调
     */
    private OnGeoFenceListener mOnGeoFenceListener = new OnGeoFenceListener() {
        @Override
        public void onGeoFenceCreateFinished(int code, String msg) {
            KLog.d("onGeoFenceCreateFinished：code=" + code + "--msg=" + msg);
            //nothing
        }
    };

    /**
     * 围栏状态改变回调
     */
    private OnFenceStatusChangeListener mOnFenceStatusChangeListener = new OnFenceStatusChangeListener() {
        @Override
        public void onFenceStatusChange(int action, String customId) {
            if (action == MapConstant.GEO_FENCE_IN) {//进入围栏
                AutoExecuteData autoExecuteData = mFences.get(customId);
                KLog.d(String.format("onFenceStatusChange 进入围栏：customId= %s,data= %s", customId, String.valueOf(autoExecuteData)));
                if (autoExecuteData != null && SmartConstants.GO_HOME.equals(autoExecuteData.cmdName)) {
                    KLog.d("onFenceStatusChange 进入围栏：cmdName=" + autoExecuteData.cmdName);
                    if (!TimeUtils.isCurrentInTimeScope(autoExecuteData.startTimeHour, 0, autoExecuteData.endTimeHour, 0)) {
                        return;
                    }
                    showNotification(getString(R.string.scene_do_success, autoExecuteData.cmdName), customId);
                    SmartHomeManager.getInstance().sceneControl(autoExecuteData.cmdName);
                    XMToast.showToast(getApplication(), getString(R.string.scene_do_success, autoExecuteData.cmdName));
                }

            } else if (action == MapConstant.GEO_FENCE_OUT) {//出围栏
                AutoExecuteData autoExecuteData = mFences.get(customId);
                KLog.d(String.format("onFenceStatusChange 出围栏：customId= %s,data= %s", customId, String.valueOf(autoExecuteData)));
                if (autoExecuteData != null && SmartConstants.OUT_HOME.equals(autoExecuteData.cmdName)) {
                    KLog.d("onFenceStatusChange 出围栏：cmdName=" + autoExecuteData.cmdName);
                    if (!TimeUtils.isCurrentInTimeScope(autoExecuteData.startTimeHour, 0, autoExecuteData.endTimeHour, 0)) {
                        return;
                    }
                    showNotification(getString(R.string.scene_do_success, autoExecuteData.cmdName), customId);
                    SmartHomeManager.getInstance().sceneControl(autoExecuteData.cmdName);
                    XMToast.showToast(getApplication(), getString(R.string.scene_do_success, autoExecuteData.cmdName));
                }
            }
        }
    };

    private void showNotification(String sceneName, String customId) {
        Notification notification = NotificationUtil.builder(this,
                "公子小白智能家居", getString(R.string.scene_do_success, sceneName), IconCompat.createWithResource(this, R.drawable.icon_default_icon).toIcon(),
                null, System.currentTimeMillis(), true).setAutoCancel(true).build();

        NotificationManagerCompat.from(this)
                .notify(null, Integer.parseInt(customId), notification);

        EventTtsManager.getInstance().init(this);
        EventTtsManager.getInstance().startSpeaking(this.getString(R.string.auto_excute_for_you, sceneName));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.d("onCreate");
        EventBus.getDefault().register(this);
        mGeoFenceClient = new GeoFenceClient(this);
        mGeoFenceClient.setGeoFenceListener(mOnGeoFenceListener);
        mGeoFenceClient.setOnFenceStatusChangeListener(mOnFenceStatusChangeListener);
        mGeoFenceClient.start();
        waitLocalLocation();
    }

    /**
     * 接收到设置家地址成功的事件后，更新地理围栏
     *
     * @param i
     */
    @Subscriber(tag = SmartConstants.REFRESH_XIAO_BAI_SCENE)
    private void setupHomeAddressSuccess(int i) {
        waitLocalLocation();
    }

    /**
     * 若还没拿到当前定位，则等待定位成功之后添加围栏
     */
    public void waitLocalLocation() {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                LocationInfo locationInfo = LocationManager.getInstance().getCurrentLocation();
                if (locationInfo == null) {
                    waitLocalLocation();

                } else {
                    createGeoFence();
                }
            }
        }, DELAYED_MILLIES);
    }

    private void createGeoFence() {
        deleteAllGeoFences();
        List<AutoExecuteData> autoExecuteData = getAutoExecuteData();
        if (ListUtils.isEmpty(autoExecuteData)) {
//            stopSelf();
            return;
        }

        int size = autoExecuteData.size();
        for (int i = 0; i < size; i++) {
            AutoExecuteData autoExecuteDatum = autoExecuteData.get(i);
            LatLng latLng = new LatLng(autoExecuteDatum.lat, autoExecuteDatum.lng);
            mGeoFenceClient.addGeoFence(latLng, autoExecuteDatum.distance, i + "");
            mFences.put(i + "", autoExecuteDatum);
        }
    }

    private List<AutoExecuteData> getAutoExecuteData() {
        List<AutoExecuteData> autoExecuteData = new ArrayList<>();
        autoExecuteData.clear();
        boolean goHomeAuto = TPUtils.get(this, SmartConstants.KEY_ARRIVE_XIAOBAI_IS_AUTO, false);
        SearchAddressInfo goHomeAddressInfo = TPUtils.getObject(this, SmartConstants.KEY_ARRIVE_ADDRESS_INFO, SearchAddressInfo.class);
        ExecuteCondition goHomeExecute = TPUtils.getObject(this, SmartConstants.KEY_ARRIVE_EXECUTE_CONDITION, ExecuteCondition.class);
        if (goHomeExecute == null) {
            goHomeExecute = new ExecuteCondition("0:00", "24:00", 500);
        }
        if (goHomeAuto && goHomeAddressInfo != null && goHomeAddressInfo.latLonPoint != null) {
            AutoExecuteData goHome = new AutoExecuteData();
            goHome.cmdName = SmartConstants.GO_HOME;
            goHome.distance = goHomeExecute.getDistance();
            goHome.lat = goHomeAddressInfo.latLonPoint.getLatitude();
            goHome.lng = goHomeAddressInfo.latLonPoint.getLongitude();
            goHome.startTimeHour = Integer.parseInt(goHomeExecute.getStartTime().split(":")[0]);
            goHome.endTimeHour = Integer.parseInt(goHomeExecute.getEndTime().split(":")[0]);
            autoExecuteData.add(goHome);
        }

        boolean outHomeAuto = TPUtils.get(this, SmartConstants.KEY_LEAVE_XIAOBAI_IS_AUTO, false);
        ExecuteCondition outHomeExecute = TPUtils.getObject(this, SmartConstants.KEY_LEAVE_EXECUTE_CONDITION, ExecuteCondition.class);
        if (outHomeExecute == null) {
            outHomeExecute = new ExecuteCondition("0:00", "24:00", 500);
        }
        if (outHomeAuto && goHomeAddressInfo != null && goHomeAddressInfo.latLonPoint != null) {
            AutoExecuteData outHome = new AutoExecuteData();
            outHome.cmdName = SmartConstants.OUT_HOME;
            outHome.distance = outHomeExecute.getDistance();
            outHome.lat = goHomeAddressInfo.latLonPoint.getLatitude();
            outHome.lng = goHomeAddressInfo.latLonPoint.getLongitude();
            outHome.startTimeHour = Integer.parseInt(outHomeExecute.getStartTime().split(":")[0]);
            outHome.endTimeHour = Integer.parseInt(outHomeExecute.getEndTime().split(":")[0]);
            autoExecuteData.add(outHome);
        }
        return autoExecuteData;
    }

    private void deleteAllGeoFences() {
        for (String id : mFences.keySet()) {
            if (mGeoFenceClient != null) {
                mGeoFenceClient.removeGeoFence(id);
            }
        }
        mFences.clear();
    }

    private void deleteGeoFence(String id) {
        if (mGeoFenceClient != null) {
            mGeoFenceClient.removeGeoFence(id);
        }
        if (mFences.containsKey(id)) {
            mFences.remove(id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.d("onDestroy");
        EventBus.getDefault().unregister(this);
        if (mGeoFenceClient != null) {
            mGeoFenceClient.stop();
        }
    }
}
