package com.xiaoma.launcher.recommend.model;

/**
 * @date: 15:06 2018/12/18 0018.
 * @author: huanghai
 * @Description: 停车场的动态信息
 */
public class ParkingSpotDynamicInfo {

    //停车场id
    private String id;
    //停车场当前可用停车位  整数
    private String availablePlaces;
    /**
     * 停车难易程度
     *  1:容易
     *  2:普通
     *  3:困难
     */
    private String difficulty;
    /**
     * 标准收费高低
     *  1:低价
     *  2:中价
     *  3:高价
     */
    private String feeLevel;
    /**
     * 优惠收费高低
     *  1:低价
     *  2:中价
     *  3:高价
     */
    private String feeLevelEx;
    //下小时预测收费  单位 元
    private String feePredict;
    //找车位所需时间  单位分钟
    private String findTime;
    /**
     *  综合推荐度
     *      1:推荐
     *      2:普通
     *      3:不推荐
     */

    private String recommendation;
    /**
     * 停车场当前繁忙状态
     *      1:空
     *      2:忙
     *      3:满
     */
    private String status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(String availablePlaces) {
        this.availablePlaces = availablePlaces;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getFeeLevel() {
        return feeLevel;
    }

    public void setFeeLevel(String feeLevel) {
        this.feeLevel = feeLevel;
    }

    public String getFeeLevelEx() {
        return feeLevelEx;
    }

    public void setFeeLevelEx(String feeLevelEx) {
        this.feeLevelEx = feeLevelEx;
    }

    public String getFeePredict() {
        return feePredict;
    }

    public void setFeePredict(String feePredict) {
        this.feePredict = feePredict;
    }

    public String getFindTime() {
        return findTime;
    }

    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
