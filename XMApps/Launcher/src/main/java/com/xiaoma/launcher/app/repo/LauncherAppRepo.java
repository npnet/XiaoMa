package com.xiaoma.launcher.app.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.launcher.app.model.AppItem;
import com.xiaoma.launcher.app.model.LauncherApp;
import com.xiaoma.launcher.app.model.LauncherItemBean;
import com.xiaoma.launcher.app.model.WhiteApp;
import com.xiaoma.launcher.app.model.WhiteListBean;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.GsonHelper;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class LauncherAppRepo {
    @Query("DELETE FROM LauncherApp")
    protected abstract void clear();

    @Insert
    protected abstract void save(List<LauncherApp> launcherApp);

    @Transaction
    public void saveLauncherApps(List<LauncherApp> launcherApp) {
        clear();
        save(launcherApp);
    }

    @Query("SELECT * FROM LauncherApp")
    public abstract List<LauncherApp> getLauncherApps();

    /**
     * 从缓存里获取应用列表,先从DB获取,没有则从默认本地配置获取
     */
    @NonNull
    @Transaction()
    public List<LauncherApp> getLauncherAppsFromCache(Context context) {
        List<LauncherApp> launcherApps = getLauncherApps();
        if (launcherApps == null) {
            launcherApps = new ArrayList<>();
        }
        if (launcherApps.isEmpty()) {
            try {
                InputStreamReader input = new InputStreamReader(context.getAssets().open("config/LauncherAppList.json"));
                char[] buf = new char[4096];
                int len;
                StringBuilder json = new StringBuilder();
                while ((len = input.read(buf)) > 0) {
                    json.append(buf, 0, len);
                }
                List<LauncherItemBean> bean = GsonHelper.fromJsonToList(json.toString(), LauncherItemBean[].class);
                launcherApps.addAll(toLauncherApps(bean));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return launcherApps;
    }

    /**
     * 从服务器拉取应用列表
     */
    public void fetchLauncherApps(SimpleCallback<List<LauncherApp>> callback) {
        RequestManager.getInstance().getWhiteApp(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
                    @Override
                    public void doWork(Object lastResult) {
                        List<LauncherApp> launcherApps = null;
                        try {
                            String json = response.body();
                            WhiteListBean whiteListBean = GsonHelper.fromJson(json, WhiteListBean.class);
                            if (whiteListBean != null) {
                                List<LauncherItemBean> launcherItemBeans = new ArrayList<>();
                                for (WhiteApp whiteApp : whiteListBean.data) {
                                    launcherItemBeans.add(whiteApp.toLauncherItemBean());
                                }
                                launcherApps = toLauncherApps(launcherItemBeans);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (launcherApps != null && !launcherApps.isEmpty()) {
                            saveLauncherApps(launcherApps);
                        }
                        doNext(launcherApps);
                    }
                }).next(new Work<List<LauncherApp>>() {
                    @Override
                    public void doWork(List<LauncherApp> launcherApps) {
                        if (launcherApps != null) {
                            if (callback != null) {
                                callback.onSuccess(launcherApps);
                            }
                        } else {
                            onError(response);
                        }
                    }
                }).start();
            }

            @Override
            public void onError(Response<String> response) {
                if (callback != null) {
                    callback.onError(-1, "");
                }
            }
        });
    }

    @NonNull
    private static List<LauncherApp> toLauncherApps(List<LauncherItemBean> launcherItemBeans) {
        List<LauncherApp> launcherApps = new ArrayList<>();
        if (launcherItemBeans != null) {
            for (LauncherItemBean launcherItem : launcherItemBeans) {
                AppItem appItem = launcherItem.getDefaultApp();
                if (appItem == null)
                    continue;
                LauncherApp app = new LauncherApp();
                app.setAppName(appItem.appName);
                app.setAppNameEn(appItem.appNameEn);
                app.setPackageName(appItem.packageName);
                app.setIconUrl(appItem.iconUrl);
                launcherApps.add(app);
            }
        }
        return launcherApps;
    }
}