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
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.CMSceneDataManager;
import com.xiaoma.smarthome.common.manager.RequestManager;
import com.xiaoma.smarthome.common.model.HomeBean;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.systemuilib.NotificationUtil;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zy 2018/8/1 22:14
 * <p>
 * 场景自动执行
 */
public class SceneAutoExecuteService extends Service {

    private static final int DELAYED_MILLIES = 2000;

    private GeoFenceClient mGeoFenceClient;
    //添加的所有围栏
    private HashMap<String, SceneBean> mFences = new HashMap<>();

    private Runnable creatGeoRunnable = new Runnable() {
        @Override
        public void run() {
            LocationInfo locationInfo = LocationManager.getInstance().getCurrentLocation();
            if (locationInfo == null) {
                waitLocalLocation();
            } else {
                createGeoFence();
            }
        }
    };
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
            SceneBean sceneBean = mFences.get(customId);
            //触发开关，1表示开,0表示关
            if (sceneBean == null || sceneBean.getAutoCondition() == null || sceneBean.getAutoCondition().getAutoFlag() == SmartConstants.AUTO_EXCUTE_OFF) {
                return;
            }
            KLog.d("fence callback: " + sceneBean.getSceneName() + " distance: " + sceneBean.getAutoCondition().getRadius());

            String effictTime = sceneBean.getEffictTime();
            if (effictTime.contains("~")) {
                //eg: 8:00~23:00
                String[] time = sceneBean.getEffictTime().split("~");
                String[] start = time[0].split(":");
                String[] end = time[1].split(":");
                //如果不在范围时间内 就不执行
                if (!TimeUtils.isCurrentInTimeScope(start[0], start[1], end[0], end[1])) {
                    return;
                }
            } else {
                //只有一个时间 eg: 8:30
                String[] start = effictTime.split(":");
                Calendar instance = Calendar.getInstance();
                instance.set(Calendar.HOUR_OF_DAY, Integer.valueOf(start[0]));
                instance.set(Calendar.MINUTE, Integer.valueOf(start[1]));
                //(一分钟误差)
                instance.add(Calendar.MINUTE, 1);
                int endHour = instance.get(Calendar.HOUR_OF_DAY);
                int endMin = instance.get(Calendar.MINUTE);
                //是否在时间范围内
                if (!TimeUtils.isCurrentInTimeScope(Integer.valueOf(start[0]), Integer.valueOf(start[1]), endHour, endMin)) {
                    return;
                }
            }

            SceneBean.AutoConditionBean autoCondition = sceneBean.getAutoCondition();
            if (action == MapConstant.GEO_FENCE_IN && autoCondition.getRuleFlag() == SmartConstants.INNER) {
                excuteScene(sceneBean.getSceneId(), sceneBean.getSceneName(), sceneBean.getImgUrl(), customId);
            } else if (action == MapConstant.GEO_FENCE_OUT && autoCondition.getRuleFlag() == SmartConstants.BEYOND) {
                excuteScene(sceneBean.getSceneId(), sceneBean.getSceneName(), sceneBean.getImgUrl(), customId);
            }
        }
    };

    private void excuteScene(String sceneId, final String sceneName, final String sceneImg, final String customId) {
        RequestManager.getInstance().excuteCMScene(sceneId, new ResultCallback<XMResult<String>>() {

            @Override
            public void onSuccess(XMResult<String> result) {
                KLog.d("excute scene success: " + sceneName);
                Notification notification = NotificationUtil.builder(SceneAutoExecuteService.this,
                        getString(R.string.smart_home),
                        getString(R.string.scene_do_success, sceneName),
                        IconCompat.createWithContentUri(sceneImg).toIcon(),
                        null,
                        System.currentTimeMillis(), true)
                        .setAutoCancel(true).build();

                NotificationManagerCompat.from(SceneAutoExecuteService.this)
                        .notify(null, Integer.parseInt(customId), notification);

                EventTtsManager.getInstance().init(SceneAutoExecuteService.this);
                EventTtsManager.getInstance().startSpeaking(SceneAutoExecuteService.this.getString(R.string.auto_excute_for_you, sceneName));

            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.d("excute scene fail: " + sceneName);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.d("onCreate, sceneservice");
//        EventBus.getDefault().register(this);
        mGeoFenceClient = new GeoFenceClient(this);
        mGeoFenceClient.setGeoFenceListener(mOnGeoFenceListener);
        mGeoFenceClient.setOnFenceStatusChangeListener(mOnFenceStatusChangeListener);
        mGeoFenceClient.start();
        waitLocalLocation();
    }

    /**
     * 接收到设置家地址成功的事件或者刷新场景，更新地理围栏
     */
//    @Subscriber(tag = SmartConstants.REFRESH_SCENE)
//    private void setupHomeAddressSuccess(String msg) {
//        waitLocalLocation();
//    }


    /**
     * 若还没拿到当前定位，则等待定位成功之后添加围栏
     */
    public void waitLocalLocation() {
        ThreadDispatcher.getDispatcher().postDelayed(creatGeoRunnable, DELAYED_MILLIES, Priority.NORMAL);
    }

    private void createGeoFence() {
        deleteAllGeoFences();
        List<SceneBean> sceneBeans = CMSceneDataManager.getInstance().getSceneData();
        //查询是否有家庭地址，如果没有就不执行服务
        HomeBean homeBean = CMSceneDataManager.getInstance().getHomeBean();
        if (ListUtils.isEmpty(sceneBeans) || homeBean == null) {
//            stopSelf();
            ThreadDispatcher.getDispatcher().remove(creatGeoRunnable);
            return;
        }
        for (int i = 0; i < sceneBeans.size(); i++) {
            SceneBean sceneBean = sceneBeans.get(i);
            if (sceneBean == null || sceneBean.getAutoCondition() == null) {
                continue;
            }
            SceneBean.AutoConditionBean autoCondition = sceneBean.getAutoCondition();
            LatLng latLng = new LatLng(homeBean.getLatitude(), homeBean.getLongitude());
            mGeoFenceClient.addGeoFence(latLng, autoCondition.getRadius(), String.valueOf(i));
            mFences.put(String.valueOf(i), sceneBean);
        }
        KLog.d("createGeoFence, mFences size:" + mFences.size());
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
//        EventBus.getDefault().unregister(this);
        if (mGeoFenceClient != null) {
            mGeoFenceClient.stop();
        }
    }
}
