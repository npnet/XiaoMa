package com.xiaoma.smarthome.common.manager;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.component.AppHolder;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.model.AirControl;
import com.xiaoma.smarthome.common.model.DeviceInfo;
import com.xiaoma.smarthome.common.model.LocalDeviceInfo;
import com.xiaoma.smarthome.common.processor.CompletedListener;
import com.xiaoma.smarthome.common.processor.GowildDateProcessor;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gowild.appsdk.SdkApp;
import gowild.appsdk.interf.IListener;
import gowild.appsdk.job.appliance.api.ApiAppliance;
import gowild.appsdk.job.login.api.ApiLogin;
import gowild.appsdk.job.login.interf.ILoginOutListener;
import gowild.appsdk.util.ThreadUtils;

public class GowildSmartHomeManager implements ISmartHomeManager {

    private Context mContext;

    private static GowildSmartHomeManager INSTANCE;

    private GowildSmartHomeManager() {

    }

    public static GowildSmartHomeManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GowildSmartHomeManager();
        }
        return INSTANCE;
    }

    @Override
    public void init(Context context) {
        try {
            mContext = context.getApplicationContext();
            new SdkApp().init(context);
            KLog.d("init");
        } catch (Exception e) {
            KLog.e("init " + e.getMessage());
        }
    }

    @Override
    public boolean isLogin() {
        KLog.d("isLogin start");
        try {
            boolean login = ApiLogin.getInstance().isLogin();
            KLog.d("isLogin login：" + login);
            KLog.d("isLogin end");
            return login;
        } catch (Exception e) {
            KLog.e("isLogin " + e.getMessage());
        }
        KLog.d("hasBindRobot end");
        return false;
    }

    @Override
    public boolean isBind() {
        try {
            return ApiLogin.getInstance().hasBindRobot();
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("query bind fail!!!");
        }
        return false;
    }

    @Override
    public void login(final String userName, final String passWord, final OnSmartHomeLoginCallback callback) {
        KLog.d("login start");
        try {
            ThreadUtils.excute(new Runnable() {
                @Override
                public void run() {
                    if (isLogin()) {
                        KLog.d("app instance.isLogin(): true");
                        if (!isBind()) {
                            callback.onLoginFail(mContext.getString(R.string.please_bind_robot_first));
                            return;
                        }
                        callback.onLoginSuccess();

                    } else {
                        ApiLogin.getInstance().login(userName, passWord, new IListener() {
                            @Override
                            public void onSuccess(String s, Object o) {
                                KLog.d("login success s=" + s);

                                if (!isBind()) {
                                    callback.onLoginFail(mContext.getString(R.string.please_bind_robot_first));
                                    return;
                                }
                                callback.onLoginSuccess();
                            }

                            @Override
                            public void onFail(int i, String s, Object o) {
                                KLog.d("login fail s=" + s);
                                if (o != null && o instanceof String) {
                                    String jsonStr = (String) o;
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonStr);
                                        String desc = jsonObject.getString("desc");
                                        int code = jsonObject.getInt("code");
                                        if (code == 2205) {
                                            callback.onLoginFail(mContext.getString(R.string.login_fail_check));
                                        } else {
                                            callback.onLoginFail(desc);
                                        }
                                        return;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                callback.onLoginFail(mContext.getString(R.string.login_fail_check));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            KLog.e("login " + e.getMessage());
        }
        KLog.d("login end");
    }

    @Override
    public void getAllDeviceList(final OnGetAllDevicesCallback callback) {
        KLog.d("getAllDeviceList start");
        try {
            ThreadUtils.excute(new Runnable() {
                @Override
                public void run() {
                    if (!isBind()) {
                        callback.onFail(SmartConstants.ERROR_CODE_OTHERS, mContext.getString(R.string.please_bind_robot_first));
                        return;
                    }

                    if (!isLogin()) {
                        KLog.d("app instance.isLogin(): false");
                        callback.onFail(SmartConstants.ERROR_CODE_UNLOGIN, mContext.getString(R.string.login_fail));
                        return;
                    }

                    ApiAppliance.getInstance().getAllDeviceList(new IListener() {
                        @Override
                        public void onSuccess(String s, Object o) {
                            KLog.d("getAllDeviceList onSuccess s" + s);
                            List<DeviceInfo> infos = GsonHelper.fromJson(s, new TypeToken<List<DeviceInfo>>() {
                            }.getType());
                            new GowildDateProcessor().process(infos, new CompletedListener<List<LocalDeviceInfo>>() {
                                @Override
                                public void completed(List<LocalDeviceInfo> deviceInfos) {
                                    callback.onSuccess(deviceInfos);
                                }
                            });
                        }

                        @Override
                        public void onFail(int i, String s, Object o) {
                            KLog.d("getAllDeviceList onFail s" + s);
                            callback.onFail(SmartConstants.ERROR_CODE_OTHERS, mContext.getString(R.string.fetch_data_fail));
                        }
                    });
                }
            });

        } catch (Exception e) {
            KLog.e("getAllDeviceList " + e.getMessage());
        }
        KLog.d("getAllDeviceList end");
    }

    @Override
    public void sceneControl(String sceneName) {
        try {
            if (!TextUtils.isEmpty(sceneName)) {
                ApiAppliance.getInstance().sceneControl(sceneName);
            }
        } catch (Exception e) {
            KLog.e("sceneControl" + e.getMessage());
        }
    }

    @Override
    public void release() {
        try {
            ApiLogin.getInstance().setLoginOutListener(null);
            ApiLogin.getInstance().release();
            ApiAppliance.getInstance().release();
        } catch (Exception e) {
            KLog.e("release " + e.getMessage());
        }
    }

    @Override
    public void setLoginOutListener(final OnSmartHomeLoginOutCallback callback) {
        KLog.d("setLoginOutListener start");
        try {
            ApiLogin.getInstance().setLoginOutListener(new ILoginOutListener() {
                @Override
                public void onLoginOut() {
                    KLog.d("setLoginOutListener ：onLoginOut");
                    if (callback != null) {
                        callback.onLoginOut();
                    }
                }
            });
        } catch (Exception e) {
            KLog.e("setLoginOutListener " + e.getMessage());
        }
        KLog.d("setLoginOutListener end");
    }

    @Override
    public void loginOut() {
        KLog.d("loginOut start");
        try {
            ApiLogin.getInstance().loginOut();
            TPUtils.put(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_ARRIVE_XIAOBAI_IS_AUTO, false);
            TPUtils.put(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_LEAVE_XIAOBAI_IS_AUTO, false);
            TPUtils.putObject(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_ARRIVE_ADDRESS_INFO, null);
            TPUtils.putObject(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_ARRIVE_EXECUTE_CONDITION, null);
            TPUtils.putObject(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_LEAVE_EXECUTE_CONDITION, null);
            TPUtils.putObject(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_LOGIN_ACCOUNT, null);
        } catch (Exception e) {
            KLog.d("loginOut " + e.getMessage());
        }
        KLog.d("loginOut end");
    }


    public boolean hasBindRobot() {

        return false;
    }


    /**
     * 语音控制接口
     *
     * @param text 文本信息
     */
    private void voiceControl(String text) {

    }


    /**
     * 打开设备
     *
     * @param deviceName 设备名称
     */
    private void deviceOpen(String deviceName) {

    }

    /**
     * 关闭设备
     *
     * @param deviceName 设备名称
     */
    private void deviceClose(String deviceName) {

    }

    /**
     * 停止设备
     *
     * @param deviceName 设备名称
     */
    private void deviceStop(String deviceName) {

    }

    /**
     * 色温灯控制
     *
     * @param deviceName 设备名称
     */
    private void colorTempLightControl(String deviceName, int light, String color) {

    }

    /**
     * rgb灯控制
     *
     * @param deviceName 设备名称
     */
    private void rgbLightControl(String deviceName, String color) {

    }

    /**
     * 空调控制
     *
     * @param airControl 空调控制参数
     */
    private void airControl(AirControl airControl) {


    }
}