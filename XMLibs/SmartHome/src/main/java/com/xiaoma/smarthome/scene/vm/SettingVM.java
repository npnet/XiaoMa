package com.xiaoma.smarthome.scene.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.scene.vm
 *  @file_name:      SettingVM
 *  @author:         Rookie
 *  @create_time:    2019/4/26 14:50
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.common.manager.RequestManager;
import com.xiaoma.smarthome.scene.model.SceneBean;

public class SettingVM extends BaseViewModel {

    private MutableLiveData<XmResource<String>> xmSettingData;

    private MutableLiveData<XmResource<String>> xmExcuteScene;

    public SettingVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<String>> getXmSettingData() {
        if (xmSettingData == null) {
            xmSettingData = new MutableLiveData<>();
        }
        return xmSettingData;
    }

    public MutableLiveData<XmResource<String>> getXmExcuteScene() {
        if (xmExcuteScene == null) {
            xmExcuteScene = new MutableLiveData<>();
        }
        return xmExcuteScene;
    }

    public void excuteScene(String sceneId) {
        RequestManager.getInstance().excuteCMScene(sceneId, new ResultCallback<XMResult<String>>() {

            @Override
            public void onSuccess(XMResult<String> result) {
                getXmExcuteScene().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getXmExcuteScene().setValue(XmResource.<String>error(msg));
            }
        });
    }

    public void updateSceneCondition(SceneBean.AutoConditionBean bean) {
        RequestManager.getInstance().updateSceneCon(bean, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getXmSettingData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getXmSettingData().setValue(XmResource.<String>error(msg));
            }
        });
    }
}
