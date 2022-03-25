package com.xiaoma.xting.sdk.utils;

import android.util.Log;

import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.common.playerSource.contract.PlayerPlayMode;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.online.model.PlayMode;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.bean.XMSubordinatedAlbum;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.util.ModelUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/26
 */
public class HimalayanPlayerUtils {
    private static final String TAG = HimalayanPlayerUtils.class.getSimpleName();
    public static final int INDEX_NOT_FOUND = -1;
    public static final String CLOCK_00_00 = "00:00";
    public static final String CLOCK_00_59 = "00:59";
    public static final String CLOCK_24_00 = "24:00";
    public static final String CLOCK_23_59 = "23:59";

    private static ThreadLocal<DateFormat> sLocalDate = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("HH:mm");
        }
    };

    private HimalayanPlayerUtils() {
        throw new UnsupportedOperationException("not allowed!");
    }

    public static XMPlayableModel toXMPlayModel(PlayableModel sound) {
        if (sound == null) {
            return null;
        }

        String soundKind = sound.getKind();
        switch (soundKind) {
            case PlayableModel.KIND_TRACK:
                return new XMTrack((Track) sound);
            case PlayableModel.KIND_RADIO:
                return toRadio(sound);
            case PlayableModel.KIND_SCHEDULE:
                return toSchedule(sound);
            default:
                Log.d(TAG, "{toXMPlayModel}-[not allowed kind] : " + soundKind);
                break;
        }

        return null;
    }

    public static XMRadio toRadio(PlayableModel sound) {
        if (sound == null) {
            return null;
        }

        if (sound instanceof Track) {
            return track2Radio((Track) sound);
        } else {
            return new XMRadio((Radio) sound);
        }
    }

    public static XMSchedule toSchedule(PlayableModel sound) {
        if (sound == null) {
            return null;
        }

        if (sound instanceof Track) {
            return track2Schedule((Track) sound);
        } else {
            return new XMSchedule((Schedule) sound);
        }
    }

    public static XMTrack schedule2Track(XMSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        return new XMTrack(ModelUtil.scheduleToTrack(schedule.getSDKBean()));
    }

    public static XMTrack radio2Track(XMRadio xmRadio) {
        if (xmRadio == null) {
            return null;
        }

        return new XMTrack(ModelUtil.radioToTrack(xmRadio.getSDKBean(), false));
    }

    public static XMRadio track2Radio(Track track) {
        if (track == null) {
            return null;
        }

        return new XMRadio(ModelUtil.trackToRadio(track));
    }

    public static XMSchedule track2Schedule(Track track) {
        if (track == null) {
            return null;
        }

        return new XMSchedule(ModelUtil.trackToSchedule(track));
    }

    public static boolean isRadio(XMPlayableModel playableModel) {
        if (playableModel == null) {
            return false;
        }

        return XMPlayableModel.KIND_RADIO.equals(playableModel.getKind());
    }

    public static boolean isTrack(XMPlayableModel playableModel) {
        if (playableModel == null) {
            return false;
        }

        return XMPlayableModel.KIND_TRACK.equals(playableModel.getKind());
    }

    public static int getPageFromOrderNum(int orderNum) {
        return orderNum / 20 + 1;
    }

    public static String getTimeWithFormatHHMM() {
        return sLocalDate.get().format(new Date());
    }

    public static int checkIndexWithId(List<? extends XMPlayableModel> list, long playId) {
        if (playId == -1) return INDEX_NOT_FOUND;
        for (int i = 0, n = list.size(); i < n; i++) {
            XMPlayableModel playModel = list.get(i);
            if (playModel.getDataId() == playId) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    public static int checkIndexWithTime(List<? extends XMPlayableModel> list) {
        return checkIndexWithTime(list, 0);
    }

    public static int checkIndexWithTime(List<? extends XMPlayableModel> list, int fromIndex) {
        if (ListUtils.isEmpty(list)) return INDEX_NOT_FOUND;
        if (fromIndex < 0) fromIndex = 0;
        if (fromIndex > list.size()) fromIndex = 0;
        DateFormat dateFormat = sLocalDate.get();
        String curTime = "";
        if (dateFormat != null) {
            curTime = dateFormat.format(new Date());
        }
        int n = list.size() - 1;
        for (int i = fromIndex; i < n; i++) {
            XMPlayableModel model = list.get(i);
            String startTime = "";
            String endTime = "";
            if (model instanceof XMSchedule) {
                startTime = ((XMSchedule) model).getStartTime();
                endTime = ((XMSchedule) model).getEndTime();
            } else if (model instanceof XMTrack) {
                startTime = ((XMTrack) model).getStartTime();
                endTime = ((XMTrack) model).getEndTime();
            }
            if (curTime.compareTo(startTime) >= 0
                    && curTime.compareTo(endTime) < 0) {
                return i;
            }
        }
        XMPlayableModel model = list.get(n);
        String endTime = "";
        if (model instanceof XMSchedule) {
            endTime = ((XMSchedule) model).getEndTime();
        } else if (model instanceof XMTrack) {
            endTime = ((XMTrack) model).getEndTime();
        }
        if (CLOCK_00_00.equals(endTime)) {
            if (curTime.compareTo(CLOCK_24_00) < 0) {
                return n;
            }
        }
        return INDEX_NOT_FOUND;
    }

    public static boolean isNewDayStart() {
        String curTime = getTimeWithFormatHHMM();
        return curTime.compareTo(CLOCK_00_00) >= 0
                && curTime.compareTo(CLOCK_00_59) < 0;
    }

    public static PlayMode mapXmlyPlayMode(@PlayerPlayMode int playMode) {
        PlayMode xmlyPlayMode = PlayMode.LIST;
        if (playMode == PlayerPlayMode.SEQUENTIAL) {
            xmlyPlayMode = PlayMode.LIST;
        } else if (playMode == PlayerPlayMode.LIST_LOOP) {
            xmlyPlayMode = PlayMode.LIST_LOOP;
        } else if (playMode == PlayerPlayMode.SINGLE_LOOP) {
            xmlyPlayMode = PlayMode.SINGLE_LOOP;
        } else if (playMode == PlayerPlayMode.SHUFFLE) {
            xmlyPlayMode = PlayMode.RANDOM;
        }
        return xmlyPlayMode;
    }

    public static XMTrack toXMTrack(PlayerInfo playerInfo) {
        XMTrack xmTrack = new XMTrack(new Track());
        XMSubordinatedAlbum xmSubordinatedAlbum = new XMSubordinatedAlbum(new SubordinatedAlbum());
        xmSubordinatedAlbum.setAlbumId(playerInfo.getAlbumId());
        xmSubordinatedAlbum.setCoverUrlMiddle(playerInfo.getCoverUrl());
        xmSubordinatedAlbum.setAlbumTitle(playerInfo.getAlbumName());

        xmTrack.setAlbum(xmSubordinatedAlbum);

        xmTrack.setDataId(playerInfo.getProgramId());
        xmTrack.setCoverUrlMiddle(playerInfo.getCoverUrl());
        xmTrack.setUpdatedAt(playerInfo.getUpdateTime());
        xmTrack.setDuration((int) playerInfo.getDuration() / 1000);
        xmTrack.setKind(XMPlayableModel.KIND_TRACK);
//        xmTrack.setOrderNum(playerInfo.getOrderNum());

        return xmTrack;
    }

    public static XMSchedule toSchedule(PlayerInfo playerInfo) {
        if (playerInfo == null) {
            return null;
        }
        XMSchedule xmSchedule = new XMSchedule(new Schedule());
        xmSchedule.setKind(XMPlayableModel.KIND_SCHEDULE);
        xmSchedule.setStartTime(CLOCK_00_00);
        xmSchedule.setEndTime(CLOCK_23_59);
        Program program = new Program();
        program.setBackPicUrl(playerInfo.getImgUrl());
        program.setProgramName(playerInfo.getProgramName());
        xmSchedule.setRelatedProgram(program);
        xmSchedule.setDataId(playerInfo.getProgramId());
        xmSchedule.setRadioId(playerInfo.getAlbumId());
        xmSchedule.setRadioName(playerInfo.getAlbumName());
        xmSchedule.setRadioPlayCount((int) playerInfo.getPlayCount());

        return xmSchedule;
    }
}
