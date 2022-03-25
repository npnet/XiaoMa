package com.xiaoma.shop.common.util;

import android.util.Log;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.xiaoma.db.DBManager;
import com.xiaoma.shop.business.model.VehicleSoundDbBean;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/12
 * @Describe:
 */

public class VehicleSoundDbUtils {


        private static final String DELETE_LIMIT = "50";//删除多少个
        private static final int DELETE_THRESHOLD = 300;//大于多少个开始进行删除



    /**
     * 保存下载记录 和 删除 过期记录
     */
    public static boolean saveAndDeleteExpiredRecords(int id,String filePath, @ResourceType int type) {
        boolean isDeleted = false;
        // 保存之前，先进行数据清理
        List<VehicleSoundDbBean> deleteBeans = findDeletes(type);
        if (!ListUtils.isEmpty(deleteBeans)) {
            DBManager.getInstance().getDBManager().delete(deleteBeans);
            KLog.i("filOut| "+"[saveAndDeleteExpiredRecords]->delete download record");
            isDeleted = true;
        }
        VehicleSoundDbBean dbBean = query(id, type);
        if (dbBean == null) {
            dbBean = new VehicleSoundDbBean();
        }
        long download_time = System.currentTimeMillis();
        dbBean.setId(id);
        dbBean.setFilePath(filePath);
        dbBean.setResourceType(type);
        dbBean.setDownloadTime(download_time);
        DBManager.getInstance().getDBManager().save(dbBean);
        KLog.i("filOut| "+"[saveAndDeleteExpiredRecords]->保存下载记录");
        return isDeleted;
    }

    private static List<VehicleSoundDbBean> findDeletes(@ResourceType int type) {
        List<VehicleSoundDbBean> vehicleSoundDbBeans = null;
        QueryBuilder<VehicleSoundDbBean> condition = new QueryBuilder<>(VehicleSoundDbBean.class)
                .whereEquals(VehicleSoundDbBean.RESOURCE_TYPE, "" + type);
        int allSize = DBManager.getInstance().getDBManager().queryData(condition).size();
        if (allSize > DELETE_THRESHOLD) {
            QueryBuilder<VehicleSoundDbBean> queryBuilder = new QueryBuilder<>(VehicleSoundDbBean.class)
                    .whereEquals(VehicleSoundDbBean.RESOURCE_TYPE, type + "")
                    .appendOrderAscBy(VehicleSoundDbBean.DOWNLOAD_TIME)
                    .limit(DELETE_LIMIT);
            vehicleSoundDbBeans = DBManager.getInstance().getDBManager().queryData(queryBuilder);
        }
        return vehicleSoundDbBeans;
    }

    public static void delete(VehicleSoundEntity.SoundEffectListBean bean, @ResourceType int type) {
        VehicleSoundDbBean dbBean = query(bean.getId(), type);
        if (dbBean == null) return;
        DBManager.getInstance().getDBManager().delete(dbBean);
    }

    public static VehicleSoundDbBean query(long id, @ResourceType int type) {
        QueryBuilder<VehicleSoundDbBean> queryBuilder = new QueryBuilder<>(VehicleSoundDbBean.class)
                .where(VehicleSoundDbBean.ID + "=?", new String[]{id + ""})
                .whereAppendAnd()
                .whereAppend(VehicleSoundDbBean.RESOURCE_TYPE + "=?", new String[]{type + ""})
                .limit("1");
        List<VehicleSoundDbBean> vehicleSoundDbBeans = DBManager.getInstance().getDBManager().queryData(queryBuilder);
        if (ListUtils.isEmpty(vehicleSoundDbBeans)) {
            return null;
        }
        return vehicleSoundDbBeans.get(0);
    }

}
