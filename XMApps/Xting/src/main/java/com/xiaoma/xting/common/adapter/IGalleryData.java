package com.xiaoma.xting.common.adapter;

import android.content.Context;

import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;

/**
 * @author KY
 * @date 2018/10/10
 */
public interface IGalleryData {
    /**
     * 获取item的封面图片url
     *
     * @return url
     */
    String getCoverUrl();

    /**
     * 获取item的顶部标题
     * @return 顶部标题
     */
    CharSequence getTitleText(Context context);

    /**
     * 获取item的封面底部标题
     * @return 封面底部标题
     */
    CharSequence getFooterText(Context context);

    /**
     * 获取item的底部标题
     * @return 底部标题
     */
    CharSequence getBottomText(Context context);

    /**
     * 用于判断正在播放的音频和当前页面列表显示的音频是否为同一个，用来显示跑马灯效果
     *
     * @return uuid
     */
    long getUUID();

    /**
     * 获取数据源 用于判断当前播放音频于点击的是否是同一个被
     *
     * @return
     */
    @PlayerSourceType
    int getSourceType();
}
