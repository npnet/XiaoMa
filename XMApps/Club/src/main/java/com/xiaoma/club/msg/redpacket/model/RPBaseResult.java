package com.xiaoma.club.msg.redpacket.model;

import com.google.gson.annotations.SerializedName;
import com.xiaoma.club.common.model.ClubBaseResult;

/**
 * Created by LKF on 2019-4-17 0017.
 */
public class RPBaseResult extends ClubBaseResult {
    @SerializedName("data")
    private Data data;

    public RedPacketInfo getRedPacketInfo() {
        return data != null ? data.redPacketInfo : null;
    }

    protected static class Data {
        @SerializedName("redEnvelope")
        private RedPacketInfo redPacketInfo;
    }
}
