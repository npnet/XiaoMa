package com.xiaoma.autotracker;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.xiaoma.autotracker.db.AutoTrackDBManager;
import com.xiaoma.autotracker.lifecycle.XmDataActivityLifecycleCallbacks;
import com.xiaoma.autotracker.lifecycle.XmDataFragmentLifecycleCallbacks;
import com.xiaoma.autotracker.model.AppViewScreen;
import com.xiaoma.autotracker.model.BusinessType;
import com.xiaoma.autotracker.model.TrackerEventType;
import com.xiaoma.autotracker.util.UploadScreenManager;
import com.xiaoma.autotracker.util.XmEventException;
import com.xiaoma.db.DBManager;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author taojin
 * @date 2018/12/5
 */
public class XmAutoTracker {
    private static boolean isInit = false;
    private Application application;
    private String userId;

    private XmDataActivityLifecycleCallbacks activityLifecycleCallbacks;

    public static XmAutoTracker getInstance() {
        return XmAutoTrackerHolder.instance;
    }

    private XmAutoTracker() {
        activityLifecycleCallbacks = new XmDataActivityLifecycleCallbacks();
    }

    public static class XmAutoTrackerHolder {
        static final XmAutoTracker instance = new XmAutoTracker();
    }

    public void init(Application application, String userId) {
        this.userId = userId;
        if (!isInit) {
            isInit = true;
            this.application = application;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                if (DBManager.getInstance().getDBManager().isDBManagerInitSuccess()) {
                    application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
                    AppObserver.getInstance().init(application);
                    AppObserver.getInstance().addAppStateChangedListener(new AppObserver.AppStateChangedListener() {
                        @Override
                        public void onForegroundChanged(boolean isForeground) {
                            if (isForeground) {
                                AutoTrackDBManager.getInstance().saveAppEvent(AppViewScreen.APPFOREGROUND.getAppStatus());
                            } else {
                                AutoTrackDBManager.getInstance().saveAppEvent(AppViewScreen.APPBACKGROUND.getAppStatus());
                            }
                        }
                    });
                } else {
                    autotrackerInitException();
                }
            }


            AutoTrackDBManager.getInstance().timerUpload();
            AutoTrackDBManager.getInstance().uploadAppOnOffEvent(BusinessType.APPON.getBusinessValue());

            registerUploadScreenshot();
        }
    }

    public Application getApplication() {
        return application;
    }

    public String getUserId() {
        return userId;
    }

    public void setApplication(Application application) {
        this.application = application;
    }


    /**
     * ??????????????????????????????
     *
     * @param event        ????????????
     * @param id           ?????????????????????id
     * @param pagePath     ????????????
     * @param pagePathDesc ??????????????????
     */
    public void onEvent(String event, String id, String pagePath, String pagePathDesc) {
        AutoTrackDBManager.getInstance().saveOnClickEvent(event, id, pagePath, pagePathDesc);
    }

    public void onPunchEvent(String event, int isSigned, int days, String pagePath, String pagePathDesc) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("isSigned", isSigned);
            jsonObject.put("days", days);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onEvent(event, jsonObject.toString(), pagePath, pagePathDesc);
    }

    /**
     * ??????id???name??????
     */
    public void onBusinessInfoEvent(String event, String name, String id, String pagePath, String pagePathDesc) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onEvent(event, jsonObject.toString(), pagePath, pagePathDesc);
    }

    /**
     * ??????????????????????????????
     */
    public void onEventSpeechInfo(String event, long voiceTime, String voiceContent, String id, String pagePath, String pagePathDesc) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("????????????", voiceTime);
            jsonObject.put("????????????", voiceContent);
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onEvent(event, jsonObject.toString(), pagePath, pagePathDesc);
    }

    /**
     * ??????????????????????????????
     */
    public void onEventMotorcade(String event, int hidden, int display, String pagePath, String pagePathDesc) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("?????????????????????", hidden);
            jsonObject.put("?????????????????????", display);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onEvent(event, jsonObject.toString(), pagePath, pagePathDesc);
    }

    /**
     * ??????????????????????????????
     *
     * @param event        ????????????
     * @param pagePath     ????????????
     * @param pagePathDesc ??????????????????
     */
    public void onEvent(String event, String pagePath, String pagePathDesc) {
        onEvent(event, null, pagePath, pagePathDesc);
    }

    /**
     * ??????????????????
     * playType{@link com.xiaoma.autotracker.model.PlayType}
     *
     * @param playTime
     */
    public void onEventPlayTime(long playTime, String playType) {
        AutoTrackDBManager.getInstance().uploadPlayTimeEvent(playTime, playType);
    }

    /**
     * ????????????????????????
     *
     * @param isSuccess ????????????
     * @param original  ??????????????????
     * @param result    ??????????????????
     */
    public void onEventSpeechRec(String event, String isSuccess, String original,
                                 String result, String pagePath, String pagePathDesc) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("??????????????????", original);
            jsonObject.put("??????????????????", result);
            jsonObject.put("????????????", isSuccess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onEvent(event, jsonObject.toString(), pagePath, pagePathDesc);
    }


    /**
     * ??????????????????
     *
     * @param event        ????????????
     * @param pagePath     ????????????
     * @param pagePathDesc ??????????????????
     */
    public void onEventDirectUpload(TrackerEventType type, String event, String id, String pagePath, String pagePathDesc) {
        AutoTrackDBManager.getInstance().directUploadEvent(type, event, id, pagePath, pagePathDesc);
    }

    public void onHiddenChanged(Fragment f, boolean hidden) {
        ((XmDataFragmentLifecycleCallbacks) activityLifecycleCallbacks.getFragmentLifecycleCallbacks()).onFragmentVisibilityChanged(!hidden, f);
    }

    public void setUserVisibleHint(Fragment f, boolean isVisibleToUser) {
        ((XmDataFragmentLifecycleCallbacks) activityLifecycleCallbacks.getFragmentLifecycleCallbacks()).onFragmentVisibilityChanged(isVisibleToUser, f);
    }

    /**
     * ????????????????????????Application???????????????????????????
     */
    public void saveCacheToDb() {
        AutoTrackDBManager.getInstance().saveCacheDataToDb();
    }


    /**
     * ??????????????????(?????????????????????????????????)
     *
     * @param content {??????????????????Json?????????String}
     */
    public void onEventListenInfo(String content) {

        if (!isJson(content)) {
            checkJsonFormatException(content);
            return;
        }

        AutoTrackDBManager.getInstance().uploadListenEvent(content);

    }

    /**
     * ??????????????????(?????????????????????id???????????????)
     *
     * @param content {??????????????????Json?????????String}
     */
    public void onEventClubScore(String content) {

        if (!isJson(content)) {
            checkJsonFormatException(content);
            return;
        }

        AutoTrackDBManager.getInstance().uploadClubScore(content);

    }

    private void onEvent(int eventType, int viewPageType, long onlineTime, String uiName, String uiPath, long operateTime, String appendInfo) {

    }

    private boolean isJson(String str) {
        boolean result = false;
        if (StringUtil.isNotEmpty(str)) {
            str = str.trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                result = true;
            } else if (str.startsWith("[") && str.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }


    private void checkJsonFormatException(String str) {
        String errorMsg = " if you want to collect business data you must use jsonString to wrapper " + str + " for example:\n"
                + "{\n" +
                "id:" + "business id,\n" +
                "value:" + str + "\n" +
                "}\n"
                + "...please check...";
        XmEventException.doException(errorMsg);
    }

    private void autotrackerInitException() {
        String errorMsg = " sorry, db init failed\n"
                + "...please check...";
        XmEventException.doException(errorMsg);
    }

    private void registerUploadScreenshot() {
        getApplication().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    KLog.d("onReceive push screenshot");
                    UploadScreenManager.getInstance().uploadScreenshot(activityLifecycleCallbacks.getCurrentActivity(), getUserId());
                } catch (Exception e) {
                    KLog.e(e.getMessage());
                }
            }
        }, new IntentFilter(AutoTrackerConstants.PUSH_UPLOAD_SCREENSHOT));
    }

}
