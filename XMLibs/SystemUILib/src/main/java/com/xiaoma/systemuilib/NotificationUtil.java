package com.xiaoma.systemuilib;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.v4.graphics.drawable.IconCompat;
import android.widget.RemoteViews;

import com.xiaoma.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by LKF on 2019-3-22 0022.
 */
public class NotificationUtil {

    public static Notification.Builder builder(Context context,
                                               String title, String content, Icon icon, PendingIntent intent) {
        return builder(context, title, content, icon, intent, System.currentTimeMillis());
    }

    public static Notification.Builder builder(Context context,
                                               String title, String content, Icon icon, PendingIntent intent,
                                               long when) {
        return builder(context, title, content, icon, intent, when, false);
    }

    public static Notification.Builder builder(Context context,
                                               String title, String content, Icon icon, PendingIntent intent,
                                               long when, boolean showHeadsUp) {
        final String channelId = "NChannel_" + context.getPackageName();
        final String channelName = AppUtils.getAppName(context);
        return builder(context, channelId, channelName,
                title, content, icon, intent,
                when, showHeadsUp);
    }

    public static Notification.Builder builder(Context context, String channelId, String channelName,
                                               String title, String content, Icon icon, PendingIntent intent,
                                               long when, boolean showHeadsUp) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (nm == null)
            return null;
        final Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT));
            builder = new Notification.Builder(context, channelId);
        } else {
            builder = new Notification.Builder(context);
        }
        final Icon smallIcon = IconCompat.createWithResource(context, context.getApplicationInfo().icon).toIcon();
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setLargeIcon(icon)
                .setWhen(when)
                .setAutoCancel(true);
        if (intent != null) {
            builder.setContentIntent(intent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && showHeadsUp) {
            final RemoteViews headsupView = new RemoteViews(context.getPackageName(), R.layout.headsup_sample);
            headsupView.setImageViewIcon(R.id.iv_icon, icon);
            headsupView.setTextViewText(R.id.tv_title, title);
            headsupView.setTextViewText(R.id.tv_text, content);
            headsupView.setTextViewText(R.id.tv_time, getHeadsUpTimeDisplay(context, when));
            builder.setCustomHeadsUpContentView(headsupView);
        }
        return builder;
    }

    public static String getHeadsUpTimeDisplay(Context context, long time) {
        final long currTime = System.currentTimeMillis();
        final long millisInterval = currTime - time;

        final long minutesInterval = TimeUnit.MINUTES.convert(millisInterval, TimeUnit.MILLISECONDS);
        if (minutesInterval < 1) {
            // 1分钟内
            return context.getString(R.string.notification_headsup_time_display_just);
        }
        if (minutesInterval < 60) {
            // 1小时内
            return minutesInterval + context.getString(R.string.notification_headsup_time_display_minute);
        }
        final long hoursInterval = TimeUnit.HOURS.convert(minutesInterval, TimeUnit.MINUTES);
        if (hoursInterval < 24) {
            // 24小时内
            return hoursInterval + context.getString(R.string.notification_headsup_time_display_hour);
        }
        final long daysInterval = TimeUnit.DAYS.convert(hoursInterval, TimeUnit.HOURS);
        if (daysInterval < 4) {
            // 4天内
            return daysInterval + context.getString(R.string.notification_headsup_time_display_day);
        }
        final Date msgDate = new Date(time);
        final Date today = new Date(currTime);
        if (msgDate.getYear() == today.getYear()) {
            // 1年内
            return new SimpleDateFormat("MM-dd", Locale.CHINA).format(msgDate);
        }
        // 不在同一年
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(msgDate);
    }
}