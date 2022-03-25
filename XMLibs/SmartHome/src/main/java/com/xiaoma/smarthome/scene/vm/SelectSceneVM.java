package com.xiaoma.smarthome.scene.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.scene.vm
 *  @file_name:      SelectSceneVM
 *  @author:         Rookie
 *  @create_time:    2019/3/5 10:20
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

import java.util.List;

public class SelectSceneVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<SceneBean>>> xmSceneData;

    private MutableLiveData<XmResource<String>> xmExcuteScene;

    private MutableLiveData<XmResource<String>> xmLogout;

    public SelectSceneVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<String>> getXmLogout() {
        if (xmLogout == null) {
            xmLogout = new MutableLiveData<>();
        }
        return xmLogout;
    }

    public MutableLiveData<XmResource<List<SceneBean>>> getXmSceneData() {
        if (xmSceneData == null) {
            xmSceneData = new MutableLiveData<>();
        }
        return xmSceneData;
    }

    public MutableLiveData<XmResource<String>> getXmExcuteScene() {
        if (xmExcuteScene == null) {
            xmExcuteScene = new MutableLiveData<>();
        }
        return xmExcuteScene;
    }

    /**
     * 获取情景数据
     */
    public void fetchSceneData() {
        getXmSceneData().setValue(XmResource.<List<SceneBean>>loading());

        RequestManager.getInstance().fetchCMSceneList(new ResultCallback<XMResult<List<SceneBean>>>() {
            @Override
            public void onSuccess(XMResult<List<SceneBean>> result) {
                getXmSceneData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getXmSceneData().setValue(XmResource.<List<SceneBean>>error(msg));
            }
        });
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

    public void logout(){
        RequestManager.getInstance().logoutCM(new ResultCallback<XMResult<String>>() {

            @Override
            public void onSuccess(XMResult<String> result) {
                getXmLogout().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getXmLogout().setValue(XmResource.<String>error(msg));
            }
        });
    }


}
