package com.xiaoma.login.business.ui.infoview;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.login.R;


/**
 * @author Gillben
 * date: 2018/12/03
 */
public abstract class BasePersonalInfoDialog extends DialogFragment implements View.OnClickListener {

    private static final int DIALOG_WIDTH = 560;
    private static final int DIALOG_HEIGHT = 240;

    protected OnDialogCallback onDialogCallback;
    private TextView tvSure;
    private TextView tvCancel;
    private TextView tvTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.custom_dialog2);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.dialog_personal_info, container, false);
        LinearLayout ContentContainer = inflateView.findViewById(R.id.content_container);
        if (contentLayoutId() > 0) {
            inflater.inflate(contentLayoutId(), ContentContainer, true);
        } else if (contentView() != null) {
            ContentContainer.addView(contentView());
        }
        return inflateView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(isCancelableOutside());
        initView(view);
        initClickListener(view);
        onBindView(view);
    }

    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvSure = view.findViewById(R.id.tv_sure);
        tvCancel = view.findViewById(R.id.tv_cancel);

        tvTitle.setText(getTitle());
    }

    private void initClickListener(@NonNull View view) {
        tvSure.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        if (getClickViewIds() != null) {
            for (int id : getClickViewIds()) {
                view.findViewById(id).setOnClickListener(this);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setAttributes(changeWindowParams(lp));
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    protected abstract int contentLayoutId();

    protected View contentView() {
        return null;
    }

    protected abstract String getTitle();

    /**
     * 绑定View的回调
     *
     * @param view root view
     */
    protected abstract void onBindView(View view);

    /**
     * 是否可点击外围区域dismiss
     *
     * @return boolean
     */
    protected abstract boolean isCancelableOutside();

    /**
     * 确定事件被触发
     */
    protected abstract void onSure();

    /**
     * cancel事件是否被消费
     *
     * @return 是否被消费
     */
    protected boolean onCancel() {
        return false;
    }

    /**
     * @param lp 需要改变Windows
     * @return
     */
    protected WindowManager.LayoutParams changeWindowParams(WindowManager.LayoutParams lp) {
        lp.width = DIALOG_WIDTH;
        lp.height = DIALOG_HEIGHT;
        return lp;
    }

    /**
     * view点击的分发
     *
     * @param view 被点击的View
     */
    protected void onViewClick(View view) {

    }

    protected int[] getClickViewIds() {
        return null;
    }

    public View getSureButton() {
        return tvSure;
    }

    public final void setOnDialogCallback(OnDialogCallback callback) {
        this.onDialogCallback = callback;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_sure) {
            onSure();
        } else if (i == R.id.tv_cancel) {
            if (!onCancel()) {
                dismiss();
            }
        } else {
            onViewClick(v);
        }
    }


    public interface OnDialogCallback {
        void success(String content);
    }

    protected void onSuccessResult(String content) {
        if (onDialogCallback != null) {
            onDialogCallback.success(content);
        }
    }

}
