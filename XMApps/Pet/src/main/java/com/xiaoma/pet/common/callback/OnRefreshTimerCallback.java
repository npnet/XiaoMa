package com.xiaoma.pet.common.callback;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/24 0024 15:42
 *   desc:   刷新计时器
 * </pre>
 */
public interface OnRefreshTimerCallback {

    void refresh(int hour, int min);

    void finish();
}
