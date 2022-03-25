package com.xiaoma.launcher.travel.film.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.film.vm
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/2/22 17:54
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.CollectItems;
import com.xiaoma.trip.common.RequestManager;

public class CollectVM extends BaseCollectVM {

    private MutableLiveData<XmResource<CollectItems>> collectList;

    public CollectVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<CollectItems>> getCollectFilms() {
        if (collectList == null) {
            collectList = new MutableLiveData<>();
        }
        return collectList;
    }

    public void fetchCollectFilms(String collectionType,String type, int pageNo) {
        getCollectFilms().setValue(XmResource.<CollectItems>loading());
        RequestManager.getInstance().getCollectItems(collectionType,pageNo, LauncherConstants.PAGE_SIZE, type, new ResultCallback<XMResult<CollectItems>>() {
            @Override
            public void onSuccess(XMResult<CollectItems> result) {
                CollectItems data = result.getData();
                if (data != null && data.getCollections() != null) {
                    getCollectFilms().setValue(XmResource.success(result.getData()));
                } else {
                    getCollectFilms().setValue(XmResource.<CollectItems>failure("result null"));
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                getCollectFilms().setValue(XmResource.<CollectItems>failure(msg));
            }
        });
    }


}
