package com.xiaoma.skin.manager;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * Created by Thomas on 2018/12/19 0019
 */
public interface XmSkin {

    void initSkin(Application application);

    void init(Application application);

    void loadSkin();

    void loadSkin(XmSkinLoaderListener listener);

    void loadSkin(String skinFileName, int strategy);

    void loadSkin(String skinFileName, XmSkinLoaderListener listener, int strategy);

    void restoreDefaultTheme(XmSkinLoaderListener listener);

    String getCurSkinName();

    Resources getSkinResources();

    String getSkinPkgName();

    boolean isDefaultSkin();

    int getColor(int resId);

    Drawable getDrawable(int resId);

    Drawable getDrawable(int resId,Context context);

    ColorStateList getColorStateList(int resId);
}
