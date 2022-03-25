package com.xiaoma.ui;

import android.view.View;
import android.view.Window;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ThreadUtils;

/**
 * @author youthyJ
 * @date 2018/9/30
 */
public class UIUtils {
    private UIUtils() throws Exception {
        throw new Exception();
    }

    public static void runOnMain(Runnable task) {
        if (task == null) {
            return;
        }
        if (ThreadUtils.isMainThread()) {
            task.run();
            return;
        }
        ThreadDispatcher.getDispatcher().postOnMain(task);
    }

    public static void hideNavigationBar(Window win) {
        hideNavigationBar(win, false);
    }

    public static void hideNavigationBar(Window win, boolean once) {
        if (win == null)
            return;
        final View decorView = win.getDecorView();
        if (decorView == null)
            return;
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        if (!once) {
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                }
            });
        }
    }
}
