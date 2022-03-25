package com.xiaoma.app.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xiaoma.app.R;
import com.xiaoma.app.model.NotificationModel;
import com.xiaoma.app.ui.activity.AppDetailsActivity;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.systemuilib.NotificationUtil;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.HashMap;

/**
 * @author taojin
 * @date 2019/3/27
 */
public class AppNotificationHelper {


    private static AppNotificationHelper instance;
    private int requestId = 0;
    private int maxIdSize = 10000;
    //应用升级提醒
    public static final int APP_UPDATE_TIP = 8001;
    //应用升级完成
    public static final int APP_UPDATE_COMPLETE = 8002;
    //应用安装失败
    public static final int APP_INSTALL_FAILED = 8003;
    //应用下载完成
    public static final int APP_DOWNLOAD_COMPLETE = 8004;

    private HashMap<String, String> appHashMap = new HashMap<>();


    /**
     * 获取单例
     *
     * @return 单例对象
     */
    public static AppNotificationHelper getInstance() {
        if (instance == null) {
            instance = new AppNotificationHelper();
        }
        return instance;
    }

    public void putAppInfo(String packageName, String iconUrl) {
        if (appHashMap == null) {
            return;
        }

        appHashMap.put(packageName, iconUrl);
    }


    public void handleAppNotification(Context context, int type, String iconUrl, String title, String content, String packageName, long time) {
        Intent intent;
        NotificationModel notificationModel;
        switch (type) {
            case APP_UPDATE_TIP:
                intent = new Intent(context, AppDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ConfigConstants.APP_STORE_TYPE_KEY, ConfigConstants.APP_STORE_TYPE_XIAOMA_APP);
                intent.putExtra(ConfigConstants.APP_STORE_PACKAGENAME_KEY, packageName);
                notificationModel = createNotificationModel(intent, iconUrl, title, content, packageName, time);
                requestImgAndNotify(context, notificationModel);
                break;
            case APP_UPDATE_COMPLETE:
                PackageManager pm = context.getPackageManager();
                intent = pm.getLaunchIntentForPackage(packageName);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                notificationModel = createNotificationModel(intent, iconUrl, title, content, packageName, time);
                requestImgAndNotify(context, notificationModel);
                break;
            case APP_INSTALL_FAILED:
                intent = new Intent(context, AppDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ConfigConstants.APP_STORE_TYPE_KEY, ConfigConstants.APP_STORE_TYPE_XIAOMA_APP);
                intent.putExtra(ConfigConstants.APP_STORE_PACKAGENAME_KEY, packageName);
                notificationModel = createNotificationModel(intent, iconUrl, title, content, packageName, time);
                requestImgAndNotify(context, notificationModel);
                break;
            case APP_DOWNLOAD_COMPLETE:
                intent = new Intent(context, AppDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ConfigConstants.APP_STORE_TYPE_KEY, ConfigConstants.APP_STORE_TYPE_XIAOMA_APP);
                intent.putExtra(ConfigConstants.APP_STORE_PACKAGENAME_KEY, packageName);
                notificationModel = createNotificationModel(intent, iconUrl, title, content, packageName, time);
                requestImgAndNotify(context, notificationModel);
                break;
        }
    }


    /**
     * 请求通知栏图标，显示通知栏
     *
     * @param context           上下文
     * @param notificationModel 封装通知栏内容数据模型
     */
    public void requestImgAndNotify(final Context context, final NotificationModel notificationModel) {
        try {
            Glide.with(context)
                    .download(StringUtil.isEmpty(notificationModel.getPicUrl()) ? appHashMap.get(notificationModel.getPackageName()) : notificationModel.getPicUrl())
                    .into(new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull final File resource, @Nullable Transition<? super File> transition) {
                            final Uri uri = Uri.fromFile(resource);
                            show(context, notificationModel, IconCompat.createWithContentUri(uri).toIcon());
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            show(context, notificationModel, IconCompat.createWithResource(context, R.drawable.icon_app).toIcon());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

        } catch (Exception e) {
            KLog.e(e.toString());
        }
    }


    /**
     * 显示通知栏
     *
     * @param context 上下文
     * @param model   通知栏数据模型
     */
    private void show(final Context context, final NotificationModel model, final Icon icon) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                //获取PendingIntent
                if (requestId > maxIdSize) {
                    requestId = 0;
                }
                Notification notification;
                if (model.getJumpIntent() != null) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId++,
                            model.getJumpIntent(), PendingIntent.FLAG_CANCEL_CURRENT);
                    notification = NotificationUtil.builder(context, model.getTitle(),
                            model.getContent(), icon, pendingIntent,
                            model.getTime(), true).setAutoCancel(true).build();
                } else {
                    notification = NotificationUtil.builder(context, model.getTitle(),
                            model.getContent(), icon, null,
                            model.getTime(), true).setAutoCancel(true).build();
                }

                NotificationManagerCompat.from(context).notify(model.getPackageName(), model.getPackageName().hashCode(), notification);

            }
        });
    }


    private NotificationModel createNotificationModel(Intent intent, String iconUrl, String title, String content, String packageName, long time) {
        NotificationModel model = new NotificationModel();
        model.setPicUrl(iconUrl);
        model.setContent(content);
        model.setJumpIntent(intent);
        model.setTitle(title);
        model.setPackageName(packageName);
        model.setTime(time);

        return model;
    }

}
