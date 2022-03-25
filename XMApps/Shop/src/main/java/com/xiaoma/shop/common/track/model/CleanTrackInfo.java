package com.xiaoma.shop.common.track.model;

import com.google.gson.annotations.SerializedName;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.utils.GsonHelper;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/8
 */
public class CleanTrackInfo {

    @SerializedName("i")
    private String cleanType;
    @SerializedName("j")
    private String cleanSize;

    public CleanTrackInfo(@ResourceType int type, String size) {
        this.cleanSize = size;
        if (type == ResourceType.SKIN) {
            cleanType = EventConstant.PageDesc.FRAGMENT_BUYED_SKIN;
        } else {
            cleanType = EventConstant.PageDesc.FRAGMENT_BUYED_VOICE;
        }
    }

    public String getCleanType() {
        return cleanType;
    }

    public void setCleanType(String cleanType) {
        this.cleanType = cleanType;
    }

    public String getCleanSize() {
        return cleanSize;
    }

    public void setCleanSize(String cleanSize) {
        this.cleanSize = cleanSize;
    }

    public String toTrackString() {
        return GsonHelper.toJson(this);
    }
}
