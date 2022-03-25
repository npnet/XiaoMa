package com.xiaoma.assistant.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.HoroscopeBean;
import com.xiaoma.utils.StringUtil;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2017/5/28
 * 星座的view
 */

public class ViewHoroscope extends FrameLayout {

    private ImageView pic;
    private TextView title;
    private TextView allTitle;
    private TextView allDesc;
    private TextView healthTitle;
    private TextView healthDesc;
    private TextView workTitle;
    private TextView workDesc;
    private TextView loveTitle;
    private TextView loveDesc;
    private TextView moneyTitle;
    private TextView moneyDesc;
    private TextView allScore;
    private TextView loveScore;
    private TextView moneyScore;
    private TextView luckColor;
    private TextView luckHoroscope;
    private RelativeLayout dayLayout;
    private LinearLayout monthLayout;
    private RelativeLayout luckAllTitleLayout;
    private RelativeLayout luckHealthTitleLayout;
    private RelativeLayout luckWorkTitleLayout;
    private RelativeLayout luckLoveTitleLayout;
    private RelativeLayout luckMoneyTitleLayout;

    public ViewHoroscope(@NonNull Context context) {
        super(context);
        initView();
    }

    public ViewHoroscope(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ViewHoroscope(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.view_assistant_horoscope, this);
        pic = view.findViewById(R.id.iv_horscope);
        title = view.findViewById(R.id.horscope_title);
        allTitle = view.findViewById(R.id.luck_all_title);
        allDesc = view.findViewById(R.id.luck_all_desc);
        healthTitle = view.findViewById(R.id.luck_health_title);
        healthDesc = view.findViewById(R.id.luck_health_desc);
        workTitle = view.findViewById(R.id.luck_work_title);
        workDesc = view.findViewById(R.id.luck_work_desc);
        loveTitle = view.findViewById(R.id.luck_love_title);
        loveDesc = view.findViewById(R.id.luck_love_desc);
        moneyTitle = view.findViewById(R.id.luck_money_title);
        moneyDesc = view.findViewById(R.id.luck_money_desc);
        allScore = view.findViewById(R.id.luck_all_score);
        loveScore = view.findViewById(R.id.luck_love_score);
        moneyScore = view.findViewById(R.id.luck_money_score);
        luckColor = view.findViewById(R.id.luck_color);
        luckHoroscope = view.findViewById(R.id.luck_horscope);

        luckAllTitleLayout= view.findViewById(R.id.rl_luck_all_title);
        luckHealthTitleLayout= view.findViewById(R.id.rl_luck_health_title);
        luckWorkTitleLayout= view.findViewById(R.id.rl_luck_work_title);
        luckLoveTitleLayout= view.findViewById(R.id.rl_luck_love_title);
        luckMoneyTitleLayout= view.findViewById(R.id.rl_luck_money_title);
        dayLayout = view.findViewById(R.id.ll_day_luck);
        monthLayout = view.findViewById(R.id.ll_wmy_luck);
    }


    public void setData(HoroscopeBean horoscope) {
        if (horoscope == null) {
            return;
        }

        if (!TextUtils.isEmpty(horoscope.name)) {
            setHorscopePic(horoscope.name);
        }

        if ("today".equals(horoscope.type)) {
            title.setText(horoscope.name + getString(R.string.horscope_fortune_today));
            setDayLuck(horoscope);
        } else if ("tomorrow".equals(horoscope.type)) {
            title.setText(horoscope.name + getString(R.string.horscope_fortune_tomorrow));
            setDayLuck(horoscope);
        } else if ("week".equals(horoscope.type)) {
            title.setText(horoscope.name + getString(R.string.horscope_fortune_week));
            setWeekLuck(horoscope);
        } else if ("nextweek".equals(horoscope.type)) {
            title.setText(horoscope.name + getString(R.string.horscope_fortune_nextweek));
            setWeekLuck(horoscope);
        } else if ("month".equals(horoscope.type)) {
            title.setText(horoscope.name + getString(R.string.horscope_fortune_month));
            setMonthLuck(horoscope);
        } else if ("year".equals(horoscope.type)) {
            title.setText(horoscope.name + getString(R.string.horscope_fortune_year));
            setYearLuck(horoscope);
        }
    }

    private void setDayLuck(HoroscopeBean horoscopeBean) {
        dayLayout.setVisibility(VISIBLE);
        monthLayout.setVisibility(GONE);
        allScore.setText(setHoroscopeTextColor(getString(R.string.horscope_index_all), horoscopeBean.all,R.color.assistant_yellow_color));
        loveScore.setText(setHoroscopeTextColor(getString(R.string.horscope_index_love), horoscopeBean.love,R.color.assistant_yellow_color));
        moneyScore.setText(setHoroscopeTextColor(getString(R.string.horscope_index_money), horoscopeBean.money,R.color.assistant_yellow_color));
        luckColor.setText(setHoroscopeTextColor(getString(R.string.horscope_lucky_color), horoscopeBean.color,R.color.white));
        luckHoroscope.setText(setHoroscopeTextColor(getString(R.string.horscope_lucky_friend), horoscopeBean.QFriend,R.color.white));
    }

    private void setWeekLuck(HoroscopeBean horoscopeBean) {
        dayLayout.setVisibility(GONE);
        monthLayout.setVisibility(VISIBLE);
        luckAllTitleLayout.setVisibility(GONE);
        luckWorkTitleLayout.setVisibility(GONE);
        healthDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.health,getString(R.string.horscope_empty)));
        loveDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.love,getString(R.string.horscope_empty)));
        moneyDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.money,getString(R.string.horscope_empty)));
    }

    private void setMonthLuck(HoroscopeBean horoscopeBean) {
        dayLayout.setVisibility(GONE);
        monthLayout.setVisibility(VISIBLE);
        allDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.all,getString(R.string.horscope_empty)));
        healthDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.health,getString(R.string.horscope_empty)));
        loveDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.love,getString(R.string.horscope_empty)));
        moneyDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.money,getString(R.string.horscope_empty)));
        workDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.work,getString(R.string.horscope_empty)));
    }

    private void setYearLuck(HoroscopeBean horoscopeBean) {
        dayLayout.setVisibility(GONE);
        monthLayout.setVisibility(VISIBLE);
        luckHealthTitleLayout.setVisibility(GONE);
        allDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.text,getString(R.string.horscope_empty)));
        loveDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.love,getString(R.string.horscope_empty)));
        moneyDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.finance,getString(R.string.horscope_empty)));
        workDesc.setText(StringUtil.defaultIfEmpty(horoscopeBean.career,getString(R.string.horscope_empty)));
    }

    private void setHorscopePic(String name) {
        /*String[] appArray = getContext().getResources().getStringArray(R.array.horscope_name);
        TypedArray typedArray = getContext().getResources().obtainTypedArray(R.array.horscope_icon);
        int[] resIds = new int[typedArray.length()];
        int len = typedArray.length();
        for (int i = 0; i < len; i++) {
            resIds[i] = typedArray.getResourceId(i, 0);
        }

        typedArray.recycle();
        for (int i = 0; i < appArray.length; i++) {
            if (name.contains(appArray[i])) {
                pic.setImageResource(resIds[i]);
                break;
            }
        }*/
    }

    private SpannableString setHoroscopeTextColor(String format, String content,int colorResId) {
        String text = format + content;
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(colorResId));
        spannableString.setSpan(colorSpan, format.length(), format.length() + content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    private String getString(int resId){
        return getContext().getString(resId);
    }
}
