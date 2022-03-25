package com.xiaoma.personal.taskcenter.model;

import com.xiaoma.personal.taskcenter.constract.TaskType;

import java.io.Serializable;

/**
 * Created by kaka
 * on 19-1-15 下午4:48
 * <p>
 * desc: 任务列表
 * </p>
 */
public class OperateTask implements Serializable {
    /**
     * 任务类型icon
     */
    public String catalogIcon;
    /**
     * 奖励icon
     */
    public String rewardIcon;
    /**
     * 任务总等级
     */
    public int totalLevel;
    /**
     * 任务类型
     */
    public Catalog catalog;
    /**
     * 任务描述
     */
    public String description;
    /**
     * 任务误标题
     */
    public String title;
    /**
     * 任务跳转URI
     */
    public String jumpUri;
    /**
     * 任务完成的等级
     */
    public int finishedLevel;
    /**
     * 任务开始时间
     */
    public long startTime;
    /**
     * 当前等级获取的积分
     */
    public int currentLevelRewordScore;
    /**
     * 任务id
     */
    public int id;
    /**
     * 任务结束时间
     */
    public long endTime;
    /**
     * 任务完成状态
     */
    public Status status;

    public String getCatalogIcon() {
        return catalogIcon;
    }

    public String getRewardIcon() {
        return rewardIcon;
    }

    public int getTotalLevel() {
        return totalLevel;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getJumpUri() {
        return jumpUri;
    }

    public int getFinishedLevel() {
        return finishedLevel;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getCurrentLevelRewordScore() {
        return currentLevelRewordScore;
    }

    public int getId() {
        return id;
    }

    public long getEndTime() {
        return endTime;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * @see TaskType
     */
    public static class Catalog {
        /**
         * code : 0
         * name : 每日任务
         * iconUrl : http://www.carbuyin.net/sl/filePath/f96bf900-3e0f-49a3-a80c-702cc1f15294.png
         */

        public int code;
        public String name;
        public String iconUrl;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        @Override
        public String toString() {
            return "Catalog{" +
                    "code=" + code +
                    ", name='" + name + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
                    '}';
        }
    }

    /**
     * 任务状态Bean
     */
    public static class Status {
        /**
         * 任务完成状态: 0待完成/1已完成
         */
        public int code;
        /**
         * 任务完成按钮文字：去完成/已完成
         */
        public String name;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "code=" + code +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OperateTask{" +
                "catalogIcon='" + catalogIcon + '\'' +
                ", rewardIcon='" + rewardIcon + '\'' +
                ", totalLevel=" + totalLevel +
                ", catalog=" + catalog +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", jumpUri='" + jumpUri + '\'' +
                ", finishedLevel=" + finishedLevel +
                ", startTime=" + startTime +
                ", currentLevelRewordScore=" + currentLevelRewordScore +
                ", id=" + id +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
}
