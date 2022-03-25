package com.xiaoma.music.common.manager;

import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.login.LoginManager;
import com.xiaoma.music.kuwo.model.SaveMusicData;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by ZYao.
 * Date ：2018/11/14 0014
 */
public class MusicDbManager {

    private static final String SAVE_MUSIC_TYPE = "saveMusicType";
    private static final String RID = "rid";
    public static final int DELETE_SUCCESS = 1;
    public static final int MAX_HISTORY_MUSIC = 100;
    public static final String USB_ARTIST = "artist";
    public static final String USB_ALBUM = "album";
    public static final String USB_NAME = "name";
    public static final String USB_REAL_NAME = "realName";

    public static MusicDbManager getInstance() {
        return InstanceHolder.instance;
    }


    private static class InstanceHolder {
        static final MusicDbManager instance = new MusicDbManager();
    }


    private static IDatabase getDBManager() {
        if (LoginManager.getInstance().isUserLogin()) {
            return DBManager.getInstance().getUserDBManager(LoginManager.getInstance().getLoginUserId());
        } else {
            return DBManager.getInstance().getDBManager();
        }
    }

    private static IDatabase getDBManager(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            return DBManager.getInstance().getUserDBManager(userId);
        } else {
            return DBManager.getInstance().getDBManager();
        }
    }

    public synchronized void saveHistoryMusic(XMMusic music) {
        if (XMMusic.isEmpty(music)) {
            return;
        }
        SaveMusicData musicData = new SaveMusicData();
        musicData.setMusic(music.getSDKBean());
        musicData.setRid(music.getSDKBean().rid);
        musicData.setSaveMusicType(SaveMusicData.SaveMusicType.HISTORY);
        getDBManager().save(musicData);
        for (OnHistoryChangedListener historyChangedListener : historyChangedListeners) {
            if (historyChangedListener != null) {
                historyChangedListener.onAddHistory();
            }
        }
        List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class,
                SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.HISTORY);
        if (xmMusicList != null && xmMusicList.size() > MAX_HISTORY_MUSIC) {
            Collections.reverse(xmMusicList);
            List<SaveMusicData> temp = new ArrayList<>();
            for (int i = 0; i < xmMusicList.size(); i++) {
                if (i >= MAX_HISTORY_MUSIC) {
                    SaveMusicData saveMusicData = xmMusicList.get(i);
                    if (saveMusicData != null) {
                        temp.add(saveMusicData);
                    }
                }
            }
            getDBManager().delete(temp);
        }
    }

    public XMMusic queryHistoryMusicById(long id) {
        XMMusic music = null;
        try {
            String keys[] = {SAVE_MUSIC_TYPE, RID};
            String values[][] = {{SaveMusicData.SaveMusicType.HISTORY}, {String.valueOf(id)}};
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class, keys, values);
            if (!ListUtils.isEmpty(xmMusicList)) {
                SaveMusicData musicData = xmMusicList.get(0);
                if (musicData != null && musicData.getMusic() != null) {
                    music = new XMMusic(musicData.getMusic());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return music;
    }

    public List<XMMusic> queryHistoryMusic() {
        List<XMMusic> musicList = null;
        try {
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class,
                    SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.HISTORY);
            musicList = new ArrayList<>();
            if (!ListUtils.isEmpty(xmMusicList)) {
                for (SaveMusicData musicData : xmMusicList) {
                    if (musicData != null && musicData.getMusic() != null) {
                        XMMusic music = new XMMusic(musicData.getMusic());
                        musicList.add(music);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicList;
    }

    public void deleteHistoryMusic(XMMusic music, boolean showToUser) {
        try {
            long rid = music.getRid();
            String keys[] = {SAVE_MUSIC_TYPE, RID};
            String values[][] = {{SaveMusicData.SaveMusicType.HISTORY}, {String.valueOf(rid)}};
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class, keys, values);
            if (!ListUtils.isEmpty(xmMusicList)) {
                final long delete = getDBManager().delete(xmMusicList);
                List<SaveMusicData> musicList = getDBManager().queryByWhere(SaveMusicData.class,
                        SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.HISTORY);
                if (showToUser && musicList.size() == 0 && delete == DELETE_SUCCESS) {
                    for (OnHistoryChangedListener historyChangedListener : historyChangedListeners) {
                        if (historyChangedListener != null) {
                            historyChangedListener.onClearAllHistorySuccess();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAllHistoryMusic() {
        try {
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class,
                    SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.HISTORY);
            if (!ListUtils.isEmpty(xmMusicList)) {
                getDBManager().delete(xmMusicList);
                for (OnHistoryChangedListener historyChangedListener : historyChangedListeners) {
                    if (historyChangedListener != null) {
                        historyChangedListener.onClearAllHistorySuccess();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAllHistoryMusic(String userId) {
        try {
            List<SaveMusicData> xmMusicList = getDBManager(userId).queryByWhere(SaveMusicData.class,
                    SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.HISTORY);
            if (!ListUtils.isEmpty(xmMusicList)) {
                getDBManager(userId).delete(xmMusicList);
                for (OnHistoryChangedListener historyChangedListener : historyChangedListeners) {
                    if (historyChangedListener != null) {
                        historyChangedListener.onClearAllHistorySuccess();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCollectionMusic(XMMusic music) {
        try {
            if (XMMusic.isEmpty(music)) {
                return;
            }
            SaveMusicData musicData = new SaveMusicData();
            musicData.setMusic(music.getSDKBean());
            musicData.setRid(music.getSDKBean().rid);
            musicData.setSaveMusicType(SaveMusicData.SaveMusicType.COLLECTION);
            getDBManager().save(musicData);
            Set<OnCollectionStatusChangeListener> listeners = new ArraySet<>(collectionStatusChangeListeners);
            for (OnCollectionStatusChangeListener listener : listeners) {
                listener.onAddCollection();
            }
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class,
                    SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.COLLECTION);
            if (xmMusicList != null && xmMusicList.size() > MAX_HISTORY_MUSIC) {
                Collections.reverse(xmMusicList);
                List<SaveMusicData> temp = new ArrayList<>();
                for (int i = 0; i < xmMusicList.size(); i++) {
                    if (i >= MAX_HISTORY_MUSIC) {
                        SaveMusicData saveMusicData = xmMusicList.get(i);
                        if (saveMusicData != null) {
                            temp.add(saveMusicData);
                        }
                    }
                }
                getDBManager().delete(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<XMMusic> queryCollectionMusic() {
        List<XMMusic> musicList = new ArrayList<>();
        try {
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class,
                    SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.COLLECTION);
            if (!ListUtils.isEmpty(xmMusicList)) {
                for (SaveMusicData musicData : xmMusicList) {
                    if (musicData != null && musicData.getMusic() != null) {
                        XMMusic music = new XMMusic(musicData.getMusic());
                        musicList.add(music);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicList;
    }

    public XMMusic queryCollectionMusicById(long id) {
        XMMusic music = null;
        try {
            String keys[] = {SAVE_MUSIC_TYPE, RID};
            String values[][] = {{SaveMusicData.SaveMusicType.COLLECTION}, {String.valueOf(id)}};
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class, keys, values);
            if (!ListUtils.isEmpty(xmMusicList)) {
                SaveMusicData musicData = xmMusicList.get(0);
                if (musicData != null && musicData.getMusic() != null) {
                    music = new XMMusic(musicData.getMusic());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return music;
    }

    public void deleteCollectionMusic(XMMusic music) {
        try {
            long rid = music.getRid();
            String keys[] = {SAVE_MUSIC_TYPE, RID};
            String values[][] = {{SaveMusicData.SaveMusicType.COLLECTION}, {String.valueOf(rid)}};
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class, keys, values);
            if (!ListUtils.isEmpty(xmMusicList)) {
                getDBManager().delete(xmMusicList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<OnCollectionStatusChangeListener> listeners = new ArraySet<>(collectionStatusChangeListeners);
        for (OnCollectionStatusChangeListener listener : listeners) {
            listener.onRemoveCollection();
        }
    }

    public void saveUsbMusicList(List<UsbMusic> usbMusicList) {
        getDBManager().saveAll(usbMusicList);
    }

    public void removeUsbMusicList() {
        getDBManager().delete(UsbMusic.class);
    }

    public List<UsbMusic> queryAllUsbMusic() {
        return getDBManager().queryAll(UsbMusic.class);
    }

    public List<UsbMusic> queryUsbMusicByArtist(String artist) {
        String keys[] = {USB_ARTIST};
        String values[][] = {{artist}};
        return getDBManager().queryByWhere(UsbMusic.class, keys, values);
    }

    public List<UsbMusic> queryUsbMusicByAlbum(String album) {
        String keys[] = {USB_ALBUM};
        String values[][] = {{album}};
        return getDBManager().queryByWhere(UsbMusic.class, keys, values);
    }

    public List<UsbMusic> queryUsbMusicByAlbum(String artist, String album) {
        String keys[] = {USB_ARTIST, USB_ALBUM};
        String values[][] = {{artist}, {album}};
        return getDBManager().queryByWhere(UsbMusic.class, keys, values);
    }

    public List<UsbMusic> queryUsbMusicByArtistAndName(String artist, String name) {
        String keysFirst[] = {USB_ARTIST, USB_NAME};
        String keysSecond[] = {USB_ARTIST, USB_REAL_NAME};
        String values[][] = {{artist}, {name}};
        List<UsbMusic> usbMusicList = new ArrayList<>();
        List<UsbMusic> firstUsbMusics = getDBManager().queryByWhere(UsbMusic.class, keysFirst, values);
        if (firstUsbMusics != null) {
            usbMusicList.addAll(firstUsbMusics);
        }
        List<UsbMusic> secondUsbMusics = getDBManager().queryByWhere(UsbMusic.class, keysSecond, values);
        if (secondUsbMusics != null) {
            for (UsbMusic secondUsbMusic : secondUsbMusics) {
                if (usbMusicList.contains(secondUsbMusic)) {
                    continue;
                }
                usbMusicList.add(secondUsbMusic);
            }
        }
        return usbMusicList;
    }

    public List<UsbMusic> queryUsbMusicByName(String name) {
        String keysFirst[] = {USB_NAME};
        String keysSecond[] = {USB_REAL_NAME};
        String values[][] = {{name}};
        List<UsbMusic> usbMusicList = new ArrayList<>();
        List<UsbMusic> firstUsbMusics = getDBManager().queryByWhere(UsbMusic.class, keysFirst, values);
        if (firstUsbMusics != null) {
            usbMusicList.addAll(firstUsbMusics);
        }
        List<UsbMusic> secondUsbMusics = getDBManager().queryByWhere(UsbMusic.class, keysSecond, values);
        if (secondUsbMusics != null) {
            for (UsbMusic secondUsbMusic : secondUsbMusics) {
                if (usbMusicList.contains(secondUsbMusic)) {
                    continue;
                }
                usbMusicList.add(secondUsbMusic);
            }
        }
        return usbMusicList;
    }


    private List<OnHistoryChangedListener> historyChangedListeners = new ArrayList<>();

    public void addHistoryChangedListener(OnHistoryChangedListener listener) {
        if (listener != null) {
            historyChangedListeners.add(listener);
        }
    }

    public void removeHistoryChangedListener(OnHistoryChangedListener listener) {
        if (listener != null) {
            historyChangedListeners.remove(listener);
        }
    }

    private Set<OnCollectionStatusChangeListener> collectionStatusChangeListeners = new ArraySet<>();

    public void addCollectionStatusChangeListener(OnCollectionStatusChangeListener listener) {
        if (listener != null) {
            collectionStatusChangeListeners.add(listener);
        }
    }

    public void removeCollectionStatusChangeListener(OnCollectionStatusChangeListener listener) {
        if (listener != null) {
            collectionStatusChangeListeners.remove(listener);
        }
    }

    public void clearCollectionMusic() {
        try {
            List<SaveMusicData> xmMusicList = getDBManager().queryByWhere(SaveMusicData.class,
                    SAVE_MUSIC_TYPE, SaveMusicData.SaveMusicType.COLLECTION);
            if (!ListUtils.isEmpty(xmMusicList)) {
                getDBManager().delete(xmMusicList);

                Set<OnCollectionStatusChangeListener> listeners = new ArraySet<>(collectionStatusChangeListeners);
                for (OnCollectionStatusChangeListener listener : listeners) {
                    listener.onRemoveCollection();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnHistoryChangedListener {
        void onClearAllHistorySuccess();

        void onAddHistory();
    }

    public interface OnCollectionStatusChangeListener {
        void onAddCollection();

        void onRemoveCollection();
    }
}
