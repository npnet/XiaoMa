package com.xiaoma.xting.koala.contract;

import com.xiaoma.xting.common.playerSource.contract.RadioPlayQuality;
import com.xiaoma.xting.koala.bean.XMPlayItem;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/10
 */
public interface IKoalaPlayerControl {

    void stop();

    void pause();

    void play();

    void reset();

    void playPre();

    void playNext();

    void seek(int position);

    void setPlayMode(int mode);

    void switchPlayerStatus();

    void clearPlayerList();

    void play(XMPlayItem playItem);

    void playStart();

    void playAlbum(long id);

    void playPgc(long id);

    void playAudioFromPlayList(long audioId);

    void playWithIndex(int index);

    void addPlayList(List<XMPlayItem> list);

    void addPlayListHead(List<XMPlayItem> list);

    void destroy();

    void setSoundQuality(@RadioPlayQuality int soundQuality);

}
