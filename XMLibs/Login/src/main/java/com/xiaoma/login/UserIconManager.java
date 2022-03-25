package com.xiaoma.login;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.User;
import com.xiaoma.systemuilib.StatusBarControl;
import com.xiaoma.systemuilib.StatusBarSlot;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;

import java.util.Objects;

public class UserIconManager {
    private static final String TAG = UserIconManager.class.getSimpleName();
    private static UserIconManager instance;
    private String mDisplayName;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mUpdateAvatarTask = new Runnable() {
        @Override
        public void run() {
            User user = UserManager.getInstance().getCurrentUser();
            if (user != null && user.getId() > 0) {
                Log.e(TAG, "updateStatusBar: current user: " + user.getName());
                updateStatusBar(user);
            } else {
                Log.e(TAG, "updateStatusBar: invalid user: " + (user != null ? user.getId() : "null"));
                removeIcon();
            }
        }
    };

    private UserIconManager() {

    }

    public static UserIconManager getInstance() {
        if (instance == null) {
            synchronized (UserIconManager.class) {
                if (instance == null) {
                    instance = new UserIconManager();
                }
            }
        }
        return instance;
    }

    public void removeIcon() {
        StatusBarControl.removeIcon(AppHolder.getInstance().getAppContext(), StatusBarSlot.SLOT_USER_INFO);
    }

    public synchronized void updateUserAvatar() {
        mHandler.removeCallbacks(mUpdateAvatarTask);
        mHandler.postDelayed(mUpdateAvatarTask, 1000);
    }

    private void updateStatusBar(@NonNull User user) {
        try {
            String displayName;
            if (Objects.equals(mDisplayName, displayName = getDisplayName(user))) {
                Log.e(TAG, "user name is the same");
                return;
            }
            Log.e(TAG, "set icon user start: " + displayName);
            Application appContext = AppHolder.getInstance().getAppContext();
            Drawable drawable = appContext.getDrawable(R.drawable.icon_default_user_state_bar);
            Bitmap bitmap = drawWithTextBehind(appContext, drawable, displayName);
            StatusBarControl.setIcon(appContext,
                    StatusBarSlot.SLOT_USER_INFO, Icon.createWithBitmap(bitmap));
            mDisplayName = displayName;
            Log.e(TAG, "set icon user end: " + displayName);
        } catch (Exception e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) {
                XMToast.toastException(AppHolder.getInstance().getAppContext(), "set icon error:" + e.getMessage());
            }
        }
    }

    private static String getDisplayName(User u) {
        if (u == null)
            return "";
        if (LoginConstants.TOURIST_USER_ID == u.getId())
            return AppHolder.getInstance().getAppContext().getString(R.string.traveller);
        return StringUtil.optString(u.getName());
    }

    private static Bitmap drawWithTextBehind(Context context, Drawable src, String text) {
        Paint paint = getTextPaint(context);
        final int textPaddingStart = 10;

        final Bitmap bmpRet = Bitmap.createBitmap((int) (
                        src.getIntrinsicWidth() + textPaddingStart + paint.measureText(text)),
                src.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmpRet);
        // 绘制Icon
        src.setBounds(0, 0, src.getIntrinsicWidth(), src.getIntrinsicHeight());
        src.draw(canvas);

        // 绘制文字
        final Paint.FontMetrics fm = paint.getFontMetrics();
        final float x = src.getIntrinsicWidth() + textPaddingStart;
        final float y = (bmpRet.getHeight() - (fm.top + fm.bottom)) / 2;
        canvas.drawText(text, x, y, paint);
        return bmpRet;
    }

    private static Paint getTextPaint(Context context) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setColor(context.getColor(R.color.white));
        return paint;
    }
}
