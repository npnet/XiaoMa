package com.xiaoma.personal.taskcenter.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.PageWrapper;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.R;
import com.xiaoma.personal.coin.model.CoinAndSignInfo;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.taskcenter.model.TaskNote;

import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/12/03
 *     desc   :
 * </pre>
 */
public class TaskCenterVM extends BaseViewModel {

    private MutableLiveData<XmResource<CoinAndSignInfo>> mSignedInfo;
    private MutableLiveData<XmResource<XMResult<OnlyCode>>> mSignInState;

    public TaskCenterVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<CoinAndSignInfo>> getSignedInfoLiveData() {
        if (mSignedInfo == null) {
            mSignedInfo = new MutableLiveData<>();
        }
        return mSignedInfo;
    }

    public MutableLiveData<XmResource<XMResult<OnlyCode>>> getSignInStateLiveData() {
        if (mSignInState == null) {
            mSignInState = new MutableLiveData<>();
        }
        return mSignInState;
    }

    private MutableLiveData<XmResource<List<TaskNote>>> mTaskNoteListLiveData;

    public MutableLiveData<XmResource<List<TaskNote>>> getTaskNoteListLiveData() {
        if (mTaskNoteListLiveData == null) {
            mTaskNoteListLiveData = new MutableLiveData<>();
        }
        return mTaskNoteListLiveData;
    }

    public void fetchTaskNoteList(int page) {
        getTaskNoteListLiveData().setValue(XmResource.<List<TaskNote>>loading());
        RequestManager.fetchTaskNote(new ResultCallback<XMResult<PageWrapper<TaskNote>>>() {
            @Override
            public void onSuccess(XMResult<PageWrapper<TaskNote>> result) {
                List<TaskNote> list = result.getData().getList();
                getTaskNoteListLiveData().setValue(XmResource.success(list));
            }

            @Override
            public void onFailure(int code, String msg) {
                getTaskNoteListLiveData().setValue(XmResource.<List<TaskNote>>error(code, msg));
            }

        }, page);
    }

    public void fetchSignedInInfo() {
        getSignedInfoLiveData().setValue(XmResource.<CoinAndSignInfo>loading());
        RequestManager.getUserCarCoin(new ResultCallback<XMResult<CoinAndSignInfo>>() {
            @Override
            public void onSuccess(XMResult<CoinAndSignInfo> model) {
                if (model == null) {
                    onFailure(-1, getApplication().getString(R.string.no_network));
                    return;
                }
                if (model.isSuccess() && model.getData() != null) {
                    getSignedInfoLiveData().setValue(XmResource.success(model.getData()));
                } else {
                    onFailure(model.getResultCode(), model.getResultMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                getSignedInfoLiveData().setValue(XmResource.<CoinAndSignInfo>failure(msg));
            }
        });
    }

    public void signIn() {
        getSignInStateLiveData().setValue(XmResource.<XMResult<OnlyCode>>loading());
        RequestManager.signInToday(new ResultCallback<XMResult<OnlyCode>>() {
            @Override
            public void onSuccess(XMResult<OnlyCode> model) {
                if (model == null) {
                    onFailure(-1, getApplication().getString(R.string.no_network));
                    return;
                }
                if (model.isSuccess()) {
                    getSignInStateLiveData().setValue(XmResource.success(model));
                    fetchSignedInInfo();
                } else {
                    onFailure(model.getResultCode(), model.getResultMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                getSignInStateLiveData().setValue(XmResource.<XMResult<OnlyCode>>failure(msg));
            }
        });
    }
}
