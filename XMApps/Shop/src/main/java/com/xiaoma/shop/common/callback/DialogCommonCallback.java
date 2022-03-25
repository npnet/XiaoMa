package com.xiaoma.shop.common.callback;

import android.view.View;

import com.xiaoma.ui.dialog.XmDialog;

public interface DialogCommonCallback {

        int getLayoutId();

        void prepare(View view);

        void onConfirm();

        void onCancel();

        void onClick(View view, XmDialog xmDialog);
}