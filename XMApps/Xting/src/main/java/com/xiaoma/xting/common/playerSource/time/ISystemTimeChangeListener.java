package com.xiaoma.xting.common.playerSource.time;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/20
 */
public interface ISystemTimeChangeListener {

    void onMinuteChanged();

    void onSystemTimeChanged();

    void onSystemDateChanged();
}
