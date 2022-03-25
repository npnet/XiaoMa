package com.xiaoma.club.common.view;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.ui.dialog.XmDialog;

/**
 * Author: loren
 * Date: 2019/1/24 0024
 */

public class ClubConfirmDialog {

    private XmDialog dialog;
    private View view;

    public ClubConfirmDialog(FragmentActivity activity) {
        view = View.inflate(activity, R.layout.dialog_club_confirm, null);
        dialog = new XmDialog.Builder(activity)
                .setView(view)
                .create();
        setNegativeButton(activity.getString(R.string.club_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public ClubConfirmDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title) && view != null) {
            TextView textView = view.findViewById(R.id.confirm_dialog_title);
            textView.setText(title);
        }
        return this;
    }

    public ClubConfirmDialog setPositiveButton(String msg, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(msg) && view != null) {
            Button button = view.findViewById(R.id.btn_confirm_sure);
            button.setText(msg);
            button.setOnClickListener(listener);
        }
        return this;
    }

    public ClubConfirmDialog setNegativeButton(String msg, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(msg) && view != null) {
            Button button = view.findViewById(R.id.btn_confirm_cancel);
            button.setText(msg);
            button.setOnClickListener(listener);
        }
        return this;
    }

    public ClubConfirmDialog setCancelable(boolean cancelable) {
        if (dialog != null) {
            dialog.setCancelable(cancelable);
        }
        return this;
    }

    public ClubConfirmDialog setNegativeButtonVisibility(boolean visibility) {
        if (view != null) {
            Button button = view.findViewById(R.id.btn_confirm_cancel);
            if (visibility) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.GONE);
            }
        }
        return this;
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
