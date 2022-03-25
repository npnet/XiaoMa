package com.xiaoma.music.kuwo.proxy;

import com.xiaoma.music.kuwo.impl.IMusicListControl;
import com.xiaoma.music.kuwo.manager.KuwoMusicListControl;
import com.xiaoma.music.kuwo.model.XMListType;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import cn.kuwo.base.bean.Music;

/**
 * Created by ZYao.
 * Date ：2018/10/22 0022
 */
public class XMKWMusicListControlProxy implements IMusicListControl {

    private IMusicListControl musicListControl;

    public static XMKWMusicListControlProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final XMKWMusicListControlProxy instance = new XMKWMusicListControlProxy();
    }

    public XMKWMusicListControlProxy() {
        musicListControl = new KuwoMusicListControl();
    }

    @Override
    public XMMusicList insertList(XMListType t, String listName) {
        return musicListControl.insertList(t, listName);
    }

    @Override
    public XMMusicList insertListAutoRename(XMListType t, String listName) {
        return musicListControl.insertListAutoRename(t, listName);
    }

    @Override
    public boolean deleteList(XMListType t) {
        return musicListControl.deleteList(t);
    }

    @Override
    public boolean deleteList(String listName) {
        return musicListControl.deleteList(listName);
    }

    @Override
    public XMMusicList getList(String listName) {
        return musicListControl.getList(listName);
    }

    @Override
    public XMMusicList getUniqueList(XMListType t) {
        return musicListControl.getUniqueList(t);
    }

    @Override
    public Collection<XMMusicList> getList(XMListType t) {
        return musicListControl.getList(t);
    }

    @Override
    public Collection<String> getListName(XMListType t) {
        return musicListControl.getListName(t);
    }

    @Override
    public Collection<String> getInsertableMusicListName() {
        return musicListControl.getInsertableMusicListName();
    }

    @Override
    public Collection<XMMusicList> getAllList() {
        return musicListControl.getAllList();
    }

    @Override
    public List<XMMusicList> getShowList() {
        return musicListControl.getShowList();
    }

    @Override
    public int insertMusic(String listName, XMMusic music) {
        return musicListControl.insertMusic(listName, music);
    }

    @Override
    public int insertMusic(String listName, List<XMMusic> music) {
        return musicListControl.insertMusic(listName, music);
    }

    @Override
    public int insertMusic(String listName, XMMusic music, int position) {
        return musicListControl.insertMusic(listName, music, position);
    }

    @Override
    public int insertMusic(String listName, List<XMMusic> music, int position) {
        return musicListControl.insertMusic(listName, music, position);
    }

    @Override
    public boolean deleteMusic(String listName) {
        return musicListControl.deleteMusic(listName);
    }

    @Override
    public boolean deleteMusic(String listName, int position) {
        return musicListControl.deleteMusic(listName, position);
    }

    @Override
    public boolean deleteMusic(String listName, int start, int count) {
        return musicListControl.deleteMusic(listName, start, count);
    }

    @Override
    public boolean deleteMusic(String listName, Collection<Integer> position) {
        return musicListControl.deleteMusic(listName, position);
    }

    @Override
    public boolean deleteMusic(String listName, XMMusic music) {
        return musicListControl.deleteMusic(listName, music);
    }

    @Override
    public boolean deleteMusic(String listName, List<XMMusic> musics) {
        return musicListControl.deleteMusic(listName, musics);
    }

    @Override
    public int deleteMusicEx(String listName, XMMusic music) {
        return musicListControl.deleteMusicEx(listName, music);
    }

    @Override
    public int deleteMusicEx(String listName, List<XMMusic> musics) {
        return musicListControl.deleteMusicEx(listName, musics);
    }

    @Override
    public int indexOf(String listName, XMMusic music) {
        return musicListControl.indexOf(listName, music);
    }

    @Override
    public boolean sortMusic(String listName, Comparator<Music> comparator) {
        return musicListControl.sortMusic(listName, comparator);
    }

    @Override
    public boolean syn() {
        return musicListControl.syn();
    }

    public void storeMusic(XMMusic music, XMListType xmListType) {
        ArrayList<XMMusic> toStoreMusic = new ArrayList<>(1);
        toStoreMusic.add(music);
        XMMusicList musicList = getList(xmListType.getTypeName());
        List<Music> musics = new ArrayList<>();
        if (musicList != null && musicList.getSDKBean() != null) {
            musics = musicList.getSDKBean().toList();
        }
        int musicIndex = 0;
        if (musicList != null) {
            musicIndex = musics.indexOf(music.getSDKBean());
        }
        if (xmListType == XMListType.LIST_MY_FAVORITE) {
            if (musicIndex < 0) {
                insertMusic(xmListType.getTypeName(), toStoreMusic, 0);
            } else {
                deleteMusic(xmListType.getTypeName(), musicIndex);
            }
        } else {
            if (musicIndex == 0) {
                return;
            } else if (musicIndex > 0) {
                boolean b = deleteMusic(xmListType.getTypeName(), musicIndex);
                if (b) {
                    insertMusic(xmListType.getTypeName(), music, 0);
                }
            } else {
                insertMusic(xmListType.getTypeName(), toStoreMusic, 0);
            }

        }
    }

    public XMMusic isFavorite(XMMusic xmMusic) {
        XMMusic music = null;
        if (XMMusic.isEmpty(xmMusic)) {
            return null;
        }
        XMMusicList musicList = getList(XMListType.LIST_MY_FAVORITE.getTypeName());
        List<Music> musics = new ArrayList<>();
        if (musicList != null && musicList.getSDKBean() != null) {
            musics = musicList.getSDKBean().toList();
        }
        for (Music item : musics) {
            if (item != null && item.rid == xmMusic.getRid()) {
                music = new XMMusic(item);
            }
        }
        return music;
    }
}
