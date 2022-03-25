package com.xiaoma.club.common.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.utils.StringUtil;

import java.util.Calendar;

/**
 * Author: loren
 * Date: 2018/12/29 0029
 */

public class ViewUtils {

    public static void setMsgCounts(TextView msgTv, int count) {
        if (msgTv == null) {
            return;
        }
        if (count <= 0) {
            msgTv.setVisibility(View.GONE);
        } else if (count <= 99) {
            msgTv.setVisibility(View.VISIBLE);
            msgTv.setText(String.valueOf(count));
        } else {
            msgTv.setVisibility(View.VISIBLE);
            msgTv.setText(StringUtil.format("%d+", 99));
        }
    }

    public static void setGroupActivityDate(Context context, TextView textView, long begin, long end) {
        if (context == null || textView == null) {
            return;
        }
        String activity = context.getString(R.string.activity);
        String year = context.getString(R.string.year);
        String mouth = context.getString(R.string.mouth);
        String day = context.getString(R.string.day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(begin);
        int beginYear = calendar.get(Calendar.YEAR);
        int beginMonth = calendar.get(Calendar.MONTH) + 1;
        int beginDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTimeInMillis(end);
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH) + 1;
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(Calendar.YEAR);
        if (beginYear != endYear) {
            textView.setText(activity + " " + beginYear + year + beginMonth + mouth + beginDay + day + "-" + endYear + year + endMonth + mouth + endDay + day);
        } else {
            if (beginYear != currentYear) {
                textView.setText(activity + " " + beginYear + year + beginMonth + mouth + beginDay + day + "-" + endYear + year + endMonth + mouth + endDay + day);
            } else {
                if (beginMonth == endMonth) {
                    textView.setText(activity + " " + beginMonth + mouth + beginDay + day + "-" + endDay + day);
                } else {
                    textView.setText(activity + " " + beginMonth + mouth + beginDay + day + "-" + endMonth + mouth + endDay + day);
                }
            }
        }
    }

    public static void setDateText(Context context, TextView textView, long date) {
        if (context == null || textView == null) {
            return;
        }
        String year = context.getString(R.string.year);
        String mouth = context.getString(R.string.mouth);
        String day = context.getString(R.string.day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int beginYear = calendar.get(Calendar.YEAR);
        int beginMonth = calendar.get(Calendar.MONTH) + 1;
        int beginDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String h = hour < 10 ? "0" + hour : String.valueOf(hour);
        int minute = calendar.get(Calendar.MINUTE);
        String m = minute < 10 ? "0" + minute : String.valueOf(minute);
        textView.setText(beginYear + year + beginMonth + mouth + beginDay + day + h + ":" + m);
    }

    public static void setTabTextStyle(Context context, TextView view, boolean isSelect) {
        if (view == null || context == null) {
            return;
        }
        String text = view.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        SpannableString spannableString = new SpannableString(text);
        if (isSelect) {
            int index = text.indexOf(" ");
            view.setTextColor(context.getResources().getColor(R.color.club_white));
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.search_key_word)), index, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_sign_number)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        view.setText(spannableString);
    }

}
