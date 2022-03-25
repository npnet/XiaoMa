package com.xiaoma.songname.model;

/**
 * 积分结算情况
 */
public class GameResult {
    private int totalPoint;//当前得分
    private String ratio;//击败人数比例

    public int getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
