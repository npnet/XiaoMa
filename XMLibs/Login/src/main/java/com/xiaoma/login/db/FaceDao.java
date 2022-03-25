package com.xiaoma.login.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by kaka
 * on 19-5-19 下午2:41
 * <p>
 * desc: #a
 * </p>
 */
@Dao
public interface FaceDao {
    /**
     * 查询人脸记录
     *
     * @return 人脸记录
     */
    @Query("SELECT * FROM faceStore")
    List<FaceStore> query();

    /**
     * 查询人脸Id对应的faceValue
     *
     * @return faceValue
     */
    @Query("SELECT FaceStore.face FROM faceStore WHERE FaceStore.faceId=:faceId")
    int queryById(int faceId);

    /**
     * 查询人脸对应的faceId
     *
     * @return faceValue
     */
    @Query("SELECT FaceStore.faceId FROM faceStore WHERE FaceStore.face=:face")
    int queryByFace(int face);

    /**
     * 保存人脸记录
     *
     * @param faceStore faceStore
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(FaceStore faceStore);

    /**
     * 获取所有用户的FaceId list
     */
    @Query("SELECT FaceStore.faceId FROM FaceStore WHERE FaceStore.faceId!=0")
    List<Integer> queryUsedFaceId();

    /**
     * 删除对应的人脸识别id
     *
     * @param faceId faceId
     */
    @Query("DELETE FROM FaceStore WHERE FaceStore.faceId=:faceId")
    void deleteById(int faceId);
}
