package com.xiaoma.vrpractice.common.view.tuneruler;

/**
 * @author KY
 * @date 2018/11/10/16
 */

public interface RulerCallback {
    //选取刻度变化的时候回调
    void onScaleChanging(int scale, boolean byHand);
    //选取刻度变化完成的时候回调
//    void afterScaleChanged(float scale);
}
