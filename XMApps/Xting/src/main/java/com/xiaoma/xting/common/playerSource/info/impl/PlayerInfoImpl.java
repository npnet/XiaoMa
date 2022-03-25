package com.xiaoma.xting.common.playerSource.info.impl;

import android.util.Log;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.AppHolder;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.IPlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.launcher.XtingAudioClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.xiaoma.config.bean.SourceType.NET_FM;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public class PlayerInfoImpl implements IPlayerInfo {

    private static final String TAG = PlayerInfoImpl.class.getSimpleName();

    public static final String EXTRA_CATEGORY_ID = "categoryId";
    public static final String EXTRA_PRE_SHOW = "preShowF";
    public static final String EXTRA_HISTORY = "history";
    public static final String EXTRA_CREATE_DATE = "createDate";
    public static final String EXTRA_ALBUM_URL = "albumUrl";

    private List<IPlayerInfo> mPlayerInfoList;
    // 维护的数据
    private PlayerInfo mPlayerInfo;
    private @PlayerStatus
    int mStatus;

    private int mAssistantStatus; //只区分播放和暂停处理
    private Boolean mSubscribe;

    private long mPlayStartFrom;
    private RecordInfo mRecordInfo;

    private PlayerInfoImpl() {
        mAssistantStatus = PlayerStatus.DEFAULT;
        mStatus = PlayerStatus.DEFAULT;
        mPlayerInfoList = new ArrayList<>();
    }

    public static PlayerInfoImpl newSingleton() {
        return Holder.sINSTANCE;
    }

    @Override
    public void onPlayerInfoChanged(final PlayerInfo playerInfo) {
        PrintInfo.print(TAG, "onPlayerInfoChanged", String.valueOf(playerInfo), true);

        if (playerInfo != null) {
            if (!(playerInfo.equals(mPlayerInfo)
                    && playerInfo.getProgramId() == mPlayerInfo.getProgramId())) {
                if (!playerInfo.isFromRecordF()) {
                    playerInfo.setCategoryId(XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).getCurCategoryId());
                    SharedPrefUtils.cachePlayerInfo(AppHolder.getInstance().getAppContext(), playerInfo);
                }
                int type = playerInfo.getType();
                PlayerSourceFacade.newSingleton().setSourceType(type);

                if (type == PlayerSourceType.RADIO_YQ) {
                    PlayerInfoImpl.newSingleton().resetStatus();
                    SubscribeInfo subscribeInfo = XtingUtils.getSubscribeDao().selectBy(playerInfo.getType(), playerInfo.getProgramId());
                    onProgramSubscribeChanged(subscribeInfo != null);
                } else {
                    if (type == PlayerSourceType.RADIO_XM) {
                        SubscribeInfo subscribeInfo = XtingUtils.getSubscribeDao().selectBy(PlayerSourceType.HIMALAYAN, playerInfo.getAlbumId());
                        onProgramSubscribeChanged(subscribeInfo != null);
                    }
                    if (type == PlayerSourceType.HIMALAYAN || playerInfo.isFromRecordF()) {
                        onPlayerProgress(playerInfo.getProgress(), playerInfo.getDuration());
                    }
                    if (type == PlayerSourceType.KOALA) {
                        SubscribeInfo subscribeInfo = XtingUtils.getSubscribeDao().selectBy(PlayerSourceType.KOALA, playerInfo.getAlbumId());
                        onProgramSubscribeChanged(subscribeInfo != null);
                    }
                    insertRecordInfo(playerInfo);
                }
            }
        }
        mPlayerInfo = playerInfo;
        shareStatusWithAssistant(mStatus);

        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (mPlayerInfoList.isEmpty()) return;
                for (IPlayerInfo info : mPlayerInfoList) {
                    info.onPlayerInfoChanged(playerInfo);
                }
            }
        });

    }

    @Override
    public void onPlayerStatusChanged(@PlayerStatus final int status) {
        PrintInfo.print(TAG, "onPlayerStatusChanged", String.format("mStatus = %1$s,status = %2$s,PlayerInfo = %3$s", mStatus, status, mPlayerInfo), false);
        if (mStatus != status) {
            mStatus = status;
            boolean isPlaying = (status == PlayerStatus.PLAYING);
            //语音需要上传播放状态信息
            if (!isPlaying) {
                shareStatusWithAssistant(PlayerStatus.PAUSE);
                if (mPlayerInfo != null) {
                    updateRecordInfo(mPlayerInfo.getProgress(), mPlayerInfo.getDuration());
                }
            } else {
                shareStatusWithAssistant(PlayerStatus.PLAYING);
            }
            updateTrackAtStatus(status);
        } else {
            if (status != PlayerStatus.ERROR) {
                return;
            }
        }
//        shareStatusWithAssistant(status);
        if (mPlayerInfoList.isEmpty()) return;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                toastStatus(status);
                for (IPlayerInfo info : mPlayerInfoList) {
                    info.onPlayerStatusChanged(status);
                }
            }
        });
    }

    private void shareStatusWithAssistant(@PlayerStatus int status) {
        shareStatusWithAssistant(status, false);
//        if (mAssistantStatus != status) {
//            mAssistantStatus = status;
//            int type = PlayerSourceFacade.newSingleton().getSourceType();
//            SourceType sourceType = null;
//            if (type == PlayerSourceType.HIMALAYAN || type == PlayerSourceType.KOALA || type == PlayerSourceType.RADIO_XM) {
//                int tempAudioFocusLoss = AudioFocusManager.getInstance().getTempAudioFocusLoss();
//                //短时间失去焦点，暂停状态不传递
//                if ((tempAudioFocusLoss == 1 || tempAudioFocusLoss == 2) && status == PlayerStatus.PAUSE) {
//                    Log.d(TAG, "shareStatusWithAssistant: temp pause");
//                    return;
//                }
//                sourceType = NET_FM;
//                SourceUtils.setSourceStatus(sourceType, status == PlayerStatus.PLAYING);
//                RemoteIatManager.getInstance().uploadPlayState(status == PlayerStatus.PLAYING, AppType.INTERNET_RADIO);
//            }
//        }
    }

    /**
     * 刷状态给语音
     *
     * @param status
     * @param forceUpdate 强制刷新,避免焦点问题导致的刷新状态错误
     */
    public void shareStatusWithAssistant(@PlayerStatus int status, boolean forceUpdate) {
        if (forceUpdate) {
            mAssistantStatus = status;
            int type = PlayerSourceFacade.newSingleton().getSourceType();
            if (type == PlayerSourceType.HIMALAYAN || type == PlayerSourceType.KOALA || type == PlayerSourceType.RADIO_XM) {
                SourceUtils.setSourceStatus(NET_FM, status == PlayerStatus.PLAYING);
                RemoteIatManager.getInstance().uploadPlayState(status == PlayerStatus.PLAYING, AppType.INTERNET_RADIO);
            }
        } else {
            if (mAssistantStatus != status) {
                mAssistantStatus = status;
                int type = PlayerSourceFacade.newSingleton().getSourceType();
                if (type == PlayerSourceType.HIMALAYAN || type == PlayerSourceType.KOALA || type == PlayerSourceType.RADIO_XM) {
                    int tempAudioFocusLoss = AudioFocusManager.getInstance().getTempAudioFocusLoss();
                    //短时间失去焦点，暂停状态不传递
                    if ((tempAudioFocusLoss == 1 || tempAudioFocusLoss == 2) && status == PlayerStatus.PAUSE) {
                        Log.d(TAG, "shareStatusWithAssistant: temp pause");
                        return;
                    }
                    SourceUtils.setSourceStatus(NET_FM, status == PlayerStatus.PLAYING);
                    RemoteIatManager.getInstance().uploadPlayState(status == PlayerStatus.PLAYING, AppType.INTERNET_RADIO);
                }
            }
        }

    }

    private void setPlayType(boolean isPlaying) {
        int type = PlayerSourceFacade.newSingleton().getSourceType();
        switch (type) {
            case PlayerSourceType.HIMALAYAN:
            case PlayerSourceType.RADIO_XM:
            case PlayerSourceType.KOALA:
                SourceUtils.setSourceStatus(NET_FM, isPlaying);
                break;
            case PlayerSourceType.RADIO_YQ:
                SourceUtils.setSourceStatus(SourceType.FM, true);
                break;
        }
    }

    private int mAutoPlayNextCount = 0;

    private void toastStatus(@PlayerStatus int status) {
        if (status == PlayerStatus.ERROR) {
            if (PlayerSourceFacade.newSingleton().getSourceType() != PlayerSourceType.RADIO_YQ) {
                XMToast.toastException(AppHolder.getInstance().getAppContext(), R.string.net_work_error);
            }
        } else if (status == PlayerStatus.ERROR_BY_PLAYER) {
            XMToast.toastException(AppHolder.getInstance().getAppContext(), R.string.error_by_player_new);
            autoPlayNext(3);
        } else if (status == PlayerStatus.ERROR_BY_DATA_SOURCE) {
            XMToast.toastException(AppHolder.getInstance().getAppContext(), R.string.error_by_data_source);
//            autoPlayNext(3);
        }
    }

    /**
     * 电台播放文件异常的时候，自动切换下一曲，限制数量在 {3} 次内，超过之后不再继续切
     *
     * @param maxAutoNext
     */
    private void autoPlayNext(int maxAutoNext) {
        if (mAutoPlayNextCount < maxAutoNext) {
            mAutoPlayNextCount++;
            PlayerSourceFacade.newSingleton().getPlayerControl().playNext();
        } else {
            mAutoPlayNextCount = 0;
        }
    }

    @Override
    public void onPlayerProgress(final long progress, final long duration) {
        if (mPlayerInfo != null) {
            mPlayerInfo.setProgress(progress);
            mPlayerInfo.setDuration(duration);
        }
        if (mPlayerInfoList.isEmpty()) return;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                for (IPlayerInfo info : mPlayerInfoList) {
                    info.onPlayerProgress(progress, duration);
                }
            }
        });

    }

    @Override
    public void onProgramSubscribeChanged(boolean subscribe) {
        PrintInfo.print(TAG, "onProgramSubscribeChanged", String.format("subscribe = %1$s", subscribe), true);
        mSubscribe = subscribe;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (mPlayerInfoList.isEmpty()) return;
                for (IPlayerInfo info : mPlayerInfoList) {
                    info.onProgramSubscribeChanged(mSubscribe);
                }
            }
        });

    }

    public void refreshSubscribe() {
        PrintInfo.print(TAG, "refreshSubscribe", null, true);
        int type = mPlayerInfo.getType();
        if (type == PlayerSourceType.RADIO_YQ) {
            PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(XtingUtils.getSubscribeDao().selectBy(mPlayerInfo.getType(),
                    mPlayerInfo.getProgramId()) != null);
        } else {
            if (type == PlayerSourceType.RADIO_XM) {
                type = PlayerSourceType.HIMALAYAN;
            }
            PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(XtingUtils.getSubscribeDao().selectBy(type, mPlayerInfo.getAlbumId()) != null);
        }
    }

    public PlayerInfo getPlayerInfo() {
        return mPlayerInfo;
    }

    @PlayerStatus
    public int getPlayStatus() {
        return mStatus;
    }

    public boolean isThisPlayerInfoPlaying(PlayerInfo playerInfo, boolean historyF) {
        return (mStatus == PlayerStatus.PLAYING || mStatus == PlayerStatus.LOADING)
                && mPlayerInfo != null
                && mPlayerInfo.equals(playerInfo)
                && (!historyF || mPlayerInfo.getProgramId() == playerInfo.getProgramId());
    }

    public void registerPlayerInfoListener(IPlayerInfo listener) {
        if (listener == null) return;
        if (!mPlayerInfoList.isEmpty()) {
            Iterator<IPlayerInfo> iterator = mPlayerInfoList.listIterator();
            while (iterator.hasNext()) {
                IPlayerInfo playerListener = iterator.next();
                if (playerListener.getClass().equals(listener.getClass())) {
                    iterator.remove();
                    break;
                }
            }
        }
        syncInPlayerInfo(listener);
        mPlayerInfoList.add(listener);
    }

    public void unRegisterPlayerInfoListener(IPlayerInfo listener) {
        mPlayerInfoList.remove(listener);
    }

    public void clear() {
        mPlayerInfoList.clear();
    }

    public void reset() {
        mPlayerInfo = null;
        resetStatus();
    }

    public void resetStatus() {
        mStatus = PlayerStatus.DEFAULT;
        mAssistantStatus = PlayerStatus.DEFAULT;
    }

    private void syncInPlayerInfo(IPlayerInfo listener) {
        if (mPlayerInfo != null) {
            listener.onPlayerInfoChanged(mPlayerInfo);
            listener.onPlayerStatusChanged(mStatus);
            listener.onPlayerProgress(mPlayerInfo.getProgress(), mPlayerInfo.getDuration());

            long uniqueId;
            int sourceType = mPlayerInfo.getSourceType();
            if (sourceType == PlayerSourceType.RADIO_YQ) {
                uniqueId = mPlayerInfo.getProgramId();
            } else {
                uniqueId = mPlayerInfo.getAlbumId();
                if (sourceType == PlayerSourceType.RADIO_XM) {
                    sourceType = PlayerSourceType.HIMALAYAN;
                }
            }
            SubscribeInfo bean = XtingUtils.getSubscribeDao().selectBy(sourceType, uniqueId);
            mSubscribe = (bean != null);
            listener.onProgramSubscribeChanged(mSubscribe);
        }
    }

    private void updateTrackAtStatus(int status) {
        if (mPlayerInfo == null) return;
        if (status > PlayerStatus.LOADING) {
            if (status == PlayerStatus.PLAYING) {
                mPlayStartFrom = System.currentTimeMillis();
            } else {
                if (mPlayStartFrom == 0) {
                    return;
                }
                long elapseInMS = System.currentTimeMillis() - mPlayStartFrom;
                if (elapseInMS > 2000) {
                    int type = PlayerSourceFacade.newSingleton().getSourceType();
                    StringBuilder updateType = new StringBuilder();
                    switch (type) {
                        case PlayerSourceType.HIMALAYAN:
                        case PlayerSourceType.RADIO_XM:
                            updateType.append("xmly")
                                    .append("-");
                            int sourceSubType = mPlayerInfo.getSourceSubType();
                            if (sourceSubType == PlayerSourceSubType.RADIO) {
                                updateType.append("radio");
                            } else if (sourceSubType == PlayerSourceSubType.SCHEDULE) {
                                updateType.append("schedule");
                            } else if (sourceSubType == PlayerSourceSubType.TRACK) {
                                updateType.append("track");
                            } else {
                                Log.d(TAG, "{updateTrackAtStatus}-[error sub type] : " + sourceSubType);
                            }
                            break;
                        case PlayerSourceType.KOALA:
                            updateType.append("koala")
                                    .append("-");
                            int subType = mPlayerInfo.getSourceSubType();
                            if (subType == PlayerSourceSubType.KOALA_PGC_RADIO) {
                                updateType.append("pgc");
                            } else if (subType == PlayerSourceSubType.KOALA_ALBUM) {
                                updateType.append("album");
                            } else {
                                Log.d(TAG, "{updateTrackAtStatus}-[error sub type] : " + subType);
                            }
                            break;
                    }
                    XmAutoTracker.getInstance().onEventPlayTime(elapseInMS, updateType.toString());
                    XmTracker.getInstance().uploadEvent(elapseInMS, TrackerCountType.FMTIME.getType());
                    mPlayStartFrom = 0;
                }
            }
        }
    }

    private void insertRecordInfo(PlayerInfo playerInfo) {
        PrintInfo.print(TAG, "insertRecordInfo", String.format("playerInfo = %1$s", playerInfo));
        if (playerInfo.getType() == PlayerSourceType.RADIO_YQ) return;
        if (playerInfo.isPreShowF()) return;

        RecordInfo recordInfo = XtingUtils.getRecordDao().selectBy(playerInfo.getAlbumId());
        if (recordInfo != null) {
            int type = recordInfo.getType();
            if (type == PlayerSourceType.RADIO_XM) {
                PrintInfo.print(TAG, "Delete Record 1");
                XtingUtils.getRecordDao().delete(recordInfo);
            } else if (type == PlayerSourceType.HIMALAYAN) {
                if (playerInfo.getSourceSubType() != PlayerSourceSubType.TRACK) { //如果是播放网络电台，已经记录就不再记录
                    return;
                }
            }

        }
        PrintInfo.print(TAG, "DB_Record", String.format("RecordName = %1$s", playerInfo.getProgramName()));
        mRecordInfo = BeanConverter.toRecordInfo(playerInfo);
        XtingUtils.getRecordDao().insert(mRecordInfo);
        if (XtingUtils.getRecordDao().count() > XtingConstants.MAX_COUNT_100) {
            List<RecordInfo> recordInfos = XtingUtils.getRecordDao().selectAll();
            PrintInfo.print(TAG, "Delete Record 2");
            XtingUtils.getRecordDao().delete(recordInfos.get(recordInfos.size() - 1));
        }
    }

    private void updateRecordInfo(long progress, long duration) {
        PrintInfo.print(TAG, "updateRecordInfo", String.format("mRecordInfo = %1$s", mRecordInfo));
        if (mRecordInfo != null) {
            mRecordInfo.setProgress(progress);
            if (duration > 0) {
                mRecordInfo.setDuration(duration);
            }
            //可能暂停的之前 数据库被删 update就会出现问题
            XtingUtils.getRecordDao().insert(mRecordInfo);
        }
    }

    interface Holder {
        PlayerInfoImpl sINSTANCE = new PlayerInfoImpl();
    }
}
