package com.xiaoma.smarthome.common.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.common.manager.RequestManager;
import com.xiaoma.smarthome.common.model.MiUserInfo;
import com.xiaoma.smarthome.common.model.XiaoMiTtsBean;

/**
 * @author taojin
 * @date 2019/2/27
 */
public class XiaoMiVM extends BaseViewModel {

    private MutableLiveData<XmResource<XMResult<String>>> xmBindStatus;

    private MutableLiveData<XmResource<MiUserInfo>> miUserInfo;

    private MutableLiveData<XmResource<XMResult<String>>> miAuthInfo;

    private MutableLiveData<XmResource<XMResult<String>>> miParser;

    private MutableLiveData<XmResource<XMResult<String>>> miLogOut;


    public XiaoMiVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<XMResult<String>>> getXmBindStatus() {
        if (xmBindStatus == null) {
            xmBindStatus = new MutableLiveData<>();
        }
        return xmBindStatus;
    }

    public MutableLiveData<XmResource<MiUserInfo>> getMiUserInfo() {
        if (miUserInfo == null) {
            miUserInfo = new MutableLiveData<>();
        }
        return miUserInfo;
    }

    public MutableLiveData<XmResource<XMResult<String>>> getMiAuthInfo() {
        if (miAuthInfo == null) {
            miAuthInfo = new MutableLiveData<>();
        }

        return miAuthInfo;
    }

    public MutableLiveData<XmResource<XMResult<String>>> getMiParser() {
        if (miParser == null) {
            miParser = new MutableLiveData<>();
        }

        return miParser;
    }

    public MutableLiveData<XmResource<XMResult<String>>> getMiLogOut() {
        if (miLogOut == null) {
            miLogOut = new MutableLiveData<>();
        }

        return miLogOut;
    }


    /**
     * 检查小米绑定状态
     */
    public void checkUserBindMi() {
        RequestManager.getInstance().checkUserBindXiaomi(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getXmBindStatus().setValue(XmResource.success(result));
            }

            @Override
            public void onFailure(int code, String msg) {
                getXmBindStatus().setValue(XmResource.failure(msg));
            }
        });
    }

    /**
     * 小米授权
     */
    public void fetchMiAuthInfo() {
        getMiAuthInfo().setValue(XmResource.loading());
        RequestManager.getInstance().postMiAuth(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getMiAuthInfo().setValue(XmResource.success(result));
            }

            @Override
            public void onFailure(int code, String msg) {
                getMiAuthInfo().setValue(XmResource.failure(msg));
            }
        });
    }

    /**
     * 小米账户信息
     */
    public void fetchMiUserInfo() {
        getMiUserInfo().setValue(XmResource.loading());
        RequestManager.getInstance().postMiUserInfo(new ResultCallback<XMResult<MiUserInfo>>() {
            @Override
            public void onSuccess(XMResult<MiUserInfo> result) {
                getMiUserInfo().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getMiUserInfo().setValue(XmResource.failure(msg));
            }
        });
    }

    /**
     * 小米垂类
     *
     * @param voiceText
     * @param lat
     * @param lon
     */
    public void textParserMi(String voiceText, double lat, double lon) {
        RequestManager.getInstance().textParserMI(voiceText, lat, lon, new ResultCallback<XMResult<XiaoMiTtsBean>>() {
            @Override
            public void onSuccess(XMResult<XiaoMiTtsBean> result) {
               // getMiParser().setValue(XmResource.success());
            }

            @Override
            public void onFailure(int code, String msg) {
               // getMiParser().setValue(XmResource.failure(msg));
            }
        });
    }

    /**
     * 退出小米账号
     */
    public void logOutMi() {
        getMiLogOut().setValue(XmResource.loading());
        RequestManager.getInstance().logOutMi(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getMiLogOut().setValue(XmResource.success(result));
            }

            @Override
            public void onFailure(int code, String msg) {
                getMiLogOut().setValue(XmResource.<XMResult<String>>failure(msg));
            }
        });
    }
}
