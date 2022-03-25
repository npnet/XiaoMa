package com.xiaoma.xting.common.playerSource.info;

import android.text.TextUtils;

import com.xiaoma.component.AppHolder;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.koala.bean.XMAlbumDetails;
import com.xiaoma.xting.koala.bean.XMRadioDetailColumnMember;
import com.xiaoma.xting.koala.utils.KoalaPlayerUtils;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.sdk.bean.XMAlbum;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/28
 */
public class BeanConverter {

    public static final int RATE_SEC_2_MS = 1000;
    public static final String CLOCK_00_00 = "00:00";
    public static final String CLOCK_24_00 = "24:00";

    public static String mAlbumUrl;

    private BeanConverter() {
        throw new UnsupportedOperationException("not allowed!");
    }

    public static void setAlbumUrl(String url) {
        mAlbumUrl = url;
    }

    public static PlayerInfo toPlayerInfo(XMPlayableModel curSound) {
        if (curSound == null) return null;
        PlayerInfo playerInfo = new PlayerInfo();
        int sourceType = PlayerSourceFacade.newSingleton().getSourceType();
        if (sourceType == PlayerSourceType.HIMALAYAN) {
            playerInfo.setType(PlayerSourceType.HIMALAYAN);
        } else {
            playerInfo.setType(PlayerSourceType.RADIO_XM);
        }

        String kind = curSound.getKind();
        if (kind != null && kind.contains(XMPlayableModel.KIND_TRACK)) {
            XMTrack track = (XMTrack) curSound;
            playerInfo.setAlbumId(track.getAlbum().getAlbumId());
            playerInfo.setProgramId(track.getDataId());
            playerInfo.setSourceSubType(PlayerSourceSubType.TRACK);
            playerInfo.setImgUrl(track.getValidCover());
            playerInfo.setAlbumName(track.getAlbum().getAlbumTitle());
            playerInfo.setProgramName(track.getTrackTitle());
            playerInfo.setUpdateTime(track.getUpdatedAt());
            playerInfo.setPlayCount(track.getPlayCount());
            playerInfo.setPage(track.getOrderNum() / XtingConstants.PAGE_COUNT + 1);
            RecordInfo recordInfo = XtingUtils.getRecordDao().selectBy(playerInfo.getType(), playerInfo.getProgramId());
            if (recordInfo != null) {
                playerInfo.setProgress(recordInfo.getProgress());
            } else {
                playerInfo.setProgress(track.getLastPlayedMills());
            }
            playerInfo.setDuration(track.getDuration() * RATE_SEC_2_MS);

        } else if (XMPlayableModel.KIND_RADIO.equals(kind)) {
            XMRadio radio = (XMRadio) curSound;
            playerInfo.setSourceSubType(PlayerSourceSubType.RADIO);
            playerInfo.setAlbumId(radio.getDataId());
            playerInfo.setProgramId(radio.getScheduleID());
            playerInfo.setImgUrl(radio.getValidCover());
            playerInfo.setAlbumName(radio.getRadioName());
            playerInfo.setProgramName(radio.getProgramName());
            playerInfo.setUpdateTime(radio.getUpdateAt());
            playerInfo.setPlayCount(radio.getRadioPlayCount());
            playerInfo.setProgress(-1);
            playerInfo.setPage(0);
            playerInfo.setExtra1(CLOCK_00_00);
            playerInfo.setExtra2(CLOCK_24_00);
        } else if (XMPlayableModel.KIND_SCHEDULE.equals(kind)) {
            XMSchedule schedule = (XMSchedule) curSound;
            playerInfo.setSourceSubType(PlayerSourceSubType.RADIO); //电台是以专辑的形式存在于历史 或者 别的记录中
            playerInfo.setAlbumId(schedule.getRadioId());
            playerInfo.setProgramId(schedule.getDataId());
            playerInfo.setImgUrl(schedule.getRelatedProgram().getBackPicUrl());
            RecordInfo recordInfo = XtingUtils.getRecordDao().selectRadio(playerInfo.getType(), playerInfo.getAlbumId());
            if (recordInfo != null) {
                playerInfo.setAlbumName(recordInfo.getAlbumName());
            } else {
                playerInfo.setAlbumName(schedule.getRadioName());
            }
            playerInfo.setProgramName(schedule.getRelatedProgram().getProgramName());
            playerInfo.setUpdateTime(schedule.getUpdateAt());
            playerInfo.setPlayCount(schedule.getRadioPlayCount());
            playerInfo.setProgress(-1);
            playerInfo.setPage(0);
            if (CLOCK_00_00.equals(schedule.getEndTime())) {
                schedule.setEndTime(CLOCK_24_00);
            }
            playerInfo.setExtra1(schedule.getStartTime());
            playerInfo.setExtra2(schedule.getEndTime());
        } else {
            return null;
        }
        return playerInfo;
    }

