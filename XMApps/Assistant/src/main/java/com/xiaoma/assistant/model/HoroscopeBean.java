package com.xiaoma.assistant.model;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.Assistant;
import com.xiaoma.assistant.R;
import com.xiaoma.utils.StringUtil;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2017/5/28
 */

public class HoroscopeBean implements Serializable {

    /**
     * love : 爱情运变好，对另一半，有更多浪漫的新点子，你们之间也伴随着机智的思想交锋，充满激情。单身的人，又可以呼朋引伴出去嗨了。
     * <p>
     * date : 2017年05月
     * all : 这个月吃喝玩乐能够带给你真正的放松，在娱乐中常常灵光一现，惊艳众人，这让你在工作中更有活力和精力，良好的团队合作让你内心安宁，同时有助于自己价值观向积极方向转变。水星进入工作宫后，心思转向工作，踏实做事。
     * <p>
     * money : 小心对财运太过悲观，通过踏实的工作可以扭转你的悲观情绪。
     * <p>
     * work : 你的幽默风趣为你吸引了众人的关注，这鼓励你更热情积极的表现，从而使你更善于应酬活动，火星进入合作宫，忙于各种合作。
     * <p>
     * name : 射手座
     * health : 注意保护大腿关节。
     * <p>
     * type : month
     * <p>
     * color : 白（日的）
     * <p>
     * QFriend ： 白羊座（日的）
     * <p>
     * career : 事业（年的）
     * <p>
     * finance ：财运（年的）
     * <p>
     * text ： 综合（年的）
     */

    public String love;
    public String date;
    public String all;
    public String money;
    public String work;
    public String name;
    public String health;
    public String type;
    public String color;
    public String QFriend;
    public String career;
    public String text;
    public String finance;

    public String getLuck() {
        if ("today".equals(type) || "tomorrow".equals(type)) {
            return getDayLuck();
        } else if ("week".equals(type) || "nextweek".equals(type)) {
            return getWeekLuck();
        } else if ("month".equals(type)) {
            return getMonthLuck();
        } else {
            return getYearLuck();
        }
    }

    private String getYearLuck() {
        Context context = Assistant.getInstance().getApplication().getApplicationContext();
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.horoscope_luck_all))
                .append(StringUtil.defaultIfEmpty(all, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_love))
                .append(StringUtil.defaultIfEmpty(love, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_money))
                .append(StringUtil.defaultIfEmpty(money, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_work))
                .append(StringUtil.defaultIfEmpty(work, context.getString(R.string.horscope_empty)));
        return builder.toString();
    }

    private String getMonthLuck() {
        Context context = Assistant.getInstance().getApplication().getApplicationContext();
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.horoscope_luck_all))
                .append(StringUtil.defaultIfEmpty(all, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_health))
                .append(StringUtil.defaultIfEmpty(health, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_love))
                .append(StringUtil.defaultIfEmpty(love, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_money))
                .append(StringUtil.defaultIfEmpty(money, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_work))
                .append(StringUtil.defaultIfEmpty(work, context.getString(R.string.horscope_empty)));
        return builder.toString();
    }

    private String getWeekLuck() {
        Context context = Assistant.getInstance().getApplication().getApplicationContext();
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.horoscope_health))
                .append(StringUtil.defaultIfEmpty(health, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_love))
                .append(StringUtil.defaultIfEmpty(love, context.getString(R.string.horscope_empty)))
                .append(context.getString(R.string.horoscope_money))
                .append(StringUtil.defaultIfEmpty(money, context.getString(R.string.horscope_empty)));
        return builder.toString();
    }

    public String getDayLuck() {
        Context context = Assistant.getInstance().getApplication().getApplicationContext();
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.horscope_index_all))
                .append(all)
                .append(context.getString(R.string.horscope_index_love))
                .append(love)
                .append(context.getString(R.string.horscope_index_money))
                .append(money)
                .append(context.getString(R.string.horscope_lucky_color))
                .append(color)
                .append(context.getString(R.string.horscope_lucky_friend))
                .append(QFriend);
        return builder.toString();
    }
}
