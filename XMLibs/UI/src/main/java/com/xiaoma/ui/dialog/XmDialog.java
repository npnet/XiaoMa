package com.xiaoma.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.xiaoma.model.annotation.PageDescComponent;

/**
 * <pre>
 *     author : wutao
 *     e-mail : ldlywt@163.com
 *     time   : 2018/08/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@PageDescComponent("XmDialog")
public class XmDialog extends AbsDialogFragment implements View.OnClickListener {
    private static final String KEY_CONTROLLER = "Controller";
    protected Controller mController;
    private OnActivityResult onActivityResult;

    public XmDialog() {
        mController = new Controller();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            mController = (Controller) savedInstanceState.getSerializable(KEY_CONTROLLER);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = 0;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mController.getOnDismissListener() != null) {
            mController.getOnDismissListener().onDismiss(dialog);
        }
    }

    @Override
    public XmDialog show() {
//        mController
//                .getFragmentManager()
//                .beginTransaction()
//                .add(this, mController.getTag())
//                .commitAllowingStateLoss();
        show(mController.getFragmentManager(), mController.getTag());
        return this;

    }

    @Override
    public int getGravity() {
        return mController.getGravity();
    }

    @Override
    public float getDimAmount() {
        return mController.getDimAmount();
    }

    @Override
    public int getDialogHeight() {
        return mController.getHeight();
    }

    @Override
    public int getDialogWidth() {
        return mController.getWidth();
    }

    @Override
    public String getFragmentTag() {
        return mController.getTag();
    }

    @Override
    public void onClick(View view) {
        mController.getOnViewClickListener().onViewClick(view, this);
    }


    @Override
    protected int getLayoutRes() {
        return mController.getLayoutRes();
    }

    @Override
    protected View getDialogView() {
        return mController.getDialogView();
    }

    @Override
    protected boolean isCancelableOutside() {
        return mController.isCancelableOutside();
    }

    @Override
    protected int getDialogAnimationRes() {
        return mController.getDialogAnimationRes();
    }

    public static class Builder {

        Controller.Params params;

        public Builder(FragmentActivity activity) {
            params = new Controller.Params();
            params.mFragmentManager = activity.getSupportFragmentManager();

        }

        public Builder(FragmentManager fragmentManager) {
            params = new Controller.Params();
            params.mFragmentManager = fragmentManager;
        }

        public Builder setLayoutRes(@LayoutRes int layoutRes) {
            params.mLayoutRes = layoutRes;
            return this;
        }

        public Builder setView(View view) {
            params.mDialogView = view;
            return this;
        }

        public Builder setWidth(int widthPx) {
            params.mWidth = widthPx;
            return this;
        }

        public Builder setHeight(int heightPx) {
            params.mHeight = heightPx;
            return this;
        }

        /**
         * 设置弹窗宽度是屏幕宽度的比例 0 -1
         */
        public Builder setScreenWidthAspect(Context context, float widthAspect) {
            params.mWidth = (int) (getScreenWidth(context) * widthAspect);
            return this;
        }

        /**
         * 设置弹窗高度是屏幕高度的比例 0 -1
         */
        public Builder setScreenHeightAspect(Context context, float heightAspect) {
            params.mHeight = (int) (getScreenHeight(context) * heightAspect);
            return this;
        }

        public Builder setGravity(int gravity) {
            params.mGravity = gravity;
            return this;
        }

        public Builder setCancelableOutside(boolean cancel) {
            params.mIsCancelableOutside = cancel;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
            params.mOnDismissListener = dismissListener;
            return this;
        }

        /**
         * 设置弹窗背景透明度(0-1f)
         *
         * @param dim
         * @return
         */
        public Builder setDimAmount(float dim) {
            params.mDimAmount = dim;
            return this;
        }

        public Builder setTag(String tag) {
            params.mTag = tag;
            return this;
        }

        public Builder addOnClickListener(int... ids) {
            params.ids = ids;
            return this;
        }

        public Builder setOnViewClickListener(OnViewClickListener listener) {
            params.mOnViewClickListener = listener;
            return this;
        }

        public Builder setDialogAnimationRes(int animationRes) {
            params.mDialogAnimationRes = animationRes;
            return this;
        }

        public XmDialog create() {
            XmDialog dialog = new XmDialog();
            params.apply(dialog.mController);
            return dialog;
        }
    }

    @Override
    public void bindView(View view) {
        if (null != mController.getIds() && mController.getIds().length > 0) {
            for (int viewId : mController.getIds()) {
                view.setClickable(true);
                view.findViewById(viewId).setOnClickListener(this);
            }
        }
    }

    public void setOnActivityResult(OnActivityResult callback) {
        onActivityResult = callback;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (onActivityResult != null) {
            onActivityResult.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    public interface OnActivityResult {
        void onActivityResult(XmDialog dialog, int requestCode, int resultCode, Intent data);
    }
}
