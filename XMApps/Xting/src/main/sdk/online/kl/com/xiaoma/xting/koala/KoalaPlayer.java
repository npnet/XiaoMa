package com.xiaoma.xting.koala;

import android.content.Context;
import android.util.Log;

import com.kaolafm.opensdk.ResType;
import com.kaolafm.sdk.core.mediaplayer.IPlayerStateListener;
import com.kaolafm.sdk.core.mediaplayer.PlayItem;
import com.kaolafm.sdk.core.mediaplayer.PlayerListManager;
import com.kaolafm.sdk.core.mediaplayer.PlayerManager;
import com.kaolafm.sdk.core.mediaplayer.PlayerRadioListManager;
import com.kaolafm.sdk.core.model.PlayerRadioListItem;
import com.xiaoma.component.AppHolder;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.koala.bean.XMAudioDetails;
import com.xiaoma.xting.koala.bean.XMListPageAudioDetails;
import com.xiaoma.xting.koala.bean.XMPlayItem;
import com.xiaoma.xting.koala.contract.IKoalaPlayerControl;
import com.xiaoma.xting.koala.contract.IKoalaPlayerInfo;
import com.xiaoma.xting.koala.contract.impl.KoalaPlayerCallbackImpl;
import com.xiaoma.xting.koala.utils.KoalaPlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/3
 */
public class KoalaPlayer implements IKoalaPlayerInfo, IKoalaPlayerControl {

    private static final String TAG = KoalaPlayer.class.getSimpleName();
    private boolean mPlayFromRecordT = true; //目前需求是默认从播放记录开始播放
    private int mPlayType;
    private PlayerManager mPlayer;
    private PlayerListManager mListManager;
    private IPlayerStateListener mPlayerStateListener = new IPlayerStateListener() {
        @Override
        public void onIdle(PlayItem playItem) {
            KoalaPlayerCallbackImpl.newSingleton().onIdle(new XMPlayItem(playItem));
        }

        @Override
        public void onPlayerPreparing(PlayItem playItem) {
            KoalaPlayerCallbackImpl.newSingleton().onPlayerPreparing(new XMPlayItem(playItem));
        }

        @Override
        public void onPlayerPlaying(PlayItem playItem) {
            if (AudioFocusManager.getInstance().getLossByOther()) {
                mPlayer.pause();
                return;
            }
            KoalaPlayerCallbackImpl.newSingleton().onPlayerPlaying(new XMPlayItem(playItem));
        }

        @Override
        public void onPlayerPaused(PlayItem playItem) {
            KoalaPlayerCallbackImpl.newSingleton().onPlayerPaused(new XMPlayItem(playItem));
        }

        @Override
        public void onProgress(String s, int i, int i1, boolean b) {
            KoalaPlayerCallbackImpl.newSingleton().onProgress(s, i, i1, b);
        }

        @Override
        public void onPlayerFailed(PlayItem playItem, int i, int i1) {
            KoalaPlayerCallbackImpl.newSingleton().onPlayerFailed(new XMPlayItem(playItem), i, i1);
        }

        @Override
        public void onPlayerEnd(PlayItem playItem) {
            KoalaPlayerCallbackImpl.newSingleton().onPlayerEnd(new XMPlayItem(playItem));
        }

        @Override
        public void onSeekStart(String s) {
            KoalaPlayerCallbackImpl.newSingleton().onSeekStart(s);
        }

        @Override
        public void onSeekComplete(String s) {
            KoalaPlayerCallbackImpl.newSingleton().onSeekComplete(s);
        }

        @Override
        public void onBufferingStart(PlayItem playItem) {
            KoalaPlayerCallbackImpl.newSingleton().onBufferingStart(new XMPlayItem(playItem));
        }

        @Override
        public void onBufferingEnd(PlayItem playItem) {
            KoalaPlayerCallbackImpl.newSingleton().onBufferingEnd(new XMPlayItem(playItem));
        }
    };

    private KoalaPlayer() {
        mListManager = PlayerListManager.getInstance();
        mPlayer = PlayerManager.getInstance(AppHolder.getInstance().getAppContext());
    }

    public static KoalaPlayer newSingleton() {
        return Holder.sINSTANCE;
    }

    @Override
    public void init(Context context) {
        logInfo("init");
        if (context == null) {
            throw new IllegalArgumentException("context should not be null!");
        }
        addPlayerStateListener();
    }

    @Override
    public void stop() {
        logInfo("stop");
        mPlayer.stop();
    }

    @Override
    public void pause() {
        logInfo("pause");
        mPlayer.pause();
    }

    @Override
    public void play() {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("play");
        mPlayer.play();
    }

    @Override
    public void reset() {
        logInfo("reset");
        mPlayer.reset();
    }

    @Override
    public void playPre() {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playPre");
        mPlayer.playPre();
    }

