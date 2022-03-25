package com.xiaoma.ui.dialog.impl;

import android.support.annotation.StringRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoma.ui.R;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/21
 */
public class XMMiddleTextDialog extends XMDialogImpl {
    private TextView mMsgTV;
    private TextView mLeftTV;
    private TextView mRightTV;
    private TextView mTitleTV;

    private String mMsgStr;
    @StringRes
    private int mMsgStrRes;
    @StringRes
    private int mTitleStrRes;
    @StringRes
    private int mLeftStrRes;
    @StringRes
    private int mRightStrRes;

    private IOnDialogClickListener mLeftClickListener, mRightClickListener;

    public static XMMiddleTextDialog newInstance() {
        return new XMMiddleTextDialog();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_fragment_middle_text;
    }

    @Override
    public void bindView(View view) {
        mMsgTV = view.findViewById(R.id.tvMsg);
        mTitleTV = view.findViewById(R.id.tvTitle);
        mLeftTV = view.findViewById(R.id.tvLeft);
        mRightTV = view.findViewById(R.id.tvRight);

        if (isValidResId(mMsgStrRes)) {
            setText(mMsgTV, mMsgStrRes);
        } else {
            setText(mMsgTV, mMsgStr);
        }
        setText(mTitleTV, mTitleStrRes);
        setText(mLeftTV, mLeftStrRes);
        setText(mRightTV, mRightStrRes);

        mLeftTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (mLeftClickListener != null) {
                    mLeftClickListener.onDialogClick(mLeftTV);
                }
            }
        });


        mRightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (mRightClickListener != null) {
                    mRightClickListener.onDialogClick(mRightTV);
                }
            }
        });
    }

    @Override
    protected String getDialogTag() {
        return XMMiddleTextDialog.class.getSimpleName();
    }

    @Override
    public int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public XMMiddleTextDialog setTitle(@StringRes int strRes) {
        this.mTitleStrRes = strRes;
        setText(mTitleTV, strRes);
        return this;
    }

    public XMMiddleTextDialog setMsg(@StringRes int strRes) {
        mMsgStr = null;
        mMsgStrRes = strRes;
        setText(mMsgTV, strRes);
        return this;
    }

    public XMMiddleTextDialog setMsg(String msg) {
        mMsgStrRes = 0;
        mMsgStr = msg;
        setText(mMsgTV, msg);
        return this;
    }

    public XMMiddleTextDialog setLeftClickListener(@StringRes int strRes, IOnDialogClickListener listener) {
        mLeftStrRes = strRes;
        mLeftClickListener = listener;
        setText(mLeftTV, strRes);
        return this;
    }

    public XMMiddleTextDialog setRightClickListener(@StringRes int strRes, IOnDialogClickListener listener) {
        mRightStrRes = strRes;
        setText(mRightTV, strRes);
        mRightClickListener = listener;
        return this;
    }

    public void updateMsg(@StringRes int strRes) {
        setMsg(strRes);
    }
}
