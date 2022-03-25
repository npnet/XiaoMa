package com.xiaoma.bluetooth.phone.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.service.CallWindowService;
import com.xiaoma.bluetooth.phone.main.ui.MainActivity;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;

/**
 * Created by qiuboxiang on 2018/12/4 11:53
 */
public class RouteUtils {

    public static final int REQUEST_FLOAT_WINDOW_PERMISSION_CODE = 1000;
    public static final int DELAY_START_WINDOW_SERVICE = 50;

    public static void startWindowService(Activity activity, Fragment fragment) {
        if (fragment != null) {
            activity = fragment.getActivity();
        }
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(activity)) {
                showFloatWindow(activity);
            } else {
                showAlertDialog(activity, fragment);
            }
        } else {
            showFloatWindow(activity);
        }
    }

    public static void startWindowService(Activity activity) {
        startWindowService(activity, null);
    }

    public static void startWindowService(Fragment fragment) {
        startWindowService(null, fragment);
    }

    private static void showAlertDialog(final Activity activity, final Fragment fragment) {
        View view = View.inflate(activity, R.layout.dialog_request_window_permission, null);
        FragmentManager fragmentManager = (fragment != null) ? fragment.getFragmentManager() : ((MainActivity) activity).getSupportFragmentManager();
        final XmDialog builder = new XmDialog.Builder(fragmentManager)
                .setDimAmount(0.3f)
                .setGravity(Gravity.CENTER)
                .setView(view)
                .create();
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(activity.getString(R.string.system_alert_window_permission_request));

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.confirmRequestWindowPermission)
            @ResId(R.id.tv_confirm)
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                if (fragment != null) {
                    fragment.startActivityForResult(intent, REQUEST_FLOAT_WINDOW_PERMISSION_CODE);
                } else {
                    activity.startActivityForResult(intent, REQUEST_FLOAT_WINDOW_PERMISSION_CODE);
                }
                builder.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.cancelRequestWindowPermission)
            @ResId(R.id.tv_cancel)
            public void onClick(View v) {
                XMToast.showToast(activity, R.string.system_alert_window_permission_reject);
                builder.dismiss();
            }
        });
        builder.show();
    }

    public static void startWindowServiceDelayed(final Activity activity, Handler handler) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RouteUtils.startWindowService(activity);
            }
        }, DELAY_START_WINDOW_SERVICE);
    }

    private static void showFloatWindow(Activity activity) {
        PhoneStateManager.getInstance(activity).setWindowMode(true);
        activity.moveTaskToBack(false);
        Intent intent = new Intent(activity, CallWindowService.class);
        activity.startService(intent);
    }

    public static void stopWindowService(Activity activity) {
        PhoneStateManager.getInstance(activity).setWindowMode(false);
        Intent intent = new Intent(activity, CallWindowService.class);
        activity.stopService(intent);
    }

    public static void showMainActivity(Context context) {
        if (!AppUtils.isExistActivity(MainActivity.class, context)) {
            Log.d("RouteUtils","show MainActivity by intent");
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.d("RouteUtils","show MainActivity by moveTaskToFront");
            AppUtils.moveTaskToFront(context, context.getPackageName());
        }
    }
}
