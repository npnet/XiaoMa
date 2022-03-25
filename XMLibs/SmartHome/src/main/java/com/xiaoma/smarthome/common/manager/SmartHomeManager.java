package com.xiaoma.smarthome.common.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common.manager
 *  @file_name:      SmartHomeManager
 *  @author:         Rookie
 *  @create_time:    2019/3/1 10:21
 *  @description：   TODO             */

import android.content.Context;

import com.xiaoma.component.AppHolder;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.model.LocalDeviceInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.List;

public class SmartHomeManager {

    public static final int TYPE_GOLID = 1;

    public static final int TYPE_HUAWEI = 2;

    private SmartHomeManagerProxy mHomeManager;

    private static SmartHomeManager INSTANCE;

    private ISmartHomeManager.OnGetAllDevicesCallback mGetAllDevicesCallback;

    private boolean mInited;
    private ISmartHomeManager.OnSmartHomeLoginCallback mLoginCallback;
    private ISmartHomeManager.OnSmartHomeLoginOutCallback mCallback;

    private SmartHomeManager() {

    }

    public static SmartHomeManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SmartHomeManager();
        }
        return INSTANCE;
    }

    public void init(Context context, int managerType) {
        if (TYPE_GOLID == managerType) {
            mHomeManager = SmartHomeManagerProxyFactory.getGowildManagerProxy();
            mHomeManager.init(context);
        } else if (TYPE_HUAWEI == managerType) {
            mHomeManager = SmartHomeManagerProxyFactory.getHuaWeiManagerProxy();
            mHomeManager.init(context);
        }
        mInited = true;
    }

    public boolean isLogin() {
        if (mHomeManager == null) {
            return false;
        }
        return mHomeManager.isLogin();
    }


    public void registerSmartHomeLoginCallback(ISmartHomeManager.OnSmartHomeLoginCallback callback) {
        mLoginCallback = callback;

    }

    public void unregisterSmartHomeLoginCallback() {
        mLoginCallback = null;
    }


    public void login(String userName, String passWord) {
        if (mHomeManager == null) {
            if (mLoginCallback != null) {
                mLoginCallback.onLoginFail(AppHolder.getInstance().getAppContext().getString(R.string.login_fail));
                KLog.e("mHomeManager is null!");
            }
            return;
        }
        mHomeManager.login(userName, passWord, new ISmartHomeManager.OnSmartHomeLoginCallback() {
            @Override
            public void onLoginSuccess() {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoginCallback != null) {
                            mLoginCallback.onLoginSuccess();
                        }
                    }
                });
            }

            @Override
            public void onLoginFail(final String errorMsg) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoginCallback != null) {
                            mLoginCallback.onLoginFail(errorMsg);
                        }
                    }
                });
            }
        });
    }

    public boolean loginOut() {
        if (mHomeManager == null) {
            return false;
        }
        mHomeManager.loginOut();
        return true;
    }


    public void getAllDeviceList() {
        if (mHomeManager == null) {
            if (mGetAllDevicesCallback != null) {
                mGetAllDevicesCallback.onFail(SmartConstants.ERROR_CODE_OTHERS, AppHolder.getInstance().getAppContext().getString(R.string.fetch_data_fail));
                KLog.e("mHomeManager is null!");
            }
            return;
        }
        ThreadDispatcher.getDispatcher().postOnMainDelayed(timeoutRunnable, SmartConstants.TIME_OUT_TIME);

        mHomeManager.getAllDeviceList(new ISmartHomeManager.OnGetAllDevicesCallback() {
            @Override
            public void onSuccess(final List<LocalDeviceInfo> deviceInfos) {
                EventBus.getDefault().post(SmartConstants.SCENE_LIST, GsonHelper.toJson(deviceInfos));
                ThreadDispatcher.getDispatcher().removeOnMain(timeoutRunnable);
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mGetAllDevicesCallback != null) {
                            mGetAllDevicesCallback.onSuccess(deviceInfos);
                        }
                    }
                });
            }

            @Override
            public void onFail(final int errorCode, final String errorMsg) {
                ThreadDispatcher.getDispatcher().removeOnMain(timeoutRunnable);
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mGetAllDevicesCallback != null) {
                            mGetAllDevicesCallback.onFail(errorCode, errorMsg);
                        }
                    }
                });
            }
        });
    }

    public void registerGetAllDevicesCallback(ISmartHomeManager.OnGetAllDevicesCallback callback) {
        mGetAllDevicesCallback = callback;
    }

    public void unregisterGetAllDevicesCallback() {
        mGetAllDevicesCallback = null;
    }

    Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (mGetAllDevicesCallback != null) {
                mGetAllDevicesCallback.onFail(SmartConstants.ERROR_CODE_TIMEOUT, "timeout");
                mGetAllDevicesCallback = null;
            }
        }
    };


    public void sceneControl(String sceneName) {
        if (mHomeManager == null) {
            return;
        }
        mHomeManager.sceneControl(sceneName);
        EventBus.getDefault().post(SmartConstants.EXECUTION_SCENE, sceneName);
    }


    public void registerLoginOutListener(ISmartHomeManager.OnSmartHomeLoginOutCallback callback) {
        mCallback = callback;
        if (mHomeManager == null) {
            return;
        }
        mHomeManager.setLoginOutListener(new ISmartHomeManager.OnSmartHomeLoginOutCallback() {
            @Override
            public void onLoginOut() {
                if (mCallback != null) {
                    mCallback.onLoginOut();
                }
            }
        });
    }

    public void unregisterLoginOutListener() {
        mCallback = null;
    }

    public boolean isBind() {
        if (mHomeManager == null) {
            return false;
        }
        return mHomeManager.isBind();
    }

    public void release() {
        if (mHomeManager == null) {
            return;
        }
        mHomeManager.release();
        mHomeManager = null;
        INSTANCE = null;
    }

    public boolean isInited() {
        return mInited;
    }
}
