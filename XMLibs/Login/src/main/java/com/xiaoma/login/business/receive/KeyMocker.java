package com.xiaoma.login.business.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.login.sdk.CarKeyFactory;
import com.xiaoma.ui.toast.XMToast;

/**
 * Created by kaka
 * on 19-4-17 下午2:56
 * <p>
 * desc: #a
 * </p>
 */
public class KeyMocker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String keyId = intent.getStringExtra("id");
        CarKeyFactory.getSDK().setCarKey(keyId);
        XMToast.showToast(context, "设置钥匙成功：\n    id：" + keyId);
    }
}
