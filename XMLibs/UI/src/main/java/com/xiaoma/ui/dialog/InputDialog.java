package com.xiaoma.ui.dialog;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ui.R;

/**
 * author: andy、JF
 * date:   2019/6/5 20:14
 * desc:
 */
public class InputDialog {
    private XmDialog dialog;
    private View view;
    private EditText editText;
    private TextView confirmView;
    private OnConfirmClickListener mOnConfirmClickListener;
    private OnCancelClickListener mOnCancelClickListener;

    public InputDialog(FragmentActivity activity) {
        view = View.inflate(activity, R.layout.dialog_input, null);
        editText = view.findViewById(R.id.blt_name_et);
        confirmView = view.findViewById(R.id.confirm);
        dialog = new XmDialog.Builder(activity)
                .setView(view)
                .create();
        setNegativeButton("取消", new OnCancelClickListener() {
            @Override
            public void onCancelClick() {
                dialog.dismiss();
            }
        });

        initEvent();
    }

    private void initEvent() {
        if (view != null) {
            final EditText editText = view.findViewById(R.id.blt_name_et);
            view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnConfirmClickListener != null) {
                        mOnConfirmClickListener.onConfirmClick(editText.getText().toString());
                    }
                }
            });

            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCancelClickListener != null) {
                        mOnCancelClickListener.onCancelClick();
                    }
                }
            });
        }
    }


    public InputDialog setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title) && view != null) {
            TextView textView = view.findViewById(R.id.title);
            textView.setText(title);
        }
        return this;
    }

    public InputDialog setPositiveButton(String msg, OnConfirmClickListener listener) {
        if (!TextUtils.isEmpty(msg) && view != null) {
            TextView button = view.findViewById(R.id.confirm);
            button.setText(msg);
            this.mOnConfirmClickListener = listener;

        }
        return this;
    }

    public InputDialog setNegativeButton(String msg, OnCancelClickListener listener) {
        if (!TextUtils.isEmpty(msg) && view != null) {
            TextView button = view.findViewById(R.id.cancel);
            button.setText(msg);
            this.mOnCancelClickListener = listener;
        }
        return this;
    }

    public InputDialog setCancelable(boolean cancelable) {
        if (dialog != null) {
            dialog.setCancelable(cancelable);
        }
        return this;
    }

    public EditText getEditText() {
        return this.editText;
    }

    public TextView getConfirmView() {
        return this.confirmView;
    }

    public void setSoftInputMode(int focusType) {
        dialog.getDialog().getWindow().setSoftInputMode(focusType);
    }

    public InputDialog setNegativeButtonVisibility(boolean visibility) {
        if (view != null) {
            TextView cancel_button = view.findViewById(R.id.cancel);
            TextView confirm_button = view.findViewById(R.id.confirm);
            ImageView imageView = view.findViewById(R.id.image_divide_vertical);

            if (visibility) {
                cancel_button.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                cancel_button.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                confirm_button.setBackgroundResource(R.drawable.dialog_bg_button_long);
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


    public interface OnConfirmClickListener {
        public void onConfirmClick(String editext);
    }

    public interface OnCancelClickListener {
        public void onCancelClick();
    }

}
