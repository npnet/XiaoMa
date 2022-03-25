package com.xiaoma.config.utils;

import android.text.TextUtils;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.bean.SourceType;
import java.io.File;


/**
 * @author: iSun
 * @date: 2019/8/5 0005
 */
public class SourceUtils {
    private static final SourceType DEF_TYPE = SourceType.NET_MUSIC;//默认音源
    public static String pausePath = ConfigManager.FileConfig.getGlobalConfigFolder().getAbsolutePath() + "/SourceType_Pause.xmcfg";
    public static String playPath = ConfigManager.FileConfig.getGlobalConfigFolder().getAbsolutePath() + "/SourceType_Play.xmcfg";


    /**
     * 获取释放音源类型
     *
     * @return
     */
    public static SourceType getPlayType() {
        return getSourceStatus(true);
    }


    /**
     * 获取暂停音源类型
     *
     * @return
     */
    public static SourceType getPauseType() {
        return getSourceStatus(false);
    }


    /**
     * 设置音源类型
     *
     * @param sourceType
     * @return
     */
    public static boolean setPlaySrouce(SourceType sourceType) {
        return setSourceStatus(sourceType, true);
    }


    public static boolean setSourceStatus(SourceType sourceType, boolean isPlaying) {
        boolean result = false;
        if (isPlaying) {
            result = ConfigFileUtils.writeCover(sourceType.getSourceDesc(sourceType), new File(playPath));
        } else {
            if (getPlayType() == sourceType) {
                setPlaySrouce(SourceType.NONE);
            }
            result = ConfigFileUtils.writeCover(sourceType.getSourceDesc(sourceType), new File(pausePath));
        }
        return result;
    }


    public static SourceType getSourceStatus(boolean isPlaying) {
        SourceType sourceType = DEF_TYPE;
        try {
            if (isPlaying) {
                String playType = ConfigFileUtils.read(new File(playPath));
                sourceType = TextUtils.isEmpty(playType) ? DEF_TYPE : SourceType.valueOf(playType);
            } else {
                String pauseType = ConfigFileUtils.read(new File(pausePath));
                SourceType sourceType1 = SourceType.NET_MUSIC;
                sourceType = TextUtils.isEmpty(pauseType) ? DEF_TYPE : sourceType1.getSourceType(pauseType);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return sourceType;
    }


}
