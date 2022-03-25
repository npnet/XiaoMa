package com.xiaoma.assistant.manager.api;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.aidl.model.ScheduleBean;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.EventConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.NaviState;

/**
 * Created by qiuboxiang on 2019/4/9 17:58
 * Desc:
 */
public class LauncherApiManager extends ApiManager {

    private static final String TAG = "QBX 【" + LauncherApiManager.class.getSimpleName() + "】";
    private static LauncherApiManager mInstance;

    private LauncherApiManager() {
    }

    public static LauncherApiManager getInstance() {
        if (mInstance == null) {
            synchronized (LauncherApiManager.class) {
                if (mInstance == null) {
                    mInstance = new LauncherApiManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.LAUNCHER, CenterConstants.LAUNCHER_PORT);
    }

    @Override
    public void init() {
        getNaviState();
    }

    private void getNaviState() {
        Log.d(TAG, "getNaviState");
        requestWithoutTTS(CenterConstants.LauncherThirdAction.QUERY_NAVI_STATE, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                int naviState = extra.getInt(CenterConstants.Assistant.RESULT);
                Log.d("QBX", "getNaviState: " + naviState);
              /*  if (naviState == XmMapNaviManager.getInstance().getCurrentStatus()) {
                    return;
                }*/
                XmMapNaviManager.getInstance().setCurrentStatus(naviState);
                if (naviState == 4 || naviState == 7) {
                    RemoteIatManager.getInstance().uploadNaviState(NaviState.navigation);
                } else if (naviState == 2) {
                    RemoteIatManager.getInstance().uploadNaviState(NaviState.routing);
                } else {
                    RemoteIatManager.getInstance().uploadNaviState(NaviState.noNavi);
                }
            }
        });
    }

    public void createSchedule(ScheduleBean scheduleBean) {
        Log.d(TAG, "createSchedule: ");
        Bundle bundle = new Bundle();
        bundle.putParcelable(CenterConstants.LauncherThirdBundleKey.SCHEDULE_BEAN, scheduleBean);
        request(CenterConstants.LauncherThirdAction.CREATE_SCHEDULE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.LauncherThirdBundleKey.RESULT);
                closeAfterSpeak(success ? context.getString(R.string.added) : context.getString(R.string.add_failure));
                XmAutoTracker.getInstance().onEvent(EventConstants.createSchedule, GsonHelper.toJson(scheduleBean),
                        "AssistantService", EventConstants.speechService);
            }
        });
    }

    public void querySchedule(IClientCallback callback) {
        Log.d(TAG, "querySchedule: ");
        request(CenterConstants.LauncherThirdAction.QUERY_SCHEDULE, null, callback);
    }
}
