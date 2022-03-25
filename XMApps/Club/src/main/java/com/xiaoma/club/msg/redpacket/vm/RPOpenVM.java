package com.xiaoma.club.msg.redpacket.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;

/**
 * Created by LKF on 2019-4-15 0015.
 */
public class RPOpenVM extends BaseViewModel {
    private final MutableLiveData<RPState> mRPState = new MutableLiveData<>();

    public RPOpenVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<RPState> getRPState() {
        return mRPState;
    }

    /**
     * 当前页面状态
     */
    public enum RPState {
        /**
         * 可拆红包
         */
        RP_CAN_OPEN,
        /**
         * 拆红包中
         */
        RP_OPENING,
        /**
         * 定位中(位置红包)
         */
        RP_LOCATING,
        /**
         * 定位失败
         */
        RP_LOCATED_FAIL,
        /**
         * 不在可拆红包的区域内(位置红包)
         */
        RP_OUT_OF_AREA,
        /**
         * 红包被抢光了
         */
        RP_EMPTY,
        /**
         * 红包已过期
         */
        RP_EXPIRED,
    }
}
