package com.xiaoma.music.common.adapter;

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
    String getTitleText();

    /**
     * 获取item的封面底部标题
     * @return 封面底部标题
     */
    String getFooterText();

    /**
     * 获取item的底部标题
     * @return 底部标题
     */
    String getBottomText();
}
