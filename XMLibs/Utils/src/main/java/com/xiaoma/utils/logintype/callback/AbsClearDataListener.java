package com.xiaoma.utils.logintype.callback;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/30
 * @Describe: 清理缓存数据回调
 */

public interface AbsClearDataListener {

    //切换账户的时候清理数据，可以在中getLoginType判断上一次
    void onSwitchUserClear(long currentUserId, String loginMethod);

}
