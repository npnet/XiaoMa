package com.xiaoma.skin.receiver;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.util.Log;

import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.loader.XMSDCardLoader;
import com.xiaoma.skin.manager.XmSkinLoaderListener;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinInfo;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.Objects;

import skin.support.SkinCompatManager;

import static com.xiaoma.skin.constant.SkinConstants.THEME_MAX_ID;
import static com.xiaoma.skin.constant.SkinConstants.THEME_MIN_ID;

/**
 * @author taojin
 * @date 2019/3/29
 */

public class SkinReceiver extends BroadcastReceiver {

    public static final String SKINS_SD_PATH = "sd_path";
    private static final String TAG = SkinReceiver.class.getSimpleName();

    private static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SkinConstants.SKIN_ACTION);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_DAOMENG);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_QINGSHE);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_XM);
        return intentFilter;
    }

    public static void registerReceiver(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null!");
        }
        context.registerReceiver(new SkinReceiver(), getIntentFilter());
    }


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "{onReceive}-[intent] : " + intent.getAction() + " / " + intent.getExtras());
        if (SkinConstants.SKIN_ACTION.equals(intent.getAction())) {
            updateConfigurationForWindowBg(context, SkinConstants.THEME_DEFAULT);
            XmSkinManager.getInstance().loadSkin("",new XmSkinLoaderListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess() {

                    skinSuccess(context);
                }

                @Override
                public void onFailed(String errMsg) {
                }
            },SkinCompatManager.SKIN_LOADER_STRATEGY_NONE);
        } else if (SkinConstants.SKIN_ACTION_DAOMENG.equals(intent.getAction())) {
            updateConfigurationForWindowBg(context, SkinConstants.THEME_DAOMENG);
            XmSkinManager.getInstance().loadSkin("DaoMeng.skin", new XmSkinLoaderListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    KLog.d("换肤成功");
                    skinSuccess(context);
                }

                @Override
                public void onFailed(String errMsg) {
                    KLog.d("换肤失败");
                }
            }, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
        } else if (SkinConstants.SKIN_ACTION_QINGSHE.equals(intent.getAction())) {
            updateConfigurationForWindowBg(context, SkinConstants.THEME_QINGSHE);
            XmSkinManager.getInstance().loadSkin("QingShe.skin", new XmSkinLoaderListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    KLog.d("换肤成功");
                    skinSuccess(context);
                }

                @Override
                public void onFailed(String errMsg) {
                    KLog.d("换肤失败");
                }
            }, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
        } else if (Objects.equals(SkinConstants.SKIN_ACTION_XM, intent.getAction())) {
            XmSkinManager.getInstance().loadSkin(context.getApplicationContext().getPackageName().replace(".","_"), new XmSkinLoaderListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    int themeId = 0;
                    SkinInfo skinMsg = SkinUtils.getSkinMsg();
                    if (skinMsg != null) {
                        TPUtils.put(context, SkinUtils.SKIN_PATH, skinMsg.skinPath);
                        themeId = skinMsg.skinStyle;
                    }
                    themeId = Math.max(Math.min(themeId, THEME_MAX_ID), THEME_MIN_ID);
                    updateConfigurationForWindowBg(context, themeId);
                    skinSuccess(context);
                    KLog.d("换肤成功");
                }

                @Override
                public void onFailed(String errMsg) {
                    KLog.d("换肤失败");
                }
            }, XMSDCardLoader.SKIN_LOADER_STRATEGY_SDCARD);
        }
    }

    private  final String launcherPackageName = "com.xiaoma.launcher";

    private void skinSuccess(Context context){
        String packageName = context.getPackageName();
        if(launcherPackageName.equals(packageName)){
            SkinUtils.sendBroadcast(context,SkinConstants.SKIN_SUCCESS);
        }
    }

    private void updateConfigurationForWindowBg(Context context, int themeId) {
        Log.d(TAG, "updateConfigurationForWindowBg -> themeId: " + themeId);
        IActivityManager am = ActivityManager.getService();
        try {
            final int newMnc = themeId + 400;
            Configuration curConfig = am.getConfiguration();
            if (curConfig.mcc != newMnc) {
                curConfig.mcc = newMnc;
                am.updateConfiguration(curConfig);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
