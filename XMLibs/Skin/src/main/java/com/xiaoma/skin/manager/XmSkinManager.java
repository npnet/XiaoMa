package com.xiaoma.skin.manager;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.utils.SkinUtils;


/**
 * Created by ZhangYao.
 * Date ：2018/9/11 0011
 */
public class XmSkinManager {

    public static XmSkinManager getInstance() {
        return XmSkinHolder.instance;
    }

    private static class XmSkinHolder {
        static final XmSkinManager instance = new XmSkinManager();
    }

    private XmSkin getSkinHolder() {
        return new SkinFactory().createSkinSupport();
    }

    public void initSkin(Application application) {
        getSkinHolder().initSkin(application);
    }

    public void init(Application application){
        getSkinHolder().init(application);
    }
    public void loadSkin() {
        getSkinHolder().loadSkin();
    }

    public void loadSkin(XmSkinLoaderListener listener) {
        getSkinHolder().loadSkin(listener);
    }

    public void loadSkin(String skinFileName, int strategy) {
        getSkinHolder().loadSkin(skinFileName, strategy);
    }

    public void loadSkinByPath(Context context, String skinId, String skinPath, int skinStyle) {
        SkinUtils.saveSkinMsg(SkinUtils.TYPE_PATH, "", skinPath, skinId, skinStyle);
        SkinUtils.sendBroadcast(context, SkinConstants.SKIN_ACTION_XM);
    }

    /**
     * @return 皮肤id
     */
    public int loadSkinByName(Context context, String skinId, String skinName) {
        int themeId = SkinConstants.THEME_ZHIHUI;
        SkinUtils.saveSkinMsg(SkinUtils.TYPE_NAME, skinName, "", skinId, -1);
        String action = null;
        if (SkinConstants.SKIN_NAME_DAOMENG.equals(skinName)) {
            action = SkinConstants.SKIN_ACTION_DAOMENG;
            themeId = SkinConstants.THEME_DAOMENG;
        } else if (SkinConstants.SKIN_NAME_QINGSHE.equals(skinName)) {
            action = SkinConstants.SKIN_ACTION_QINGSHE;
            themeId = SkinConstants.THEME_QINGSHE;

        }
        SkinUtils.sendBroadcast(context, action);
        return themeId;
    }

    public void loadSkin(String skinFileName, XmSkinLoaderListener listener,int strategy) {
        getSkinHolder().loadSkin(skinFileName, listener, strategy);
    }

    public void restoreDefault(Context context){
        SkinUtils.saveSkinMsg(SkinUtils.TYPE_DEFAULT,"","","",-1);
        SkinUtils.sendBroadcast(context,SkinConstants.SKIN_ACTION);
    }

    public String getCurSkinName() {
        return getSkinHolder().getCurSkinName();
    }

    public Resources getSkinResources() {
        return getSkinHolder().getSkinResources();
    }

    public String getSkinPkgName() {
        return getSkinHolder().getSkinPkgName();
    }

    public boolean isDefaultSkin() {
        return getSkinHolder().isDefaultSkin();
    }

    public int getColor(int resId) {
        return getSkinHolder().getColor(resId);
    }

    public Drawable getDrawable(int resId) {
        return getSkinHolder().getDrawable(resId);
    }

    public Drawable getDrawable(int resId,Context context) {
        return getSkinHolder().getDrawable(resId,context);
    }
    public ColorStateList getColorStateList(int resId) {
        return getSkinHolder().getColorStateList(resId);
    }

}
