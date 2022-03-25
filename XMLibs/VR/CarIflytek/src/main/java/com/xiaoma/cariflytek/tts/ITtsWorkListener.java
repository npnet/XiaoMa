package com.xiaoma.cariflytek.tts;

/**
 * @author: iSun
 * @date: 2019/6/22 0022
 */
public interface ITtsWorkListener {
    public void onStart();

    public void onEnd(boolean status, int roleId);
}
