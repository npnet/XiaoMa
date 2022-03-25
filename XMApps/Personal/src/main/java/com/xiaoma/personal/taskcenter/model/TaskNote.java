package com.xiaoma.personal.taskcenter.model;

import java.io.Serializable;

/**
 * Created by kaka
 * on 19-1-15 下午5:05
 * <p>
 * desc: 历史任务记录
 * </p>
 */
public class TaskNote implements Serializable {
    /**
     * 任务奖励积分
     */
    public int score;
    /**
     * 任务描述
     */
    public String action;
    /**
     * 任务icon
     */
    public String iconUrl;
    /**
     * 任务完成时间
     */
    public long createDate;

    /**
     * 任务类型
     */
    public int scoreType;

    @Override
    public String toString() {
        return "TaskNote{" +
                "score=" + score +
                ", action='" + action + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
