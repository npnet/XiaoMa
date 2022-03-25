package com.xiaoma.motorcade.setting.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.utils.GsonHelper;

import java.util.Collections;
import java.util.List;

/**
 * @author zs
 * @date 2019/1/21 0021.
 */
public class StatusVM extends BaseViewModel {

    public MutableLiveData<XmResource<List<GroupMemberInfo>>> groupMemberList;

    public StatusVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<GroupMemberInfo>>> getGroupMemberList() {
        if (groupMemberList == null) {
            groupMemberList = new MutableLiveData<>();
        }
        return groupMemberList;
    }

    public void fetchMember(String carTeamId) {
        getGroupMemberList().setValue(XmResource.<List<GroupMemberInfo>>loading());
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
                    model.removeAll(Collections.singleton(null));
                    getGroupMemberList().postValue(XmResource.response(model));
                } else {
                    getGroupMemberList().postValue(XmResource.<List<GroupMemberInfo>>failure("empty"));
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                getGroupMemberList().postValue(XmResource.<List<GroupMemberInfo>>error(msg));
            }
        });

    }


    @Override
    protected void onCleared() {
        super.onCleared();
        groupMemberList = null;
    }

}
