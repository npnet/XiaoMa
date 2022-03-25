package com.xiaoma.motorcade.main.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMConference;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.motorcade.common.manager.ConferenceManager;
import com.xiaoma.motorcade.common.manager.MotorcadeRepo;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.MeetingInfo;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.util.Collections;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/1/16 0016
 */
public class MainVM extends AndroidViewModel {

    private static final String TAG = "MainFragment_join";
    private static final int NEED_CREATE_MEETING = 40061;
    private MutableLiveData<XmResource<List<GroupCardInfo>>> motorcadeInfoInfoList;
    private MutableLiveData<XmResource<MeetingInfo>> meetingInfo;
    private MutableLiveData<Integer> joinNum;
    private UICallback mUICallback;

    public MainVM(@NonNull Application application) {
        super(application);
    }

    public void setUICallback(UICallback UICallback) {
        mUICallback = UICallback;
    }

    public MutableLiveData<XmResource<List<GroupCardInfo>>> getMotorcadeInfoList() {
        if (motorcadeInfoInfoList == null) {
            motorcadeInfoInfoList = new MutableLiveData<>();
        }
        return motorcadeInfoInfoList;
    }

    public MutableLiveData<XmResource<MeetingInfo>> getMeetingInfo() {
        if (meetingInfo == null) {
            meetingInfo = new MutableLiveData<>();
        }
        return meetingInfo;
    }

    public MutableLiveData<Integer> getJoinNum() {
        if (joinNum == null) {
            joinNum = new MutableLiveData<>();
        }
        return joinNum;
    }

