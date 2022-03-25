package com.xiaoma.music.export.manager;

import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.login.LoginManager;
import com.xiaoma.music.export.model.MusicTagInfo;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/5/8 0008
 */
public class MoodLightingManager {

    private static final int MAX_SAVE_MUSIC = 200;
    private static final String SAVE_MUSIC_ID = "songId";

    private static IDatabase getDBManager() {
        if (LoginManager.getInstance().isUserLogin()) {
            return DBManager.getInstance().getUserDBManager(LoginManager.getInstance().getLoginUserId());
        } else {
            return DBManager.getInstance().getDBManager();
        }
    }

    public static MoodLightingManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final MoodLightingManager instance = new MoodLightingManager();
    }

    public void saveMusicTag(MusicTagInfo musicTagInfo) {
        if (musicTagInfo == null) {
            return;
        }
        getDBManager().save(musicTagInfo);
        final List<MusicTagInfo> musicTagInfos = getDBManager().queryAll(MusicTagInfo.class);
        if (musicTagInfos != null && musicTagInfos.size() > MAX_SAVE_MUSIC) {
            List<MusicTagInfo> temp = new ArrayList<>(musicTagInfos);
            Collections.reverse(temp);
            if (temp.size() > MAX_SAVE_MUSIC) {
                temp.subList(MAX_SAVE_MUSIC, temp.size() - 1);
            }
            getDBManager().delete(temp);
        }
    }

    public MusicTagInfo queryLocalTagById(long id) {
        List<MusicTagInfo> xmMusicList = getDBManager().queryByWhere(MusicTagInfo.class,
                SAVE_MUSIC_ID, String.valueOf(id));
        if (!ListUtils.isEmpty(xmMusicList)) {
            return xmMusicList.get(0);
        }
        return null;
    }

}
