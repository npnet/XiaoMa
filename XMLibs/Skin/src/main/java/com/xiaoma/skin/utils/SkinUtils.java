package com.xiaoma.skin.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.loader.XMSDCardLoader;
import com.xiaoma.skin.manager.XmSkinLoaderListener;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;

import skin.support.SkinCompatManager;

/**
 * author: andy、JF
 * date:   2019/6/3 16:16
 * desc:
 */
public class SkinUtils {
    public static final String SKIN_NAME = "SKIN_NAME";
    public static final String SKIN_PATH = "SKIN_PATH";
    public static final String SKIN_ID = "SKIN_ID";
    public static final String SKIN_TYPE = "SKIN_TYPE";

    public static final String TYPE_PATH = "TYPE_PATH";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String TYPE_DEFAULT = "TYPE_DEFAULT";

    public static String getSkinPath() {
        File skinConfigFile = ConfigManager.FileConfig.getGlobalSkinConfigFile();
        return skinConfigFile == null ? "" : FileUtils.read(skinConfigFile);
    }


    public static void sendBroadcast(Context context) {
        Intent intent = new Intent(SkinConstants.SKIN_ACTION_XM);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcast(Context context, String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    public static void saveSkinMsg(String type, String skinName, String skinPath, String skinId, int skinStyle) {
        SkinInfo info = new SkinInfo();
        info.skinId = getContent(skinId);
        info.skinName = getContent(skinName);
        info.skinPath = getContent(skinPath);
        info.skinType = getContent(type);
        info.skinStyle = skinStyle;
        String skinMsg = GsonHelper.toJson(info);
        FileUtils.write(skinMsg, ConfigManager.FileConfig.getGlobalSkinConfigFile(), false);

    }

    public static SkinInfo getSkinMsg() {
        String skinMsg = FileUtils.read(ConfigManager.FileConfig.getGlobalSkinConfigFile());
        SkinInfo info = GsonHelper.fromJson(skinMsg, SkinInfo.class);
        return info;
    }

    /**
     * 获取当前主题对应的内置主题ID风格,如果当前使用的就是默认主题,则直接返回当前主题ID
     */
    public static int getCurSkinStyle() {
        int skinId = SkinConstants.THEME_DEFAULT;
        SkinInfo curSkinInfo = getSkinMsg();
        if (curSkinInfo != null) {
            String skinType = curSkinInfo.skinType;
            if (SkinUtils.TYPE_NAME.equals(skinType)) {
                String skinName = curSkinInfo.skinName;
                if (SkinConstants.SKIN_NAME_DAOMENG.equals(skinName)) {
                    skinId = SkinConstants.THEME_DAOMENG;
                } else if (SkinConstants.SKIN_NAME_QINGSHE.equals(skinName)) {
                    skinId = SkinConstants.THEME_QINGSHE;
                }
            } else if (SkinUtils.TYPE_PATH.equals(skinType)) {
                skinId = curSkinInfo.skinStyle;
            }
        }
        return skinId;
    }

    private static String getContent(String content) {
        return content == null ? "" : content;
    }


    public static void loadSkin(final Context context) {
        String curSkinName = XmSkinManager.getInstance().getCurSkinName();
        SkinInfo skinInfo = SkinUtils.getSkinMsg();
        if (skinInfo == null) {
            return;
        }
        String skinType = skinInfo.skinType;
        String skinName = skinInfo.skinName;
        String skinId = skinInfo.skinId;
        final String skinPath = skinInfo.skinPath;
        if (SkinUtils.TYPE_DEFAULT.equals(skinType)) {
            //默认
            if (!(TextUtils.isEmpty(skinPath) && TextUtils.isEmpty(curSkinName))) {
                //加载默认皮肤
                XmSkinManager.getInstance().loadSkin("", new XmSkinLoaderListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed(String errMsg) {
                    }
                }, SkinCompatManager.SKIN_LOADER_STRATEGY_NONE);
            }
        } else if (SkinUtils.TYPE_NAME.equals(skinType)) {
            //皮肤名、内置
            if (!skinName.equals(curSkinName)) {
                //通过保存的皮肤名加载皮肤
                XmSkinManager.getInstance().loadSkin(skinName, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
            }

        } else if (SkinUtils.TYPE_PATH.equals(skinType)) {
            //路径
            if (!TPUtils.get(context, SkinUtils.SKIN_PATH, "").equals(skinPath)) {
                //根据保存的路径加载皮肤
                XmSkinManager.getInstance().loadSkin(context.getPackageName().replace(".", "_"), new XmSkinLoaderListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        TPUtils.put(context, SkinUtils.SKIN_PATH, skinPath);
                    }

                    @Override
                    public void onFailed(String errMsg) {

                    }
                }, XMSDCardLoader.SKIN_LOADER_STRATEGY_SDCARD);
            }
        }
    }


    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
