package com.xiaoma.systemui.bussiness;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.os.UserHandle;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.common.util.LogUtil;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class BarUtil {
    private static final String TAG = "BarUtil";

    public static StatusBarIcon makeIcon(Context context, String text, int iconLevel) {
        final Resources res = context.getResources();
        final int textSize = res.getDimensionPixelSize(R.dimen.status_bar_text_pure);

        final Paint paint = getTextPaint(context);
        paint.setTextSize(textSize);
        final Paint.FontMetrics fm = paint.getFontMetrics();
        final int width = (int) paint.measureText(text);
        final int height = (int) (fm.bottom - fm.ascent);
        LogUtil.logI(TAG, "makeIcon( text: %s ) [ w : %s, h: %s ]", text, width, height);
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        // 绘制文本
        final float x = 0;
        final float y = bmp.getHeight() - (fm.bottom - fm.descent);
        canvas.drawText(text, x, y, paint);
        // 生成Icon
        return makeIcon(context, bmp, iconLevel);
    }

    public static StatusBarIcon makeIcon(Context context, @DrawableRes int drawableRes, int iconLevel) {
        return makeIcon(context, drawableRes, null, iconLevel);
    }

    public static StatusBarIcon makeIcon(Context context, @DrawableRes int drawableRes, String text, int iconLevel) {
        final Bitmap bmp;
        if (TextUtils.isEmpty(text)) {
            bmp = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        } else {
            final Paint paint = getTextPaint(context);
            paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.status_bar_text_behind_icon));
            bmp = drawWithTextBehind(context, drawableRes, text, paint);
        }
        return makeIcon(context, bmp, iconLevel);
    }

    public static StatusBarIcon makeIcon(Context context, Bitmap bitmap, int iconLevel) {
        final Icon icon = Icon.createWithBitmap(bitmap);
        return new StatusBarIcon(UserHandle.ALL, context.getPackageName(), icon, iconLevel, 0, "");
    }

    private static Bitmap drawWithTextBehind(Context context, @DrawableRes int drawableRes, String text, Paint paint) {
        final Bitmap bmpSource = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        final int textPaddingStart = 0;

        final Bitmap bmpRet = Bitmap.createBitmap((int) (bmpSource.getWidth() + textPaddingStart + paint.measureText(text)), bmpSource.getHeight(), bmpSource.getConfig());
        final Canvas canvas = new Canvas(bmpRet);
        // 绘制Icon
        canvas.drawBitmap(bmpSource, 0, 0, paint);
        // 绘制文字
        final Paint.FontMetrics fm = paint.getFontMetrics();
        final float x = bmpSource.getWidth() + textPaddingStart;
        final float y = (bmpRet.getHeight() - (fm.top + fm.bottom)) / 2;
        canvas.drawText(text, x, y, paint);

        bmpSource.recycle();

        return bmpRet;
    }

    public static Paint getTextPaint(Context context) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(context.getColor(R.color.status_bar_text_color));
        return paint;
    }

    /*public static String getNotificationItemTimeDisplay(Context context, long time) {
        final Date today = new Date();
        final Date msgDate = new Date(time);
        final Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        final Date yesterday = c.getTime();

        final @StringRes int dateFormatRes;
        if (DateTimeUtil.isSameDay(msgDate, yesterday)) {
            // 昨天
            dateFormatRes = R.string.date_format_yesterday;
        } else {
            if (msgDate.getYear() == today.getYear()) {
                if (msgDate.getMonth() == today.getMonth()
                        && msgDate.getDate() == today.getDate()) {
                    // 同一天
                    dateFormatRes = 0;
                } else {
                    // 不在同一天
                    dateFormatRes = R.string.date_format_same_year;
                }
            } else {
                // 不同年份
                dateFormatRes = R.string.date_format_different_year;
            }
        }

        final String dateTimeFormat;
        if (dateFormatRes != 0) {
            dateTimeFormat = context.getString(dateFormatRes) + " " + DateFormat.getTimeFormatString(context);
        } else {
            dateTimeFormat = DateFormat.getTimeFormatString(context);
        }

        return new SimpleDateFormat(dateTimeFormat, Locale.CHINA).format(time);
    }*/
}
