package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;

/**
 * @author youthyJ
 * @date 2018/10/17
 */
public class XMPlayableModel<T extends PlayableModel> extends XMBean<T> {
    public static final String KIND_TRACK = PlayableModel.KIND_TRACK;
    public static final String KIND_RADIO = PlayableModel.KIND_RADIO;
    public static final String KIND_SCHEDULE = PlayableModel.KIND_SCHEDULE;

    public XMPlayableModel(T playableModel) {
        super(playableModel);
    }

    public static XMPlayableModel instance(PlayableModel playableModel) {
        return new XMPlayableModel(playableModel);
    }

    public long getDataId() {
        return getSDKBean().getDataId();
    }

    public void setDataId(long dataId) {
        getSDKBean().setDataId(dataId);
    }

    public String getKind() {
        return getSDKBean().getKind();
    }

    public void setKind(String kind) {
        getSDKBean().setKind(kind);
    }

    public int getLastPlayedMills() {
        return getSDKBean().getLastPlayedMills();
    }

    public void setLastPlayedMills(int lastPlayedMills) {
        getSDKBean().setLastPlayedMills(lastPlayedMills);
    }
}
