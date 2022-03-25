package com.xiaoma.autotracker.upload;

/**
 * @author taojin
 * @date 2018/12/24
 */
public interface CollectBusinessInfo {

    /**
     * 收集应用时长
     *
     * @param onlineTime
     * @param businessType {@link com.xiaoma.autotracker.model.BusinessType}
     */
    void uploadOnlineTimeEvent(long onlineTime, String businessType);

    /**
     * 收集应用启动退出
     *
     * @param businessType
     */
    void uploadAppOnOffEvent(String businessType);

    /**
     * 收集播放时长
     *
     * @param playTime
     */
    void uploadPlayTimeEvent(long playTime,String playType);

    /**
     * 收听信息
     * @param c
     */
    void uploadListenEvent(String c);


    /**
     * 车信部落计分上报（车信专用）
     * @param content 内容(内含：计分类型、部落id、上报内容详情)
     */
    void uploadClubScore(String content);


}
