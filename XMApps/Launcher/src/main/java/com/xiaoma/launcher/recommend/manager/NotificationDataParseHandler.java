package com.xiaoma.launcher.recommend.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.model.NotificationModel;
import com.xiaoma.launcher.common.views.LauncherWebActivity;
import com.xiaoma.systemuilib.NotificationUtil;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhushi.
 * 通知数据解析
 * Date: 2019/4/9
 */
public class NotificationDataParseHandler {
    private static final String TAG = "NotificationDataParseHandler";
    private static NotificationDataParseHandler mInstance;

    private int requestId = 0;
    private int maxIdSize = 10000;

    public static NotificationDataParseHandler getInstance() {
        if (mInstance == null) {
            synchronized (NotificationDataParseHandler.class) {
                if (mInstance == null) {
                    mInstance = new NotificationDataParseHandler();
                }
            }
        }

        return mInstance;
    }

    /**
     * 解析通知数据
     *
     * @param message
     */
    public void parserNotificationData(Context context, String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);

            String id = jsonObject.getString("id");
            String iconUrl = jsonObject.getString("imgPath");
            String title = jsonObject.getString("title");
            String content = jsonObject.getString("description");
            int linkType = jsonObject.getInt("linkTypeId");
            String link = jsonObject.getString("link");
            long createDate = jsonObject.getLong("createDate");

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (linkType == LauncherConstants.HTTP_LINK) {
                intent.setClass(context, LauncherWebActivity.class);
                intent.putExtra(LauncherWebActivity.EXTRA_URL, link);

            } else if (linkType == LauncherConstants.APP_PROTOCOL) {
                Uri uri = Uri.parse(link);
                intent.setData(uri);
            }
            NotificationModel notificationModel = createNotificationModel(intent, id, iconUrl, title, content, createDate);
            requestImgAndNotify(context, notificationModel);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求通知栏图标，显示通知栏
     *
     * @param context           上下文
     * @param notificationModel 封装通知栏内容数据模型
     */
    private void requestImgAndNotify(final Context context, final NotificationModel notificationModel) {
        KLog.e(TAG, "requestImgAndNotify");
        Glide.with(context)
                .load(notificationModel.getPicUrl())
                .centerCrop()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull final Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        KLog.e(TAG, "onResourceReady");
                        Bitmap bmp = Bitmap.createBitmap(resource.getIntrinsicWidth(), resource.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmp);
                        resource.setBounds(0, 0, resource.getIntrinsicWidth(), resource.getIntrinsicHeight());
                        resource.draw(canvas);
                        show(context, notificationModel, IconCompat.createWithBitmap(bmp).toIcon());
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        KLog.e(TAG, "onLoadFailed");
                        show(context, notificationModel, IconCompat.createWithResource(context, R.drawable.icon_app_launcher).toIcon());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        KLog.e(TAG, "onLoadCleared");

                    }
                });
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
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId++, model.getJumpIntent(),
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    notification = NotificationUtil.builder(context, model.getTitle(), model.getContent(), icon, pendingIntent,
                            model.getTime(), true).setAutoCancel(true).build();

                } else {
                    notification = NotificationUtil.builder(context, model.getTitle(), model.getContent(), icon, null,
                            model.getTime(), true).setAutoCancel(true).build();
                }
                KLog.e(TAG, "show");
                NotificationManagerCompat.from(context).notify(model.getId(), model.getId().hashCode(), notification);
            }
        });
    }

    private NotificationModel createNotificationModel(Intent intent, String id, String iconUrl, String title, String content, long time) {
        NotificationModel model = new NotificationModel();
        model.setId(id);
        model.setPicUrl(iconUrl);
        model.setContent(content);
        model.setJumpIntent(intent);
        model.setTitle(title);
        model.setTime(time);

        return model;
    }
}
