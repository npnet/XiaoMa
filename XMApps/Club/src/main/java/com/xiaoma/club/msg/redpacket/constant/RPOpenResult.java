package com.xiaoma.club.msg.redpacket.constant;

/**
 * Created by LKF on 2019-4-15 0015.
 *
 * <p>拆红包结果码,指的是调用后台"拆红包"接口时,返回的resultCode的定义.
 * <p>因为后台将拆红包的resultCode单独定义,所以这里要和{@link RPOpenStatus}区分开
 */
public class RPOpenResult {
    /**
     * 抢成功
     */
    public static final int SUCCESS = 1;

    /**
     * 已领取
     */
    public static final int HAS_OBTAIN = 10032;

    /**
     * 已过期(当前用户未抢到)
     */
    public static final int EXPIRED = 10033;

    /**
     * 已抢完(当前用户未抢到)
     */
    public static final int EMPTY = 10034;
}
