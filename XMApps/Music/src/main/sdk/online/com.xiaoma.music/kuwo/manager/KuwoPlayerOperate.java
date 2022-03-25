package com.xiaoma.music.kuwo.manager;

import android.content.Context;

import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.impl.IPlayerOperate;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.OnChargeQualityListener;
import com.xiaoma.music.kuwo.listener.OnMusicChargeListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.music.kuwo.observer.KuwoPlayControlObserver;
import com.xiaoma.music.player.model.MusicQualityModel;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.MusicList;
import cn.kuwo.mod.ModMgr;
import cn.kuwo.mod.PlayMusicHelper;
import cn.kuwo.open.KwApi;
import cn.kuwo.open.OnMusicsChargeListener;
import cn.kuwo.open.base.MusicChargeType;
import cn.kuwo.service.MainService;
import cn.kuwo.service.PlayProxy;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class KuwoPlayerOperate implements IPlayerOperate {
    @Override
    public void init(Context context) {
        ModMgr.getPlayControl().init();
        ModMgr.getListMgr().init();
        ModMgr.getRadioMgr().init();
        MainService.connect();
    }

    @Override
    public int getNowPlayMusicIndex() {
        return ModMgr.getPlayControl().getNowPlayMusicIndex();
    }

    @Override
    public XMMusicList getNowPlayingList() {
        MusicList nowPlayingList = ModMgr.getPlayControl().getNowPlayingList();
        if (nowPlayingList == null) {
            return null;
        }
        return new XMMusicList(nowPlayingList);
    }

    @Override
    public XMMusic getNowPlayingMusic() {
        final Music nowPlayingMusic = ModMgr.getPlayControl().getNowPlayingMusic();
        if (nowPlayingMusic == null) {
            return null;
        }
        return new XMMusic(nowPlayingMusic);
    }

    @Override
    public int getPlayMode() {
        return ModMgr.getPlayControl().getPlayMode();
    }

    @Override
    public void setPlayMode(int var1) {
        ModMgr.getPlayControl().setPlayMode(var1);
    }

    @Override
    public void play(XMMusic music) {
        if (music != null && music.getSDKBean() != null) {
            KuwoPlayControlObserver.getInstance().initStartPlayTime();
            PlayMusicHelper.play(music.getSDKBean());
        }
    }

    @Override
    public void play(List<XMMusic> musics, int position) {
        List<Music> musicList = new ArrayList<>();
        for (XMMusic music : musics) {
            if (music == null) {
                continue;
            }
            musicList.add(music.getSDKBean());
        }
        KuwoPlayControlObserver.getInstance().initStartPlayTime();
        PlayMusicHelper.replaceAndPlay(musicList, position);
    }

    public void playBillBroadFirst() {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                OnlineMusicFactory.getKWAudioFetch().fetchBillBroad(new PlayAfterSuccessFetchListener<List<XMBillboardInfo>>() {
                    @Override
                    public void onFetchSuccess(List<XMBillboardInfo> xmBillboardInfos) {

                        if (xmBillboardInfos != null && !xmBillboardInfos.isEmpty()) {
                            XMBillboardInfo billboardInfo = xmBillboardInfos.get(0);
                            OnlineMusicFactory.getKWAudioFetch().fetchBillboardMusic(billboardInfo, 0, 5, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                                @Override
                                public void onFetchSuccess(List<XMMusic> xmMusics) {
                                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(billboardInfo.getSDKBean().getId()
                                            + billboardInfo.getSDKBean().getName(), KwPlayInfoManager.AlbumType.BILLBOARD);
                                    OnlineMusicFactory.getKWPlayer().play(xmMusics, 0);
                                    AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                                }

                                @Override
                                public void onFetchFailed(String msg) {
                                    KLog.e("playRecommendFirst play failed");
                                }
                            });
                        }
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        KLog.e("playRecommendFirst play failed");
                    }
                });
            }
        });
    }

    @Override
    public void playMusicList(XMMusicList musicList, int pos) {
        if (musicList != null && musicList.getSDKBean() != null) {
            KuwoPlayControlObserver.getInstance().initStartPlayTime();
            PlayMusicHelper.playMusicList(musicList.getSDKBean(), pos);
        }
    }

    @Override
    public void playLocalMusic(String showName, String filePath) {
        KuwoPlayControlObserver.getInstance().initStartPlayTime();
        PlayMusicHelper.playLocalMusic(showName, filePath);
    }

    @Override
    public void playLocalMusic(Map<String, String> local) {
        List<Music> musicList = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = local.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            Music music = new Music();
            music.name = entry.getKey();
            music.filePath = entry.getValue();
            musicList.add(music);
        }
        PlayMusicHelper.replaceAndPlay(musicList, 0);
    }

    @Override
    public void playRadio(int cid, String name) {
        KuwoPlayControlObserver.getInstance().initStartPlayTime();
        PlayMusicHelper.playRadio(cid, name);
    }

    @Override
    public void playRadio(XMMusicList musicList) {
        KuwoPlayControlObserver.getInstance().initStartPlayTime();
        ModMgr.getPlayControl().playRadio(musicList.getSDKBean());
    }

    @Override
    public void pause() {
        ModMgr.getPlayControl().pause();
    }

    @Override
    public void stop() {
        ModMgr.getPlayControl().stop();
    }

    @Override
    public boolean continuePlay() {
        KuwoPlayControlObserver.getInstance().initStartPlayTime();
        return ModMgr.getPlayControl().continuePlay();
    }

    @Override
    public boolean playNext() {
        KuwoPlayControlObserver.getInstance().initStartPlayTime();
        return ModMgr.getPlayControl().playNext();
    }

    @Override
    public boolean playPre() {
        KuwoPlayControlObserver.getInstance().initStartPlayTime();
        return ModMgr.getPlayControl().playPre();
    }

    @Override
    public int getStatus() {
        PlayProxy.Status status = ModMgr.getPlayControl().getStatus();
        return IKuwoConstant.getPlayStatus(status);
    }

    @Override
    public int getDuration() {
        return ModMgr.getPlayControl().getDuration();
    }

    @Override
    public int getCurrentPos() {
        return ModMgr.getPlayControl().getCurrentPos();
    }

    @Override
    public void seek(int var1) {
        ModMgr.getPlayControl().seek(var1);
    }

    @Override
    public int getBufferingPos() {
        return ModMgr.getPlayControl().getBufferingPos();
    }

    @Override
    public int getVolume() {
        return ModMgr.getPlayControl().getVolume();
    }

    @Override
    public void setVolume(int var1) {
        ModMgr.getPlayControl().setVolume(var1);
    }

    @Override
    public void setVolumeScale(float scale) {
        ModMgr.getPlayControl().setPlayerVolumeRate(scale);
    }

    @Override
    public int getMaxVolume() {
        return ModMgr.getPlayControl().getMaxVolume();
    }

    @Override
    public void setMute(boolean var1) {
        ModMgr.getPlayControl().setMute(var1);
    }

    @Override
    public boolean isMute() {
        return ModMgr.getPlayControl().isMute();
    }

    @Override
    public int getPreparePercent() {
        return ModMgr.getPlayControl().getPreparePercent();
    }

    @Override
    public void saveData(boolean var1) {
        ModMgr.getPlayControl().saveData(var1);
    }

    @Override
    public void autoPlayNext() {
        ModMgr.getPlayControl().autoPlayNext();
    }

    @Override
    public int getDownloadWhenPlayQuality() {
        return ModMgr.getSettingMgr().getDownloadWhenPlayQuality();
    }

    @Override
    public boolean setDownloadWhenPlayQuality(int quality) {
        return ModMgr.getSettingMgr().setDownloadWhenPlayQuality(quality);
    }

    @Override
    public int getNowPlayMusicBestQuality() {
        //电台等部分节目在获取时会空，产品确定此类节目音质选择不可用
        try {
            if (getNowPlayingMusic().getSDKBean().getBestResource() != null) {
                return IKuwoConstant.convertIMusicQuality(getNowPlayingMusic().getSDKBean().getBestResource().quality);
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void chargeNowPlayMusic(int quality, OnChargeQualityListener listener) {
        synchronized (this) {
            XMMusic music = getNowPlayingMusic();
            if (music == null) {
                return;
            }
            List<Music> musicList = new ArrayList<>();
            musicList.add(music.getMusic());
            KwApi.chargeMusics(1, quality, musicList, new OnMusicsChargeListener() {

                @Override
                public void onChargeSuccess(List<Music> list, List<MusicChargeType> list1) {
                    if (list1 == null || list1.isEmpty()) {
                        listener.onFailed(null);
                        return;
                    }
                    MusicQualityModel model = new MusicQualityModel();
                    model.setQuality(quality);
                    model.setChargeType(IKuwoConstant.convertChargeType(list1.get(0)));
                    listener.onSuccess(model);
                }

                @Override
                public void onChargeFaild(String s) {
                    listener.onFailed(s);
                }
            });
        }
    }

    /**
     * 歌曲权限信息校验
     *
//     * @param actionType 1: 试听，2:下载
//     * @param quality    0:默认当前播放音质, 1: 流畅音质, 2:高品音质, 3: 超品音质, 4: 无损音质。
     * @param musics     需要校验的歌曲
     * @param listener   结果回调
     * @return
     */
    @Override
    public void chargeMusics(List<XMMusic> musics, OnMusicChargeListener listener) {
        List<Music> musicList = new ArrayList<>();
        for (XMMusic music : musics) {
            if (!XMMusic.isEmpty(music)) {
                musicList.add(music.getSDKBean());
            }
        }
        KwApi.chargeMusics(1, 0, musicList, new OnMusicsChargeListener() {
            @Override
            public void onChargeSuccess(List<Music> list, List<MusicChargeType> list1) {
                List<XMMusic> xmMusics = new ArrayList<>();
                for (Music music : list) {
                    xmMusics.add(new XMMusic(music));
                }
                List<Integer> chargeType = new ArrayList<>();
                for (MusicChargeType musicChargeType : list1) {
                    chargeType.add(IKuwoConstant.convertChargeType(musicChargeType));
                }
                listener.onChargeSuccess(xmMusics, chargeType);
            }

            @Override
            public void onChargeFaild(String s) {
                listener.onChargeFailed(s);
            }
        });
    }
}