    @Override
    public void playNext() {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playNext");
        mPlayer.playNext();
    }

    @Override
    public int getCurPosition() {
        logInfo("getCurPosition");
        return mListManager.getCurPosition();
    }

    @Override
    public List<PlayerInfo> getPlayList() {
        logInfo("getPlayList");
        ArrayList<PlayItem> playList = mListManager.getPlayList();
        ArrayList<PlayerInfo> playerInfoList = new ArrayList<>(playList.size());
        for (PlayItem playItem : playList) {
            playerInfoList.add(KoalaPlayerUtils.toPlayerInfo(playItem));
        }
        return playerInfoList;
    }

    @Override
    public int getPlayListSize() {
        logInfo("getPlayListSize");
        return mListManager.getPlayListSize();
    }

    @Override
    public void addPlayerStateListener() {
        logInfo("addPlayerStateListener");
        mPlayer.addPlayerStateListener(mPlayerStateListener);
    }

    @Override
    public void removePlayerStateListener() {
        logInfo("removePlayerStateListener");
        mPlayer.removePlayerStateListener(mPlayerStateListener);
    }

    @Override
    public boolean isPGCRadio() {
        return mPlayType == ResType.TYPE_RADIO;
    }

    @Override
    public boolean isPlaying() {
        logInfo("isPlaying");
        return mPlayer.isPlaying();
    }

    @Override
    public boolean hasPre() {
        logInfo("hasPre");
        return mPlayer.hasPre();
    }

    @Override
    public boolean hasNext() {
        logInfo("hasNext");
        return mPlayer.hasNext();
    }

    @Override
    public void seek(int position) {
        logInfo("seek");
        mPlayer.seek(position);
    }

    @Override
    public void setPlayMode(int mode) {
        logInfo("setPlayMode");
        mListManager.setPlayMode(mode);
    }

    @Override
    public boolean isPaused() {
        logInfo("isPaused");
        return mPlayer.isPaused();
    }

    @Override
    public void switchPlayerStatus() {
        logInfo("switchPlayerStatus");
        mPlayer.switchPlayerStatus();
    }

    @Override
    public void clearPlayerList() {
        logInfo("clearPlayerList");
        mPlayer.clearPlayerList();
        mPlayType = 0;
    }

    @Override
    public void enablePlayer() {
        logInfo("enablePlayer");
        if (isPlayerEnable()) {
            mPlayer.enablePlayer();
        }
    }

    @Override
    public void disablePlayer() {
        logInfo("disablePlayer");
        mPlayer.disablePlayer();
    }

    @Override
    public boolean isCurPlayerInfoAlive() {
        logInfo("isCurPlayerInfoAlive");
        return mListManager.getCurPlayItem() != null;
    }

    @Override
    public boolean isPlayerEnable() {
        logInfo("isPlayerEnable");
        return mPlayer.isPlayerEnable();
    }

    @Override
    public void destroy() {
        logInfo("destroy");
        mPlayer.destroy();
    }

    @Override
    public void setSoundQuality(int soundQuality) {
        mPlayer.setSoundQuality(KoalaPlayerUtils.convertSoundQuality(soundQuality));
    }

    @Override
    public boolean isPlayerFailed() {
        logInfo("isPlayerFailed");
        return mPlayer.isPlayerFailed();
    }

