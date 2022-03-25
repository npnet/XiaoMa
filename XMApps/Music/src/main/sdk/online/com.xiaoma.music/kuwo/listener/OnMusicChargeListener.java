package com.xiaoma.music.kuwo.listener;

import com.xiaoma.music.kuwo.model.XMMusic;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/5/29 0029
 */
public interface OnMusicChargeListener {
    /**
     * 歌曲付费类型获取成功
     *
     * @param musics        校验的歌曲列表
     * @param chargeResults 校验歌曲类型列表
     */
    void onChargeSuccess(List<XMMusic> musics, List<Integer> chargeResults);

    /**
     * 歌曲付费类型获取 * @param msg
     */
    void onChargeFailed(String msg);
}
