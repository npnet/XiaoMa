package com.xiaoma.setting.other.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.setting.common.utils.DebugUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Administrator on 2018/12/4 0004.
 */

public class DisplaySettingVM extends AndroidViewModel implements XmCarVendorExtensionManager.ValueChangeListener {

    private static final String TAG = "DisplaySettingVM";
    private static final String CAR_CONTROL_REQ = "car_control_req";
    private Context mContext;
    private ValueChangeEvent event;

    private MutableLiveData<Integer> mDisplayModeValue;
    private MutableLiveData<Integer> mAutoDisplayLevelValue;
    private MutableLiveData<Integer> mDayDisplayLevelValue;
    private MutableLiveData<Integer> mNightDisplayLevelValue;
    private MutableLiveData<Integer> mDisplayBoardValue;
    private MutableLiveData<Boolean> mBanVideoStatus;

    public DisplaySettingVM(@NonNull Application application) {
        super(application);
        mContext = application;
        getManager().addValueChangeListener(this);
    }

    private IVendorExtension getSDKManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    public MutableLiveData<Integer> getDisplayMode(){
        if (mDisplayModeValue == null){
            mDisplayModeValue = new MutableLiveData<>();
        }
        mDisplayModeValue.setValue(getSDKManager().getDisplayMode());
        return mDisplayModeValue;
    }

    public boolean setDisplayMode(int mode){
        getSDKManager().setDisplayMode(mode);
        DebugUtils.e(TAG, "setDisplayMode:" + mode);
        KLog.d(CAR_CONTROL_REQ, "设置显示模式: " + mode);
        return true;
    }

    public MutableLiveData<Integer> getAutoDisplayLevel(){
        if (mAutoDisplayLevelValue == null){
            mAutoDisplayLevelValue = new MutableLiveData<>();
        }
        mAutoDisplayLevelValue.setValue(getSDKManager().getAutoDisplayLevel());
        return mAutoDisplayLevelValue;
    }

    public MutableLiveData<Integer> getDayDisplayLevel(){
        if (mDayDisplayLevelValue == null){
            mDayDisplayLevelValue = new MutableLiveData<>();
        }
        mDayDisplayLevelValue.setValue(getSDKManager().getDayDisplayLevel());
        return mDayDisplayLevelValue;
    }

    public MutableLiveData<Integer> getNightDisplayLevel(){
        if (mNightDisplayLevelValue == null){
            mNightDisplayLevelValue = new MutableLiveData<>();
        }
        mNightDisplayLevelValue.setValue(getSDKManager().getNightDisplayLevel());
        return mNightDisplayLevelValue;
    }

    public boolean setDisplayLevel(int level){
        getSDKManager().setDisplayLevel(level);
        DebugUtils.e(TAG, "setDisplayLevel:" + level);
        return true;
    }

    public MutableLiveData<Integer> getKeyBoardLevel(){
        if (mDisplayBoardValue == null){
            mDisplayBoardValue = new MutableLiveData<>();
        }
        mDisplayBoardValue.setValue(getSDKManager().getKeyBoardLevel());
        return mDisplayBoardValue;
    }

    public boolean setKeyBoardLevel(int level){
        getSDKManager().setKeyBoardLevel(level);
        DebugUtils.e(TAG, "setKeyBoardLevel:" + level);
        return true;
    }

    public MutableLiveData<Boolean> getBanVideoStatus(){
        if (mBanVideoStatus == null){
            mBanVideoStatus = new MutableLiveData<>();
        }
        mBanVideoStatus.setValue(getSDKManager().getBanVideoStatus());
        return mBanVideoStatus;
    }

    public boolean setBanVideoStatus(boolean status){
        getSDKManager().setBanVideoStatus(status);
        DebugUtils.e(TAG, "setBanVideoStatus:" + status);
        return true;
    }


    public IVendorExtension getManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    @Override
    public void onChange(int id, Object value) {
        if (event != null) {
            event.onChange(id, value);
        }
    }

    public void setOnValueChangeEvent(ValueChangeEvent event){
        this.event = event;
    }

    public interface ValueChangeEvent{
        void onChange(int id, Object value);
    }

}
