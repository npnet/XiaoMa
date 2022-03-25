package com.xiaoma.utils.logintype.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/30
 * @Describe: 登录类型 配置
 */

public class LoginTypeConfig {

    private String historyType;//上一次登录类型
    private String currentType;// 当前登录类型
    private String historyUsrId;//上一次用户id
    private String currentUsrId;//当前用户id

    private String packageNames[];


    private List<App> mApps;

    public LoginTypeConfig() {
        createPackageNames();
    }

    public void buildApps() {
        createPackageNames();

    }

    private void createPackageNames() {
        mApps = new ArrayList<>();
        packageNames = new String[]{"com.xiaoma.launcher", "com.xiaoma.music", "com.xiaoma.xting"};
        for (String packageName : packageNames) {
            App app = new App();
            app.setPackageName(packageName);
            app.setFirst(true);
            mApps.add(app);
        }
    }


    public String getHistoryType() {
        return historyType;
    }

    public String getHistoryUsrId() {
        return historyUsrId;
    }

    public void setHistoryUsrId(String historyUsrId) {
        this.historyUsrId = historyUsrId;
    }

    public String getCurrentUsrId() {
        return currentUsrId;
    }

    public void setCurrentUsrId(String currentUsrId) {
        this.currentUsrId = currentUsrId;
    }

    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentType(String currentType) {
        this.currentType = currentType;
    }

    public List<App> getApps() {
        return mApps;
    }

    public void setApps(List<App> apps) {
        mApps = apps;
    }

    public void reset() {
        if (mApps == null) return;
        for (App app : mApps) {
            app.setFirst(true);
        }
    }

    public App getApp(String packageName) {
        if (TextUtils.isEmpty(packageName) || mApps == null) return null;
        for (App app : mApps) {
            if (packageName.equals(app.getPackageName())) {
                return app;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "LoginTypeConfig{" +
                "historyType='" + historyType + '\'' +
                ", currentType='" + currentType + '\'' +
                '}';
    }

    public static class App {
        private String packageName;
        private boolean isFirst;//是不是开机后第一次启动

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public boolean isFirst() {
            return isFirst;
        }

        public void setFirst(boolean first) {
            isFirst = first;
        }
    }


}
