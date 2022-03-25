package com.xiaoma.shop.common.util;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.xiaoma.db.DBManager;
import com.xiaoma.shop.common.manager.update.UpdateOtaInfo;
import com.xiaoma.utils.ListUtils;

import java.util.List;

/**
 * Author: Ljb
 * Time  : 2019/7/9
 * Description:
 */
public class UpdateOtaInfoDbUtils {


    public static UpdateOtaInfo query(int ecu, String fileUrl) {
        QueryBuilder<UpdateOtaInfo> queryParams = new QueryBuilder<>(UpdateOtaInfo.class)
                .where(UpdateOtaInfo.ECU + "=?", new String[]{ecu + ""})
                .whereAppendAnd()
                .whereAppend(UpdateOtaInfo.FILE_URL + "=?", new String[]{fileUrl})
                .limit("1");
        List<UpdateOtaInfo> updateOtaInfos = DBManager.getInstance().getDBManager().queryData(queryParams);
        if (ListUtils.isEmpty(updateOtaInfos)) {
            return null;
        }
        return updateOtaInfos.get(0);
    }

    public static UpdateOtaInfo query(int ecu) {
        QueryBuilder<UpdateOtaInfo> queryParams = new QueryBuilder<>(UpdateOtaInfo.class)
                .where(UpdateOtaInfo.ECU + " = ?", new String[]{ecu + ""})
                .limit("1");
        List<UpdateOtaInfo> updateOtaInfos = DBManager.getInstance().getDBManager().queryData(queryParams);
        if (ListUtils.isEmpty(updateOtaInfos)) {
            return null;
        }
        return updateOtaInfos.get(0);
    }

    public static long save(UpdateOtaInfo info) {
        if (info == null) return -1;
        return DBManager.getInstance().getDBManager().save(info);
    }

    public static List<UpdateOtaInfo> queryContinueUpdate() {
        QueryBuilder<UpdateOtaInfo> queryParams = new QueryBuilder<>(UpdateOtaInfo.class)
                .where(UpdateOtaInfo.INSTALL_STATE + "=?", new String[]{UpdateOtaInfo.InstallState.INSTALLING + ""});
        return DBManager.getInstance().getDBManager().queryData(queryParams);
    }

    public static List<UpdateOtaInfo> queryContinueUpdate(int ecu) {
        QueryBuilder<UpdateOtaInfo> queryParams = new QueryBuilder<>(UpdateOtaInfo.class)
                .where(UpdateOtaInfo.INSTALL_STATE + "=?", new String[]{UpdateOtaInfo.InstallState.INSTALLING + ""})
                .whereAppend(UpdateOtaInfo.ECU + "=?", new String[]{ecu + ""});
        return DBManager.getInstance().getDBManager().queryData(queryParams);
    }

    public static void delete(UpdateOtaInfo otaInfos) {
        DBManager.getInstance().getDBManager().delete(otaInfos);
    }
}