    public void fetchMotorcadeData() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                getMotorcadeInfoList().setValue(XmResource.<List<GroupCardInfo>>loading());
            }
        });
        RequestManager.requestCarList(String.valueOf(UserUtil.getCurrentUser().getId()), new CallbackWrapper<List<GroupCardInfo>>() {
            @Override
            public List<GroupCardInfo> parse(String data) throws Exception {
                XMResult<List<GroupCardInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<GroupCardInfo>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(final List<GroupCardInfo> model) {
                super.onSuccess(model);
                if (model != null && !model.isEmpty()) {
                    model.removeAll(Collections.singleton(null));
                    getMotorcadeInfoList().postValue(XmResource.response(model));
                    getJoinNum().postValue(model.size());
                } else {
                    getMotorcadeInfoList().postValue(XmResource.<List<GroupCardInfo>>failure("empty"));
                    getJoinNum().postValue(0);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                getMotorcadeInfoList().postValue(XmResource.<List<GroupCardInfo>>error(msg));
            }
        });
    }


    public void jumpToMeeting(final long id) {
        KLog.d(TAG, "jumpToMeeting id:" + id);
        if (mUICallback != null) {
            mUICallback.onShowLoading();
        }
        getMeetingInfo().setValue(XmResource.<MeetingInfo>loading());
        RequestManager.getMeeting(id, new CallbackWrapper<MeetingInfo>() {
            @Override
            public MeetingInfo parse(String data) throws Exception {
                if (data == null) {
                    return null;
                }
                KLog.d(TAG, "getMeeting data:" + data);
                XMResult<MeetingInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<MeetingInfo>>() {
                }.getType());
                if (result == null) {
                    return null;
                }
                if (result.getResultCode() == NEED_CREATE_MEETING) {
                    MeetingInfo info = new MeetingInfo();
                    info.setId(id);
                    info.setNeedCreate(true);
                    return info;
                } else if (result.isSuccess()) {
                    result.getData().setNeedCreate(false);
                }
                return result.getData();
            }

            @Override
            public void onSuccess(MeetingInfo data) {
                super.onSuccess(data);
                if (data != null) {
                    if (data.isNeedCreate()) {
                        //新建的车队，还没有过会议
                        KLog.d(TAG, "createEMConference because of 40061 id:" + data.getId());
                        createEMConference(data.getId());
                    } else {
                        //加入会议，但是可能已过期
                        KLog.d(TAG, "joinEMConference from xiaoma server id:" + data.getId());
                        joinEMConference(data, false);
                    }
                } else {
                    getMeetingInfo().postValue(XmResource.<MeetingInfo>failure(null));
                    if (mUICallback != null) {
                        mUICallback.onDismissLoading();
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                getMeetingInfo().postValue(XmResource.<MeetingInfo>error(msg));
                if (mUICallback != null) {
                    mUICallback.onDismissLoading();
                }
            }
        });
    }

    public void reportConference(final long motorId, final String conferenceId, String password) {
        RequestManager.reportMeeting(motorId, conferenceId, password, new CallbackWrapper<MeetingInfo>() {
            @Override
            public MeetingInfo parse(String data) throws Exception {
                if (data == null) {
                    return null;
                }
                KLog.d(TAG, "reportConference data:" + data);
                XMResult<MeetingInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<MeetingInfo>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(MeetingInfo model) {
                super.onSuccess(model);
                if (model != null && motorId == model.getId()) {
                    //加入了自己创建的会议
                    getMeetingInfo().postValue(XmResource.response(model));
                } else {
                    //上报失败了，退出自己的会议再提示进入失败
                    KLog.d(TAG, "report Conference failed，someone report conference faster");
                    getMeetingInfo().postValue(XmResource.<MeetingInfo>failure(null));
                    ConferenceManager.getInstance().exitConference(null);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                //上报失败了，退出自己的会议再提示进入失败
                KLog.d(TAG, "report Conference failed， onError ：" + msg);
                getMeetingInfo().postValue(XmResource.<MeetingInfo>failure(null));
                ConferenceManager.getInstance().exitConference(null);
            }
        });
    }


    private void createEMConference(final long motorId) {
        ConferenceManager.getInstance().createAndJoinConference(new EMValueCallBack<EMConference>() {
            @Override
            public void onSuccess(EMConference emConference) {
                KLog.d(TAG, "create success id: " + emConference.getConferenceId() + "  pass: " + emConference.getPassword());
                reportConference(motorId, emConference.getConferenceId(), emConference.getPassword());
                if (mUICallback != null) {
                    mUICallback.onDismissLoading();
                }
            }

            @Override
            public void onError(int i, String s) {
                KLog.d(TAG, "create Conference failed， onError ：" + s);
                getMeetingInfo().postValue(XmResource.<MeetingInfo>failure(null));
                if (mUICallback != null) {
                    mUICallback.onDismissLoading();
                }
            }
        });
    }

    private void joinEMConference(final MeetingInfo info, final boolean isOther) {
        ConferenceManager.getInstance().joinConference(info.getMeetingAccount(), new EMValueCallBack<EMConference>() {
            @Override
            public void onSuccess(EMConference emConference) {
                KLog.d(TAG, "join success id: " + emConference.getConferenceId()
                        + "  pass: " + emConference.getPassword());
                //加入会议成功进入地图共享
                getMeetingInfo().postValue(XmResource.response(info));
                if (mUICallback != null) {
                    mUICallback.onDismissLoading();
                }
            }

            @Override
            public void onError(int i, String s) {
                KLog.d(TAG, "join onError code: " + i + "  msg: " + s + " isOther: " + isOther);
                if (!isOther) {
                    // TODO: 2019/4/17 0017 加入会议失败可能有其它原因，不止是后台返回的会议过期了
                    //后台返回的会议已过期，需自己创建
                    //if (i == EMError.CALL_CONFERENCE_NO_EXIST)
                    createEMConference(info.getId());
                } else {
                    //加入别人的会议失败
                    KLog.d(TAG, "join other Conference failed， onError ：" + s);
                    getMeetingInfo().postValue(XmResource.<MeetingInfo>failure(null));
                    if (mUICallback != null) {
                        mUICallback.onDismissLoading();
                    }
                }
            }
        });
    }

    private void joinOtherConference(final MeetingInfo meetingInfo) {
        ConferenceManager.getInstance().exitConference(new EMValueCallBack() {
            @Override
            public void onSuccess(Object o) {
                joinEMConference(meetingInfo, true);
            }

            @Override
            public void onError(int i, String s) {
                joinEMConference(meetingInfo, true);
            }
        });
    }

    public void saveMotorcadeDatas(final List<GroupCardInfo> motorcadeInfos) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                MotorcadeRepo.getInstance().getMainRepo().saveMotorcadeLists(motorcadeInfos);
            }
        });
    }

    public List<GroupCardInfo> getInfosFromRepo() {
        List<GroupCardInfo> infos = MotorcadeRepo.getInstance().getMainRepo().getcarTeamList();
        return infos;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        motorcadeInfoInfoList = null;
        joinNum = null;
    }

    public interface UICallback {
        void onShowLoading();

        void onDismissLoading();
    }
}
