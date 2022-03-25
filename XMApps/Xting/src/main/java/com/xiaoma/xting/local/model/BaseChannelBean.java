package com.xiaoma.xting.local.model;

import android.content.Context;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;
import com.xiaoma.xting.common.adapter.IGalleryData;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;

import java.io.Serializable;

/**
 * @author KY
 * @date 12/5/2018
 */
public abstract class BaseChannelBean implements Serializable, IGalleryData {
    private String channelName;
    private String channelCover;
    /**
     * FM:这里的频率被乘了1000的，方便转换成int类型，用来作为主键
     * AM:这里就是真是的振幅int值，用来作为主键
     */
    @PrimaryKey(AssignType.BY_MYSELF)
    private int channelValue;
    private String xmlyId;

    public BaseChannelBean(int channelValue) {
        this.channelValue = channelValue;
    }

    public BaseChannelBean(String channelName, String channelCover, int channelValue) {
        this.channelName = channelName;
        this.channelCover = channelCover;
        this.channelValue = channelValue;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelCover() {
        return channelCover;
    }

    public int getChannelValue() {
        return channelValue;
    }

    @Override
    public String getCoverUrl() {
        return channelCover;
    }

    @Override
    public CharSequence getFooterText(Context context) {
        return null;
    }

    @Override
    public CharSequence getBottomText(Context context) {
        return channelName;
    }

    @Override
    public long getUUID() {
        return getChannelValue();
    }

    @Override
    public int getSourceType() {
        return PlayerSourceType.RADIO_YQ;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setChannelCover(String channelCover) {
        this.channelCover = channelCover;
    }

    public void setChannelValue(int channelValue) {
        this.channelValue = channelValue;
    }

    public String getXmlyId() {
        return xmlyId;
    }

    public void setXmlyId(String xmlyId) {
        this.xmlyId = xmlyId;
    }
}
