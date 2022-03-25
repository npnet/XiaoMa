package com.xiaoma.xting.koala.bean;

import com.kaolafm.sdk.core.mediaplayer.PlayItem;
import com.xiaoma.adapter.base.XMBean;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/3
 */
public class XMPlayItem extends XMBean<PlayItem> {

    public XMPlayItem(PlayItem playItem) {
        super(playItem);
    }

    public long getAudioId() {
        return getSDKBean().getAudioId();
    }

    public String getTitle() {
        return getSDKBean().getTitle();
    }

    public String getPlayUrl() {
        return getSDKBean().getPlayUrl();
    }

    public String getOfflineUrl() {
        return getSDKBean().getOfflineUrl();
    }

    public boolean isOffline() {
        return getSDKBean().getIsOffline();
    }

    public String getOfflinePlayUrl() {
        return getSDKBean().getOfflinePlayUrl();
    }

    public int getPosition() {
        return getSDKBean().getPosition();
    }

    public void setPosition(int position) {
        getSDKBean().setPosition(position);
    }

    public int getDuration() {
        return getSDKBean().getDuration();
    }

    public int getTotalDuration() {
        return getSDKBean().getTotalDuration();
    }

    public String getAudioDes() {
        return getSDKBean().getAudioDes();
    }

    public long getAlbumId() {
        return getSDKBean().getAlbumId();
    }

    public String getAlbumPic() {
        return getSDKBean().getAlbumPic();
    }

    public String getAlbumOfflinePic() {
        return getSDKBean().getAlbumOfflinePic();
    }

    public String getAlbumName() {
        return getSDKBean().getAlbumName();
    }

    public int getOrderNum() {
        return getSDKBean().getOrderNum();
    }

    public String getMp3PlayUrl() {
        return getSDKBean().getMp3PlayUrl();
    }

    public String getM3u8PlayUrl() {
        return getSDKBean().getM3u8PlayUrl();
    }

    public String getShareUrl() {
        return getSDKBean().getShareUrl();
    }

    public long getCategoryId() {
        return getSDKBean().getCategoryId();
    }

    public String getHosts() {
        return getSDKBean().getHosts();
    }

    public long getFileSize() {
        return getSDKBean().getFileSize();
    }

    public int getIsLiked() {
        return getSDKBean().getIsLiked();
    }

    public String getUpdateTime() {
        return getSDKBean().getUpdateTime();
    }

    public long getCreateTime() {
        return getSDKBean().getCreateTime();
    }

    public String getClockId() {
        return getSDKBean().getClockId();
    }

    public boolean isInterrupted() {
        return getSDKBean().getIsInterrupted();
    }

    public int getDataSrc() {
        return getSDKBean().getDataSrc();
    }

    public boolean isLivingUrl() {
        return getSDKBean().isLivingUrl();
    }

    public long getStartTime() {
        return getSDKBean().getStartTime();
    }

    public long getFinishTime() {
        return getSDKBean().getFinishTime();
    }

    public String getBeginTime() {
        return getSDKBean().getBeginTime();
    }

    public String getEndTime() {
        return getSDKBean().getEndTime();
    }

    public long getLiveId() {
        return getSDKBean().getLiveId();
    }

    public String getAudioPic() {
        return getSDKBean().getAudioPic();
    }

    public String getDnsAddress() {
        return getSDKBean().getDnsAddress();
    }

    public String getMid() {
        return getSDKBean().getMid();
    }

    public int getStatus() {
        return getSDKBean().getStatus();
    }

    public int getIsThirdParty() {
        return getSDKBean().getIsThirdParty();
    }

    public int getType() {
        return getSDKBean().getType().ordinal();
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}
