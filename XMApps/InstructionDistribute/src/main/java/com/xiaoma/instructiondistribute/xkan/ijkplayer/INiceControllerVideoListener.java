package com.xiaoma.instructiondistribute.xkan.ijkplayer;

/**
 * @author taojin
 * @date 2018/11/19
 */
public interface INiceControllerVideoListener {

    /**
     * 点击上
     */
    void onClickPrevious();

    /**
     * 点击下
     */
    void onClickNext();

    /**
     * 点击关闭
     */
    void onClickClose();

    /**
     * 播放结束
     */
    void onFinish();
}
