package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;

import java.util.List;
import java.util.Map;

/**
 * @author youthyJ
 * @date 2018/10/18
 */
public class XMAdvertis extends XMBean<Advertis> {
    public XMAdvertis(Advertis advertis) {
        super(advertis);
    }

    public String getRecSrc() {
        return getSDKBean().getRecSrc();
    }

    public void setRecSrc(String recSrc) {
        getSDKBean().setRecSrc(recSrc);
    }

    public String getRecTrack() {
        return getSDKBean().getRecTrack();
    }

    public void setRecTrack(String recTrack) {
        getSDKBean().setRecTrack(recTrack);
    }

    public int getShowTime() {
        return getSDKBean().getShowTime();
    }

    public void setShowTime(int showTime) {
        getSDKBean().setShowTime(showTime);
    }

    public boolean isHasCountDownFinished() {
        return getSDKBean().isHasCountDownFinished();
    }

    public void setHasCountDownFinished(boolean hasCountDownFinished) {
        getSDKBean().setHasCountDownFinished(hasCountDownFinished);
    }

    public boolean getIsAutoNotifyInstall() {
        return getSDKBean().getIsAutoNotifyInstall();
    }

    public void setIsAutoNotifyInstall(boolean isAutoNotifyInstall) {
        getSDKBean().setIsAutoNotifyInstall(isAutoNotifyInstall);
    }

    public int getAdid() {
        return getSDKBean().getAdid();
    }

