package com.xiaoma.xting.koala.utils;

import com.kaolafm.opensdk.api.media.model.AudioDetails;
import com.kaolafm.opensdk.utils.operation.OperationAssister;
import com.kaolafm.sdk.core.mediaplayer.PlayItem;
import com.kaolafm.sdk.core.mediaplayer.PlayItemType;
import com.kaolafm.sdk.core.mediaplayer.PlayerManager;
import com.kaolafm.sdk.core.mediaplayer.SoundQuality;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.RadioPlayQuality;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.koala.bean.XMAudioDetails;
import com.xiaoma.xting.koala.bean.XMCategoryMember;
import com.xiaoma.xting.koala.bean.XMPlayItem;
import com.xiaoma.xting.koala.bean.XMRadioDetailColumnMember;
import com.xiaoma.xting.koala.contract.KoalaPlayItemType;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/3
 */
public class KoalaPlayerUtils {

    private KoalaPlayerUtils() {

    }

    public static String getImage(XMCategoryMember member) {
        return OperationAssister.getImage(member.getSDKBean());
    }

    public static String getImage(XMRadioDetailColumnMember bean) {
        return OperationAssister.getImage(bean.getSDKBean());
    }

    public static PlayerInfo toPlayerInfo(PlayItem item) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(PlayerSourceType.KOALA);
        playerInfo.setSourceSubType(PlayerSourceSubType.KOALA_ALBUM);
        playerInfo.setAlbumId(item.getAlbumId());
        playerInfo.setAlbumName(item.getAlbumName());
        playerInfo.setProgramName(item.getTitle());
        playerInfo.setProgramId(item.getAudioId());
        String audioPic = item.getAudioPic();
        if (audioPic == null || audioPic.isEmpty()) {
            playerInfo.setImgUrl(item.getAlbumPic());
        } else {
            playerInfo.setImgUrl(audioPic);
        }

