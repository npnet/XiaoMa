package com.xiaoma.xting.sdk.model;

import com.xiaoma.xting.common.XtingConstants;

/**
 * @author youthyJ
 * @date 2018/10/23
 */
public enum BandType {
    AM(XtingConstants.FMAM.TYPE_AM),
    FM(XtingConstants.FMAM.TYPE_FM);


    private int band;

    BandType(int band) {
        this.band = band;
    }

    public int getBand() {
        return band;
    }

    public static BandType valueOf(int band) {
        for (BandType bandType : BandType.values()) {
            if (bandType.getBand() == band) {
                return bandType;
            }
        }
        return null;
    }
}
