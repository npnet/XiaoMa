package com.xiaoma.club.msg.redpacket.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.club.msg.redpacket.datasource.RPRequest;
import com.xiaoma.club.msg.redpacket.model.LocationRp;
import com.xiaoma.club.msg.redpacket.model.LocationRpInfo;
import com.xiaoma.club.msg.redpacket.model.LocationRpResult;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.network.callback.ModelCallback;
import com.xiaoma.utils.GsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/4/16 0016
 */
public class RPLocationVM extends BaseViewModel {

    private MutableLiveData<List<LocationRpInfo>> locationRpList;

    public RPLocationVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<LocationRpInfo>> getLocationRpList() {
        if (locationRpList == null) {
            locationRpList = new MutableLiveData<>();
        }
        return locationRpList;
    }

    public void requestGroupRpList(long groupId) {
        RPRequest.requestGroupRpLocationList(groupId, new ModelCallback<LocationRpResult>() {

            @Override
            public void onSuccess(LocationRpResult model) {
                if (model != null && model.getData() != null) {
                    LocationRp data = model.getData();
                    List<LocationRpInfo> redEnvelopeList = data.getRedEnvelopeList();
                    getLocationRpList().setValue(redEnvelopeList);
                } else {
                    getLocationRpList().setValue(new ArrayList<LocationRpInfo>());
                }
            }

            @Override
            public void onError(int code, String msg) {
                getLocationRpList().setValue(new ArrayList<LocationRpInfo>());
            }

            @Override
            public LocationRpResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, LocationRpResult.class);
            }
        });
    }

    public void requestSingleRpList(long userId) {
        RPRequest.requestSingleRpLocationList(userId, new ModelCallback<LocationRpResult>() {
            @Override
            public LocationRpResult parse(String data) throws Exception {
                return  GsonHelper.fromJson(data, LocationRpResult.class);
            }

            @Override
            public void onSuccess(LocationRpResult model) {
                if (model != null && model.getData() != null) {
                    LocationRp data = model.getData();
                    List<LocationRpInfo> redEnvelopeList = data.getRedEnvelopeList();
                    getLocationRpList().setValue(redEnvelopeList);
                } else {
                    getLocationRpList().setValue(new ArrayList<LocationRpInfo>());
                }
            }

            @Override
            public void onError(int code, String msg) {
                getLocationRpList().setValue(new ArrayList<LocationRpInfo>());
            }
        });
    }
}