        playerInfo.setProgress(item.getPosition());
        int duration = item.getDuration();
        if (duration == 0) {
            duration = item.getTotalDuration();
        }
        playerInfo.setDuration(duration);
        return playerInfo;
    }

    public static PlayItem toPlayItem(AudioDetails audioDetails) {
        if (audioDetails == null) {
            return null;
        }
        PlayItem playItem = new PlayItem();
        playItem.setAudioId(audioDetails.getAudioId());
        playItem.setTitle(audioDetails.getAudioName());
        playItem.setTotalDuration(audioDetails.getOriginalDuration());
        playItem.setDuration(audioDetails.getDuration());
        if (PlayerManager.CAN_USE_M3U8_PLAYER) {
            String aacPlayUrl = audioDetails.getAacPlayUrl();
            playItem.setDuration(audioDetails.getOriginalDuration());
            playItem.setPlayUrl(aacPlayUrl);
            playItem.setOfflineUrl(aacPlayUrl);
            playItem.setFileSize(audioDetails.getAacFileSize());
        } else {
            playItem.setPlayUrl(audioDetails.getAacPlayUrl());
            playItem.setDuration(audioDetails.getDuration());
        }
        playItem.setAudioDes(audioDetails.getAudioDes());
        playItem.setAlbumId(audioDetails.getAlbumId());
        playItem.setAlbumPic(audioDetails.getAlbumPic());
        playItem.setAlbumName(audioDetails.getAlbumName());
        playItem.setAudioPic(audioDetails.getAudioPic());
        playItem.setOrderNum(audioDetails.getOrderNum());
        playItem.setMp3PlayUrl(audioDetails.getMp3PlayUrl32());
        playItem.setCategoryId(audioDetails.getCategoryId());

        playItem.setUpdateTime(String.valueOf(audioDetails.getUpdateTime()));
        playItem.setClockId(audioDetails.getClockId());

        playItem.setPosition(0);
        playItem.setIsOffline(false);
        return playItem;
    }

    public static PlayItem transformPlayItem(AudioDetails details) {
        PlayItem playItem = new PlayItem();
        playItem.setAlbumId(details.getAlbumId());
        playItem.setAlbumName(details.getAlbumName());
        playItem.setAlbumPic(details.getAlbumPic());
        playItem.setAudioId(details.getAudioId());
        playItem.setAudioPic(details.getAudioPic());
        playItem.setAudioDes(details.getAudioDes());
        playItem.setClockId(details.getClockId());
        playItem.setCategoryId(details.getCategoryId());
        playItem.setMp3PlayUrl(details.getMp3PlayUrl32());
        playItem.setFileSize(details.getMp3FileSize32());
        playItem.setDuration(details.getDuration());
        playItem.setIsLivingUrl(false);
        playItem.setPlayUrl(details.getMp3PlayUrl32());
        playItem.setUpdateTime(String.valueOf(details.getUpdateTime()));
        return playItem;
    }

    public static PlayItemType toPlayItemType(@KoalaPlayItemType int itemType) {
        PlayItemType playItemType = PlayItemType.DEFAULT;
        switch (itemType) {
            case KoalaPlayItemType.DEFAULT:
                break;
            case KoalaPlayItemType.LIVE_PLAYBACK:
                playItemType = PlayItemType.LIVE_PLAYBACK;
                break;
            case KoalaPlayItemType.LIVE_AUDITION_EDIT:
                playItemType = PlayItemType.LIVE_AUDITION_EDIT;
                break;
            case KoalaPlayItemType.LIVE_AUDITION:
                playItemType = PlayItemType.LIVE_AUDITION;
                break;
            case KoalaPlayItemType.LIVING:
                playItemType = PlayItemType.LIVING;
                break;
            case KoalaPlayItemType.BROADCAST_LIVING:
                playItemType = PlayItemType.BROADCAST_LIVING;
                break;
            case KoalaPlayItemType.BROADCAST_PLAYBACK:
                playItemType = PlayItemType.BROADCAST_PLAYBACK;
                break;
            default:

                break;
        }
        return playItemType;
    }

    @KoalaPlayItemType
    public static int toXMPlayItem(PlayItemType itemType) {
        return itemType.ordinal();
    }

    public static PlayerInfo toPlayerInfo(XMPlayItem item) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(PlayerSourceType.KOALA);
        playerInfo.setSourceSubType(PlayerSourceSubType.KOALA_ALBUM);
        playerInfo.setAlbumId(item.getAlbumId());
        playerInfo.setAlbumName(item.getAlbumName());
        playerInfo.setProgramName(item.getTitle());
        playerInfo.setProgramId(item.getAudioId());
        String audioPic = item.getAudioPic();
        if (audioPic == null || audioPic.isEmpty()) {
            playerInfo.setImgUrl(item.getAlbumPic());
        } else {
            playerInfo.setImgUrl(audioPic);
        }
        RecordInfo recordInfo = XtingUtils.getRecordDao().selectBy(PlayerSourceType.KOALA, playerInfo.getProgramId());
        if (recordInfo != null) {
            item.setPosition((int) recordInfo.getProgress());
            playerInfo.setProgress(recordInfo.getProgress());
            playerInfo.setDuration(recordInfo.getDuration());
        } else {
            playerInfo.setProgress(item.getPosition());
            playerInfo.setDuration(item.getDuration());
        }

        return playerInfo;
    }

    public static PlayerInfo toPlayerInfo(XMAudioDetails xmAudioDetails) {
        PlayItem playItem = toPlayItem(xmAudioDetails.getSDKBean());
        return toPlayerInfo(playItem);
    }

    public static int convertSoundQuality(@RadioPlayQuality int soundQuality) {
        if (soundQuality == RadioPlayQuality.LOW_QUALITY) {
            return SoundQuality.LOW_QUALITY;
        } else if (soundQuality == RadioPlayQuality.STANDARD_QUALITY) {
            return SoundQuality.STANDARD_QUALITY;
        } else if (soundQuality == RadioPlayQuality.HIGH_QUALITY) {
            return SoundQuality.HIGH_QUALITY;
        } else if (soundQuality == RadioPlayQuality.HIGHER_QUALITY) {
            return SoundQuality.HIGHER_QUALITY;
        }
        return SoundQuality.STANDARD_QUALITY;
    }
}
