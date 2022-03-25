package com.xiaoma.service.common.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;

import com.xiaoma.service.R;
import com.xiaoma.service.order.ui.OrderActivity;
import com.xiaoma.systemuilib.NotificationUtil;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author taojin
 * @date 2019/3/27
 */
public class CarServiceNotificationHelper {


    private static CarServiceNotificationHelper instance;
    private int requestId = 0;
    private int maxIdSize = 10000;
    private String packageName = "com.xiaoma.service";


    /**
     * 获取单例
     *
     * @return 单例对象
     */
    public static CarServiceNotificationHelper getInstance() {
        if (instance == null) {
            instance = new CarServiceNotificationHelper();
        }
        return instance;
    }


    public void handleCarServiceNotification(Context context, String title, String content) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ServicedModel servicedModel = new ServicedModel();
        servicedModel.setTitle(title);
        servicedModel.setContent(content);
        servicedModel.setJumpIntent(intent);

        show(context, servicedModel, IconCompat.createWithResource(context, R.drawable.icon_app_service).toIcon());

    }

    public void handOrderNotification(Context context, String date, String time, String title, String content) {

        sendServiceNotificationBroadcast(context, date, time, title, content);
    }


    /**
     * 显示通知栏
     *
     * @param context 上下文
     * @param model   通知栏数据模型
     */
    private void show(final Context context, final ServicedModel model, final Icon icon) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                //获取PendingIntent
                if (requestId > maxIdSize) {
                    requestId = 0;
                }
                Notification notification;
                if (model.jumpIntent != null) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId++,
                            model.jumpIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    notification = NotificationUtil.builder(context, model.title,
                            model.content, icon, pendingIntent,
                            System.currentTimeMillis(), true).setAutoCancel(true).build();
                } else {
                    notification = NotificationUtil.builder(context, model.title,
                            model.content, icon, null,
                            System.currentTimeMillis(), true).setAutoCancel(true).build();
                }

                NotificationManagerCompat.from(context).notify(packageName, packageName.hashCode(), notification);

            }
        });
    }


    private void sendServiceNotificationBroadcast(Context context, String startDate, String startTime, String title, String content) {
        String[] time = startTime.split("-");

        Intent intent = new Intent();
        intent.putExtra("packageName", "com.xiaoma.service");
        intent.putExtra("className", "com.xiaoma.service.order.ui.OrderListActivity");
        intent.putExtra("startDate", dateConvert(startDate));
        intent.putExtra("startTime", time[0]);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.setAction("com.xiaoma.service.notification");

        context.sendBroadcast(intent);
    }


    private String dateConvert(String date) {
        Date tempDate = TimeUtils.string2Date(date, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));

        return TimeUtils.date2String(tempDate, new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()));
    }

    public class ServicedModel {
        private String title;
        private String content;
        private Intent jumpIntent;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Intent getJumpIntent() {
            return jumpIntent;
        }

        public void setJumpIntent(Intent jumpIntent) {
            this.jumpIntent = jumpIntent;
        }
    }

}
