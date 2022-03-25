package com.xiaoma.skin.manager;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.xiaoma.skin.inflater.XmSkinAndroidSupportV7ViewInflater;
import com.xiaoma.skin.loader.XMSDCardLoader;

import skin.support.SkinCompatManager;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.content.res.SkinCompatResources;
import skin.support.design.app.SkinMaterialViewInflater;
import skin.support.load.SkinAssetsLoader;

/**
 * Created by Thomas on 2018/12/19 0019
 */

class XmSkinSupport implements XmSkin {
    private boolean mInit;

    @Override
    public synchronized void initSkin(Application application) {
        if (mInit)
            return;
        SkinCompatManager.withoutActivity(application)                  // 基础控件换肤初始化
                .addStrategy(new SkinAssetsLoader())                      // 配置sdcard加载策略
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .addInflater(new XmSkinAndroidSupportV7ViewInflater())  // Android v7常用view支持换肤
                .addStrategy(new XMSDCardLoader())                      // 自定义加载策略，指定SDCard路径
                .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(true)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
        mInit = true;
    }

    //初始化框架，关闭所有Activity皮肤换肤，需要换肤的Activity自己添加@Skinable
    public void init(Application application){
       initSkin(application);
       SkinCompatManager.getInstance().setSkinAllActivityEnable(false);
    }

    @Override
    public void loadSkin() {
        SkinCompatManager.getInstance().loadSkin();
    }

    @Override
    public void loadSkin(final XmSkinLoaderListener listener) {
        SkinCompatManager.getInstance().loadSkin(new SkinCompatManager.SkinLoaderListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailed(String errMsg) {
                listener.onFailed(errMsg);
            }
        });
    }

    @Override
    public void loadSkin(String skinFileName, int strategy) {
        SkinCompatManager.getInstance().loadSkin(skinFileName, strategy);
    }

    @Override
    public void loadSkin(String skinFileName, final XmSkinLoaderListener listener, int strategy) {
        SkinCompatManager.getInstance().loadSkin(skinFileName, new SkinCompatManager.SkinLoaderListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailed(String errMsg) {
                listener.onFailed(errMsg);
            }
        }, strategy);
    }


    public void restoreDefaultTheme(final XmSkinLoaderListener listener){
        SkinCompatManager.getInstance().loadSkin("", new SkinCompatManager.SkinLoaderListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailed(String errMsg) {
                listener.onFailed(errMsg);
            }
        },SkinCompatManager.SKIN_LOADER_STRATEGY_NONE);

    }    @Override
    public String getCurSkinName() {
        return SkinCompatManager.getInstance().getCurSkinName();
    }

    @Override
    public Resources getSkinResources() {
        return SkinCompatResources.getInstance().getSkinResources();
    }

    @Override
    public String getSkinPkgName() {
        return SkinCompatResources.getInstance().getSkinPkgName();
    }

    @Override
    public boolean isDefaultSkin() {
        return SkinCompatResources.getInstance().isDefaultSkin();
    }

    @Override
    public int getColor(int resId) {
        return SkinCompatResources.getInstance().getColor(resId);
    }

    @Override
    public Drawable getDrawable(int resId) {
        if(SkinCompatManager.getInstance() == null){
//            return context.getResources().getDrawable(resId);
        }
        return SkinCompatResources.getInstance().getDrawable(resId);
    }

    public Drawable getDrawable(int resId,Context context) {
        if(SkinCompatManager.getInstance() == null){
            return context.getResources().getDrawable(resId);
        }
        return SkinCompatResources.getInstance().getDrawable(resId);
    }

    @Override
    public ColorStateList getColorStateList(int resId) {
        return SkinCompatResources.getInstance().getColorStateList(resId);
    }
}
