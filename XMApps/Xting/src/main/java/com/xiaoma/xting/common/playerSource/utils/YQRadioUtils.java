package com.xiaoma.xting.common.playerSource.utils;

import android.text.TextUtils;

import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;

import java.text.DecimalFormat;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/31
 */
public class YQRadioUtils {
    private YQRadioUtils() {

    }

    public static String getFMTitle(int fmChannel) {
        return "FM " + new DecimalFormat("0.0").format(fmChannel / 1000f);
    }

    public static String getAMTitle(int amChannel) {
        return "AM " + amChannel;
    }

    public static String getRadioTitle(SubscribeInfo bean) {
        String programName = bean.getProgramName();
        if (!TextUtils.isEmpty(programName)) {
            return programName;
        } else {
            long albumId = bean.getAlbumId();
            if (albumId >= XtingConstants.FMAM.getFMStart()
                    && albumId <= XtingConstants.FMAM.getFMEnd()) {
                return getFMTitle((int) albumId);
            } else {
                return getAMTitle((int) albumId);
            }
        }
    }
}
