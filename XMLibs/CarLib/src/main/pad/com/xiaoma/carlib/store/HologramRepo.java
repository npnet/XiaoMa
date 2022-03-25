package com.xiaoma.carlib.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiaoma.config.ConfigManager;

import java.io.File;

/**
 * Created by LKF on 2019-6-6 0006.
 * 全息影像数据类
 *
 * @hide
 */
public class HologramRepo {
    public static final int DEFAULT_ROLE_ID = 65281;
    public static final int DEFAULT_CLOTH_ID = 65281;
    public static final int DEFAULT_HOLO_ID = -1;

    private static SharedPreferences sPreferences;
    private static final String SP_FILE_NAME = "hologram_using";
    private static final String HOLOGRAM_USING_ROLE_ID = "hologram_using_role_id";
    private static final String HOLOGRAM_USING_HOLO_ID = "hologram_using_holo_id";
    private static final String HOLOGRAM_USING_CLOTH_ID_PREFIX = "hologram_using_cloth_id_";

    public static void putUsingHoloId(Context context, int roleId) {
        getPreferences(context).edit()
                .putInt(HOLOGRAM_USING_HOLO_ID, roleId)
                .apply();
    }

    public static int getUsingHoloId(Context context) {
       return getPreferences(context).getInt(HOLOGRAM_USING_HOLO_ID, DEFAULT_HOLO_ID);
    }

    public static void putUsingRoleId(Context context, int roleId) {
        getPreferences(context).edit()
                .putInt(HOLOGRAM_USING_ROLE_ID, roleId)
                .apply();
    }

    public static int getUsingRoleId(Context context) {
        return getPreferences(context).getInt(HOLOGRAM_USING_ROLE_ID, DEFAULT_ROLE_ID);
    }

    public static void putUsingClothId(Context context, int roleId, int clothId) {
        getPreferences(context).edit()
                .putInt(getRoleClothKey(roleId), clothId)
                .apply();
    }

    public static int getUsingClothId(Context context, int roleId) {
        return getPreferences(context).getInt(getRoleClothKey(roleId), DEFAULT_CLOTH_ID);
    }

    private static String getRoleClothKey(int roleId) {
        return HOLOGRAM_USING_CLOTH_ID_PREFIX + roleId;
    }

    private static synchronized SharedPreferences getPreferences(Context context) {
        if (sPreferences == null) {
            File dir = ConfigManager.FileConfig.getShopHologramFolder();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File spFile = new File(dir, SP_FILE_NAME);
            sPreferences = context.getSharedPreferences(spFile, Context.MODE_WORLD_WRITEABLE);
        }
        return sPreferences;
    }
}
