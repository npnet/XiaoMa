package com.xiaoma.pet.common.callback;

import com.xiaoma.pet.model.UpgradeRewardInfo;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/23 0023 19:06
 *   desc:   宠物提交升级回调
 * </pre>
 */
public interface OnUpgradeCallback {

    void success(UpgradeRewardInfo upgradeRewardInfo);

    void failed(int code, String msg);
}
