package com.xiaoma.drivingscore.historyRecord.model;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/7
 */
public class DriveRecordDetails {

    private int score;
    private int speedUpCount;
    private int slowDownCount;
    private int turnCount;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSpeedUpCount() {
        return speedUpCount;
    }

    public void setSpeedUpCount(int speedUpCount) {
        this.speedUpCount = speedUpCount;
    }

    public int getSlowDownCount() {
        return slowDownCount;
    }

    public void setSlowDownCount(int slowDownCount) {
        this.slowDownCount = slowDownCount;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    @Override
    public String toString() {
        return "DriveRecordDetails{" +
                "score=" + score +
                ", speedUpCount=" + speedUpCount +
                ", slowDownCount=" + slowDownCount +
                ", turnCount=" + turnCount +
                '}';
    }
}
