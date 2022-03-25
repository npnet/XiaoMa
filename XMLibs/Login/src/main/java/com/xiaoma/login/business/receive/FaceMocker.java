package com.xiaoma.login.business.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.login.sdk.FaceFactory;
import com.xiaoma.ui.toast.XMToast;

public class FaceMocker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int faceId = intent.getIntExtra("face", 0);
        FaceFactory.getSDK().setMockFace(faceId);
        XMToast.showToast(context, "设置FaceId成功：\n    id：" + faceId);
    }
}
