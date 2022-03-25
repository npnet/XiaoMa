package com.xiaoma.utils.logintype.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.logintype.callback.AbsClearDataListener;
import com.xiaoma.utils.logintype.constant.LoginTypeConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/03
 * @Describe: 清理数据的广播
 */

public class CleanDataBroadcastReceiver extends BroadcastReceiver {

    public static final int DEFAULT_USER_ID = 1058;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        long userId = intent.getLongExtra(LoginTypeConstant.KEY_LOGIN_USER_ID, DEFAULT_USER_ID);
        String loginMethod = intent.getStringExtra(LoginTypeConstant.KEY_LOGIN_METHOD);
        List<AbsClearDataListener> clearDataListeners = LoginTypeManager.getInstance().getClearDataListener();
        if (TextUtils.isEmpty(action) || ListUtils.isEmpty(clearDataListeners)) return;
        if (LoginTypeConstant.BROADCAST_ACTION_SWITCH_USER_CLEAR.equals(action)) {
            for (AbsClearDataListener clearDataListener : clearDataListeners) {
                try {
                    clearDataListener.onSwitchUserClear(userId, loginMethod);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
