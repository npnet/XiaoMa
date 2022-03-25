package com.xiaoma.launcher.app.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.db.DBManager;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.app.listener.AppChangeListener;
import com.xiaoma.launcher.app.model.AppItem;
import com.xiaoma.launcher.app.model.LauncherItemBean;
import com.xiaoma.launcher.app.model.WhiteApp;
import com.xiaoma.launcher.app.model.WhiteListBean;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2017/4/26
 * Desc:Launcher 加载的应用和白名单管理
 */
public class AppManager {
    private static final String TAG = "AppManager";
    private static final String WHITE_APP_LIST = "WhiteApp";
    private static final String TYPE_DEF = "0";//默认状态
    private static final String TYPE_ADD = "1";//不安装也显示
    public static final String TYPE_PLUGIN = "3";//插件
    private static final int IS_FORCE_REFRESH = 1;//强制刷新应用
    private static AppManager manager;
    public final List<String> datas = new CopyOnWriteArrayList<>();
    private Context context;
    //Launcher 显示应用列表（本地保存的）
    private final List<LauncherItemBean> appBeanList = new CopyOnWriteArrayList<>();
    //白名单列列表（服务器下发的）
    private final List<WhiteApp> whiteAppList = new CopyOnWriteArrayList<>();
    private AppChangeListener listener;
    private String versionCode;
    private boolean appRemoveNotify = true;
    private int isForceRefresh;

    private AppManager() {
        context = AppHolder.getInstance().getAppContext();
        getLauncherAppListFromDB();
    }

    public static AppManager getInstance() {
        if (manager == null)
            manager = new AppManager();
        return manager;
    }

    /**
     * 清除数据
     */
    public void clearData() {
        datas.clear();
        appBeanList.clear();
        whiteAppList.clear();
    }

    public void setAppChangeListener(AppChangeListener listener) {
        this.listener = listener;
    }

    private void getLauncherAppListFromDB() {
        appBeanList.clear();
        List<LauncherItemBean> itemBeans = DBManager.getInstance().getDBManager().queryAll(LauncherItemBean.class);
        if (itemBeans != null) {
            appBeanList.addAll(itemBeans);
        }
        checkAppListIsLegitimate(appBeanList);
    }

