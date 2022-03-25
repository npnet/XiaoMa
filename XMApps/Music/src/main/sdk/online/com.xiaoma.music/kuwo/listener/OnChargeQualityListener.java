package com.xiaoma.music.kuwo.listener;

import com.xiaoma.music.player.model.MusicQualityModel;

/**
 * Author: loren
 * Date: 2019/6/19 0019
 */
public interface OnChargeQualityListener {

    void onSuccess(MusicQualityModel model);

    void onFailed(String msg);
}
