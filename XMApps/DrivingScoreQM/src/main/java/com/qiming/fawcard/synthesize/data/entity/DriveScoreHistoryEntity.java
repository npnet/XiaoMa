package com.qiming.fawcard.synthesize.data.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "drive_score_history")
public class DriveScoreHistoryEntity implements Serializable {

    /**
     * 主键
     */
    @DatabaseField(columnName = "id", generatedId = true)
    public int id;

    /**
     * 开始时间
     */
    @DatabaseField(columnName = "start_time")
    public Long startTime;

    /**
     * 结束时间
     */
    @DatabaseField(columnName = "end_time")
    public Long endTime;

    /**
     * 行驶时间
     */
    @DatabaseField(columnName = "travel_time")
    public Long travelTime;

    /**
     * 行驶距离
     */
    @DatabaseField(columnName = "travel_dist")
    public Double travelDist;

    /**
     * 得分
     */
    @DatabaseField(columnName = "score")
    public Double score;

    /**
     * 急加速次数
     */
    @DatabaseField(columnName = "acc_num")
    public Long accNum;

    /**
     * 急减速次数
     */
    @DatabaseField(columnName = "dec_num")
    public Long decNum;

    /**
     * 急转弯次数
     */
    @DatabaseField(columnName = "turn_num")
    public Long turnNum;

    /**
     * 平均车速
     */
    @DatabaseField(columnName = "avg_speed")
    public Double avgSpeed;

    /**
     * 平均油耗
     */
    @DatabaseField(columnName = "avg_fuel")
    public Double avgFuel;

    /**
     * 是否为有效对象
     * 行程结束了 且超过2公里 才为有效
     */
    @DatabaseField(columnName = "is_valid")
    public boolean isValid;

    public DriveScoreHistoryEntity() {
        id = -1;
        startTime = System.currentTimeMillis();
        endTime = 0L;
        travelTime = 0L;
        travelDist = 0.0;
        score = 100.0;
        accNum = 0L;
        decNum = 0L;
        turnNum = 0L;
        avgSpeed = 0.0;
        avgFuel = 0.0;
        isValid = false; // 默认都是无效的 ，无效数据不在历史历史页面展示 当获取到驾驶评分时 再更新这个字段
    }

    @Override
    public String toString() {
        return "DriveScoreHistoryEntity{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", travelTime=" + travelTime +
                ", travelDist=" + travelDist +
                ", score=" + score +
                ", accNum=" + accNum +
                ", decNum=" + decNum +
                ", turnNum=" + turnNum +
                ", avgSpeed=" + avgSpeed +
                ", avgFuel=" + avgFuel +
                '}';
    }
}
