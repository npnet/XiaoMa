package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.media.model.AlbumDetails;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class XMAlbumDetails extends XMBaseMediaDetails<AlbumDetails> {

    public XMAlbumDetails(AlbumDetails albumDetails) {
        super(albumDetails);
    }

    public int getCountNum() {
        return getSDKBean().getCountNum();
    }

    public void setCountNum(int countNum) {
        getSDKBean().setCountNum(countNum);
    }

    public int getSortType() {
        return getSDKBean().getSortType();
    }

    public void setSortType(int sortType) {
        getSDKBean().setSortType(sortType);
    }

    public int getHasCopyright() {
        return getSDKBean().getHasCopyright();
    }

    public void setHasCopyright(int hasCopyright) {
        getSDKBean().setHasCopyright(hasCopyright);
    }

    public String getProduce() {
        return getSDKBean().getProduce();
    }

    public void setProduce(String produce) {
        getSDKBean().setProduce(produce);
    }

    public String getStatus() {
        return getSDKBean().getStatus();
    }

    public void setStatus(String status) {
        getSDKBean().setStatus(status);
    }

    public String getUpdateDay() {
        return getSDKBean().getUpdateDay();
    }

    public void setUpdateDay(String updateDay) {
        getSDKBean().setUpdateDay(updateDay);
    }

    public String getCopyrightLabel() {
        return getSDKBean().getCopyrightLabel();
    }

    public void setCopyrightLabel(String copyrightLabel) {
        getSDKBean().setCopyrightLabel(copyrightLabel);
    }

    public long getLastCheckDate() {
        return getSDKBean().getLastCheckDate();
    }

    public void setLastCheckDate(long lastCheckDate) {
        getSDKBean().setLastCheckDate(lastCheckDate);
    }
}
