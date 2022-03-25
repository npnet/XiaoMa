package com.xiaoma.update.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.update.manager.AppUpdateManager;
import com.xiaoma.update.model.ApkVersionInfo;

/**
 * Created by zhushi.
 * Date: 2018/11/22
 */
public class UpdateActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isForceUpdate = getIntent().getBooleanExtra(AppUpdateCheck.IS_FORCEUPDATE, false);
        ApkVersionInfo updateAppInfo = (ApkVersionInfo) getIntent().getSerializableExtra(AppUpdateCheck.UPDATEAPPINFO);
        AppUpdateManager updateManager = new AppUpdateManager(this, updateAppInfo);
        if (isForceUpdate && null != updateAppInfo) {
            updateManager.showForceUpdateDialog();

        } else if (!isForceUpdate && null != updateAppInfo) {
            updateManager.showSelectUpdateDialog();

        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
    }
}
