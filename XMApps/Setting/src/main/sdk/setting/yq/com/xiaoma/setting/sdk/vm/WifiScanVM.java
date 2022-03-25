package com.xiaoma.setting.sdk.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.fsl.android.tbox.bean.TBoxWifiInfo;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.component.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
public class WifiScanVM extends BaseViewModel {

    private Context context;
    private MutableLiveData<List<TBoxWifiInfo>> listMutableLiveData;
    private WifiManager wifiManager;
    private List<TBoxWifiInfo> beans = new ArrayList<>();
    private int wifiStatus = SDKConstants.WifiMode.OFF;

    public WifiScanVM(@NonNull Application application) {
        super(application);
        this.context = application;
    }

}
