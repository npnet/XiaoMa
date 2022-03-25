package com.xiaoma.xting.koala.contract;

import android.annotation.IntDef;

import com.kaolafm.sdk.core.mediaplayer.SoundQuality;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
@IntDef({KoalaQuality.LOW_QUALITY, KoalaQuality.STANDARD_QUALITY,
        KoalaQuality.HIGH_QUALITY, KoalaQuality.HIGHER_QUALITY})
@Retention(RetentionPolicy.SOURCE)
public @interface KoalaQuality {
    int LOW_QUALITY = SoundQuality.LOW_QUALITY; //低品质
    int STANDARD_QUALITY = SoundQuality.STANDARD_QUALITY; //标准品质
    int HIGH_QUALITY = SoundQuality.HIGH_QUALITY;//高品质
    int HIGHER_QUALITY = SoundQuality.HIGHER_QUALITY;//超高品质
}
