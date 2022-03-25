package com.xiaoma.music.kuwo.manager;

import com.xiaoma.music.kuwo.impl.IMusicListControl;
import com.xiaoma.music.kuwo.model.XMListType;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import cn.kuwo.base.bean.ListType;
import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.MusicList;
import cn.kuwo.mod.ModMgr;

/**
 * Created by ZYao.
 * Date ：2018/10/22 0022
 */
public class KuwoMusicListControl implements IMusicListControl {
    @Override
    public XMMusicList insertList(XMListType t, String listName) {
        final ListType listType = XMListType.recoveryXMListType(t);
        final MusicList music = ModMgr.getListMgr().insertList(listType, listName);
        return new XMMusicList(music);
    }

    @Override
    public XMMusicList insertListAutoRename(XMListType t, String listName) {
        final ListType listType = XMListType.recoveryXMListType(t);
        final MusicList music = ModMgr.getListMgr().insertListAutoRename(listType, listName);
        return new XMMusicList(music);
    }

    @Override
    public boolean deleteList(XMListType t) {
        final ListType listType = XMListType.recoveryXMListType(t);
        return ModMgr.getListMgr().deleteList(listType);
    }

    @Override
    public boolean deleteList(String listName) {
        return ModMgr.getListMgr().deleteList(listName);
    }

    @Override
    public XMMusicList getList(String listName) {
        final MusicList list = ModMgr.getListMgr().getList(listName);
        return new XMMusicList(list);
    }

    @Override
    public XMMusicList getUniqueList(XMListType t) {
        final ListType listType = XMListType.recoveryXMListType(t);
        final MusicList uniqueList = ModMgr.getListMgr().getUniqueList(listType);
        return new XMMusicList(uniqueList);
    }

    @Override
    public Collection<XMMusicList> getList(XMListType t) {
        final ListType listType = XMListType.recoveryXMListType(t);
        final Collection<MusicList> list = ModMgr.getListMgr().getList(listType);
        Collection<XMMusicList> xmMusicLists = new ArrayList<>();
        for (MusicList music : list) {
            if (music == null) {
                continue;
            }
            xmMusicLists.add(new XMMusicList(music));
        }
        return xmMusicLists;
    }

    @Override
    public Collection<String> getListName(XMListType t) {
        final ListType listType = XMListType.recoveryXMListType(t);
        return ModMgr.getListMgr().getListName(listType);
    }

    @Override
    public Collection<String> getInsertableMusicListName() {
        return ModMgr.getListMgr().getInsertableMusicListName();
    }

    @Override
    public Collection<XMMusicList> getAllList() {
        final Collection<MusicList> list = ModMgr.getListMgr().getAllList();
        Collection<XMMusicList> xmMusicLists = new ArrayList<>();
        for (MusicList music : list) {
            if (music == null) {
                continue;
            }
            xmMusicLists.add(new XMMusicList(music));
        }
        return xmMusicLists;
    }

    @Override
    public List<XMMusicList> getShowList() {
        final List<MusicList> list = ModMgr.getListMgr().getShowList();
        List<XMMusicList> xmMusicLists = new ArrayList<>();
        for (MusicList music : list) {
            if (music == null) {
                continue;
            }
            xmMusicLists.add(new XMMusicList(music));
        }
        return xmMusicLists;
    }

    @Override
    public int insertMusic(String listName, XMMusic music) {
        if (music == null || music.getSDKBean() == null) {
            return -1;
        }
        return ModMgr.getListMgr().insertMusic(listName, music.getSDKBean());
    }

    @Override
    public int insertMusic(String listName, List<XMMusic> music) {
        if (ListUtils.isEmpty(music)) {
            return -1;
        }
        List<Music> musicList = new ArrayList<>();
        for (XMMusic xmMusic : music) {
            if (xmMusic == null || xmMusic.getSDKBean() == null) {
                continue;
            }
            musicList.add(xmMusic.getSDKBean());
        }
        return ModMgr.getListMgr().insertMusic(listName, musicList);
    }

    @Override
    public int insertMusic(String listName, XMMusic music, int position) {
        if (music == null || music.getSDKBean() == null) {
            return -1;
        }
        return ModMgr.getListMgr().insertMusic(listName, music.getSDKBean(), position);
    }

    @Override
    public int insertMusic(String listName, List<XMMusic> music, int position) {
        if (ListUtils.isEmpty(music)) {
            return -1;
        }
        List<Music> musicList = new ArrayList<>();
        for (XMMusic xmMusic : music) {
            if (xmMusic == null || xmMusic.getSDKBean() == null) {
                continue;
            }
            musicList.add(xmMusic.getSDKBean());
        }
        return ModMgr.getListMgr().insertMusic(listName, musicList, position);
    }

    @Override
    public boolean deleteMusic(String listName) {
        return ModMgr.getListMgr().deleteMusic(listName);
    }

    @Override
    public boolean deleteMusic(String listName, int position) {
        return ModMgr.getListMgr().deleteMusic(listName, position);
    }

    @Override
    public boolean deleteMusic(String listName, int start, int count) {
        return ModMgr.getListMgr().deleteMusic(listName, start, count);
    }

    @Override
    public boolean deleteMusic(String listName, Collection<Integer> position) {
        return ModMgr.getListMgr().deleteMusic(listName, position);
    }

    @Override
    public boolean deleteMusic(String listName, XMMusic music) {
        if (music == null || music.getSDKBean() == null) {
            return false;
        }
        return ModMgr.getListMgr().deleteMusic(listName, music.getSDKBean());
    }

    @Override
    public boolean deleteMusic(String listName, List<XMMusic> musics) {
        if (ListUtils.isEmpty(musics)) {
            return false;
        }
        List<Music> musicList = new ArrayList<>();
        for (XMMusic xmMusic : musics) {
            if (xmMusic == null || xmMusic.getSDKBean() == null) {
                continue;
            }
            musicList.add(xmMusic.getSDKBean());
        }
        return ModMgr.getListMgr().deleteMusic(listName, musicList);
    }

    @Override
    public int deleteMusicEx(String listName, XMMusic music) {
        if (music == null || music.getSDKBean() == null) {
            return -1;
        }
        return ModMgr.getListMgr().deleteMusicEx(listName, music.getSDKBean());
    }

    @Override
    public int deleteMusicEx(String listName, List<XMMusic> musics) {
        if (ListUtils.isEmpty(musics)) {
            return -1;
        }
        List<Music> musicList = new ArrayList<>();
        for (XMMusic xmMusic : musics) {
            if (xmMusic == null || xmMusic.getSDKBean() == null) {
                continue;
            }
            musicList.add(xmMusic.getSDKBean());
        }
        return ModMgr.getListMgr().deleteMusicEx(listName, musicList);
    }

    @Override
    public int indexOf(String listName, XMMusic music) {
        if (music == null || music.getSDKBean() == null) {
            return -1;
        }
        return ModMgr.getListMgr().indexOf(listName, music.getSDKBean());
    }

    @Override
    public boolean sortMusic(String listName, Comparator<Music> comparator) {
        return ModMgr.getListMgr().sortMusic(listName, comparator);
    }

    @Override
    public boolean syn() {
        return ModMgr.getListMgr().syn();
    }
}
