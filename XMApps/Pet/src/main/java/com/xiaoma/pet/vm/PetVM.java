package com.xiaoma.pet.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.callback.OnUpgradeCallback;
import com.xiaoma.pet.common.utils.UpgradeEnergyHandler;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.pet.model.UpgradeRewardInfo;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/3 0003 17:45
 *   desc:   宠物基本信息
 * </pre>
 */
public class PetVM extends BaseViewModel {

    public PetVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<XmResource<PetInfo>> getPetInfo() {
        final MutableLiveData<XmResource<PetInfo>> mutableLiveData = new MutableLiveData<>();
        RequestManager.getPetInfo(new ResultCallback<XMResult<PetInfo>>() {
            @Override
            public void onSuccess(XMResult<PetInfo> result) {
                mutableLiveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                mutableLiveData.setValue(XmResource.<PetInfo>failure(String.valueOf(code)));
            }
        });
        return mutableLiveData;
    }


    public void checkUpgrade(PetInfo petInfo, final OnHandleUpgradeAction onHandleUpgradeAction) {
        final int newLevel = petInfo.getGrade() + 1;
        UpgradeEnergyHandler.getInstance().postUpgradeInfo(newLevel, petInfo.getExperienceValue(), new OnUpgradeCallback() {
            @Override
            public void success(UpgradeRewardInfo upgradeRewardInfo) {
                if (onHandleUpgradeAction != null) {
                    onHandleUpgradeAction.upgradeAction(newLevel, upgradeRewardInfo);
                }
            }

            @Override
            public void failed(int code, String msg) {
                if (onHandleUpgradeAction != null) {
                    onHandleUpgradeAction.upgradeFailed();
                }
            }
        });

    }


    public void initPet(Context context) {
        RequestManager.initPet(context.getString(R.string.pet_app), "Hi,guys!", new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                KLog.d("init pet success.");
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w("init pet failed.");
            }
        });
    }


    public interface OnHandleUpgradeAction {
        void upgradeAction(int newLevel, UpgradeRewardInfo upgradeRewardInfo);

        void upgradeFailed();
    }

}
