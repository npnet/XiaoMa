package com.xiaoma.pet.common.utils;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.annotation.PetEvolution;
import com.xiaoma.pet.common.callback.OnUpgradeCallback;
import com.xiaoma.pet.model.UpgradeRewardInfo;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/23 0023 17:29
 *   desc:   宠物升级处理
 * </pre>
 */
public final class UpgradeEnergyHandler {


    public static UpgradeEnergyHandler getInstance() {
        return Holder.UPGRADE_HANDLER;
    }


    /**
     * 计算是否升级
     *
     * @param level         当前等级
     * @param curExperience 当前经验
     */
    public boolean checkUpgrade(int level, long curExperience) {
        long experience = calculationNextLevelExperience(level);
        return curExperience >= experience;
    }


    /**
     * 计算经验百分比
     *
     * @param level      当前等级
     * @param experience 当前经验值
     */
    public float calculationPercent(int level, long experience) {
        long needExperience = calculationNextLevelExperience(level);
        return (float) experience / needExperience;
    }


    public void postUpgradeInfo(int upgrade, long experience, final OnUpgradeCallback callback) {
        int rawLevel = upgrade - 1;
        long balanceExperience = getBalanceExperience(rawLevel, experience);
        RequestManager.petUpgrade(upgrade, balanceExperience, new ResultCallback<XMResult<UpgradeRewardInfo>>() {
            @Override
            public void onSuccess(XMResult<UpgradeRewardInfo> result) {
                if (callback != null) {
                    callback.success(result.getData());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.failed(code, msg);
                }
            }
        });
    }


    /**
     * 由当前等级推算升下一级所需能量
     * <p>
     * 运算公式 等级^2*10-10
     *
     * @param curLevel 当前等级
     */
    public long calculationNextLevelExperience(int curLevel) {
        int nextLevel = curLevel + 1;
        return (long) (Math.pow(nextLevel, 2) * 10 - 10);
    }

    private UpgradeEnergyHandler() {
    }

    /**
     * 获取宠物等级状态
     * <p>
     * 幼儿：1~6级
     * 童年：7~13级
     * 少年：14~22级
     * 青年：23~33级
     * 壮年：33级以后
     *
     * @param level 等级
     */
    public String getPetEvolutionStatus(int level) {
        if (level == 7) {
            return PetEvolution.CHILDHOOD;
        } else if (level == 14) {
            return PetEvolution.JUVENILE;
        } else if (level == 23) {
            return PetEvolution.YOUNG;
        } else if (level == 34) {
            return PetEvolution.STRONG;
        }
        return null;
    }

    private long getBalanceExperience(int level, long curExperience) {
        long experience = calculationNextLevelExperience(level);
        return curExperience - experience;
    }


    private static class Holder {
        private final static UpgradeEnergyHandler UPGRADE_HANDLER = new UpgradeEnergyHandler();
    }
}
