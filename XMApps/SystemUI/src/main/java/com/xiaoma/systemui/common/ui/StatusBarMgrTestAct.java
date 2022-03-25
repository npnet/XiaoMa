package com.xiaoma.systemui.common.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.StatusBarManager;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import com.xiaoma.systemui.R;
import com.xiaoma.systemuilib.NotificationUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LKF on 2018/11/8 0008.
 */
public class StatusBarMgrTestAct extends Activity implements View.OnClickListener {
    private static final String STATUS_ICON_SLOT = "XMStatusBarTest";

    private StatusBarManager mStatusBarManager;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_status_bar_mgr_test);
        findViewById(R.id.btn_expand).setOnClickListener(this);
        findViewById(R.id.btn_collapse).setOnClickListener(this);
        findViewById(R.id.btn_add_icon).setOnClickListener(this);
        findViewById(R.id.btn_remove_icon).setOnClickListener(this);
        findViewById(R.id.btn_add_notify).setOnClickListener(this);
        findViewById(R.id.btn_remove_notify).setOnClickListener(this);
        mStatusBarManager = (StatusBarManager) getSystemService(Service.STATUS_BAR_SERVICE);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_expand:
                    mStatusBarManager.expandNotificationsPanel();
                    break;
                case R.id.btn_collapse:
                    mStatusBarManager.collapsePanels();
                    break;
                case R.id.btn_add_icon:
                    mStatusBarManager.setIcon(STATUS_ICON_SLOT, R.drawable.icon_default_icon, 0, STATUS_ICON_SLOT);
                    break;
                case R.id.btn_remove_icon:
                    mStatusBarManager.removeIcon(STATUS_ICON_SLOT);
                    break;
                case R.id.btn_add_notify:
                    addNotify();
                    break;
                case R.id.btn_remove_notify:
                    removeNotify();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNotify() {
        final Date date = new Date();
        /*final int dayOfMonth = new Random().nextInt(31) + 1;
        date.setDate(dayOfMonth);*/

        final int notificationId = Integer.parseInt(new SimpleDateFormat("MdHHmmss", Locale.CHINA).format(date.getTime()));
        final Icon icon = Icon.createWithResource(this, R.drawable.icon_default_icon);
        final String title = "通知标题: " + notificationId;
        final String text = "通知内容: " + notificationId;
        final long when = date.getTime();

        final Intent intent = new Intent(this, NotificationSkipAct.class)
                .putExtra(NotificationSkipAct.EXTRA_TEXT, text);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification n = NotificationUtil.builder(this, title, text, icon, pendingIntent, when, true).build();
        NotificationManagerCompat.from(this).notify(notificationId, n);
    }

    private void removeNotify() {
        final NotificationManager mgr = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        mgr.cancelAll();
    }
}
