package com.xiaoma.update.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.Ignore;
import com.xiaoma.update.R;
import com.xiaoma.update.manager.AppUpdateManager;
import com.xiaoma.update.model.ApkVersionInfo;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.math.BigDecimal;

/**
 * Created by Thomas on 2018/10/15 0015
 */

public class AppUpdateDialog implements View.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener, DialogInterface.OnKeyListener {

    private View contentView;
    private ApkVersionInfo updateAppInfo;
    private ImageView appIcon;
    private TextView tvAppName;
    private TextView tvAppSize;
    private TextView tvUpdateInfo;
    private TextView btnForceUpdate;
    private TextView btnCancelUpdate;
    private TextView btnUpdate;
    private Context context;
    private OnUpdateDialogListener listener;
    private static final int ONE_ZERO_TWO_FOUR = 1024;
    private Dialog mAppUpdateDialog;

    public AppUpdateDialog(Context context) {
        this.context = context;
        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_update_app, null);
        appIcon = contentView.findViewById(R.id.app_icon);
        tvAppName = contentView.findViewById(R.id.app_name);
        tvAppSize = contentView.findViewById(R.id.app_size);
        tvUpdateInfo = contentView.findViewById(R.id.tv_update_info);
        btnForceUpdate = contentView.findViewById(R.id.btn_force_update);
        btnCancelUpdate = contentView.findViewById(R.id.btn_cancel_update);
        btnUpdate = contentView.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);
        btnCancelUpdate.setOnClickListener(this);
        btnForceUpdate.setOnClickListener(this);
        tvUpdateInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        mAppUpdateDialog = new Dialog(context, R.style.transparent_dialog);
        mAppUpdateDialog.setCanceledOnTouchOutside(false);
        mAppUpdateDialog.setCancelable(false);
        mAppUpdateDialog.setOnDismissListener(this);
        mAppUpdateDialog.setOnShowListener(this);
        mAppUpdateDialog.setOnKeyListener(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mAppUpdateDialog.addContentView(contentView, layoutParams);
    }

    private void setDialogSize(Dialog dialog, double preInWidth) {
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        int width = context.getResources().getDisplayMetrics().widthPixels;
//        int height = context.getResources().getDisplayMetrics().heightPixels;
        layoutParams.width = (int) (width * preInWidth);
//        layoutParams.height = (int) (height * preInHeight);
        dialog.getWindow().setAttributes(layoutParams);
    }

    public void setOnUpdateDialogListener(OnUpdateDialogListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public void setUpdateAppInfo(ApkVersionInfo updateAppInfo) {
        this.updateAppInfo = updateAppInfo;
        ImageLoader.with(context)
                .load(updateAppInfo.getIconPathUrl())
                .dontAnimate()
                .placeholder(R.drawable.icon_app_downding_place)
                .error(R.drawable.icon_app_downding_place)
                .into(appIcon);
        tvAppName.setText(updateAppInfo.getAppName());
        tvAppSize.setText(byteToM(updateAppInfo.getSize()));
        if (updateAppInfo.isIsForceUpdate()) {
            tvUpdateInfo.setText(R.string.force_update_content_null);
            btnForceUpdate.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            btnCancelUpdate.setText(R.string.quit_app);

        } else {
            tvUpdateInfo.setText(R.string.normal_update_content_null);
            btnUpdate.setVisibility(View.VISIBLE);
            btnForceUpdate.setVisibility(View.GONE);
            btnCancelUpdate.setText(R.string.wait_update);
        }
    }

    // 将字节转换为MB
    private static String byteToM(long byteSize) {
        BigDecimal fileSize = new BigDecimal(byteSize);
        BigDecimal megabyte = new BigDecimal(ONE_ZERO_TWO_FOUR * ONE_ZERO_TWO_FOUR);
        float returnValue = fileSize.divide(megabyte, 1, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1) {
            return (returnValue + "MB");
        }
        BigDecimal kilobyte = new BigDecimal(ONE_ZERO_TWO_FOUR);
        returnValue = fileSize.divide(kilobyte, 1, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "KB");
    }

    @Override
    @Ignore
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_update) {
            if (listener != null) {
                dismiss();
                listener.onSelectUpdate(updateAppInfo);
            }

        } else if (i == R.id.btn_cancel_update) {
            if (updateAppInfo.isIsForceUpdate()) {
                //回到桌面
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                context.startActivity(homeIntent);
                mAppUpdateDialog.dismiss();
                AppUpdateManager.killProcess();

            } else {
                mAppUpdateDialog.dismiss();
                AppUpdateManager.killProcess();
            }

        } else if (i == R.id.btn_force_update) {
            if (listener != null) {
                listener.onForceUpdate(updateAppInfo);
            }
        }
    }

    public void show() {
        if (mAppUpdateDialog != null) {
            setDialogSize(mAppUpdateDialog, 0.34f);
            mAppUpdateDialog.show();
        }
        if (!updateAppInfo.isIsForceUpdate()) {
            TPUtils.put(context, context.getPackageName() + AppUpdateManager.CANCEL_DATE_KEY, StringUtil.getDateByYMD());
        }
    }

    public void setForceUpdateClickable(boolean clickable) {
        if (btnForceUpdate != null & !clickable) {
            btnForceUpdate.setClickable(false);
            btnForceUpdate.setText(R.string.update_downloading);
        }
    }

    public void dismiss() {
        if (mAppUpdateDialog != null) {
            mAppUpdateDialog.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            ((Activity) context).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {

    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }


    public interface OnUpdateDialogListener {
        void onSelectUpdate(ApkVersionInfo updateAppInfo);

        void onForceUpdate(ApkVersionInfo updateAppInfo);
    }
}
