package com.xiaoma.ui.dialog;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ui.R;

/**
 * author: andy、JF
 * date:   2019/5/16 17:04
 * desc:
 */
public class ConfirmDialog {
    private XmDialog dialog;
    private View view;

    public ConfirmDialog(FragmentActivity activity) {
        view = View.inflate(activity, R.layout.dialog_confirm_ui, null);
        dialog = new XmDialog.Builder(activity)
                .setView(view)
                .create();
        setNegativeButton(activity.getString(R.string.dialog_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public ConfirmDialog(FragmentActivity activity, boolean cancelOutside) {
        view = View.inflate(activity, R.layout.dialog_confirm_ui, null);
        dialog = new XmDialog.Builder(activity)
                .setView(view)
                .setCancelableOutside(cancelOutside)
                .create();
        setNegativeButton(activity.getString(R.string.dialog_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public ConfirmDialog setContent(CharSequence content) {
        if (!TextUtils.isEmpty(content) && view != null) {
            TextView textView = view.findViewById(R.id.confirm_dialog_content);
            textView.setText(content);
        }
        return this;
    }

    public ConfirmDialog setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title) && view != null) {
            TextView textView = view.findViewById(R.id.confirm_dialog_title);
            textView.setText(title);
        }
        return this;
    }

    public ConfirmDialog setPositiveButton(String msg, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(msg) && view != null) {
            Button button = view.findViewById(R.id.btn_confirm_sure);
            button.setText(msg);
            button.setOnClickListener(listener);
        }
        return this;
    }

    public ConfirmDialog setNegativeButton(String msg, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(msg) && view != null) {
            Button button = view.findViewById(R.id.btn_confirm_cancel);
            button.setText(msg);
            button.setOnClickListener(listener);
        }
        return this;
    }

    public ConfirmDialog setCancelable(boolean cancelable) {
        if (dialog != null) {
            dialog.setCancelable(cancelable);
        }
        return this;
    }

    public ConfirmDialog setNegativeButtonVisibility(boolean visibility) {
        if (view != null) {
            Button cancel_button = view.findViewById(R.id.btn_confirm_cancel);
            Button sure_button = view.findViewById(R.id.btn_confirm_sure);
            ImageView imageView = view.findViewById(R.id.image_divide_vertical);

            if (visibility) {
                cancel_button.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                cancel_button.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                sure_button.setBackgroundResource(R.drawable.dialog_bg_button_long);
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

    public boolean isShow() {
        return dialog != null && dialog.isAdded();
    }

    public View getView() {
        return view;
    }
}
