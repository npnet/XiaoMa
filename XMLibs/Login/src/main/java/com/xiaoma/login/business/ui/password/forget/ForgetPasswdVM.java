package com.xiaoma.login.business.ui.password.forget;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.login.common.RequestManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.callback.SimpleCallback;

/**
 * Created by kaka
 * on 19-5-24 下午5:12
 * <p>
 * desc: #a
 * </p>
 */
public class ForgetPasswdVM extends BaseViewModel {

    public ForgetPasswdVM(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<User> mUser;
    private MutableLiveData<String> mPageTag;
    private MutableLiveData<String> mPasswd;

    public MutableLiveData<User> getUser() {
        if (mUser == null) {
            mUser = new MutableLiveData<>();
        }
        return mUser;
    }

    public void setUser(User user) {
        getUser().setValue(user);
    }

    public MutableLiveData<String> getPasswd() {
        if (mPasswd == null) {
            mPasswd = new MutableLiveData<>();
        }
        return mPasswd;
    }

    public void setPasswd(String passwd) {
        getPasswd().setValue(passwd);
    }

    public MutableLiveData<String> getPageTag() {
        if (mPageTag == null) {
            mPageTag = new MutableLiveData<>();
        }
        return mPageTag;
    }

    public void setPageTag(String pageTag) {
        getPageTag().setValue(pageTag);
    }

    public void resetPasswd(String id, String passwd, final SimpleCallback<String> callback) {
        RequestManager.resetPasswd(id, passwd, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                if (result.isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(-1, null);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                callback.onError(code, msg);
            }
        });
    }
}
