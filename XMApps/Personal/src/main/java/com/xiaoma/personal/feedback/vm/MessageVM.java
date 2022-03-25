package com.xiaoma.personal.feedback.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.feedback.model.MessageInfo;

/**
 * @author Gillben
 * date: 2018/12/05
 * <p>
 * 处理反馈消息请求
 */
public class MessageVM extends BaseViewModel {


    public MessageVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<XmResource<MessageInfo>> getMessageList(int pageNum, int pageSize) {
        final MutableLiveData<XmResource<MessageInfo>> liveData = new MutableLiveData<>();

        RequestManager.getFeedbackMessageRecord(pageNum, pageSize, new ResultCallback<XMResult<MessageInfo>>() {
            @Override
            public void onSuccess(XMResult<MessageInfo> model) {
                if (model != null) {
                    liveData.setValue(XmResource.success(model.getData()));
                } else {
                    liveData.setValue(XmResource.failure("mode is null."));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                liveData.setValue(XmResource.failure(msg));
            }
        });
        return liveData;
    }


}
