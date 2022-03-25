package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/21 0021 10:04
 *   desc:   宠物升级奖励
 * </pre>
 */
public class UpgradeRewardInfo implements Parcelable {


    public static final Creator<UpgradeRewardInfo> CREATOR = new Creator<UpgradeRewardInfo>() {
        @Override
        public UpgradeRewardInfo createFromParcel(Parcel in) {
            return new UpgradeRewardInfo(in);
        }

        @Override
        public UpgradeRewardInfo[] newArray(int size) {
            return new UpgradeRewardInfo[size];
        }
    };
    /**
     * upgradeReward : {"id":2,"createDate":"2019-04-11 16:19:19","modifyDate":null,"enableStatus":1,"rewardDetails":null,"grade":2,"channelId":"AA1090","disabledFlag":1}
     * nextLevelReward : {"id":3,"createDate":"2019-04-11 16:19:19","modifyDate":null,"enableStatus":1,"rewardDetails":null,"grade":3,"channelId":"AA1090","disabledFlag":1}
     */

    private UpgradeRewardBean upgradeReward;
    private NextLevelRewardBean nextLevelReward;

    protected UpgradeRewardInfo(Parcel in) {
        upgradeReward = in.readParcelable(UpgradeRewardBean.class.getClassLoader());
        nextLevelReward = in.readParcelable(NextLevelRewardBean.class.getClassLoader());
    }

    public UpgradeRewardBean getUpgradeReward() {
        return upgradeReward;
    }

    public void setUpgradeReward(UpgradeRewardBean upgradeReward) {
        this.upgradeReward = upgradeReward;
    }

    public NextLevelRewardBean getNextLevelReward() {
        return nextLevelReward;
    }

    public void setNextLevelReward(NextLevelRewardBean nextLevelReward) {
        this.nextLevelReward = nextLevelReward;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(upgradeReward, flags);
        dest.writeParcelable(nextLevelReward, flags);
    }

    public static class UpgradeRewardBean implements Parcelable {
        public static final Creator<UpgradeRewardBean> CREATOR = new Creator<UpgradeRewardBean>() {
            @Override
            public UpgradeRewardBean createFromParcel(Parcel in) {
                return new UpgradeRewardBean(in);
            }

            @Override
            public UpgradeRewardBean[] newArray(int size) {
                return new UpgradeRewardBean[size];
            }
        };
        /**
         * id : 2
         * createDate : 2019-04-11 16:19:19
         * modifyDate : null
         * enableStatus : 1
         * rewardDetails : null
         * grade : 2
         * channelId : AA1090
         * disabledFlag : 1
         */

        private int id;
        private String createDate;
        private String modifyDate;
        private int enableStatus;
        private List<RewardDetails> gameRewardVoList;
        private int grade;
        private String channelId;
        private int disabledFlag;

        protected UpgradeRewardBean(Parcel in) {
            id = in.readInt();
            createDate = in.readString();
            modifyDate = in.readString();
            enableStatus = in.readInt();
            gameRewardVoList = in.createTypedArrayList(RewardDetails.CREATOR);
            grade = in.readInt();
            channelId = in.readString();
            disabledFlag = in.readInt();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }

        public int getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(int enableStatus) {
            this.enableStatus = enableStatus;
        }

        public List<RewardDetails> getGameRewardVoList() {
            return gameRewardVoList;
        }

        public void setGameRewardVoList(List<RewardDetails> gameRewardVoList) {
            this.gameRewardVoList = gameRewardVoList;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public int getDisabledFlag() {
            return disabledFlag;
        }

        public void setDisabledFlag(int disabledFlag) {
            this.disabledFlag = disabledFlag;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(createDate);
            dest.writeString(modifyDate);
            dest.writeInt(enableStatus);
            dest.writeTypedList(gameRewardVoList);
            dest.writeInt(grade);
            dest.writeString(channelId);
            dest.writeInt(disabledFlag);
        }
    }

    public static class NextLevelRewardBean implements Parcelable {
        public static final Creator<NextLevelRewardBean> CREATOR = new Creator<NextLevelRewardBean>() {
            @Override
            public NextLevelRewardBean createFromParcel(Parcel in) {
                return new NextLevelRewardBean(in);
            }

            @Override
            public NextLevelRewardBean[] newArray(int size) {
                return new NextLevelRewardBean[size];
            }
        };
        /**
         * id : 3
         * createDate : 2019-04-11 16:19:19
         * modifyDate : null
         * enableStatus : 1
         * rewardDetails : null
         * grade : 3
         * channelId : AA1090
         * disabledFlag : 1
         */

        private int id;
        private String createDate;
        private String modifyDate;
        private int enableStatus;
        private List<RewardDetails> gameRewardVoList;
        private int grade;
        private String channelId;
        private int disabledFlag;

        protected NextLevelRewardBean(Parcel in) {
            id = in.readInt();
            createDate = in.readString();
            modifyDate = in.readString();
            enableStatus = in.readInt();
            gameRewardVoList = in.createTypedArrayList(RewardDetails.CREATOR);
            grade = in.readInt();
            channelId = in.readString();
            disabledFlag = in.readInt();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }

        public int getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(int enableStatus) {
            this.enableStatus = enableStatus;
        }

        public List<RewardDetails> getGameRewardVoList() {
            return gameRewardVoList;
        }

        public void setGameRewardVoList(List<RewardDetails> gameRewardVoList) {
            this.gameRewardVoList = gameRewardVoList;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public int getDisabledFlag() {
            return disabledFlag;
        }

        public void setDisabledFlag(int disabledFlag) {
            this.disabledFlag = disabledFlag;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(createDate);
            dest.writeString(modifyDate);
            dest.writeInt(enableStatus);
            dest.writeTypedList(gameRewardVoList);
            dest.writeInt(grade);
            dest.writeString(channelId);
            dest.writeInt(disabledFlag);
        }
    }
}
