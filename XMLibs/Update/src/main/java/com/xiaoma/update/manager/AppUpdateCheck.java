package com.xiaoma.update.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.update.common.RequestManager;
import com.xiaoma.update.model.ApkVersionInfo;
import com.xiaoma.update.views.UpdateActivity;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.HashMap;

/**
 * Created by zhushi.
 * Date: 2019/1/3
 */
public class AppUpdateCheck {
    public static final String ARGS_PACKAGE_NAME = "packageName";
    public static final String IS_FORCEUPDATE = "isForceUpdate";
    public static final String UPDATEAPPINFO = "updateAppInfo";

    private Context mContext;
    private String mPackageName;
    private static AppUpdateCheck mAppUpdateCheck;
    private BroadcastReceiver networkChangeReceiver;

    public static AppUpdateCheck getInstance() {
        if (mAppUpdateCheck == null) {
            synchronized (AppUpdateCheck.class) {
                if (mAppUpdateCheck == null) {
                    mAppUpdateCheck = new AppUpdateCheck();
                }
            }
        }
        return mAppUpdateCheck;
    }

    private AppUpdateCheck() {
    }

    public void checkAppUpdate(String packageName, Context context) {
        mPackageName = packageName;
        mContext = context;
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtils.isConnected(mContext)) {
//                    registerNetworkChangedReceiver();
                    return;
                }
                HashMap<String, Object> urlMaps = new HashMap<>();
                urlMaps.put(ARGS_PACKAGE_NAME, mPackageName);
                RequestManager.checkAppUpdate(urlMaps, new ResultCallback<XMResult<ApkVersionInfo>>() {
                    @Override
                    public void onSuccess(XMResult<ApkVersionInfo> result) {
                        ApkVersionInfo apkVersionInfo = result.getData();
                        if (apkVersionInfo != null && apkVersionInfo.isIsNeedUpdate()) {
                            dispatchUpdate(apkVersionInfo);
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
//                        if(code == -1){
//                            registerNetworkChangedReceiver();
//                        }
                    }
                });
            }
        });

    }

    private void dispatchUpdate(ApkVersionInfo apkVersionInfo) {
        if (apkVersionInfo.isIsForceUpdate()) {
            startUpdateActivity(true, apkVersionInfo);

        } else {
            String time = TPUtils.get(mContext, mContext.getPackageName() + AppUpdateManager.CANCEL_DATE_KEY, "");
            if (time.equals(StringUtil.getDateByYMD())) {
                return;
            }
            startUpdateActivity(false, apkVersionInfo);
        }
    }

    private void startUpdateActivity(boolean isForceUpdate, ApkVersionInfo updateAppInfo) {
        Intent intent = new Intent(mContext, UpdateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IS_FORCEUPDATE, isForceUpdate);
        intent.putExtra(UPDATEAPPINFO, updateAppInfo);
        mContext.startActivity(intent);
    }
}
