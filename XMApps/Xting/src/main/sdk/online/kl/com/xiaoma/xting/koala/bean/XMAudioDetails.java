package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.media.model.AudioDetails;
import com.kaolafm.opensdk.api.media.model.Host;
import com.kaolafm.opensdk.utils.BeanUtil;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class XMAudioDetails extends XMBean<AudioDetails> {

    public XMAudioDetails(AudioDetails audioDetails) {
        super(audioDetails);
    }

    public long getAudioId() {
        return getSDKBean().getAudioId();
    }

    public void setAudioId(long audioId) {
        getSDKBean().setAudioId(audioId);
    }

    public String getAudioName() {
        return getSDKBean().getAudioName();
    }

    public void setAudioName(String audioName) {
        getSDKBean().setAudioName(audioName);
    }

    public String getAudioPic() {
        return getSDKBean().getAudioPic();
    }

    public void setAudioPic(String audioPic) {
        getSDKBean().setAudioPic(audioPic);
    }

    public String getAudioDes() {
        return getSDKBean().getAudioDes();
    }

    public void setAudioDes(String audioDes) {
        getSDKBean().setAudioDes(audioDes);
    }

    public long getAlbumId() {
        return getSDKBean().getAlbumId();
    }

    public void setAlbumId(long albumId) {
        getSDKBean().setAlbumId(albumId);
    }

    public String getAlbumName() {
        return getSDKBean().getAlbumName();
    }

    public void setAlbumName(String albumName) {
        getSDKBean().setAlbumName(albumName);
    }

    public String getAlbumPic() {
        return getSDKBean().getAlbumPic();
    }

    public void setAlbumPic(String albumPic) {
        getSDKBean().setAlbumPic(albumPic);
    }

    public int getOrderNum() {
        return getSDKBean().getOrderNum();
    }

    public void setOrderNum(int orderNum) {
        getSDKBean().setOrderNum(orderNum);
    }

    public String getMp3PlayUrl32() {
        return getSDKBean().getMp3PlayUrl32();
    }

    public void setMp3PlayUrl32(String mp3PlayUrl32) {
        getSDKBean().setMp3PlayUrl32(mp3PlayUrl32);
    }

    public String getMp3PlayUrl64() {
        return getSDKBean().getMp3PlayUrl64();
    }

    public void setMp3PlayUrl64(String mp3PlayUrl64) {
        getSDKBean().setMp3PlayUrl64(mp3PlayUrl64);
    }

    public String getAacPlayUrl() {
        return getSDKBean().getAacPlayUrl();
    }

    public void setAacPlayUrl(String aacPlayUrl) {
        getSDKBean().setAacPlayUrl(aacPlayUrl);
    }

    public String getAacPlayUrl32() {
        return getSDKBean().getAacPlayUrl32();
    }

    public void setAacPlayUrl32(String aacPlayUrl32) {
        getSDKBean().setAacPlayUrl32(aacPlayUrl32);
    }

    public String getAacPlayUrl64() {
        return getSDKBean().getAacPlayUrl64();
    }

    public void setAacPlayUrl64(String aacPlayUrl64) {
        getSDKBean().setAacPlayUrl64(aacPlayUrl64);
    }

    public String getAacPlayUrl128() {
        return getSDKBean().getAacPlayUrl128();
    }

    public void setAacPlayUrl128(String aacPlayUrl128) {
        getSDKBean().setAacPlayUrl128(aacPlayUrl128);
    }

    public int getAacFileSize() {
        return getSDKBean().getAacFileSize();
    }

    public void setAacFileSize(int aacFileSize) {
        getSDKBean().setAacFileSize(aacFileSize);
    }

    public int getMp3FileSize32() {
        return getSDKBean().getMp3FileSize32();
    }

    public void setMp3FileSize32(int mp3FileSize32) {
        getSDKBean().setMp3FileSize32(mp3FileSize32);
    }

    public int getMp3FileSize64() {
        return getSDKBean().getMp3FileSize64();
    }

    public void setMp3FileSize64(int mp3FileSize64) {
        getSDKBean().setMp3FileSize64(mp3FileSize64);
    }

    public long getUpdateTime() {
        return getSDKBean().getUpdateTime();
    }

    public void setUpdateTime(long updateTime) {
        getSDKBean().setUpdateTime(updateTime);
    }

    public String getClockId() {
        return getSDKBean().getClockId();
    }

    public void setClockId(String clockId) {
        getSDKBean().setClockId(clockId);
    }

    public int getDuration() {
        return getSDKBean().getDuration();
    }

    public void setDuration(int duration) {
        getSDKBean().setDuration(duration);
    }

    public int getOriginalDuration() {
        return getSDKBean().getOriginalDuration();
    }

    public void setOriginalDuration(int originalDuration) {
        getSDKBean().setOriginalDuration(originalDuration);
    }

    public int getListenNum() {
        return getSDKBean().getListenNum();
    }

    public void setListenNum(int listenNum) {
        getSDKBean().setListenNum(listenNum);
    }

    public int getLikedNum() {
        return getSDKBean().getLikedNum();
    }

    public void setLikedNum(int likedNum) {
        getSDKBean().setLikedNum(likedNum);
    }

    public int getHasCopyright() {
        return getSDKBean().getHasCopyright();
    }

    public void setHasCopyright(int hasCopyright) {
        getSDKBean().setHasCopyright(hasCopyright);
    }

    public int getCommentNum() {
        return getSDKBean().getCommentNum();
    }

    public void setCommentNum(int commentNum) {
        getSDKBean().setCommentNum(commentNum);
    }

    public int getTrailerStart() {
        return getSDKBean().getTrailerStart();
    }

    public void setTrailerStart(int trailerStart) {
        getSDKBean().setTrailerStart(trailerStart);
    }

    public int getTrailerEnd() {
        return getSDKBean().getTrailerEnd();
    }

    public void setTrailerEnd(int trailerEnd) {
        getSDKBean().setTrailerEnd(trailerEnd);
    }

    public int getCategoryId() {
        return getSDKBean().getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        getSDKBean().setCategoryId(categoryId);
    }

    public String getSource() {
        return getSDKBean().getSource();
    }

    public void setSource(String source) {
        getSDKBean().setSource(source);
    }

    public int getIsListened() {
        return getSDKBean().getIsListened();
    }

    public void setIsListened(int isListened) {
        getSDKBean().setIsListened(isListened);
    }

    public String getIcon() {
        return getSDKBean().getIcon();
    }

    public void setIcon(String icon) {
        getSDKBean().setIcon(icon);
    }

    public List<XMHost> getHost() {
        List<Host> hostList = getSDKBean().getHost();
        if (hostList == null || hostList.isEmpty()) {
            return null;
        }

        List<XMHost> xmHostList = new ArrayList<>(hostList.size());
        for (Host host : hostList) {
            xmHostList.add(new XMHost(host));
        }
        return xmHostList;
    }

    public void setHost(List<XMHost> host) {
        if (host == null || host.isEmpty()) {
            getSDKBean().setHost(null);
        }

        List<Host> hostList = new ArrayList<>(host.size());
        for (XMHost xmHost : host) {
            hostList.add(xmHost.getSDKBean());
        }

        getSDKBean().setHost(hostList);
    }

    public int getIsThirdParty() {
        return getSDKBean().getIsThirdParty();
    }

    public void setIsThirdParty(int isThirdParty) {
        getSDKBean().setIsThirdParty(isThirdParty);
    }

    public XMPlayItem toPlayItem() {
        return new XMPlayItem(BeanUtil.translateToPlayItem(getSDKBean()));
    }
}
