package com.xiaoma.assistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IAssistantViewListener;
import com.xiaoma.assistant.ui.adapter.AssistantAdapter;
import com.xiaoma.vr.model.SeoptType;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/26
 * Desc：语音界面UI基类
 */
public abstract class BaseAssistantDialog {
    protected Context context;
    protected Dialog mVoiceDialog;
    protected RelativeLayout mRlRootView;
    protected long startTime;
    protected long secondLayoutStartTime;
    //当前的dialog会话id
    protected long dialogSession;
    protected boolean isFirstAlertUnWork = false;
    protected boolean isFullScreenDialog = false;
    protected boolean isShowingHelp = false;
    protected boolean mIsShowHalfScreen = false;
    protected IAssistantViewListener listener;
    protected AssistantAdapter mAdapter;

    public abstract boolean createView(Context context);

    public abstract void show(SeoptType localSeoptType);

    public abstract void setAdapter(List<com.xiaoma.vr.model.ConversationItem> list);

    public abstract void notifyDataSetChanged();

    public abstract void setAssistantListener(IAssistantViewListener listener);

    public abstract void closeView();


    public long getDialogSession() {
        return dialogSession;
    }

    public void setDialogSession(long dialogSession) {
        this.dialogSession = dialogSession;
    }

    public boolean showHalfScreenDialog() {
        mIsShowHalfScreen = true;
        mRlRootView.setBackgroundResource(R.drawable.bg_assistant_dialog);
        return setDialogSize(mVoiceDialog, 0.5f, 1.0f, false);
    }

    public boolean showFullScreenDialog() {
        mIsShowHalfScreen = false;
        mRlRootView.setBackgroundResource(R.drawable.bg_assistant_dialog);
        return setDialogSize(mVoiceDialog, 1.0f, 1.0f, true);
    }

    private boolean setDialogSize(Dialog dialog, double preInWidth, double preInHeight, boolean isFullScreenDialog) {
        if (dialog == null || dialog.getWindow() == null) {
            return false;
        }
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.LEFT;
        int width = context.getResources().getDisplayMetrics().widthPixels;
        //不对高度处理是因为已经全屏，此处计算的高度包含了标题栏的高度，xml中进行padding和margin会不准确
        int height = context.getResources().getDisplayMetrics().heightPixels;
        lp.width = (int) (width * preInWidth);
        lp.height = (int) (height * preInHeight);
        if (lp.width == 1755) {
            lp.width = 1920;
        }
        dialog.getWindow().setAttributes(lp);
        this.isFullScreenDialog = isFullScreenDialog;
        if (isFullScreenDialog) {
            hideSystemUI(dialog.getWindow().getDecorView());
        }
        return true;
    }

    public void hideSystemUI(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    public String getString(int id) {
        if (id < 0 || context == null) {
            return "";
        }

        return context.getApplicationContext().getString(id);
    }

}
