package com.xiaoma.xting.launcher;

import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.online.model.AlbumBean;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMTrack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/23
 */
public class ToLauncherBeanConvert {

    public static final String TAG = "ToLauncherBeanConvert";
    private static DecimalFormat FORMAT = new DecimalFormat("0.0");

    public static ArrayList<AudioInfo> convertToAudioInfoList(List<AlbumBean> list) {
        ArrayList<AudioInfo> audioInfos = new ArrayList<>();
        AudioInfo bean;
        for (AlbumBean albumBean : list) {
            bean = new AudioInfo();
            bean.setUniqueId(albumBean.getId());
            bean.setCover(albumBean.getValidCover());
            bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
            bean.setTitle(albumBean.getAlbumTitle());
            bean.setSubTitle(albumBean.getAnnouncer().getNickname());

            audioInfos.add(bean);
        }
        return audioInfos;
    }

    public static ArrayList<AudioInfo> fmList2AudioInfoList(List<FMChannelBean> list) {
        ArrayList<AudioInfo> audioInfos = new ArrayList<>();
        AudioInfo bean;
        for (FMChannelBean channelBean : list) {
            bean = new AudioInfo();
//            bean.setAlbumId(channelBean.getXmlyId());
            bean.setUniqueId(channelBean.getUUID());
            bean.setCover(channelBean.getCoverUrl());
            bean.setAudioType(AudioConstants.AudioTypes.XTING_LOCAL_FM);
            bean.setTitle(String.format("%1$sMHz", FORMAT.format(channelBean.getChannelValue() / 1000f)));
            bean.setSubTitle(channelBean.getChannelName());

            audioInfos.add(bean);
        }

        return audioInfos;
    }

    public static ArrayList<AudioInfo> trackList2AudioInfoList(List<XMTrack> list) {
        ArrayList<AudioInfo> audioInfos = new ArrayList<>();
        AudioInfo bean;
        for (XMTrack track : list) {
            bean = new AudioInfo();
            bean.setUniqueId(track.getDataId());
            bean.setAlbumId(track.getAlbum().getAlbumId());
            bean.setCover(track.getCoverUrlMiddle());
            bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
            bean.setTitle(track.getAlbum().getAlbumTitle());
            bean.setSubTitle(track.getTrackTitle());
            audioInfos.add(bean);
        }

        return audioInfos;
    }

    public static ArrayList<AudioInfo> radioList2AudioInfoList(List<XMRadio> list) {
        ArrayList<AudioInfo> audioInfos = new ArrayList<>();
        AudioInfo bean;
        for (XMRadio radio : list) {
            bean = new AudioInfo();
            bean.setUniqueId(radio.getScheduleID());
            bean.setAlbumId(radio.getDataId());
            bean.setCover(radio.getValidCover());
            bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_RADIO);
            bean.setTitle(radio.getRadioName());
            bean.setSubTitle(radio.getProgramName());
            audioInfos.add(bean);
        }

        return audioInfos;
    }

    public static ArrayList<AudioInfo> toAudioInfo(List<PlayerInfo> list) {
        ArrayList<AudioInfo> audioInfos = new ArrayList<>();
        AudioInfo bean;
        for (PlayerInfo playerInfo : list) {
            bean = new AudioInfo();
            bean.setUniqueId(playerInfo.getProgramId());
            bean.setAlbumId(playerInfo.getAlbumId());
            bean.setCover(playerInfo.getCoverUrl());
            bean.setTitle(playerInfo.getAlbumName());
            bean.setSubTitle(playerInfo.getProgramName());
            bean.setPlayCount((int) playerInfo.getPlayCount());
            bean.setPage(playerInfo.getPage());

            int sourceType = playerInfo.getSourceType();
            if (sourceType == PlayerSourceType.HIMALAYAN) {
                int sourceSubType = playerInfo.getSourceSubType();
                if (sourceSubType == PlayerSourceSubType.TRACK) {
                    bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
                } else {
                    bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_RADIO);
                }
            } else if (sourceType == PlayerSourceType.KOALA) {
                bean.setAudioType(AudioConstants.AudioTypes.XTING_KOALA_ALBUM);
            } else if (sourceType == PlayerSourceType.RADIO_YQ) {
                bean.setAudioType(AudioConstants.AudioTypes.XTING_LOCAL_FM);
            } else if (sourceType == PlayerSourceType.RADIO_XM) {
                bean.setAudioType(AudioConstants.AudioTypes.XTING);
            }

            audioInfos.add(bean);
        }

        return audioInfos;
    }
}
