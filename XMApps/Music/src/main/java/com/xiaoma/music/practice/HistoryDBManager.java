package com.xiaoma.music.practice;

import android.text.TextUtils;

import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.login.LoginManager;
import com.xiaoma.utils.ListUtils;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/11
 *     desc   :
 * </pre>
 */
public class HistoryDBManager {

    private static final HistoryDBManager HISTORYDBMANAGER = new HistoryDBManager();

    private static final int MAX_HISTORY_SIZE = 10;

    public static HistoryDBManager getInstance() {
        return HISTORYDBMANAGER;
    }

    private IDatabase getDBManager() {
        if (LoginManager.getInstance().isUserLogin()) {
            String loginUserId = LoginManager.getInstance().getLoginUserId();
            if (loginUserId == null || TextUtils.isEmpty(loginUserId.trim())) {
                return DBManager.getInstance().getDBManager();
            }
            return DBManager.getInstance().getUserDBManager(loginUserId);
        } else {
            return DBManager.getInstance().getDBManager();
        }
    }


    /**
     * 获取所有音乐
     */
    public List<MusicHistoryBean> findMusicAll() {
        List<MusicHistoryBean> musicHistoryBeans = getDBManager().queryAll(MusicHistoryBean.class);
        if (!ListUtils.isEmpty(musicHistoryBeans) && musicHistoryBeans.size() > MAX_HISTORY_SIZE) {
            return musicHistoryBeans.subList(musicHistoryBeans.size() - MAX_HISTORY_SIZE, musicHistoryBeans.size());
        }
        return musicHistoryBeans;
    }

    /**
     * 删除所有的音乐
     *
     * @return 返回的是删除的数目
     */
    public long deleteMusicHistory() {
        return getDBManager().delete(MusicHistoryBean.class);
    }

    /**
     * 添加音乐
     *
     * @return 返回的是id（第几条数据）
     */
    public synchronized long addMusics(List<MusicHistoryBean> musicHistoryBeans) {
        return (long) getDBManager().saveAll(musicHistoryBeans);
    }

    /**
     * 添加音乐
     *
     * @return 返回的是id（第几条数据）
     */
    public synchronized void addMusicHistory(MusicHistoryBean musicHistoryBean) {
        //去重
        List<MusicHistoryBean> list = getDBManager().queryByWhere(MusicHistoryBean.class, "name", musicHistoryBean.getName());
        if (!ListUtils.isEmpty(list)) {
            return;
        }
        getDBManager().save(musicHistoryBean);
    }

    /**
     * 删除音乐
     *
     * @return 失败是-1；成功是1。
     */
    public long deleteMusicItem(MusicHistoryBean musicHistoryBean) {
        return getDBManager().delete(musicHistoryBean);
    }


}

