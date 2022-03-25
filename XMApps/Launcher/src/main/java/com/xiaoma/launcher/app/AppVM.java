package com.xiaoma.launcher.app;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.launcher.app.model.LauncherApp;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.repo.RepoManager;
import com.xiaoma.model.User;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/8/20 0020
 */
public class AppVM extends BaseViewModel {

    private static final String TAG = "AppVM";
    private MutableLiveData<List<LauncherApp>> mAppList;
    private MutableLiveData<User> user;

    public AppVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<LauncherApp>> getAppList() {
        if (mAppList == null) {
            mAppList = new MutableLiveData<>();
        }
        return mAppList;
    }

    public MutableLiveData<User> getUser() {
        if (user == null) {
            user = new MutableLiveData<>();
        }
        return user;
    }

    public void fetchAppList() {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                List<LauncherApp> launcherAppsFromCache = RepoManager.getInstance().getLauncherAppRepo()
                        .getLauncherAppsFromCache(getApplication());
                handle360(launcherAppsFromCache);
                if (!ListUtils.isEmpty(launcherAppsFromCache)) {
                    KLog.e(TAG, "fetchAppList" + launcherAppsFromCache.toString());
                    getAppList().postValue(launcherAppsFromCache);
                }
                RepoManager.getInstance().getLauncherAppRepo().fetchLauncherApps(null);
            }
        });
    }

    private void handle360(List<LauncherApp> data) {
        KLog.e(TAG, "has 360" + XmCarConfigManager.has360LookAround());
        if (!XmCarConfigManager.has360LookAround()) {
            if (data != null) {
                final Iterator<LauncherApp> iterator = data.iterator();
                while (iterator.hasNext()) {
                    final LauncherApp next = iterator.next();
                    if (LauncherConstants.PANORAMIC_IMAGE.equals(next.getAppName())) {
                        KLog.e(TAG, "remove 360");
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void setUser(User user) {
        getUser().postValue(user);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mAppList = null;
    }
}
