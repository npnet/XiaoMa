package com.xiaoma.motorcade.map.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XMResult;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.BaseResult;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.motorcade.map.model.RobMicResult;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: loren
 * Date: 2019/4/15 0015
 */

public class ConferenceVM extends BaseViewModel {

    private static final String TAG = "ConferenceVM";

    public ConferenceVM(@NonNull Application application) {
        super(application);
    }

    private static final long ONLINE_REPORT_PERIOD = 60 * 1000;
    private static final long ROB_MIC_PERIOD = 2 * 1000;
    private ScheduledExecutorService executorService;
    private ScheduledExecutorService robService;
    private boolean isFirst = false;

    public MutableLiveData<List<GroupMemberInfo>> groupMemberList;
    public MutableLiveData<RobMicResult> robMicResult;

    public MutableLiveData<List<GroupMemberInfo>> getGroupMemberList() {
        if (groupMemberList == null) {
            groupMemberList = new MutableLiveData<>();
        }
        return groupMemberList;
    }

    public MutableLiveData<RobMicResult> getRobMicResult() {
        if (robMicResult == null) {
            robMicResult = new MutableLiveData<>();
        }
        return robMicResult;
    }

    public void startOnlineReport(final long carTeamId) {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                reportOnline(carTeamId, true);
            }
        }, 0, ONLINE_REPORT_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void reportOnline(long carTeamId, final boolean online) {
        RequestManager.reportOnline(carTeamId, online, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                KLog.d(TAG, "report success, online status: " + online);
            }
        });
    }

    public void fetchMember(String carTeamId) {
        RequestManager.getTeamMemebers(carTeamId, new CallbackWrapper<List<GroupMemberInfo>>() {
            @Override
            public List<GroupMemberInfo> parse(String data) throws Exception {
                XMResult<List<GroupMemberInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<GroupMemberInfo>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(List<GroupMemberInfo> model) {
                super.onSuccess(model);
                if (model != null && !model.isEmpty()) {
                    getGroupMemberList().setValue(model);
                } else {
                    getGroupMemberList().postValue(null);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                getGroupMemberList().postValue(null);
            }
        });

    }

    public void startRobMic(final long carTeamId) {
        this.isFirst = true;
        if (robService == null || robService.isShutdown()) {
            robService = Executors.newSingleThreadScheduledExecutor();
        }
        robService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                robMicPeriod(carTeamId);
            }
        }, 0, ROB_MIC_PERIOD, TimeUnit.MILLISECONDS);
    }

    private void robMicPeriod(final long carTeamId) {
        RequestManager.robMicrophone(carTeamId, new CallbackWrapper<RobMicResult>() {
            @Override
            public RobMicResult parse(String data) throws Exception {
                if (data == null) {
                    return null;
                }
                KLog.d(TAG, "robMicPeriod id: " + data);
                XMResult<RobMicResult> result = GsonHelper.fromJson(data, new TypeToken<XMResult<RobMicResult>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                RobMicResult robMicResult = result.getData();
                robMicResult.setFirst(isFirst);
                isFirst = false;
                return result.getData();
            }

            @Override
            public void onSuccess(RobMicResult model) {
                super.onSuccess(model);
                getRobMicResult().postValue(model);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                getRobMicResult().postValue(null);
            }
        });
    }

    public void stopRobMic() {
        if (robService != null && !robService.isShutdown()) {
            robService.shutdownNow();
        }
    }

    public void releaseMic(final long carTeamId) {
        stopRobMic();
        RequestManager.releaseRobMicrophone(carTeamId, new CallbackWrapper<BaseResult>() {
            @Override
            public BaseResult parse(String data) throws Exception {
                if (data == null) {
                    return null;
                }
                KLog.d(TAG, "releaseMic id: " + data);
                BaseResult result = GsonHelper.fromJson(data, new TypeToken<BaseResult>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result;
            }

            @Override
            public void onSuccess(BaseResult model) {
                super.onSuccess(model);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
            }
        });
    }

    public void cancleMic(final long carTeamId) {
        stopRobMic();
        RequestManager.cancelRobMicrophone(carTeamId, new CallbackWrapper<BaseResult>() {
            @Override
            public BaseResult parse(String data) throws Exception {
                if (data == null) {
                    return null;
                }
                KLog.d(TAG, "releaseMic id: " + data);
                BaseResult result = GsonHelper.fromJson(data, new TypeToken<BaseResult>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result;
            }

            @Override
            public void onSuccess(BaseResult model) {
                super.onSuccess(model);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groupMemberList = null;
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (robService != null) {
            robService.shutdownNow();
        }
    }
}
