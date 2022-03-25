package com.xiaoma.app.model;

/**
 * Created by zhushi.
 * Date: 2018/11/27
 */
public class AppStateEvent {
    //安装
    public static final int STATE_PACKAGE_ADDED = 0;
    //替换
    public static final int STATE_PACKAGE_REPLACED = 1;
    //卸载
    public static final int STATE_PACKAGE_REMOVED = 2;

    private int appState;
    private String packageName;
    //安装卸载回调结果
    private boolean result;

    public int getAppState() {
        return appState;
    }

    public void setAppState(int appState) {
        this.appState = appState;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "PackageName:" + packageName + " AppState:" + appState + " result:" + result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
