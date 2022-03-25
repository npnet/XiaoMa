package com.xiaoma.setting.other.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;


import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.setting.common.utils.DebugUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Administrator on 2018/12/4 0004.
 */

public class ThemeSettingVM extends AndroidViewModel{

    private static final String TAG = "ThemeSettingVM";

    private Context mContext;
    private MutableLiveData<Integer> mThemeValue;

    public ThemeSettingVM(@NonNull Application application) {
        super(application);
        mContext = application;

    }

    private IVendorExtension getSDKManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    public MutableLiveData<Integer> getThemeValue(){
        if (mThemeValue == null){
            mThemeValue = new MutableLiveData<>();
            mThemeValue.setValue(getSDKManager().getTheme());
        }
        return mThemeValue;
    }

    public boolean setTheme(int theme){
        getSDKManager().setTheme(theme);
        DebugUtils.e(TAG, "setTheme:" + theme);
        XmCarVendorExtensionManager.getInstance().setTheme(theme);
        KLog.d("hzx","设置主题: " + theme);
        return true;
    }

}
