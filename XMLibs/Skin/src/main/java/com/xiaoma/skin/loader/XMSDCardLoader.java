package com.xiaoma.skin.loader;

import android.content.Context;

import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.utils.SkinInfo;
import com.xiaoma.skin.utils.SkinUtils;

import java.io.File;

import skin.support.load.SkinSDCardLoader;

/**
 * Created by ZhangYao.
 * Date ：2018/3/1 0001
 */

public class XMSDCardLoader extends SkinSDCardLoader {

    public static final int SKIN_LOADER_STRATEGY_SDCARD = Integer.MAX_VALUE;

    @Override
    protected String getSkinPath(Context context, String skinName) {
        SkinInfo skinMsg = SkinUtils.getSkinMsg();
        File skinPath = new File(skinMsg.skinPath, skinName + SkinConstants.SKIN_FILE_SUFFIX_1);
        if (!skinPath.exists()) {
            skinPath = new File(skinMsg.skinPath, skinName + SkinConstants.SKIN_FILE_SUFFIX_2);
        }
        return skinMsg == null ? "" : skinPath.getAbsolutePath();
    }

    @Override
    public int getType() {
        return SKIN_LOADER_STRATEGY_SDCARD;
    }
}
