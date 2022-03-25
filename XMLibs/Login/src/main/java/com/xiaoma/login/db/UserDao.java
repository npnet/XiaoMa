package com.xiaoma.login.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.xiaoma.model.User;

import java.util.List;

/**
 * Created by kaka
 * on 19-5-19 下午2:41
 * <p>
 * desc: #a
 * </p>
 */
@Dao
public interface UserDao {
    /**
     * 获取所有的本地缓存用户数量
     *
     * @return 所有的本地缓存用户数量
     */
    @Query("SELECT count(id) FROM user where id != 1058 and id != 0")
    int getCachedUsersCount();

    /**
     * 获取所有的本地缓存用户
     *
     * @return 所有的本地缓存用户
     */
    @Query("SELECT id,isOnLine,voicePrintClientId,masterUser,masterAccountId," +
            "bluetoothKey,commonKey,faceId,password,type,openId,createDate," +
            "modifyDate,gender,age,name,phone,picPath,headerIndex,birthDay," +
            "birthDayLong,personalSignature,imei,channelId,tboxSN,carType," +
            "distance,plateNumber,vin,engineNumber,carTypeId,carTypeLogo," +
            "isFirstCar,hxAccount,hxPassword,hxAccountService,hxPasswordService," +
            "voipAccount,voipPassword,voipAccountService,voipPasswordService,wxOpenId," +
            "bmUserId,subToken,subAccount,mkUser,mkPassword,isRead,enableStatus," +
            "privateStatus,beanNum,userShortNum,riskScore,score,h5Score," +
            "extra1,extra2,extra3 FROM user")
    List<User> getCachedUsers();

    /**
     * 根据UserId获取本地缓存用户
     *
     * @param userId userId
     * @return userId对应的本地缓存用户
     */
    @Query("SELECT id,isOnLine,voicePrintClientId,masterUser,masterAccountId," +
            "bluetoothKey,commonKey,faceId,password,type,openId,createDate," +
            "modifyDate,gender,age,name,phone,picPath,headerIndex,birthDay," +
            "birthDayLong,personalSignature,imei,channelId,tboxSN,carType," +
            "distance,plateNumber,vin,engineNumber,carTypeId,carTypeLogo," +
            "isFirstCar,hxAccount,hxPassword,hxAccountService,hxPasswordService," +
            "voipAccount,voipPassword,voipAccountService,voipPasswordService,wxOpenId," +
            "bmUserId,subToken,subAccount,mkUser,mkPassword,isRead,enableStatus," +
            "privateStatus,beanNum,userShortNum,riskScore,score,h5Score," +
            "extra1,extra2,extra3 FROM user WHERE user.id=:userId")
    User getCachedUserById(long userId);

    /**
     * 获取本地缓存的主账户
     *
     * @return 本地缓存的主账户
     */
    @Query("SELECT id,isOnLine,voicePrintClientId,masterUser,masterAccountId," +
            "bluetoothKey,commonKey,faceId,password,type,openId,createDate," +
            "modifyDate,gender,age,name,phone,picPath,headerIndex,birthDay," +
            "birthDayLong,personalSignature,imei,channelId,tboxSN,carType," +
            "distance,plateNumber,vin,engineNumber,carTypeId,carTypeLogo," +
            "isFirstCar,hxAccount,hxPassword,hxAccountService,hxPasswordService," +
            "voipAccount,voipPassword,voipAccountService,voipPasswordService,wxOpenId," +
            "bmUserId,subToken,subAccount,mkUser,mkPassword,isRead,enableStatus," +
            "privateStatus,beanNum,userShortNum,riskScore,score,h5Score," +
            "extra1,extra2,extra3 FROM user WHERE user.masterUser=1")
    User getCachedMasterUser();

    /**
     * 获取本地缓存的所有子账户
     *
     * @return 本地缓存的所有子账户
     */
    @Query("SELECT id,isOnLine,voicePrintClientId,masterUser,masterAccountId," +
            "bluetoothKey,commonKey,faceId,password,type,openId,createDate," +
            "modifyDate,gender,age,name,phone,picPath,headerIndex,birthDay," +
            "birthDayLong,personalSignature,imei,channelId,tboxSN,carType," +
            "distance,plateNumber,vin,engineNumber,carTypeId,carTypeLogo," +
            "isFirstCar,hxAccount,hxPassword,hxAccountService,hxPasswordService," +
            "voipAccount,voipPassword,voipAccountService,voipPasswordService,wxOpenId," +
            "bmUserId,subToken,subAccount,mkUser,mkPassword,isRead,enableStatus," +
            "privateStatus,beanNum,userShortNum,riskScore,score,h5Score," +
            "extra1,extra2,extra3 FROM user WHERE user.masterUser=0")
    List<User> getCachedSubUser();

    /**
     * 解绑账户绑定的蓝牙钥匙
     *
     * @param userId 用户id
     */
    @Query("UPDATE user SET bluetoothKey = NULL WHERE user.id=:userId")
    int unBindBLEKey(long userId);

    /**
     * 解绑用户绑定的普通钥匙
     *
     * @param userId 用户id
     */
    @Query("UPDATE user SET commonKey = NULL WHERE user.id=:userId")
    int unBindNormalKey(long userId);

    /**
     * 解绑用户绑定的人脸faceId
     *
     * @param userId 用户id
     */
    @Query("UPDATE user SET faceId = NULL WHERE user.id=:userId")
    int unBindFace(long userId);

    /**
     * 缓存用户信息
     *
     * @param user 用户信息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveCacheUser(User user);

    /**
     * 缓存多个用户信息
     *
     * @param users 多个用户信息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCacheUsers(List<User> users);

    /**
     * 删除缓存的用户信息
     *
     * @param user user
     */
    @Delete
    int removeCacheUser(User user);

    /**
     * 根据用户id移除缓存的用户信息
     *
     * @param userId userId
     */
    @Query("DELETE FROM user WHERE user.id=:userId")
    int removeCacheUserById(long userId);

    /**
     * 移除所有缓存的用户信息
     *
     * @return delete count
     */
    @Query("DELETE FROM user WHERE 1=1")
    int removeAllCacheUser();

    /**
     * 获取所有用户的FaceId list
     */
    @Query("SELECT user.faceId FROM user WHERE user.faceId!=0")
    List<Integer> queryUsedFaceId();
}
