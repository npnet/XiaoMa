package com.xiaoma.xting.koala;

import android.app.Application;

import com.kaolafm.opensdk.OpenSDK;
import com.kaolafm.opensdk.account.token.AccessTokenManager;
import com.kaolafm.opensdk.http.core.HttpCallback;
import com.kaolafm.opensdk.http.error.ApiException;
import com.kaolafm.sdk.core.mediaplayer.PlayItem;
import com.kaolafm.sdk.core.mediaplayer.PlayerManager;
import com.kaolafm.sdk.vehicle.GeneralCallback;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;

/**
 * <des>
 * Koala SDK接入的初始化操作
 *
 * @author YangGang
 * @date 2019/6/3
 */
public class KoalaSDK {

    public static final String SDK_MSG_NOT_INIT_SDK = "请先初始化sdk";
    private static final String TAG = "Koala_SDK";

    private final OpenSDK mSdk;
    private Application mApp;
    private boolean mIsInitialized;

    KoalaSDK() {
        mIsInitialized = false;
        mSdk = OpenSDK.getInstance();
    }

    public void init(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("application is null");
        }
        mApp = application;
        //初始化
        PrintInfo.print(TAG, "App InitSDK");
        initSDK();
    }

    public boolean isActivate() {
        return mSdk.isActivate();
    }

    public void reActivate(HttpCallback<Boolean> callback) {
        activateApp(callback);
    }

    /**
     * 初始化SDK
     */
    private void initSDK() {
        mSdk.initSDK(mApp, new HttpCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isInitSuccess) {
                PrintInfo.print(TAG, "SDK Init Success",
                        String.format("isInitSuccess = %1$s , OpenId = %2$s",
                                String.valueOf(isInitSuccess), AccessTokenManager.getInstance().getKaolaAccessToken().getOpenId()));
                mIsInitialized = isInitSuccess;
                if (isInitSuccess) {
                    activateApp();
                    PlayerManager.getInstance(mApp).setCanRequestAudioFocusOnPlayerInit(false);
                    PlayerManager.getInstance(mApp).setCanUseDefaultAudioFocusLogic(false);
                }
            }

            @Override
            public void onError(ApiException e) {
                PrintInfo.print(TAG, "SDK Init Error", String.format("errorCode = %1$s , errorMsg = %2$s", String.valueOf(e.getCode()), e.getMessage()));
            }
        });
    }

    /**
     * 应用激活 : 设备第一次启动应用时需要调用该接口，否则相关鉴权接口无法使用。
     */
    private void activateApp() {
        if (!mSdk.isActivate()) {
            //active 方法可能会抛出异常,为了避免后期导致crash,先catch住
            try {
                mSdk.activate(new HttpCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isActivated) {
                        PrintInfo.print(TAG, "Active Success", String.format("isActivated = %1$s", String.valueOf(isActivated)));
                        registerPlayStateListener();
                    }

                    @Override
                    public void onError(ApiException e) {
                        PrintInfo.print(TAG, "Active Error", String.format("errorCode = %1$s , errorMsg = %2$s", String.valueOf(e.getCode()), e.getMessage()));
                    }
                });
            } catch (Exception e) {
                PrintInfo.print(TAG, "Active Exception", String.format("IsInitSDK = %1$s , ErrorMsg = %2$s", mIsInitialized, e.getMessage()));
//                if (!mIsInitialized || SDK_MSG_NOT_INIT_SDK.equals(e.getMessage())) {
//                    PrintInfo.print(TAG, "Active Error InitSDK");
//                    initSDK();
//                }
            }
        }
    }

    /**
     * 应用激活 : 设备第一次启动应用时需要调用该接口，否则相关鉴权接口无法使用。
     */
    private void activateApp(HttpCallback<Boolean> callback) {
        if (!mSdk.isActivate()) {
            //active 方法可能会抛出异常,为了避免后期导致crash,先catch住
            try {
                mSdk.activate(new HttpCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isActivated) {
                        PrintInfo.print(TAG, "Active Success", String.format("isActivated = %1$s", String.valueOf(isActivated)));
                        registerPlayStateListener();
                        callback.onSuccess(isActivated);
                    }

                    @Override
                    public void onError(ApiException e) {
                        PrintInfo.print(TAG, "Active Error", String.format("errorCode = %1$s , errorMsg = %2$s", String.valueOf(e.getCode()), e.getMessage()));
                        callback.onError(e);
                    }
                });
            } catch (Exception e) {
                PrintInfo.print(TAG, "Active Exception", String.format("IsInitSDK = %1$s , ErrorMsg = %2$s", mIsInitialized, e.getMessage()));
                callback.onError(new ApiException("on error catch Exception"));
            }
        }
    }

    private void registerPlayStateListener() {
        PrintInfo.print(TAG, "Register State Listener");
        PlayerManager.getInstance(mApp).addStartPlayItemListener(new GeneralCallback<PlayItem>() {
            @Override
            public void onResult(PlayItem playItem) {
                PrintInfo.print(TAG, "Register State onResult", String.format("playItem = %1$s", playItem));
            }

            @Override
            public void onError(int i) {
                PrintInfo.print(TAG, "Register State onError", String.valueOf(i));
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR_BY_PLAYER);
            }

            @Override
            public void onException(Throwable throwable) {
                PrintInfo.print(TAG, "Register State onException", throwable.getMessage());
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR_BY_PLAYER);
            }
        });
    }
}
