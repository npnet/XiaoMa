package com.xiaoma.shop.business.skin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.User;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinInfo;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by LKF on 2019-7-15 0015.
 */
public class UserSkinReceiver extends BroadcastReceiver {
    public final String TAG = getClass().getSimpleName();
    private static final String TP_SKIN_INFO_PREFIX = "skinInfo_";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean isConditionMeet = SkinUsing.isConditionMeet();
        Log.e(TAG, String.format("onReceive -> action = %s, isConditionMeet: %s", action, isConditionMeet));
        if (LoginConstants.ACTION_ON_LOGIN.equals(action)) {
            if (isConditionMeet) {
                restoreSkinForUser(context);
            }
        } else if (LoginConstants.ACTION_ON_LOGOUT.equals(action)) {
            if (isConditionMeet) {
                XmSkinManager.getInstance().restoreDefault(context);
            }
        } else if (SkinConstants.SKIN_SUCCESS.equals(action)) {
            // 主题变化事件,记录当前的主题
            long uid = getCurUid();
            if (uid > 0) {
                String skinInfoJson;
                SkinInfo skinInfo = SkinUtils.getSkinMsg();
                if (skinInfo != null
                        && !TextUtils.isEmpty(skinInfoJson = GsonHelper.toJson(skinInfo))) {
                    Log.e(TAG, String.format("onReceive -> putSkinInfoJson: %s", skinInfoJson));
                    TPUtils.put(context, TP_SKIN_INFO_PREFIX + uid, skinInfoJson);
                }
            }
        }
    }

    private void restoreSkinForUser(Context context) {
        long uid = getCurUid();
        boolean restoreDefault = true;
        String dumpInfo;
        XmSkinManager skinManager = XmSkinManager.getInstance();
        if (uid > 0) {
            SkinInfo skinInfo = GsonHelper.fromJson(
                    TPUtils.get(context, TP_SKIN_INFO_PREFIX + uid, ""), SkinInfo.class);
            SkinInfo curSkinInfo = SkinUtils.getSkinMsg();
            // 没有皮肤记录时,还原为默认皮肤
            if (skinInfo != null) {
                // 当前皮肤和记录的皮肤不一致时才执行换肤还原
                if (!skinInfo.equals(curSkinInfo)) {
                    String skinType = skinInfo.skinType;
                    if (SkinUtils.TYPE_NAME.equals(skinType)) {
                        int themeId = skinManager.loadSkinByName(context, skinInfo.skinId, skinInfo.skinName);
                        setLcdSkin(themeId);
                        dumpInfo = "Restore by skin name: " + themeId;
                        restoreDefault = false;
                    } else if (SkinUtils.TYPE_PATH.equals(skinType)) {
                        // 如果还原失败,则还原为默认皮肤
                        restoreDefault = !SkinUsing.restoreSkinForUser(context);
                        dumpInfo = "Restore by skin path";
                    } else {
                        dumpInfo = "Unknown skin type !!!";
                    }
                } else {
                    dumpInfo = "Skin is the same";
                    restoreDefault = false;
                }
            } else {
                dumpInfo = "SkinInfo is null";
            }
        } else {
            dumpInfo = "Invalid uid: " + uid;
        }
        if (restoreDefault) {
            skinManager.restoreDefault(context);
            setLcdSkin(SkinConstants.THEME_DEFAULT);
        }
        Log.e(TAG, "restoreSkinForUser -> " + dumpInfo);
    }

    private void setLcdSkin(int skinId) {
        // 仪表换肤同步
        try {
            XmCarVendorExtensionManager.getInstance().setTheme(skinId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getCurUid() {
        long uid = 0;
        User u = UserManager.getInstance().getCurrentUser();
        if (u != null) {
            uid = u.getId();
        }
        Log.e(TAG, String.format("getCurUid -> uid: %s ", uid));
        return uid;
    }
}
