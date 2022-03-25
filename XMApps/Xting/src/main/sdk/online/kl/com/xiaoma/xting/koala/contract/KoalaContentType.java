package com.xiaoma.xting.koala.contract;

import android.annotation.IntDef;

import com.kaolafm.opensdk.ResType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 * 想要获取分类的内容类型
 *
 * @author YangGang
 * @date 2019/6/21
 */

@IntDef({KoalaContentType.ALL, KoalaContentType.ALBUM, KoalaContentType.BROADCAST,
        KoalaContentType.LIVING, KoalaContentType.PGC, KoalaContentType.QQ_RADIO})
@Retention(RetentionPolicy.SOURCE)
public @interface KoalaContentType {
    int ALL = ResType.TYPE_ALL; //获取所有内容类型
    int ALBUM = ResType.TYPE_ALBUM; //专辑
    int BROADCAST = ResType.TYPE_BROADCAST; //广播
    int LIVING = ResType.TYPE_LIVE; //直播
    int PGC = ResType.TYPE_RADIO; //智能电台
    int QQ_RADIO = ResType.TYPE_QQ_MUSIC; //QQ音乐电台
}
