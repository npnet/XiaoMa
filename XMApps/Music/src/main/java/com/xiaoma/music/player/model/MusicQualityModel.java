package com.xiaoma.music.player.model;

import com.xiaoma.music.R;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;

/**
 * Author: loren
 * Date: 2019/6/19 0019
 */
public class MusicQualityModel {

    @IKuwoConstant.IMusicQuality
    int quality;

    @IKuwoConstant.XMMusicChargeType
    int chargeType;

    public boolean isRadio() {
        return isRadio;
    }

    public void setRadio(boolean radio) {
        isRadio = radio;
    }

    boolean isRadio;

    public int getQualityText() {
        int resId = 0;
        switch (quality) {
            case IKuwoConstant.IMusicQuality.AUTO:
                resId = R.string.quality_default;
                break;
            case IKuwoConstant.IMusicQuality.FLUENT:
                resId = R.string.quality_fluent;
                break;
            case IKuwoConstant.IMusicQuality.HIGH_QUALITY:
                resId = R.string.quality_high;
                break;
            case IKuwoConstant.IMusicQuality.PERFECT:
                resId = R.string.quality_perfect;
                break;
            case IKuwoConstant.IMusicQuality.LOSS_LESS:
                resId = R.string.quality_loss;
                break;
        }
        return resId;
    }

    public int getChargeTypeText() {
        int resId = 0;
        switch (chargeType) {
            case IKuwoConstant.XMMusicChargeType.NEED_VIP:
            case IKuwoConstant.XMMusicChargeType.NEED_VIP_SONG:
            case IKuwoConstant.XMMusicChargeType.NEED_VIP_ALBUM:
                resId = R.string.need_vip;
                break;
            case IKuwoConstant.XMMusicChargeType.NEED_ALBUM:
            case IKuwoConstant.XMMusicChargeType.NEED_SONG:
                resId = R.string.need_buy;
                break;
        }
        return resId;
    }

    public boolean isNeedVip() {
        //只是返回是否需要vip，就算当前用户已经是vip，需要vip的歌曲还是会返回true
        return chargeType == IKuwoConstant.XMMusicChargeType.NEED_VIP ||
                chargeType == IKuwoConstant.XMMusicChargeType.NEED_VIP_SONG ||
                chargeType == IKuwoConstant.XMMusicChargeType.NEED_VIP_ALBUM;
    }

    public boolean isNeedBuy() {
        return chargeType == IKuwoConstant.XMMusicChargeType.NEED_ALBUM ||
                chargeType == IKuwoConstant.XMMusicChargeType.NEED_SONG;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getChargeType() {
        return chargeType;
    }

    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
    }

    @Override
    public String toString() {
        return "MusicQualityModel{" +
                "quality=" + quality +
                ", chargeType=" + chargeType +
                '}';
    }
}
