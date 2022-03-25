package com.xiaoma.xting.common;


import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.login.LoginManager;
import com.xiaoma.xting.common.playerSource.info.db.PlayerInfoDataBase;
import com.xiaoma.xting.common.playerSource.info.db.RecordDao;
import com.xiaoma.xting.common.playerSource.info.db.SubscribeDao;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;

import java.text.DecimalFormat;

/**
 * @author youthyJ
 * @date 2018/11/6
 */
public class XtingUtils {
    private static long mLastClickTime;
    private static final long INVALID_CLICK_DURATION = 1000;


    private XtingUtils() throws Exception {
        throw new Exception();
    }

    public static IDatabase getDBManager() {
        if (LoginManager.getInstance().isUserLogin()) {
            String loginUserId = LoginManager.getInstance().getLoginUserId();
            if (loginUserId == null || TextUtils.isEmpty(loginUserId.trim())) {
                Log.e("XtingUtils", "loginUserId is : " + loginUserId);
                return DBManager.getInstance().getDBManager();//这里可能处出现null的情况
            }
            return DBManager.getInstance().getUserDBManager(loginUserId);
        } else {
            return DBManager.getInstance().getDBManager();
        }
    }

    public static IDatabase getDBManager(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            return DBManager.getInstance().getUserDBManager(userId);
        } else {
            return DBManager.getInstance().getDBManager();
        }
    }

    public static boolean isInvalidClick() {
        long currentTime = System.currentTimeMillis();
        long clickInterval = currentTime - mLastClickTime;
        if (clickInterval > INVALID_CLICK_DURATION) {
            mLastClickTime = currentTime;
            return false;
        } else {
            return true;
        }
    }

    public static BaseChannelBean getChannelByValue(int channel) {
        if (channel >= XtingConstants.FMAM.getFMStart() && channel <= XtingConstants.FMAM.getFMEnd()) {
            FMChannelBean saveChannel = XtingUtils.getDBManager(null).queryById(channel, FMChannelBean.class);
            if (saveChannel == null) {
                saveChannel = new FMChannelBean(channel);
            }
            return saveChannel;
        } else if (channel >= XtingConstants.FMAM.getAMStart() && channel <= XtingConstants.FMAM.getAMEnd()) {
            AMChannelBean saveChannel = XtingUtils.getDBManager(null).queryById(channel, AMChannelBean.class);
            if (saveChannel == null) {
                saveChannel = new AMChannelBean(channel);
            }
            return saveChannel;
        } else {
            return null;
        }
    }

    public static BaseChannelBean getBaseChannelByValue(int channel) {
        if (channel >= XtingConstants.FMAM.getFMStart() && channel <= XtingConstants.FMAM.getFMEnd()) {
            return new FMChannelBean(channel);
        } else if (channel >= XtingConstants.FMAM.getAMStart() && channel <= XtingConstants.FMAM.getAMEnd()) {
            return new AMChannelBean(channel);
        } else {
            return null;
        }
    }

    public static String getChannelTitle(BaseChannelBean channelBean) {
        if (channelBean == null) return null;
        if (channelBean instanceof FMChannelBean) {
            return getFMTitle(channelBean.getChannelValue());
        } else {
            return getAMTitle(channelBean.getChannelValue());
        }
    }

    public static String getChannelTitle(int channel) {
        if (channel >= XtingConstants.FMAM.getFMStart() && channel <= XtingConstants.FMAM.getFMEnd()) {
            return getFMTitle(channel);
        } else if (channel >= XtingConstants.FMAM.getAMStart() && channel <= XtingConstants.FMAM.getAMEnd()) {
            return getAMTitle(channel);
        } else {
            return getFMTitle(XtingConstants.FMAM.getFMStart());
        }
    }

    public static String getChannelTitle(int channel, int band) {
        if (band == XtingConstants.FMAM.TYPE_FM
                && channel >= XtingConstants.FMAM.getFMStart()
                && channel <= XtingConstants.FMAM.getFMEnd()) {
            return getFMTitle(channel);
        } else if (band == XtingConstants.FMAM.TYPE_AM
                && channel >= XtingConstants.FMAM.getAMStart()
                && channel <= XtingConstants.FMAM.getAMEnd()) {
            return getAMTitle(channel);
        } else {
            if (band == XtingConstants.FMAM.TYPE_FM) {
                return getFMTitle(XtingConstants.FMAM.getFMStart());
            } else {
                return getAMTitle(XtingConstants.FMAM.getAMStart());
            }
        }
    }

    public static String getFMTitle(int fmChannel) {
        if (fmChannel < XtingConstants.FMAM.getFMStart() || fmChannel > XtingConstants.FMAM.getFMEnd()) {
            fmChannel = XtingConstants.FMAM.getFMStart();
        }
        return "FM " + new DecimalFormat("0.0").format(fmChannel / 1000f);
    }

    public static String getAMTitle(int amChannel) {
        if (amChannel < XtingConstants.FMAM.getAMStart() || amChannel > XtingConstants.FMAM.getAMEnd()) {
            amChannel = XtingConstants.FMAM.getAMStart();
        }
        return "AM " + amChannel;
    }

    public static PlayerInfoDataBase getUserRoomDB(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            return DBManager.getInstance().getUserRoomDB(userId, PlayerInfoDataBase.class);
        } else {
            return PlayerInfoDataBase.newSingleton();
        }
    }

    private static RecordDao sRecordDao;
    private static SubscribeDao sSubscribeDao;

    public static RecordDao getRecordDao() {
        if (sRecordDao == null) {
            synchronized (RecordDao.class) {
                if (sRecordDao == null) {
                    sRecordDao = getUserRoomDB().getRecordDao();
                }
            }
        }
        return sRecordDao;
    }

    public static SubscribeDao getSubscribeDao() {
        if (sSubscribeDao == null) {
            synchronized (SubscribeDao.class) {
                if (sSubscribeDao == null) {
                    sSubscribeDao = getUserRoomDB().getSubscribeDao();
                }
            }
        }
        return sSubscribeDao;
    }

    public static void resetDataBase() {
        sRecordDao = null;
        sSubscribeDao = null;
    }

    public static void clearAllData() {
        getSubscribeDao().clear();
        getRecordDao().clear();
    }

    private static PlayerInfoDataBase getUserRoomDB() {
        if (LoginManager.getInstance().isUserLogin()) {
            String loginUserId = LoginManager.getInstance().getLoginUserId();
            if (loginUserId == null || TextUtils.isEmpty(loginUserId.trim())) {
                Log.e("XtingUtils", "loginUserId is : " + loginUserId);
                PrintInfo.print("PlayerInfo", "GetDBBase 0");
                return PlayerInfoDataBase.newSingleton();//这里可能处出现null的情况
            }
            PrintInfo.print("PlayerInfo", "GetDBBase 1 => " + loginUserId);
            return DBManager.getInstance().getUserRoomDB(loginUserId, PlayerInfoDataBase.class);
        } else {
            PrintInfo.print("PlayerInfo", "GetDBBase 1");
            return PlayerInfoDataBase.newSingleton();
        }
    }
}
