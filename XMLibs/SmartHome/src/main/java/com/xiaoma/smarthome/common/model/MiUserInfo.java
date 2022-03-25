package com.xiaoma.smarthome.common.model;

import java.io.Serializable;

/**
 * Created by minxiwen on 2019/1/15 0015.
 */

public class MiUserInfo implements Serializable {
    /**
     * miliaoNick : NEVER_say_NEVER
     * unionId : XjirYFC_10gI9yQwwn3swNz8xb0IiJ8m8O3ChZ1g
     * miliaoIcon_orig : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p01mbelOASVo/Nf8lKDf8EV35wS_orig.jpg
     * miliaoIcon : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p01mbelOASVo/Nf8lKDf8EV35wS.jpg
     * miliaoIcon_320 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p01mbelOASVo/Nf8lKDf8EV35wS_320.jpg
     * miliaoIcon_90 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p01mbelOASVo/Nf8lKDf8EV35wS_90.jpg
     * miliaoIcon_120 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p01mbelOASVo/Nf8lKDf8EV35wS_120.jpg
     * miliaoIcon_75 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p01mbelOASVo/Nf8lKDf8EV35wS_75.jpg
     */

    private String miliaoNick;
    private String unionId;
    private String miliaoIcon_orig;
    private String miliaoIcon;
    private String miliaoIcon_320;
    private String miliaoIcon_90;
    private String miliaoIcon_120;
    private String miliaoIcon_75;

    public String getMiliaoNick() {
        return miliaoNick;
    }

    public void setMiliaoNick(String miliaoNick) {
        this.miliaoNick = miliaoNick;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getMiliaoIcon_orig() {
        return miliaoIcon_orig;
    }

    public void setMiliaoIcon_orig(String miliaoIcon_orig) {
        this.miliaoIcon_orig = miliaoIcon_orig;
    }

    public String getMiliaoIcon() {
        return miliaoIcon;
    }

    public void setMiliaoIcon(String miliaoIcon) {
        this.miliaoIcon = miliaoIcon;
    }

    public String getMiliaoIcon_320() {
        return miliaoIcon_320;
    }

    public void setMiliaoIcon_320(String miliaoIcon_320) {
        this.miliaoIcon_320 = miliaoIcon_320;
    }

    public String getMiliaoIcon_90() {
        return miliaoIcon_90;
    }

    public void setMiliaoIcon_90(String miliaoIcon_90) {
        this.miliaoIcon_90 = miliaoIcon_90;
    }

    public String getMiliaoIcon_120() {
        return miliaoIcon_120;
    }

    public void setMiliaoIcon_120(String miliaoIcon_120) {
        this.miliaoIcon_120 = miliaoIcon_120;
    }

    public String getMiliaoIcon_75() {
        return miliaoIcon_75;
    }

    public void setMiliaoIcon_75(String miliaoIcon_75) {
        this.miliaoIcon_75 = miliaoIcon_75;
    }
}