    @Override
    public void play(XMPlayItem playItem) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("play");
        mPlayer.play(playItem.getSDKBean());
    }

    @Override
    public void playStart() {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playStart");
        mPlayer.playStart();
    }

    @Override
    public boolean canReStartPlayer() {
        logInfo("canReStartPlayer");
        return mPlayer.canReStartPlayer();
    }

    @Override
    public PlayerInfo getCurPlayerInfo() {
        logInfo("getCurPlayerInfo");
        return KoalaPlayerUtils.toPlayerInfo(mListManager.getCurPlayItem());
    }

    @Override
    public int getPlayIndex() {
        logInfo("getPlayIndex");
        return mListManager.getCurPosition();
    }

    @Override
    public void playAlbum(long id) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playAlbum");
        mPlayer.playAlbum(id);
    }

    public void playAudio(long id) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playAudio");
        mPlayer.playAudio(id);
    }

    @Override
    public void playPgc(long id) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playPgc");
        mPlayer.playPgc(id);
    }

    @Override
    public void playAudioFromPlayList(long audioId) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playAudioFromPlayList");
        PlayItem playItem = mListManager.getPlayItemByAudioId(audioId);
        if (playItem == null) {
            playWithIndex(0);
        } else {
            if (mPlayFromRecordT) {
                //查看本地播放历史进行播放
                RecordInfo recordInfo = XtingUtils.getRecordDao().selectBy(PlayerSourceType.KOALA, audioId);
                if (recordInfo != null) {
                    seek((int) recordInfo.getProgress());
                    playItem.setPosition((int) recordInfo.getProgress());
                }
            }
            try {
                mPlayer.play(playItem);
            } catch (Exception e) {
                e.printStackTrace();
                logInfo(String.format("playAudioFromPlayList [ERROR] "));
            }
        }
    }

    @Override
    public void playWithIndex(int index) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("playWithIndex");
        PlayerInfo playerInfo = getPlayList().get(index);
        playAudioFromPlayList(playerInfo.getProgramId());
    }

    @Override
    public void addPlayList(List<XMPlayItem> list) {
        logInfo("addPlayList");
        if (list != null && !list.isEmpty()) {
            List<PlayItem> playItemList = new ArrayList<>(list.size());
            for (XMPlayItem xmPlayItem : list) {
                playItemList.add(xmPlayItem.getSDKBean());
            }

            mListManager.addPlaylist(playItemList);
        }
    }

    @Override
    public void addPlayListHead(List<XMPlayItem> list) {
        if (list != null && !list.isEmpty()) {
            List<PlayItem> playItemList = new ArrayList<>(list.size());
            for (XMPlayItem xmPlayItem : list) {
                playItemList.add(xmPlayItem.getSDKBean());
            }
            mListManager.addPlaylistToHeader(playItemList);
        }
    }

    //[warn] 这个必须要添加,不然考拉就会出现播放之后就暂停的问题
    public void setPlayType(int playSubType) {
        if (playSubType == PlayerSourceSubType.KOALA_PGC_RADIO) {
            mPlayType = ResType.TYPE_RADIO;
            PlayerRadioListItem playerRadioListItem = new PlayerRadioListItem();
            playerRadioListItem.setRadioType(String.valueOf(ResType.TYPE_RADIO));
            playerRadioListItem.setPlayRadioType(ResType.TYPE_RADIO);
            PlayerRadioListManager.getInstance().addRadio(playerRadioListItem);

        }
    }

    public void setPlayItemById(long programId) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("setPlayItemById");
        PlayItem playItem = mListManager.getPlayItemByAudioId(programId);
        if (playItem == null) {
            mListManager.setCurPlayItemIndex(0);
            if (mPlayFromRecordT) {
                //增加播放历史记录
                PlayItem selectPlayItem = mListManager.getPlayItem(0);
                if (selectPlayItem != null) {
                    seekFromRecord(selectPlayItem.getAudioId());
                }
            }
        } else {
            mListManager.setCurPlayItemIndex(playItem);
            if (mPlayFromRecordT) {
                //增加播放历史记录
                seekFromRecord(programId);
            }
        }
    }

    public void addToPlayer(XMListPageAudioDetails data, boolean addAfter) {
        if (!AudioFocusManager.getInstance().requestKoalaFocus()) return;
        logInfo("addToPlayer");
        List<XMAudioDetails> dataList = data.getDataList();
        if (dataList != null && !dataList.isEmpty()) {
            mPlayType = ResType.TYPE_ALBUM;
            PlayerRadioListItem playerRadioListItem = new PlayerRadioListItem();
            playerRadioListItem.setRadioType(String.valueOf(ResType.TYPE_ALBUM));
            playerRadioListItem.setPlayRadioType(ResType.TYPE_ALBUM);
            playerRadioListItem.setHaveNext(data.getHaveNext());
            playerRadioListItem.setHavePre(data.getHavePre());
            playerRadioListItem.setNextPage(data.getNextPage());
            PlayerRadioListManager.getInstance().addRadio(playerRadioListItem);

            addXMAudioDetails(dataList, addAfter);
        }
    }

    public void addXMAudioDetails(List<XMAudioDetails> audioDetailList, boolean addAfter) {
        logInfo("addXMAudioDetails");
        List<PlayItem> list = new ArrayList<>(audioDetailList.size());
        for (XMAudioDetails xmAudioDetails : audioDetailList) {
            list.add(KoalaPlayerUtils.toPlayItem(xmAudioDetails.getSDKBean()));
        }
        if (addAfter) {
            mListManager.addPlaylist(list);
        } else {
            mListManager.addPlaylistToHeader(list);
        }
    }

    public void setVoice(float leftVolume, float rightVolume) {
        mPlayer.setVolume(leftVolume, rightVolume);
    }

    public void setPlayFromRecord(boolean playFromRecordF) {
        mPlayFromRecordT = playFromRecordF;
    }

    private void seekFromRecord(long programId) {
        RecordInfo recordInfo = XtingUtils.getRecordDao().selectBy(PlayerSourceType.KOALA, programId);
        if (recordInfo != null) {
            seek((int) recordInfo.getProgress());
        }
    }

    interface Holder {
        KoalaPlayer sINSTANCE = new KoalaPlayer();
    }

    private void logInfo(String methodName) {
        Log.d(TAG, "{" + methodName + "}");
    }
}
