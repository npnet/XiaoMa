package com.xiaoma.shop.common.manager;

import com.xiaoma.shop.common.constant.UpdateResouceType;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/19
 * @Describe:
 */

public interface IUpdateOta {

    boolean getVehicleState();

    boolean delete3dFile();

    boolean pushSoundFile(@UpdateResouceType int type);

    boolean soundUpgrade(@UpdateResouceType int type);
}
