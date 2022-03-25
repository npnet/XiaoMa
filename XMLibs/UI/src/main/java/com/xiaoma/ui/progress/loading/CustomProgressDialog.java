package com.xiaoma.ui.progress.loading;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoma.ui.R;
import com.xiaoma.utils.ScreenUtils;
import com.xiaoma.utils.StringUtil;

/**
 * User: vincenthu
 * Date: 13-12-24
 * Time: 13:10
 * Description:自定义ProgressDialog
 */
public class CustomProgressDialog extends Dialog {
    private ProgressBar progress;
    private TextView progressMessage;
    private boolean cancelable = true;

    public CustomProgressDialog(Activity context) {
        super(context, R.style.custom_dialog);
        setContentView(R.layout.view_progress_dialog);
        View outerView = findViewById(R.id.rl_content_view);
        outerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelable) {
                    cancel();
                }
            }
        });
        progress = findViewById(R.id.pb_progress);
        progressMessage = findViewById(R.id.tv_message);
        ProgressDrawable drawable = new ProgressDrawable(context, progress);
        progress.setIndeterminateDrawable(drawable);
    }

    public void resetContentViewMargin(int left, int top, int right, int bottom) {
        int screenWidth = ScreenUtils.getScreenWidth(getContext());
        int screenHeight = ScreenUtils.getScreenHeight(getContext());

        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.gravity = Gravity.NO_GRAVITY;

        attr.width = screenWidth - left - right;
        attr.height = screenHeight - top - bottom;
        attr.x = left;
        attr.y = top;
    }

    public void setMessage(String message) {
        if (StringUtil.isEmpty(message)) {
            progressMessage.setVisibility(View.GONE);

        } else {
            progressMessage.setVisibility(View.VISIBLE);
            progressMessage.setText(message);
        }
    }

    public void setMessage(int message) {
        setMessage(getContext().getString(message));
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }
}
