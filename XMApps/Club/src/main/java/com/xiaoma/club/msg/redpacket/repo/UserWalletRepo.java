package com.xiaoma.club.msg.redpacket.repo;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.redpacket.datasource.RPRequest;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by LKF on 2019-4-18 0018.
 */
public class UserWalletRepo {
    private static final String KEY_WALLET_MONEY = "userWalletMoney";

    /**
     * 获取用户钱包余额
     */
    public static MutableLiveData<Integer> getWalletMoney(final Context context) {
        final String key = getWalletMoneyKeyByUser();
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(TPUtils.get(context, key, -1));
        RPRequest.queryUserWallet(new SimpleCallback<Integer>() {
            @Override
            public void onSuccess(Integer money) {
                liveData.setValue(money);
                TPUtils.put(context, key, money);
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
        return liveData;
    }

    private static String getWalletMoneyKeyByUser() {
        String key = UserWalletRepo.KEY_WALLET_MONEY;
        long uid = UserUtil.getCurrentUid();
        if (uid != 0) {
            key += ("_" + uid);
        }
        return key;
    }

}
