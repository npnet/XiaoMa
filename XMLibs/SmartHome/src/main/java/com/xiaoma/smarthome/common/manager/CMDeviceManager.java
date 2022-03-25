package com.xiaoma.smarthome.common.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common
 *  @file_name:      CMDeviceManager
 *  @author:         Rookie
 *  @create_time:    2019/4/29 14:29
 *  @description：   TODO             */

import android.content.Context;
import android.text.TextUtils;

import com.miot.common.abstractdevice.AbstractDevice;
import com.viomi.devicelib.manager.DeviceCentre;
import com.viomi.devicelib.manager.inter.IDeviceCallBack;
import com.xiaoma.component.AppHolder;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.login.model.XiaoMiBean;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;

import java.util.List;

public class CMDeviceManager {

    private List<AbstractDevice> mDeviceList;

    private CMDeviceManager() {
    }

    private static class SingletonHolder {
        private static final CMDeviceManager INSTANCE = new CMDeviceManager();
    }

    public static CMDeviceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 初始化cm
     *
     * @param context
     */
    public void initCM(Context context) {
        try {
            DeviceCentre.getInstance().init(context, SmartConstants.CM_LOGIN_TYPE, SmartConstants.CM_APPKEY);
        } catch (Exception e) {
            KLog.d("logincmsdk initcm fail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 登陆cm
     *
     * @param xiaomi
     */
    public void loginCM(XiaoMiBean xiaomi) {
        try {
            boolean flag = DeviceCentre.getInstance().loginUser(xiaomi.getAccessToken(),
                    xiaomi.getMiId(), xiaomi.getMacKey(), xiaomi.getMexpiresIn(), xiaomi.getMacAlgorithm(), xiaomi.getType());
            TPUtils.put(AppHolder.getInstance().getAppContext(), SmartConstants.CM_LOGIN_FLAG, flag);
            KLog.d("logincmsdk success: " + flag);
        } catch (Exception e) {
            e.printStackTrace();
            KLog.d("logincmsdk fail: " + e.getMessage());
            TPUtils.put(AppHolder.getInstance().getAppContext(), SmartConstants.CM_LOGIN_FLAG, false);
        }


    }

    /**
     * 刷新装备
     */
    public void refreshRemoteDevices(String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        try {
            DeviceCentre.getInstance().refreshRemoteDevices(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加装备监听
     */
    public void addDeviceCallback() {
        //搜索设备回调
        DeviceCentre.getInstance().addDeviceCallBack(new IDeviceCallBack() {
            @Override
            public void remoteDevices(List<AbstractDevice> list) {
                KLog.d("remoteDevices list size: " + list != null ? list.size() : 0);
                mDeviceList = list;
                EventBus.getDefault().post(list, SmartConstants.REFRESH_DEVICES);
            }
        });
    }

    public List<AbstractDevice> getDeviceList() {
        return mDeviceList;
    }

    /**
     * 退出登陆
     */
    public void logoutCM() {
        boolean logout;
        try {
            logout = DeviceCentre.getInstance().logout();
        } catch (Exception e) {
            logout = false;
            KLog.d("logout cm fail : " + e.getMessage());
        }
        if (logout) {
            TPUtils.put(AppHolder.getInstance().getAppContext(), SmartConstants.CM_LOGIN_FLAG, false);
        }

    }

}
