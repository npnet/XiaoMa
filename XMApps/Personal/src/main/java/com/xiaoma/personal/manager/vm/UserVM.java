package com.xiaoma.personal.manager.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.model.User;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/19
 *     desc   :
 * </pre>
 */
public class UserVM extends BaseViewModel {
    private MutableLiveData<List<User>> users;
    public UserVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<List<User>> getRecommendList() {
        if (users == null) {
            users = new MutableLiveData<>();
        }
        return users;
    }

    public void fetchUsers(long id) {

        User users = UserBindManager.getInstance().getCachedUserById(id);
        getRecommendList().setValue(Collections.singletonList(users));
    }
}
