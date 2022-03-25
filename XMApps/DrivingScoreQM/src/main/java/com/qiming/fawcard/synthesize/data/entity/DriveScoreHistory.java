package com.qiming.fawcard.synthesize.data.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "drive_score_history")
public class DriveScoreHistory implements Serializable {

    @DatabaseField(columnName = "id", generatedId = true)
    public int id;             // 主键

    @DatabaseField(columnName = "startTime")
    public Long startTime;      // 驾驶开始时间

    @DatabaseField(columnName = "endTime")
    public Long endTime;        // 驾驶结束时间

    @DatabaseField(columnName = "travelTime")
    public Long travelTime;     // 行驶时间

    @DatabaseField(columnName = "travelDist")
    public Double travelDist;   // 行驶距离

    @DatabaseField(columnName = "score")
    public Double score;        // 得分

    @DatabaseField(columnName = "accNum")
    public Long accNum;         // 急加速次数

    @DatabaseField(columnName = "decNum")
    public Long decNum;         // 急减速次数

    @DatabaseField(columnName = "turnNum")
    public Long turnNum;        // 急转弯次数

    @DatabaseField(columnName = "avgSpeed")
    public Double avgSpeed;     // 平均车速

    @DatabaseField(columnName = "avgFuel")
    public Double avgFuel;      // 平均油耗

    @DatabaseField(columnName = "isResult")
    public Boolean isResult;     // 标识每次熄火前的最后一条记录


    public DriveScoreHistory() {
        id = -1;
        startTime = 0L;
        endTime = 0L;
        travelTime = 0L;
        travelDist = 0.0;
        score = 0.0;
        accNum = 0L;
        decNum = 0L;
        turnNum = 0L;
        avgSpeed = 0.0;
        avgFuel = 0.0;
        isResult = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Long travelTime) {
        this.travelTime = travelTime;
    }

    public Double getTravelDist() {
        return travelDist;
    }

    public void setTravelDist(Double travelDist) {
        this.travelDist = travelDist;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Long getAccNum() {
        return accNum;
    }

    public void setAccNum(Long accNum) {
        this.accNum = accNum;
    }

    public Long getDecNum() {
        return decNum;
    }

    public void setDecNum(Long decNum) {
        this.decNum = decNum;
    }

    public Long getTurnNum() {
        return turnNum;
    }

    public void setTurnNum(Long turnNum) {
        this.turnNum = turnNum;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getAvgFuel() {
        return avgFuel;
    }

    public void setAvgFuel(Double avgFuel) {
        this.avgFuel = avgFuel;
    }

    public Boolean getResult() {
        return isResult;
    }

    public void setResult(Boolean result) {
        isResult = result;
    }

    @Override
    public String toString() {
        return "DriveScoreHistory{" +
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
                ", isResult=" + isResult +
                '}';
    }
}
