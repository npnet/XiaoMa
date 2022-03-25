package com.xiaoma.carlib.store;

import android.car.hardware.vendor.RobotClothes;
import android.car.hardware.vendor.RobotFigure;
import android.content.Context;

import com.xiaoma.config.ConfigManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by LKF on 2019-6-6 0006.
 * 全息影像数据类
 */
public class HologramRepo {
    public static final int DEFAULT_ROLE_ID = RobotFigure.FIGURE_1_REQ;
    public static final int DEFAULT_CLOTH_ID = RobotClothes.CLOTHES_1_REQ;
    public static final int DEFAULT_HOLO_ID = -1;

    private static final Properties sFileStore = new Properties();
    private static final String STORE_FILE_NAME = "hologram_using.properties";
    private static final String HOLOGRAM_USING_ROLE_ID = "hologram_using_role_id";
    private static final String HOLOGRAM_USING_HOLO_ID = "hologram_using_holo_id";
    private static final String HOLOGRAM_USING_CLOTH_ID_PREFIX = "hologram_using_cloth_id_";

    public static void putUsingHoloId(Context context, int roleId) {
        setProperty(HOLOGRAM_USING_HOLO_ID, roleId);
    }

    public static int getUsingHoloId(Context context) {
        return getProperty(HOLOGRAM_USING_HOLO_ID, DEFAULT_HOLO_ID);
    }

    public static void putUsingRoleId(Context context, int roleId) {
        setProperty(HOLOGRAM_USING_ROLE_ID, roleId);
    }

    public static int getUsingRoleId(Context context) {
        return getProperty(HOLOGRAM_USING_ROLE_ID, DEFAULT_ROLE_ID);
    }

    public static void putUsingClothId(Context context, int roleId, int clothId) {
        setProperty(getRoleClothKey(roleId), clothId);
    }

    public static int getUsingClothId(Context context, int roleId) {
        return getProperty(getRoleClothKey(roleId), DEFAULT_CLOTH_ID);
    }

    private static String getRoleClothKey(int roleId) {
        return HOLOGRAM_USING_CLOTH_ID_PREFIX + roleId;
    }

    private static int getProperty(String key, int defaultValue) {
        int clothId = defaultValue;
        try {
            sFileStore.load(new FileInputStream(getStoreFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String clothIdStr = sFileStore.getProperty(key, String.valueOf(defaultValue));
        try {
            clothId = Integer.parseInt(clothIdStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return clothId;
    }

    private static void setProperty(String key, int value) {
        sFileStore.setProperty(key, String.valueOf(value));
        try {
            sFileStore.store(new FileOutputStream(getStoreFile()), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getStoreFile() {
        File dir = ConfigManager.FileConfig.getShopHologramFolder();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, STORE_FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
