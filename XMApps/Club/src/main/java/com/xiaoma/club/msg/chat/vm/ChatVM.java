package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.club.common.viewmodel.BooleanState;
import com.xiaoma.club.msg.chat.model.HxChatParam;
import com.xiaoma.component.base.BaseViewModel;

/**
 * Created by LKF on 2018/10/18 0018.
 */
public class ChatVM extends BaseViewModel {
    private final MutableLiveData<Boolean> mNetworkEnable = new MutableLiveData<>();
    private final MutableLiveData<HxChatParam> mHxChatParam = new MutableLiveData<>();
    private final MutableLiveData<BooleanState> mMuteState = new MutableLiveData<>();
    private final MutableLiveData<BooleanState> mIsFriend = new MutableLiveData<>();

    public ChatVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<HxChatParam> getHxChatParam() {
        return mHxChatParam;
    }

    public MutableLiveData<Boolean> getNetworkEnable() {
        return mNetworkEnable;
    }

    /**
     * @return 禁言状态
     */
    public MutableLiveData<BooleanState> getMuteState() {
        return mMuteState;
    }

    public MutableLiveData<BooleanState> getIsFriend() {
        return mIsFriend;
    }
}
