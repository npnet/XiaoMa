package com.xiaoma.launcher.common.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.common.vm
 *  @file_name:      BaseCollectVM
 *  @author:         Rookie
 *  @create_time:    2019/2/22 17:05
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;

public class BaseCollectVM extends BaseViewModel {

    public static final int HAVE_COLLECT_STATE = 1;
    public static final int CANCLE_COLLECT_STATE = 0;
    public static final int IS_MI_QI_LING = 1;//米其林餐厅标识

    private MutableLiveData<XmResource<String>> collectItem;

    public BaseCollectVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<String>> getCollectItem() {
        if (collectItem == null) {
            collectItem = new MutableLiveData<>();
        }
        return collectItem;
    }

    /**
     * 收藏item
     *
     * @param status
     * @param collectionId
     * @param type
     * @param collectionJson
     */
    public void collectItem(int status, String collectionId, String type, String collectionJson) {

        RequestManager.getInstance().collectItem(status, collectionId, type, collectionJson, new ResultCallback<XMResult<String>>() {

            @Override
            public void onSuccess(XMResult<String> result) {
                getCollectItem().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCollectItem().setValue(XmResource.<String>failure(msg));
            }
        });

    }
}
