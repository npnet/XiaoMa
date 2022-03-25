package com.qiming.fawcard.synthesize.data.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "drive_score_history_detail")
public class DriveScoreHistoryDetailEntity implements Serializable {

    /**
     * 主键
     */
    @DatabaseField(columnName = "id", generatedId = true)
    public int id;

    /**
     * 历史记录ID（关联DriveScoreHistoryEntity的id）
     */
    @DatabaseField(columnName = "history_id")
    public int historyId;

    /**
     * 取得时间
     */
    @DatabaseField(columnName = "time")
    public Long time;

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
     */
    public boolean isValid;

    public DriveScoreHistoryDetailEntity() {
        id = -1;
        historyId = -1;
        time = 0L;
        avgSpeed = 0.0;
        avgFuel = 0.0;
        isValid = true;
    }

    @Override
    public String toString() {
        return "DriveScoreHistoryDetailEntity{" +
                "id=" + id +
                ", historyId=" + historyId +
                ", time=" + time +
                ", avgSpeed=" + avgSpeed +
                ", avgFuel=" + avgFuel +
                '}';
    }
}
