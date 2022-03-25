package com.xiaoma.songname.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.songname.common.manager.RequestManager;
import com.xiaoma.songname.model.GameResult;
import com.xiaoma.songname.model.SongNameBean;


public class SongNameVM extends BaseViewModel {

    private MutableLiveData<XmResource<SongNameBean.SongSubjectBean>> mSubject;
    private MutableLiveData<XmResource<Object>> mReportResult;
    private MutableLiveData<XmResource<GameResult>> mGameResult;

    public SongNameVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<SongNameBean.SongSubjectBean>> getSubject() {
        if (mSubject == null) {
            mSubject = new MutableLiveData<>();
        }
        return mSubject;
    }

    public MutableLiveData<XmResource<Object>> getReportResult() {
        if (mReportResult == null) {
            mReportResult = new MutableLiveData<>();
        }
        return mReportResult;
    }

    public MutableLiveData<XmResource<GameResult>> getGameResult() {
        if (mGameResult == null) {
            mGameResult = new MutableLiveData<>();
        }
        return mGameResult;
    }

    public void fetchSubject(String uid) {
        getSubject().setValue(XmResource.<SongNameBean.SongSubjectBean>loading());
        RequestManager.getInstance().getSubject(uid, new ResultCallback<XMResult<SongNameBean>>() {
            @Override
            public void onSuccess(XMResult<SongNameBean> result) {
                getSubject().setValue(XmResource.success(result.getData().getSongSubject()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getSubject().setValue(XmResource.<SongNameBean.SongSubjectBean>failure(msg));
            }
        });
    }

    public void reportResult(String uid, int subjectId) {
        RequestManager.getInstance().reportResult(uid, subjectId, new ResultCallback<XMResult<Object>>() {
            @Override
            public void onSuccess(XMResult<Object> result) {
                getReportResult().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getReportResult().setValue(XmResource.failure(msg));
            }
        });
    }

    public void getTotalPoints(String uid) {
        RequestManager.getInstance().getTotalPoints(uid, new ResultCallback<XMResult<GameResult>>() {
            @Override
            public void onSuccess(XMResult<GameResult> result) {
                getGameResult().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getGameResult().setValue(XmResource.<GameResult>failure(msg));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mSubject = null;
    }
}
