package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.component.base.BaseViewModel;

/**
 * Created by LKF on 2019-1-14 0014.
 */
public class GroupChatOptVM extends BaseViewModel {
    private final MutableLiveData<GroupCardInfo> mGroupInfo = new MutableLiveData<>();

    public GroupChatOptVM(@NonNull Application application) {
        super(application);
    }

    /**
     * @return 群组数据
     */
    public MutableLiveData<GroupCardInfo> getGroupInfo() {
        return mGroupInfo;
    }
}