    public void setAdid(int adid) {
        getSDKBean().setAdid(adid);
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public void setName(String name) {
        getSDKBean().setName(name);
    }

    public int getClickType() {
        return getSDKBean().getClickType();
    }

    public void setClickType(int clickType) {
        getSDKBean().setClickType(clickType);
    }

    public String getLinkUrl() {
        return getSDKBean().getLinkUrl();
    }

    public void setLinkUrl(String linkUrl) {
        getSDKBean().setLinkUrl(linkUrl);
    }

    public String getImageUrl() {
        return getSDKBean().getImageUrl();
    }

    public void setImageUrl(String imageUrl) {
        getSDKBean().setImageUrl(imageUrl);
    }

    public String getLogoUrl() {
        return getSDKBean().getLogoUrl();
    }

    public void setLogoUrl(String logoUrl) {
        getSDKBean().setLogoUrl(logoUrl);
    }

    public String getSoundUrl() {
        return getSDKBean().getSoundUrl();
    }

    public void setSoundUrl(String soundUrl) {
        getSDKBean().setSoundUrl(soundUrl);
    }

    public String getThirdStatUrl() {
        return getSDKBean().getThirdStatUrl();
    }

    public void setThirdStatUrl(String thirdStatUrl) {
        getSDKBean().setThirdStatUrl(thirdStatUrl);
    }

    public boolean isAutoNotifyInstall() {
        return getSDKBean().isAutoNotifyInstall();
    }

    public void setAutoNotifyInstall(boolean autoNotifyInstall) {
        getSDKBean().setAutoNotifyInstall(autoNotifyInstall);
    }

    public String getDescription() {
        return getSDKBean().getDescription();
    }

    public void setDescription(String description) {
        getSDKBean().setDescription(description);
    }

    public int getLoadingShowTime() {
        return getSDKBean().getLoadingShowTime();
    }

    public void setLoadingShowTime(int loadingShowTime) {
        getSDKBean().setLoadingShowTime(loadingShowTime);
    }

    public int getOpenlinkType() {
        return getSDKBean().getOpenlinkType();
    }

    public void setOpenlinkType(int openlinkType) {
        getSDKBean().setOpenlinkType(openlinkType);
    }

    public int getPosition() {
        return getSDKBean().getPosition();
    }

    public void setPosition(int position) {
        getSDKBean().setPosition(position);
    }

    public String getScheme() {
        return getSDKBean().getScheme();
    }

    public void setScheme(String scheme) {
        getSDKBean().setScheme(scheme);
    }

    public long getStartAt() {
        return getSDKBean().getStartAt();
    }

    public void setStartAt(long startAt) {
        getSDKBean().setStartAt(startAt);
    }

    public long getEndAt() {
        return getSDKBean().getEndAt();
    }

    public void setEndAt(long endAt) {
        getSDKBean().setEndAt(endAt);
    }

    public String getApkUrl() {
        return getSDKBean().getApkUrl();
    }

    public void setApkUrl(String apkUrl) {
        getSDKBean().setApkUrl(apkUrl);
    }

    public List<String> getClickUrls() {
        return getSDKBean().getClickUrls();
    }

    public void setClickUrls(List<String> clickUrls) {
        getSDKBean().setClickUrls(clickUrls);
    }

    public List<String> getShowUrls() {
        return getSDKBean().getShowUrls();
    }

    public void setShowUrls(List<String> showUrls) {
        getSDKBean().setShowUrls(showUrls);
    }

    public List<String> getLoadedUrls() {
        return getSDKBean().getLoadedUrls();
    }

    public void setLoadedUrls(List<String> loadedUrls) {
        getSDKBean().setLoadedUrls(loadedUrls);
    }

    public List<String> getThirdShowStatUrls() {
        return getSDKBean().getThirdShowStatUrls();
    }

    public void setThirdShowStatUrls(List<String> thirdShowStatUrls) {
        getSDKBean().setThirdShowStatUrls(thirdShowStatUrls);
    }

    public List<String> getThirdClickStatUrls() {
        return getSDKBean().getThirdClickStatUrls();
    }

    public void setThirdClickStatUrls(List<String> thirdClickStatUrls) {
        getSDKBean().setThirdClickStatUrls(thirdClickStatUrls);
    }

    public String getDynamicCover() {
        return getSDKBean().getDynamicCover();
    }

    public void setDynamicCover(String dynamicCover) {
        getSDKBean().setDynamicCover(dynamicCover);
    }

    public String getVideoCover() {
        return getSDKBean().getVideoCover();
    }

    public void setVideoCover(String videoCover) {
        getSDKBean().setVideoCover(videoCover);
    }

    public String getBgCover() {
        return getSDKBean().getBgCover();
    }

    public void setBgCover(String bgCover) {
        getSDKBean().setBgCover(bgCover);
    }

    public boolean isClosable() {
        return getSDKBean().isClosable();
    }

    public void setClosable(boolean closable) {
        getSDKBean().setClosable(closable);
    }

    public String getColorValue() {
        return getSDKBean().getColorValue();
    }

    public void setColorValue(String colorValue) {
        getSDKBean().setColorValue(colorValue);
    }

    public Map<String, String> getAppendedCovers() {
        return getSDKBean().getAppendedCovers();
    }

    public void setAppendedCovers(Map<String, String> appendedCovers) {
        getSDKBean().setAppendedCovers(appendedCovers);
    }

    public String toString() {
        return getSDKBean().toString();
    }

    public int getVolume() {
        return getSDKBean().getVolume();
    }

    public void setVolume(int volume) {
        getSDKBean().setVolume(volume);
    }

    public int getInteractiveType() {
        return getSDKBean().getInteractiveType();
    }

    public void setInteractiveType(int interactiveType) {
        getSDKBean().setInteractiveType(interactiveType);
    }

    public int getCountDown() {
        return getSDKBean().getCountDown();
    }

    public void setCountDown(int countDown) {
        getSDKBean().setCountDown(countDown);
    }

    public int getQuantity() {
        return getSDKBean().getQuantity();
    }

    public void setQuantity(int quantity) {
        getSDKBean().setQuantity(quantity);
    }

    public int getSoundType() {
        return getSDKBean().getSoundType();
    }

    public void setSoundType(int soundType) {
        getSDKBean().setSoundType(soundType);
    }

    public String getIconStyle() {
        return getSDKBean().getIconStyle();
    }

    public void setIconStyle(String iconStyle) {
        getSDKBean().setIconStyle(iconStyle);
    }

    public String getJumpType() {
        return getSDKBean().getJumpType();
    }

    public void setJumpType(String jumpType) {
        getSDKBean().setJumpType(jumpType);
    }

    public long getTrackId() {
        return getSDKBean().getTrackId();
    }

    public void setTrackId(long trackId) {
        getSDKBean().setTrackId(trackId);
    }

    public boolean isShareFlag() {
        return getSDKBean().isShareFlag();
    }

    public void setShareFlag(boolean shareFlag) {
        getSDKBean().setShareFlag(shareFlag);
    }

    public boolean isDuringPlay() {
        return getSDKBean().isDuringPlay();
    }

    public void setDuringPlay(boolean duringPlay) {
        getSDKBean().setDuringPlay(duringPlay);
    }

    public int getAdtype() {
        return getSDKBean().getAdtype();
    }

    public void setAdtype(int adtype) {
        getSDKBean().setAdtype(adtype);
    }

    public boolean isClickable() {
        return getSDKBean().isClickable();
    }

    public void setClickable(boolean clickable) {
        getSDKBean().setClickable(clickable);
    }

    public String getRealLink() {
        return getSDKBean().getRealLink();
    }

    public void setRealLink(String realLink) {
        getSDKBean().setRealLink(realLink);
    }

    public List<String> getShowTokens() {
        return getSDKBean().getShowTokens();
    }

    public void setShowTokens(List<String> showTokens) {
        getSDKBean().setShowTokens(showTokens);
    }

    public List<String> getClickTokens() {
        return getSDKBean().getClickTokens();
    }

    public void setClickTokens(List<String> clickTokens) {
        getSDKBean().setClickTokens(clickTokens);
    }

    public String getTempToken() {
        return getSDKBean().getTempToken();
    }

    public void setTempToken(String tempToken) {
        getSDKBean().setTempToken(tempToken);
    }

    public int getShowstyle() {
        return getSDKBean().getShowstyle();
    }

    public void setShowstyle(int showstyle) {
        getSDKBean().setShowstyle(showstyle);
    }

    public int getLinkType() {
        return getSDKBean().getLinkType();
    }

    public void setLinkType(int linkType) {
        getSDKBean().setLinkType(linkType);
    }

    public long getResponseId() {
        return getSDKBean().getResponseId();
    }

    public void setResponseId(long responseId) {
        getSDKBean().setResponseId(responseId);
    }

    public boolean isXmul() {
        return getSDKBean().isXmul();
    }

    public void setXmul(boolean xmul) {
        getSDKBean().setXmul(xmul);
    }

    public long getAdSoundTime() {
        return getSDKBean().getAdSoundTime();
    }

    public void setAdSoundTime(long adSoundTime) {
        getSDKBean().setAdSoundTime(adSoundTime);
    }

    public String getAdMark() {
        return getSDKBean().getAdMark();
    }

    public void setAdMark(String adMark) {
        getSDKBean().setAdMark(adMark);
    }

    public boolean isLandScape() {
        return getSDKBean().isLandScape();
    }

    public void setLandScape(boolean landScape) {
        getSDKBean().setLandScape(landScape);
    }

    public boolean isEffective() {
        return getSDKBean().isEffective();
    }

    public void setEffective(boolean effective) {
        getSDKBean().setEffective(effective);
    }

    public long getRadioId() {
        return getSDKBean().getRadioId();
    }

    public void setRadioId(long radioId) {
        getSDKBean().setRadioId(radioId);
    }
}
