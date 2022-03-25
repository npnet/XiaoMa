package com.xiaoma.login.sdk;

import com.xiaoma.login.UserBindManager;
import com.xiaoma.model.User;

/**
 * Created by kaka
 * on 19-4-26 下午8:11
 * <p>
 * desc: #a
 * </p>
 */
public class IRecordListenerImpl implements IRecordListener {

    @Override
    public void onStart() {

    }

    @Override
    public void onGuidTip(RecordGuid recordGuid) {

    }

    @Override
    public void onSuccess(int faceId) {

    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public final void onFaceAlreadyBind(int faceId) {
        User faceBoundUser = UserBindManager.getInstance().getFaceBoundUser(FaceId.valueOf(faceId));
        if (faceBoundUser == null) {
            //如果dsm发现已绑定过，但是本地数据却找不到对应账户，则通知dsm删除对应绑定关系
            FaceFactory.getSDK().deleteRecord(faceId);
            onFailure(-1, "user info error");
        } else {
            onFaceAlreadyBind(faceBoundUser);
        }
    }

    public void onFaceAlreadyBind(User user) {

    }

    @Override
    public void onEnd() {

    }
}