    private void checkAppListIsLegitimate(List<LauncherItemBean> appBeanList) {
        if (appBeanList == null) {
            return;
        }
        for (LauncherItemBean launcherItemBeanV2 : appBeanList) {
            if (launcherItemBeanV2 == null) {
                appBeanList.clear();
                break;
            }
            String itemName = launcherItemBeanV2.itemName;
            ArrayList<AppItem> list = launcherItemBeanV2.list;
            if (TextUtils.isEmpty(itemName) || ListUtils.isEmpty(list)) {
                appBeanList.clear();
                break;
            }
            for (AppItem appItem : list) {
                if (appItem == null) {
                    appBeanList.clear();
                    return;
                }
                String packageName = appItem.packageName;
                String appName = appItem.appName;
                if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(appName)) {
                    appBeanList.clear();
                    return;
                }
            }
        }
    }

    public void update() {
        getLauncherAppListFromDB();
        checkWhiteAppList(true);
    }

    /**
     * 白名单检测
     */
    public void checkWhiteAppList(boolean flag) {
        String whiteAppStr = TPUtils.get(context, WHITE_APP_LIST, "");
        KLog.d("local white app list :" + whiteAppStr);
        if (!TextUtils.isEmpty(whiteAppStr)) {
            WhiteListBean whiteListBean = GsonHelper.fromJson(whiteAppStr, WhiteListBean.class);
            if (whiteListBean != null) {
                versionCode = whiteListBean.vcc;
                if (whiteListBean.data != null && !whiteListBean.data.isEmpty()) {
                    whiteAppList.clear();
                    whiteAppList.addAll(whiteListBean.data);
                    for (int i = 0; i < whiteAppList.size(); i++) {
                        WhiteApp whiteApp = whiteAppList.get(i);
                        whiteApp.sortNumber = i;
                    }
                }
            }
            updateAppList(flag);
        }
        fetchWhiteListFromServer(versionCode);
    }

    private void fetchWhiteListFromServer(final String version) {
        RequestManager.getInstance().getWhiteApp(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String whiteJson = response.body();
                doHandleWhiteList(whiteJson, version);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }
        });
    }

    private void doHandleWhiteList(String whiteJson, String version) {
        if (!TextUtils.isEmpty(whiteJson)) {
            WhiteListBean whiteListBean = GsonHelper.fromJson(whiteJson, WhiteListBean.class);
            if (whiteListBean == null || whiteListBean.vcc == null) {
                return;
            }
            if (!whiteListBean.vcc.equals(version)) {
                KLog.d(TAG, "has360LookAround" + XmCarConfigManager.has360LookAround());
                if (!XmCarConfigManager.has360LookAround()) {
                    List<WhiteApp> whiteApps = whiteListBean.data;
                    if (whiteApps != null) {
                        for (int i = 0; i < whiteApps.size(); i++) {
                            if (LauncherConstants.PANORAMIC_IMAGE.equals(whiteApps.get(i).itemName)) {
                                whiteApps.remove(i);
                                break;
                            }
                        }
                    }
                    whiteJson = GsonHelper.toJson(whiteListBean);
                }
                isForceRefresh = whiteListBean.forceRefresh;
                TPUtils.put(context, WHITE_APP_LIST, whiteJson);
//                parseData(whiteJson);
//                updateAppList(true);
                KLog.d("server white app list :" + whiteJson);
            }
        }
    }

    private void parseData(String content) {
        WhiteListBean whiteListBean = GsonHelper.fromJson(content, WhiteListBean.class);
        if (whiteListBean != null) {
            if (whiteListBean.data != null && !whiteListBean.data.isEmpty()) {
                whiteAppList.clear();
                whiteAppList.addAll(whiteListBean.data);
                for (int i = 0; i < whiteAppList.size(); i++) {
                    WhiteApp whiteApp = whiteAppList.get(i);
                    whiteApp.sortNumber = i;
                }
            }
        }
    }

    /**
     * 白名单跟本地数据对比是否有更新
     */
    private void updateAppList(boolean flag) {
        boolean isChange = false;
        //白名单为空的时候判断应用是否是安装
        if (ListUtils.isEmpty(whiteAppList)) {
            for (int i = 0; i < appBeanList.size(); i++) {
                LauncherItemBean item = appBeanList.get(i);
                boolean isInstalled = false;
                if (item != null && item.list != null) {
                    for (AppItem appItem : item.list) {
                        boolean installed = AppUtils.isAppInstalled(context, appItem.packageName);
                        if (appItem.isInstalled != installed) {
                            appItem.isInstalled = installed;
                            isChange = true;
                        }
                        if (installed) {
                            isInstalled = true;
                        }
                    }
                    if (item.type.equals(TYPE_DEF) && !isInstalled) {
                        appBeanList.remove(i);
                        i--;
                        isChange = true;
                    }
                }
            }
            if (isChange && listener != null) {
                listener.appChange();
                //将变化后的数据保存到数据库中
                saveAppList();
            }
            return;
        }
        //系统所有已安装应用
        HashMap<String, ResolveInfo> allAppHash = loadAppHash();
        HashMap<String, LauncherItemBean> currentAppMap = loadLauncherAppHashMap(appBeanList);
        /*
         * 匹配白名单列表和本地的应用列表
         */
        for (int i = 0; i < whiteAppList.size(); i++) {
            WhiteApp appV2 = whiteAppList.get(i);
            if (appV2.list == null || appV2.list.isEmpty()) {
                continue;
            }
            if (currentAppMap.containsKey(appV2.itemName)) {
                LauncherItemBean item = currentAppMap.get(appV2.itemName);
                if (appV2.list.size() > 1 && item.list.size() != appV2.list.size()) {
                    item.packageName = "";
                }
                HashMap<String, AppItem> itemMap = change2Map(appV2.list);
                for (AppItem appItem1 : item.list) {
                    if (!itemMap.containsKey(appItem1.packageName) && !datas.contains(appItem1.appName) && appRemoveNotify) {
                        datas.add(String.format(context.getString(R.string.app_remove_notify), appItem1.appName));
                    }
                }
                item.sortNumber = appV2.sortNumber;
                item.type = appV2.type;
                item.iconUrl = appV2.iconUrl;
                item.itemName = appV2.itemName;
                item.list = appV2.list;
                if (item.list.size() == 1) {
                    item.packageName = item.list.get(0).packageName;
                }
            } else {
                if (appV2.type.equals(TYPE_ADD)) {
                    appBeanList.add(appV2.toLauncherItemBean());
                    isChange = true;
                }
            }
        }
        /*
         * 合并完白名单的应用列表跟本地的已安装的应用做一个匹配操作
         * 同时根据判断是否有默认的item信息
         */
        for (int i = 0; i < appBeanList.size(); i++) {
            LauncherItemBean item = appBeanList.get(i);
            boolean isInstalled = false;
            if (item != null && item.list != null) {
                for (AppItem appItem : item.list) {
                    if (allAppHash.containsKey(appItem.packageName)) {
                        appItem.isInstalled = true;
                        isInstalled = true;
                    } else {
                        appItem.isInstalled = false;
                    }
                }
                if (item.type.equals(TYPE_DEF) && !isInstalled) {
                    appBeanList.remove(i);
                    i--;
                    isChange = true;
                }
            }
        }
        if (isChange) {
            //重新生成一份本地应用的hashMap
            currentAppMap.clear();
            currentAppMap = loadLauncherAppHashMap(appBeanList);
        }
        /*
         * 处理白名单和系统安装应用的比对
         * （应用安装了但是Launcher上没有监听到，比如在离开launcher的时候卸载安装的）
         */
        for (WhiteApp appV2 : whiteAppList) {
            if (appV2 != null && appV2.list != null) {
                for (AppItem appItem : appV2.list) {
                    if (allAppHash.containsKey(appItem.packageName) && !currentAppMap.containsKey(appV2.itemName)) {
                        appBeanList.add(appV2.toLauncherItemBean());
                        isChange = true;
                    }
                }
            }
        }
        HashMap<String, WhiteApp> whiteAppV2HashMap = loadWhiteAppHashMap(whiteAppList);
        for (int i = 0; i < appBeanList.size(); i++) {
            if (!whiteAppV2HashMap.containsKey(appBeanList.get(i).itemName)) {
                if (appRemoveNotify && !datas.contains(appBeanList.get(i).itemName)) {
                    datas.add(String.format(context.getString(R.string.app_remove_notify), appBeanList.get(i).itemName));
                }
                appBeanList.remove(i--);
                isChange = true;
            }
        }
        if (flag) {
            isChange = true;
        }
        if (isChange && listener != null) {
            if (isForceRefresh == IS_FORCE_REFRESH) {
                List<LauncherItemBean> itemBeans = new ArrayList<>(appBeanList);
                Collections.sort(itemBeans, new Comparator<LauncherItemBean>() {
                    @Override
                    public int compare(LauncherItemBean l1, LauncherItemBean l2) {
                        return l1.sortNumber - l2.sortNumber;
                    }
                });
                appBeanList.clear();
                appBeanList.addAll(itemBeans);
            }
            listener.appChange();
            //将变化后的数据保存到数据库中
            saveAppList();
        }
        if (listener != null) {
            listener.execute();
        }
    }

    private void saveAppList() {
        DBManager.getInstance().getDBManager().delete(LauncherItemBean.class);
        DBManager.getInstance().getDBManager().delete(AppItem.class);
        DBManager.getInstance().getDBManager().saveAll(appBeanList);
    }

    private void launcherAppFromAssertV2() {
        String moreAppPath = "config/LauncherAppList.json";
        String textFromAsset = AssetUtils.getTextFromAsset(context, moreAppPath);
        appBeanList.clear();
        appBeanList.addAll(GsonHelper.fromJsonToList(textFromAsset, LauncherItemBean[].class));
        saveAppList();
    }

    private HashMap<String, ResolveInfo> loadAppHash() {
        List<ResolveInfo> resolveInfos = loadAppList();
        HashMap<String, ResolveInfo> resolveInfoHashMap = new HashMap<>();
        if (resolveInfos != null && resolveInfos.size() > 0) {
            for (ResolveInfo resolveInfo : resolveInfos) {
                resolveInfoHashMap.put(resolveInfo.activityInfo.packageName, resolveInfo);
            }
        }
        return resolveInfoHashMap;
    }

    private HashMap<String, LauncherItemBean> loadLauncherAppHashMap(List<LauncherItemBean> appBeanList) {
        HashMap<String, LauncherItemBean> dataMap = new HashMap<>();
        if (appBeanList != null && appBeanList.size() > 0) {
            for (int i = 0; i < appBeanList.size(); i++) {
                LauncherItemBean app = appBeanList.get(i);
                //处理重复的数据
                if (dataMap.containsKey(app.itemName)) {
                    appBeanList.remove(i);
                } else {
                    dataMap.put(app.itemName, app);
                }
            }
        }
        return dataMap;
    }

    private HashMap<String, WhiteApp> loadWhiteAppHashMap(List<WhiteApp> whiteAppList) {
        HashMap<String, WhiteApp> dataMap = new HashMap<>();
        if (whiteAppList != null && whiteAppList.size() > 0) {
            for (WhiteApp whiteApp : whiteAppList) {
                dataMap.put(whiteApp.itemName, whiteApp);
            }
        }
        return dataMap;
    }

    private HashMap<String, AppItem> change2Map(List<AppItem> list) {
        HashMap<String, AppItem> dataMap = new HashMap<>();
        if (list != null && !list.isEmpty()) {
            for (AppItem appItem : list) {
                dataMap.put(appItem.packageName, appItem);
            }
        }
        return dataMap;
    }

    private List<ResolveInfo> loadAppList() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfos == null)
            return new ArrayList<>();
        return resolveInfos;
    }

    public List<LauncherItemBean> getLauncherApp() {
        if (ListUtils.isEmpty(appBeanList)) {
            launcherAppFromAssertV2();
        }
        return appBeanList;
    }

}
