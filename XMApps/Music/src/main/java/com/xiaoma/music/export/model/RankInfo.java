package com.xiaoma.music.export.model;

/**
 * Created by ZYao.
 * Date ：2019/3/19 0019
 */
public class RankInfo {
    private int rankId;
    private String rankName;
    private int digest;

    public int getDigest() {
        return digest;
    }

    public void setDigest(int digest) {
        this.digest = digest;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }
}
