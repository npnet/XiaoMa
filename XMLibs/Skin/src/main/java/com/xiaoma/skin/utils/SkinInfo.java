package com.xiaoma.skin.utils;

import java.util.Objects;

/**
 * author: andy、JF
 * date:   2019/6/12 14:18
 * desc:
 */
public class SkinInfo {
    public String skinName;
    public String skinId;
    public String skinPath;
    public String skinType;
    public int skinStyle;// 皮肤对应的风格

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SkinInfo) {
            SkinInfo other = (SkinInfo) obj;
            return Objects.equals(skinName, other.skinName)
                    && Objects.equals(skinId, other.skinId)
                    && Objects.equals(skinPath, other.skinPath)
                    && Objects.equals(skinType, other.skinType)
                    && skinStyle == other.skinStyle;
        }
        return false;
    }
}
