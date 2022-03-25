package com.xiaoma.dualscreen.presentation;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import com.xiaoma.dualscreen.listener.PresentationDataListener;

/**
 * @author: iSun
 * @date: 2018/12/29 0029
 */
public abstract class BasePresentation extends Presentation implements PresentationDataListener {

    public BasePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public BasePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
    }


    protected abstract void bindData();

    protected abstract void bindView();

    public void refresh() {
        bindData();
        if (!isShowing()) {
            show();
        }
    }

    @Override
    public void show() {
        if (Settings.canDrawOverlays(getContext())) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            super.show();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getContext().startActivity(intent);
        }
    }
}
