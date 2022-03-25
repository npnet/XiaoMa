package com.xiaoma.app.views;

import android.app.Dialog;
import android.support.annotation.NonNull;
/**
 * This class is used to ensure that calls to dialog.show() are done without throwing an exception
 * Such calls could cause problems if the activity associated with the dialog is finishing
 * So we provide a show method with an optional activity parameter that can be used to protect against
 * erroneous calls to dialog.show() based on activity lifecycle, and an additional try/catch for protection
 */
public class SafeDialog {
    private Dialog dialog;

    public SafeDialog(@NonNull Dialog dialog) {
        this.dialog = dialog;
    }

    public void show() {
        try {
            dialog.show();
        } catch (RuntimeException activityFinished) {
        }
    }
}