    public static PlayerInfo toPlayerInfo(XMRadioDetailColumnMember bean) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(PlayerSourceType.KOALA);
        playerInfo.setSourceSubType(PlayerSourceSubType.KOALA_PGC_RADIO);
        playerInfo.setAlbumId(bean.getRadioId());
        playerInfo.setAlbumName(bean.getTitle());
        playerInfo.setPlayCount(bean.getPlayTimes());
        playerInfo.setImgUrl(KoalaPlayerUtils.getImage(bean));
        return playerInfo;
    }

    public static PlayerInfo toPlayerInfo(XMAlbum bean) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(PlayerSourceType.HIMALAYAN);
        playerInfo.setSourceSubType(PlayerSourceSubType.TRACK);
        playerInfo.setAlbumId(bean.getId());
        playerInfo.setImgUrl(bean.getValidCover());
        playerInfo.setAlbumName(bean.getAlbumTitle());
        playerInfo.setPlayCount(bean.getPlayCount());
        return playerInfo;
    }

    public static PlayerInfo toPlayerInfo(RecordInfo recordInfo) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(recordInfo.getType());
        playerInfo.setSourceSubType(recordInfo.getSourceSubType());
        playerInfo.setAlbumId(recordInfo.getAlbumId());
        playerInfo.setImgUrl(recordInfo.getImgUrl());
        playerInfo.setProgramId(recordInfo.getProgramId());
        playerInfo.setAlbumName(recordInfo.getAlbumName());
        playerInfo.setPage(recordInfo.getPage());
        playerInfo.setProgramName(recordInfo.getProgramName());
        playerInfo.setProgress(recordInfo.getProgress());
        playerInfo.setDuration(recordInfo.getDuration());

        return playerInfo;
    }

    public static PlayerInfo toPlayerInfo(SubscribeInfo info) {
        int type = info.getType();
        int subType = info.getSourceSubType();

        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(type);
        playerInfo.setSourceSubType(subType);
        playerInfo.setAlbumId(info.getAlbumId());

        playerInfo.setAlbumName(info.getAlbumName());
        playerInfo.setProgramName(info.getProgramName());

        if (type == PlayerSourceType.HIMALAYAN) {
            if (subType == PlayerSourceSubType.RADIO) {
                playerInfo.setImgUrl(info.getImgUrl());
                return playerInfo;
            }
        }
        List<RecordInfo> recordList = XtingUtils.getRecordDao().selectProgramByAlbum(info.getType(), info.getAlbumId());
        if (recordList != null && !recordList.isEmpty()) {
            RecordInfo recordInfo = recordList.get(0);
            playerInfo.setProgramId(recordInfo.getProgramId());
            playerInfo.setImgUrl(recordInfo.getCoverUrl());
            playerInfo.setProgress(recordInfo.getProgress());
            playerInfo.setDuration(recordInfo.getDuration());
        }
        return playerInfo;
    }

    public static RecordInfo toRecordInfo(PlayerInfo info) {
        RecordInfo recordInfo = new RecordInfo();
        recordInfo.setType(info.getType());
        recordInfo.setAlbumId(info.getAlbumId());
        recordInfo.setProgramId(info.getProgramId());
        recordInfo.setSourceSubType(info.getSourceSubType());
        recordInfo.setImgUrl(info.getImgUrl());
        recordInfo.setAlbumName(info.getAlbumName());
        recordInfo.setProgramName(info.getProgramName());
        recordInfo.setPage(info.getPage());
        recordInfo.setListenTime(System.currentTimeMillis());
        recordInfo.setProgress(info.getProgress());
        recordInfo.setDuration(info.getDuration());

        return recordInfo;
    }

    public static SubscribeInfo toSubscribeInfo(PlayerInfo info) {
        SubscribeInfo subscribeInfo = new SubscribeInfo();
        int type = info.getType();
        if (type == PlayerSourceType.RADIO_XM) {
            subscribeInfo.setType(PlayerSourceType.HIMALAYAN);
        } else {
            subscribeInfo.setType(type);
        }
        if (type == PlayerSourceType.RADIO_YQ) {
            mAlbumUrl = null;
            subscribeInfo.setAlbumId(info.getProgramId());
            subscribeInfo.setImgUrl(info.getImgUrl());
        } else {
            subscribeInfo.setAlbumId(info.getAlbumId());
            if (TextUtils.isEmpty(mAlbumUrl) || type == PlayerSourceType.RADIO_XM) {
                mAlbumUrl = null;
                subscribeInfo.setImgUrl(info.getImgUrl());
            } else {
                subscribeInfo.setImgUrl(mAlbumUrl);
            }
        }

        subscribeInfo.setSourceSubType(info.getSourceSubType());

        subscribeInfo.setAlbumName(info.getAlbumName());
        subscribeInfo.setProgramName(info.getProgramName());
        subscribeInfo.setSubscribeTime(System.currentTimeMillis());
        subscribeInfo.setUpdateTime(info.getUpdateTime());

        return subscribeInfo;
    }

    public static PlayerInfo toPlayerInfo(BaseChannelBean channelBean) {
        if (channelBean == null) {
            return null;
        }
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(PlayerSourceType.RADIO_YQ);
        playerInfo.setProgramId(channelBean.getChannelValue());
        playerInfo.setImgUrl(channelBean.getCoverUrl());
        String himalayanId = channelBean.getXmlyId();
        playerInfo.setAlbumName(channelBean.getChannelName());
        if (himalayanId == null || himalayanId.isEmpty()) {
            playerInfo.setAlbumId(-1);
        } else {
            playerInfo.setAlbumId(Long.parseLong(himalayanId));
        }
        if (channelBean instanceof FMChannelBean) {
            playerInfo.setProgramName(AppHolder.getInstance().getAppContext().getString(R.string.mini_player_fm_title, String.valueOf(channelBean.getChannelValue() / 1000f)));
            playerInfo.setSourceSubType(PlayerSourceSubType.YQ_RADIO_FM);
        } else {
            playerInfo.setProgramName(AppHolder.getInstance().getAppContext().getString(R.string.mini_player_am_title, String.valueOf(channelBean.getChannelValue())));
            playerInfo.setSourceSubType(PlayerSourceSubType.YQ_RADIO_AM);
        }
        playerInfo.setExtra1(CLOCK_00_00);
        playerInfo.setExtra2(CLOCK_24_00);
        return playerInfo;
    }

    public static PlayerInfo toPlayerInfo(XMRadioStation station) {
        if (station == null) return null;
        BaseChannelBean queryChannel;
        if (station.getRadioBand() == XtingConstants.FMAM.TYPE_FM) {
            queryChannel = XtingUtils.getDBManager(null).queryById(station.getChannel(), FMChannelBean.class);
        } else {
            queryChannel = XtingUtils.getDBManager(null).queryById(station.getChannel(), AMChannelBean.class);
        }

        return toPlayerInfo(queryChannel);
    }

    public static List<BaseChannelBean> toChannelList(List<SubscribeInfo> subscribeList) {
        if (subscribeList == null || subscribeList.isEmpty()) {
            return null;
        }

        List<BaseChannelBean> channelBeans = new ArrayList<>();
        BaseChannelBean channelBean;
        for (SubscribeInfo info : subscribeList) {
            if (info.getSourceSubType() == PlayerSourceSubType.YQ_RADIO_FM) {
                channelBean = new FMChannelBean(info.getAlbumName(), info.getImgUrl(), (int) info.getAlbumId());
            } else {
                channelBean = new AMChannelBean(info.getAlbumName(), info.getImgUrl(), (int) info.getAlbumId());
            }
            channelBeans.add(channelBean);
        }
        return channelBeans;
    }

    public static PlayerInfo toPlayerInfo(XMAlbumDetails bean) {
        if (bean == null) {
            return null;
        }

        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setAlbumId(bean.getId());
        playerInfo.setAlbumName(bean.getName());
        playerInfo.setType(PlayerSourceType.KOALA);
        playerInfo.setSourceSubType(PlayerSourceSubType.KOALA_ALBUM);
        playerInfo.setProgramId(-1);
        playerInfo.setImgUrl(bean.getImg());
        playerInfo.setUpdateTime(bean.getLastCheckDate());
        playerInfo.setPlayCount(bean.getCountNum());

        return playerInfo;
    }

}
