package com.xiaoma.xting.common.playerSource.info.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/28
 */
@Dao
public interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecordInfo... info);

    @Update
    void update(RecordInfo... info);

    @Delete
    void delete(RecordInfo... info);

    @Query("DELETE FROM RecordInfo where albumId = :albumId and type = :type")
    void deleteBy(long albumId, int type);

    @Query("DELETE FROM RecordInfo where 1=1")
    void clear();

    /**
     * @return List<PlayerInfo>
     */
    @Query("SELECT * FROM RecordInfo order by listenTime desc")
    List<RecordInfo> selectAll();

    /**
     * @param type      数据类型
     * @param programId 专辑ID
     * @return RecordInfo
     */
    @Query("SELECT * FROM RecordInfo WHERE type=:type and programId=:programId")
    RecordInfo selectBy(int type, long programId);

    @Query("SELECT * FROM RecordInfo WHERE albumId=:albumId")
    RecordInfo selectBy(long albumId);

    @Query("SELECT * FROM RecordInfo WHERE type=:type and albumId=:albumId")
    RecordInfo selectRadio(int type, long albumId);

    @Query("SELECT * FROM RecordInfo WHERE type=:type and albumId=:albumId order by listenTime desc")
    List<RecordInfo> selectProgramByAlbum(int type, long albumId);
    /**
     * @return 获取数据库数量
     */
    @Query("Select count(0) FROM RecordInfo")
    int count();
}
