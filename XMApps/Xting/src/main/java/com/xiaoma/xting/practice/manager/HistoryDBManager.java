package com.xiaoma.xting.practice.manager;

import android.text.TextUtils;

import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.login.LoginManager;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.practice.model.RadioHistoryBean;

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
     * 获取所有电台
     */
    public List<RadioHistoryBean> findRadioAll() {
        List<RadioHistoryBean> radioHistoryBeans = getDBManager().queryAll(RadioHistoryBean.class);
        if (!ListUtils.isEmpty(radioHistoryBeans) && radioHistoryBeans.size() > MAX_HISTORY_SIZE) {
            return radioHistoryBeans.subList(radioHistoryBeans.size() - MAX_HISTORY_SIZE, radioHistoryBeans.size());
        }
        return radioHistoryBeans;
    }

    /**
     * 删除所有的电台
     *
     * @return 返回的是删除的数目
     */
    public long deleteRadioAll() {
        return getDBManager().delete(RadioHistoryBean.class);
    }

    /**
     * 添加电台
     *
     * @return 返回的是id（第几条数据）
     */
    public synchronized void addRadio(RadioHistoryBean radioHistoryBean) {
        //去重
        List<RadioHistoryBean> list = getDBManager().queryByWhere(RadioHistoryBean.class, "name", radioHistoryBean.getName());
        if (!ListUtils.isEmpty(list)) {
            return;
        }
        getDBManager().save(radioHistoryBean);
    }

    /**
     * 删除电台
     *
     * @return 失败是-1；成功是1。
     */
    public long deleteRadioItem(RadioHistoryBean radioHistoryBean) {
        return getDBManager().delete(radioHistoryBean);
    }
}

