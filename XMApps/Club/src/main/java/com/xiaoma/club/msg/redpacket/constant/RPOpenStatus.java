package com.xiaoma.club.msg.redpacket.constant;

/**
 * Created by LKF on 2019-4-15 0015.
 * <p>调用"查询红包状态"接口后,返回的状态值,这个状态值有别于{@link RPOpenResult}.
 * <p>因为后台单独定义了该状态.
 */
public class RPOpenStatus {
    /**
     * 未领取
     */
    public static final int NOT_OPEN = 101;
    /**
     * 已领取
     */
    public static final int HAS_OPEN = 102;
    /**
     * 过期
     */
    public static final int EXPIRED = 103;
    /**
     * 已抢完(当前用户未抢到)
     */
    public static final int EMPTY = 104;
}
