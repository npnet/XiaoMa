package com.xiaoma.instruction.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.instruction.common.manager.InstructionRequestManager;
import com.xiaoma.instruction.mode.ManualBean;
import com.xiaoma.instruction.mode.ManualItemBean;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;

import java.util.List;

public class ManualVM extends AndroidViewModel {
    private MutableLiveData<XmResource<List<ManualBean>>> mManualBean;
    private MutableLiveData<XmResource<List<ManualItemBean>>> mManualItemBean;
    public ManualVM(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<XmResource<List<ManualBean>>> getManualDates() {
        if (mManualBean == null) {
            mManualBean = new MutableLiveData<>();
        }
        return mManualBean;
    }

    public MutableLiveData<XmResource<List<ManualItemBean>>> getManualItem() {
        if (mManualItemBean == null) {
            mManualItemBean = new MutableLiveData<>();
        }
        return mManualItemBean;
    }

    public void fetchManual(){
        getManualDates().setValue(XmResource.<List<ManualBean>>loading());
        InstructionRequestManager.getInstance().getManualInfo(new ResultCallback<XMResult<List<ManualBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ManualBean>> result) {
                getManualDates().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getManualDates().setValue(XmResource.<List<ManualBean>>error(code, msg));
            }
        });
    }

    public void fetchManualItem(String itemId) {
        getManualItem().setValue(XmResource.<List<ManualItemBean>>loading());
        InstructionRequestManager.getInstance().getManualItemInfo(itemId,new ResultCallback<XMResult<List<ManualItemBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ManualItemBean>> result) {
                getManualItem().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getManualItem().setValue(XmResource.<List<ManualItemBean>>error(code, msg));
            }
        });
    }
}
