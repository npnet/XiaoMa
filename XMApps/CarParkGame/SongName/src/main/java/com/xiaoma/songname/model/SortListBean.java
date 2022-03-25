package com.xiaoma.songname.model;

import java.util.List;

/**
 * 排行用户model
 */
public class SortListBean {

    private List<RankUserInfo> rankingList;

    public List<RankUserInfo> getRankingList() {
        return rankingList;
    }

    public void setRankingList(List<RankUserInfo> rankingList) {
        this.rankingList = rankingList;
    }

    public static class RankUserInfo {
        private long uid;
        private String username;
        private String userPic;
        private int userSex;//1男   0女
        private int totalPoint;
        private int rankNum;
        private int userAge;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserPic() {
            return userPic;
        }

        public void setUserPic(String userPic) {
            this.userPic = userPic;
        }

        public int getUserSex() {
            return userSex;
        }

        public void setUserSex(int userSex) {
            this.userSex = userSex;
        }

        public int getTotalPoint() {
            return totalPoint;
        }

        public void setTotalPoint(int totalPoint) {
            this.totalPoint = totalPoint;
        }

        public int getRankNum() {
            return rankNum;
        }

        public void setRankNum(int rankNum) {
            this.rankNum = rankNum;
        }

        public int getUserAge() {
            return userAge;
        }

        public void setUserAge(int userAge) {
            this.userAge = userAge;
        }
    }
}
