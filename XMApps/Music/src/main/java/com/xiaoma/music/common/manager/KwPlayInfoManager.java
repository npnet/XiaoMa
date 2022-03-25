package com.xiaoma.music.common.manager;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by ZYao.
 * Date ：2018/12/27 0027
 * 我们自己维护正在播放的专辑id和专辑类型
 */
public class KwPlayInfoManager {

    private String mCurrentAlbumId;

    @AlbumType
    private int mCurrentType;

    private CopyOnWriteArraySet<AlbumTypeChangedListener> listeners = new CopyOnWriteArraySet<>();

    public static KwPlayInfoManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final KwPlayInfoManager instance = new KwPlayInfoManager();
    }

    public void addAlbumTypeChangedListener(AlbumTypeChangedListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public int getCurrentType() {
        return mCurrentType;
    }


    public void removeAlbumTypeChangedListener(AlbumTypeChangedListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public void setCurrentCollectionAlbumId(String albumId) {
        if (mCurrentType == AlbumType.COLLECTION) {
            this.mCurrentAlbumId = albumId;
            for (AlbumTypeChangedListener listener : listeners) {
                listener.onChanged(albumId, AlbumType.COLLECTION);
            }
        }
    }

    public void setCurrentHistoryAlbumId(String albumId) {
        if (mCurrentType == AlbumType.HISTORY) {
            this.mCurrentAlbumId = albumId;
            for (AlbumTypeChangedListener listener : listeners) {
                listener.onChanged(albumId, AlbumType.HISTORY);
            }
        }
    }

    public void setCurrentPlayInfo(String currentAlbumId, @AlbumType int currentType) {
        this.mCurrentAlbumId = currentAlbumId;
        this.mCurrentType = currentType;
        for (AlbumTypeChangedListener listener : listeners) {
            listener.onChanged(currentAlbumId, currentType);
        }
    }

    public void clearCurrentPlayInfo(){
        this.mCurrentAlbumId = "";
        this.mCurrentType = AlbumType.NONE;
        for (AlbumTypeChangedListener listener : listeners) {
            listener.onChanged(mCurrentAlbumId, mCurrentType);
        }
    }

    public boolean isCurrentPlayInfo(String albumId, @AlbumType int type) {
        if (AudioSourceManager.getInstance().getCurrAudioSource() != AudioSource.ONLINE_MUSIC) {
            return false;
        }
        return !TextUtils.isEmpty(albumId) && albumId.equals(mCurrentAlbumId) && type == mCurrentType;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AlbumType.NONE, AlbumType.ASSISTANT, AlbumType.ALBUM, AlbumType.RADIO, AlbumType.SONG_LIST,
            AlbumType.BILLBOARD, AlbumType.COLLECTION, AlbumType.HISTORY})
    public @interface AlbumType {
        int NONE = 0;
        int ALBUM = 1;
        int RADIO = 2;
        int SONG_LIST = 3;
        int BILLBOARD = 4;
        int COLLECTION = 5;
        int HISTORY = 6;
        int ASSISTANT = 7;
    }

    public interface AlbumTypeChangedListener {
        void onChanged(String currentAlbumId, @AlbumType int currentType);
    }
}
