package com.xiaoma.xting.common.playerSource.info.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
@Dao
public interface SubscribeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SubscribeInfo... info);

    @Delete
    void delete(SubscribeInfo... info);

    @Query("DELETE FROM SubscribeInfo where albumId = :albumId and type = :type")
    void deleteBy(long albumId, int type);

    @Query("DELETE FROM SubscribeInfo where 1=1")
    void clear();

    /**
     * @return 根据收藏的先后顺序排序
     */
    @Query("SELECT * FROM SubscribeInfo order by subscribeTime desc")
    List<SubscribeInfo> selectAll();

    @Query("SELECT * FROM SubscribeInfo where type != 4 order by subscribeTime desc")
    List<SubscribeInfo> selectAllRadio();

    @Query("SELECT * FROM SubscribeInfo where type = 4 and albumId >= 87500 and albumId <= 108000 order by albumId asc")
    List<SubscribeInfo> selectAllFM();

    @Query("SELECT * FROM SubscribeInfo where type = 4 and albumId >= 531 and albumId <= 1602 order by albumId asc")
    List<SubscribeInfo> selectAllAM();

    /**
     * @param type    数据类型
     * @param albumId 专辑ID
     * @return SubscribeInfo
     */
    @Query("SELECT * FROM SubscribeInfo WHERE type=:type and albumId=:albumId")
    SubscribeInfo selectBy(int type, long albumId);

    /**
     * @return 获取数据库数量
     */
    @Query("Select count(0) FROM SubscribeInfo")
    int count();

}
