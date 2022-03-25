package com.xiaoma.club.msg.redpacket.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.redpacket.datasource.RPRequest;
import com.xiaoma.club.msg.redpacket.model.RPDetailItemInfo;
import com.xiaoma.club.msg.redpacket.model.RPDetailResult;
import com.xiaoma.club.msg.redpacket.model.RedPacketInfo;
import com.xiaoma.club.msg.redpacket.model.RpDetailInfo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.ModelCallback;
import com.xiaoma.utils.GsonHelper;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/4/17 0017
 */
public class RPDetailVM extends BaseViewModel {
    private MutableLiveData<User> senderUser;
    private MutableLiveData<RedPacketInfo> redPacketInfo;
    private MutableLiveData<RpDetailInfo> rpDetailInfo;
    private MutableLiveData<Boolean> isReceiver;


    public RPDetailVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<User> getSenderUser() {
        if (senderUser == null) {
            senderUser = new MutableLiveData<>();
        }
        return senderUser;
    }

    public MutableLiveData<RedPacketInfo> getRedPacketInfo() {
        if (redPacketInfo == null) {
            redPacketInfo = new MutableLiveData<>();
        }
        return redPacketInfo;
    }

    public MutableLiveData<RpDetailInfo> getRpDetailInfo() {
        if (rpDetailInfo == null) {
            rpDetailInfo = new MutableLiveData<>();
        }
        return rpDetailInfo;
    }

    public MutableLiveData<Boolean> getIsReceiver() {
        if (isReceiver == null) {
            isReceiver = new MutableLiveData<>();
        }
        return isReceiver;
    }

    public void requestRpDetail(long rpId) {
        RPRequest.requestRpDetail(rpId, new ModelCallback<RPDetailResult>() {
            @Override
            public RPDetailResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, RPDetailResult.class);
            }

            @Override
            public void onSuccess(RPDetailResult model) {
                if (model != null && model.getData() != null) {
                    List<RPDetailItemInfo> rpDetailItemInfos = model.getData().getRedEnvelopeReceivedDetailList();
                    getRpDetailInfo().setValue(model.getData());
                    checkIsReceiver(rpDetailItemInfos);
                } else {
                    setEmptyValue();
                }
            }

            @Override
            public void onError(int code, String msg) {
                setEmptyValue();
            }
        });
    }

    private void checkIsReceiver(List<RPDetailItemInfo> rpDetailItemInfos) {
        User user = UserUtil.getCurrentUser();
        if (user != null) {
            long id = user.getId();
            boolean isReceiver = false;
            for (RPDetailItemInfo rpDetailItemInfo : rpDetailItemInfos) {
                if (rpDetailItemInfo.getReceiverUserId() == id) {
                    isReceiver = true;
                    break;
                }
            }
            getIsReceiver().setValue(isReceiver);
        } else {
            getIsReceiver().setValue(false);
        }
    }

    private void setEmptyValue() {
        getRpDetailInfo().setValue(new RpDetailInfo());
        getIsReceiver().setValue(false);
    }
}
