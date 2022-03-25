package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.User;

/**
 * Created by LKF on 2019-1-14 0014.
 */
public class ChatOptVM extends BaseViewModel {
    private LiveData<User> mChatToUser;

    public ChatOptVM(@NonNull Application application) {
        super(application);
    }

    @MainThread
    public boolean init(String userKey) {
        if (mChatToUser != null)
            return false;
        return (mChatToUser = ClubRepo.getInstance()
                .getUserRepo()
                .getLiveDataByKey(userKey)) != null;
    }

    public LiveData<User> getChatToUser() {
        return mChatToUser;
    }
}
