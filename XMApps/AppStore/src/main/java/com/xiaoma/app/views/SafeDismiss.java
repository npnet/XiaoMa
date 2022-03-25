package com.xiaoma.app.views;

import android.content.DialogInterface;


/**
 * Avoids "IllegalArgumentException: View=com.android.internal.policy.impl.PhoneWindow$DecorView...
 * not attached to window manager"
 */
public class SafeDismiss {

    private final DialogInterface dialog;

    public SafeDismiss(DialogInterface dialog) {
        this.dialog = dialog;
    }

    public void dismiss() {
        // Run on the UI thread as otherwise Dialog will post it and you will not be able to catch the exception.
        CoreManager.getThreadDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        });
    }

    private void dismissDialog() {
        try {
            dialog.dismiss();
        } catch (RuntimeException ex) {

        }
    }
}
