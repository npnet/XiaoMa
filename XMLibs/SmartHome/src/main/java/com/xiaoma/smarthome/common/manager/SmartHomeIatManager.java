package com.xiaoma.smarthome.common.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common.manager
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/5/30 15:07
 *  @description：   TODO             */

import com.miot.common.abstractdevice.AbstractDevice;
import com.viomi.devicelib.manager.DeviceCentre;
import com.xiaoma.component.AppHolder;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.smarthome.common.callback.SMIatCallback;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SmartHomeIatManager {

    private static SmartHomeIatManager INSTANCE;

    private SmartHomeIatManager() {

    }

    public static SmartHomeIatManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SmartHomeIatManager();
        }
        return INSTANCE;
    }

    /**
     * 刷新云米场景列表
     *
     * @param callback
     */
    public void refreshScene(SMIatCallback callback) {
        if (!checkLogin()) {
            callback.callback(false);
            return;
        }
        RequestManager.getInstance().fetchCMSceneList(new ResultCallback<XMResult<List<SceneBean>>>() {
            @Override
            public void onSuccess(XMResult<List<SceneBean>> result) {
                //刷新场景列表 播报：场景列表已刷新
                EventBus.getDefault().post(result.getData(), SmartConstants.REFRESH_SCENE_LIST_TAT);
                callback.callback(true);
            }

            @Override
            public void onFailure(int code, String msg) {
                callback.callback(false);
            }
        });
    }

    /**
     * 执行云米场景
     */
    public void excuteScene(String name, SMIatCallback callback) {
        if (!checkLogin()) {
            callback.callback(false);
            return;
        }
        RequestManager.getInstance().excuteCMSceneByName(name, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                callback.callback(true);
            }

            @Override
            public void onFailure(int code, String msg) {
                callback.callback(false);
            }
        });
    }


    /**
     * 查询在线设备
     *
     * @param callback
     */
    public void queryOnlineDevices(SMIatCallback callback) {
        if (!checkLogin()) {
            callback.callback(false);
            return;
        }
        CMDeviceManager.getInstance().refreshRemoteDevices(CMSceneDataManager.getInstance().getCmToken());
        try {
            List<AbstractDevice> remoteDevices = DeviceCentre.getInstance().getRemoteDevices();
            if (ListUtils.isEmpty(remoteDevices)) {
                callback.callback(true, null);
                return;
            }
            List<String> list = new ArrayList<>();
            for (int i = 0; i < remoteDevices.size() - 1; i++) {
                if (remoteDevices.get(i).isOnline()) {
                    list.add(remoteDevices.get(i).getName());
                }
            }
            if (ListUtils.isEmpty(list)) {
                callback.callback(true, null);
                return;
            }
            callback.callback(true, list);
        } catch (Exception e) {
            e.printStackTrace();
            callback.callback(false, null);
        }

    }

    private boolean checkLogin() {
        return TPUtils.get(AppHolder.getInstance().getAppContext(), SmartConstants.CM_LOGIN_FLAG, false);
    }

}
