package com.xiaoma.launcher.common.manager;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.aidl.model.ScheduleBean;
import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.smarthome.common.model.XiaoMiTtsBean;
import com.xiaoma.launcher.schedule.manager.ScheduleDataManager;
import com.xiaoma.launcher.schedule.manager.ScheduleRemindManager;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.smarthome.common.callback.SMIatCallback;
import com.xiaoma.smarthome.common.manager.RequestManager;
import com.xiaoma.smarthome.common.manager.SmartHomeIatManager;
import com.xiaoma.smarthome.common.manager.SmartHomeManager;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/4/9 16:49
 * Desc:
 */
public class LauncherClient extends Client {

    private static LauncherClient mInstance;

    private LauncherClient() {
        super(AppHolder.getInstance().getAppContext(), CenterConstants.LAUNCHER_PORT);
    }

    public static LauncherClient getInstance() {
        if (mInstance == null) {
            synchronized (LauncherClient.class) {
                if (mInstance == null) {
                    mInstance = new LauncherClient();
                }
            }
        }
        return mInstance;
    }

    @Override
    protected void onReceive(int action, Bundle data) {

    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        switch (action) {
            case CenterConstants.LauncherThirdAction.CREATE_SCHEDULE:
                createSchedule(data, callback);
                break;
            case CenterConstants.LauncherThirdAction.QUERY_SCHEDULE:
                querySchedule(data, callback);
                break;
            case CenterConstants.LauncherThirdAction.QUERY_NAVI_STATE:
                callbackIntResult(callback, XmMapNaviManager.getInstance().getCurrentStatus(), CenterConstants.Assistant.RESULT);
                break;

            case CenterConstants.AssistantSmartHomeAction.SCENE_CONTROL_ACTION:
                String type = data.getString(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME);
                if ("回家".equals(type)) {
                    excuteScene(callback, "回家模式");
                } else if ("离家".equals(type)) {
                    excuteScene(callback, "离家模式");
                }
                break;
            case CenterConstants.AssistantSmartHomeAction.WHICH_DEVICES_ONLINE:
                SmartHomeIatManager.getInstance().queryOnlineDevices(new SMIatCallback() {
                    @Override
                    public void callback(boolean result) {
                        //登陆失败
                        Bundle bundle = new Bundle();
                        bundle.putString(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_DEVICES_ONLINE_CALLBACK,
                                "-1");
                        callback.setData(bundle);
                        callback.callback();
                    }

                    @Override
                    public void callback(boolean result, List<String> list) {
                        Bundle bundle = new Bundle();
                        bundle.putString(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_DEVICES_ONLINE_CALLBACK,
                                list == null ? "" : GsonHelper.toJson(list));
                        callback.setData(bundle);
                        callback.callback();
                    }
                });
                break;
            case CenterConstants.AssistantSmartHomeAction.REFRESH_SCENE_LIST:
                refreshScene(callback);
                break;
            case CenterConstants.AssistantSmartHomeAction.XIAOMI_SMARTHOME_MESSAGE:
                LocationInfo locationInfo = LocationManager.getInstance().getCurrentLocation();
                RequestManager.getInstance()
                        .textParserMI(data.getString(CenterConstants.AssistantSmartHomeBundleKey.XIAOMI_SMARTHOME_MESSAGE)
                                , locationInfo.getLatitude(), locationInfo.getLongitude(), new ResultCallback<XMResult<XiaoMiTtsBean>>() {
                                    @Override
                                    public void onSuccess(XMResult<XiaoMiTtsBean> result) {
                                        if (result.getData() != null) {
                                            if (!ListUtils.isEmpty(result.getData().getTts())) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString(CenterConstants.AssistantSmartHomeBundleKey.XIAOMI_SMARTHOME_CALLBACK,
                                                        result.getData().getTts().get(0).getText());
                                                callback.setData(bundle);
                                                callback.callback();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(int code, String msg) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(CenterConstants.AssistantSmartHomeBundleKey.XIAOMI_SMARTHOME_CALLBACK,
                                                msg);
                                        callback.setData(bundle);
                                        callback.callback();
                                    }
                                });
                break;
            default:
                break;
        }
    }

    private void refreshScene(ClientCallback callback) {
        SmartHomeIatManager.getInstance().refreshScene(new SMIatCallback() {
            @Override
            public void callback(boolean result) {
                callbackBooleanResult(callback, result, CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_REFRESH_SCENE_CALLBACK);
            }

            @Override
            public void callback(boolean result, List<String> list) {

            }
        });
    }

    private void excuteScene(ClientCallback callback, String sceneName) {
        //执行场景，云米和小白都执行，有一个成功就返回成功，两个都失败才返回失败
        if (SmartHomeManager.getInstance().isLogin()) {
            //小白执行场景
            SmartHomeManager.getInstance().sceneControl(sceneName);
        }

        //云米执行场景
        SmartHomeIatManager.getInstance().excuteScene(sceneName, new SMIatCallback() {
            @Override
            public void callback(boolean result) {
                callbackBooleanResult(callback, result || SmartHomeManager.getInstance().isLogin(), CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_CALLBACK);
            }

            @Override
            public void callback(boolean result, List<String> list) {

            }
        });

    }

    private void createSchedule(Bundle data, ClientCallback callback) {
        data.setClassLoader(ScheduleBean.class.getClassLoader());
        ScheduleBean bean = data.getParcelable(CenterConstants.LauncherThirdBundleKey.SCHEDULE_BEAN);
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setMessage(bean.getContent());
        String date = bean.getDatetime().getDate().replaceAll("-", "/");
        String time = getFormattedTime(bean.getDatetime().getTime());
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            callbackBooleanResult(callback, false, CenterConstants.LauncherThirdBundleKey.RESULT);
            return;
        }
        scheduleInfo.setDate(date);
        scheduleInfo.setTime(time);
        scheduleInfo.setTimestamp(DateUtil.date2Ms(date + time));
        ScheduleRemindManager.getInstance().addScheduleRemind(scheduleInfo);
        callbackBooleanResult(callback, true, CenterConstants.LauncherThirdBundleKey.RESULT);
        EventBus.getDefault().post(scheduleInfo, LauncherConstants.UPDATE_STATUS);
    }

    private String getFormattedTime(String time) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date = oldFormat.parse(time);
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void querySchedule(Bundle data, ClientCallback callback) {
        String currentFormatDate = DateUtil.getCurrentFormatDate();
        //获取当天的日程
        List<ScheduleInfo> scheduleInfos = ScheduleDataManager.getLocalScheduleInfosForDate(currentFormatDate);
        callbackListResult(callback, scheduleInfos, CenterConstants.LauncherThirdBundleKey.RESULT);
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {

    }

    private void callbackIntResult(ClientCallback callback, int result, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putInt(bundleKey, result);
        callback.setData(bundle);
        callback.callback();
    }

    private void callbackStringResult(ClientCallback callback, String result, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putString(bundleKey, result);
        callback.setData(bundle);
        callback.callback();
    }

    private void callbackBooleanResult(ClientCallback callback, boolean result, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(bundleKey, result);
        callback.setData(bundle);
        callback.callback();
    }

    private void callbackListResult(ClientCallback callback, List<ScheduleInfo> list, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(bundleKey, (ArrayList<? extends Parcelable>) list);
        callback.setData(bundle);
        callback.callback();
    }

}
