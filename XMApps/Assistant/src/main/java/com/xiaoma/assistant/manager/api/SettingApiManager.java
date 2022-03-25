package com.xiaoma.assistant.manager.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;

/**
 * @Author ZiXu Huang
 * @Data 2019/2/28
 */
public class SettingApiManager extends ApiManager {
    private static SettingApiManager instance;

//    public static Request getRequest(Context context, int action, Bundle bundle) {
//        return new Request(getLocalSourceInfo(context), new RequestHead(getRemoteSourceInfo(), action), bundle);
//    }

//    public static SourceInfo getLocalSourceInfo(Context context) {
//        return new SourceInfo(context.getPackageName(), CenterConstants.LAUNCHER_PORT);
//    }

    private SettingApiManager() {
    }

//    public static SourceInfo getRemoteSourceInfo() {
//        return new SourceInfo(CenterConstants.SETTING, CenterConstants.SETTING_PORT);
//    }

    public static SettingApiManager getInstance() {
        if (instance == null) {
            synchronized (SettingApiManager.class) {
                if (instance == null) {
                    instance = new SettingApiManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.SETTING, CenterConstants.SETTING_PORT);
    }

    public void changeDisplayMode(int displayModel) {
        Bundle bundle = new Bundle();
        bundle.putInt(CenterConstants.SettingThirdBundleKey.KEY_DISPLAY_MODE, displayModel);
        request(CenterConstants.SettingThirdAction.SET_DISPLAY_MODE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.SettingThirdBundleKey.KEY_DISPLAY_MODE);
                if (!success) {

                }
            }
        });
    }

    public void changeBrightness(int brightnessAction) {
        Bundle bundle = new Bundle();
        bundle.putInt(CenterConstants.SettingThirdBundleKey.KEY_DISPLAY_LEVEL, brightnessAction);
        request(CenterConstants.SettingThirdAction.SET_DISPLAY_LEVEL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.SettingThirdBundleKey.KEY_DISPLAY_LEVEL);
                if (!success) {

                }
            }
        });
    }

    public void changeVolume(int volumeAction, int volumeNum) {
        Bundle bundle = new Bundle();
        bundle.putInt(CenterConstants.SettingThirdBundleKey.KEY_SOUND_VALUE, volumeAction);
        bundle.putInt(CenterConstants.SettingThirdBundleKey.ADD_VOLUME, volumeNum);
        request(CenterConstants.SettingThirdAction.SET_SOUND_VALUE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.SettingThirdBundleKey.KEY_SOUND_VALUE);
                if (!success) {

                }
            }
        });
    }

    public void closeApp(Context context) {
        /*Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.SettingThirdBundleKey.IS_CLOSE_SETTING,true);
        Linker.getInstance().send(getRequest(context, CenterConstants.SettingThirdAction.CLOSE_SETTING, bundle));*/
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(home);

    }

    public void chooseSpecificPage(int page) {
        Bundle bundle = new Bundle();
        bundle.putInt(CenterConstants.SettingThirdBundleKey.SELECT_FRAGMENT, page);
        send(CenterConstants.SettingThirdAction.SELECT_SPECIFIC_FRAGMENT, bundle);
    }
}
