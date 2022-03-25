package com.xiaoma.assistant.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.NewStockBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Lai on 2019/1/16 0016.
 */

public class StockView extends LinearLayout {
    private Context context;
    private RelativeLayout mLayoutContent;
    private ImageView mImageView;
    private TextView mTvTitle;
    private TextView mTvCode;
    private TextView mTvUpdateTime;
    private TextView mTvName;
    private TextView mTvPrice;
    private TextView mTvOpeningPrice;
    private TextView mTvMaxPrice;
    private TextView mTvTurnoverRate;
    private TextView mTvYesterdayInfo;
    private TextView mTvMinPrice;
    private TextView mTvTotal;
    private TextView mTvEmpty;


    public StockView(Context context) {
        super(context);
        initView(context);
    }

    public StockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        this.context = context;
        inflate(getContext(), R.layout.view_stock, this);
        mLayoutContent = findViewById(R.id.layout_content);
        mImageView = findViewById(R.id.imageView);
        mTvTitle = findViewById(R.id.tv_title);
        mTvCode = findViewById(R.id.tv_code);
        mTvUpdateTime = findViewById(R.id.tv_update_time);
        mTvName = findViewById(R.id.tv_name);
        mTvPrice = findViewById(R.id.tv_price);
        mTvOpeningPrice = findViewById(R.id.tv_opening_price);
        mTvMaxPrice = findViewById(R.id.tv_max_price);
        mTvTurnoverRate = findViewById(R.id.tv_turnover_rate);
        mTvYesterdayInfo = findViewById(R.id.tv_yesterday_info);
        mTvMinPrice = findViewById(R.id.tv_min_price);
        mTvTotal = findViewById(R.id.tv_total);
        mTvEmpty = findViewById(R.id.tv_stock_note);
    }

    @SuppressLint("SetTextI18n")
    public void setData(NewStockBean.DataBean bean) {
        /*if (bean == null) {
            mLayoutContent.setVisibility(GONE);
            mTvEmpty.setVisibility(VISIBLE);
        } else {
            mLayoutContent.setVisibility(VISIBLE);
            mTvEmpty.setVisibility(GONE);

            mTvCode.setText(bean.stockCode);
            mTvName.setText(bean.name);
            mTvUpdateTime.setText("更新时间：" + formatTime(bean.updateDateTime));

            if (Double.parseDouble(bean.riseRate.substring(0, bean.riseRate.length() - 1)) > 0) {
                mTvPrice.setText("￥" + bean.currentPrice + " (" + "+" + bean.riseRate + ")");
                mTvPrice.setTextColor(context.getColor(R.color.red));
            } else {
                mTvPrice.setText("￥" + bean.currentPrice + " (" + bean.riseRate + ")");
                mTvPrice.setTextColor(context.getColor(R.color.stock_green));
            }
            mTvOpeningPrice.setText("今开：￥" + bean.openingPrice);
            mTvMaxPrice.setText("最高：￥" + bean.highPrice);
            colorText(mTvMaxPrice, R.color.red);
//            mTvTurnoverRate.setText("换手率："+);
            mTvYesterdayInfo.setText("昨收：￥" + bean.closingPrice);
            mTvMinPrice.setText("最低：￥" + bean.lowPrice);
            colorText(mTvMinPrice, R.color.stock_green);
            //TODO
//            mTvTotal.setText("成交量：￥"+ 470 + "万");
        }*/
        if (bean == null) {
            mLayoutContent.setVisibility(GONE);
            mTvEmpty.setVisibility(VISIBLE);
        } else {
            mLayoutContent.setVisibility(VISIBLE);
            mTvEmpty.setVisibility(GONE);

            mTvCode.setText(bean.getCode());
            mTvName.setText(bean.getName());
            mTvUpdateTime.setText(String.format(mContext.getString(R.string.update_time),bean.getTime().substring(0, bean.getTime().length() - 3)));

            String riseRate = "0.00";
            String[] s = bean.getChange().split(" ");
            if (s.length >= 2) {
                riseRate = s[1];
            }
            if (bean.getChange().contains("-")) {
                mTvPrice.setText("￥" + bean.getCurrent_price() + riseRate);
                mTvPrice.setTextColor(context.getColor(R.color.stock_green));
            } else {
                mTvPrice.setText("￥" + bean.getCurrent_price() + riseRate);
                mTvPrice.setTextColor(context.getColor(R.color.red));
            }
            String todayOpenPrice = "";
            String maxPrice = "";
            String minPrice = "";
            String yesterdayClosePrise = "";
            String changeRate = "";
            String dealAmount = "";


            List<NewStockBean.DataBean.InfoBean> info = bean.getInfo();
            if (info != null && info.size() != 0) {
                for (NewStockBean.DataBean.InfoBean infoBean : info) {
                    if (TextUtils.equals(infoBean.getName(), "今开")) {
                        todayOpenPrice = infoBean.getValue();
                    } else if (TextUtils.equals(infoBean.getName(), "最高")) {
                        maxPrice = infoBean.getValue();
                    } else if (TextUtils.equals(infoBean.getName(), "最低")) {
                        minPrice = infoBean.getValue();
                    } else if (TextUtils.equals(infoBean.getName(), "昨收")) {
                        yesterdayClosePrise = infoBean.getValue();
                    } else if (TextUtils.equals(infoBean.getName(), "换手率")) {
                        changeRate = infoBean.getValue();
                    } else if (TextUtils.equals(infoBean.getName(), "成交量")) {
                        dealAmount = infoBean.getValue();
                    }
                }
            }
            mTvOpeningPrice.setText(String.format(context.getString(R.string.open_price), todayOpenPrice));
            mTvMaxPrice.setText(String.format(context.getString(R.string.max_price), maxPrice));
            colorText(mTvMaxPrice, R.color.red);
            if(TextUtils.isEmpty(changeRate) || TextUtils.equals(changeRate, "0.00%")){
                changeRate = "-";
            }
            mTvTurnoverRate.setText(String.format(context.getString(R.string.change_rate), changeRate));
            mTvYesterdayInfo.setText(String.format(context.getString(R.string.yestoday_price), yesterdayClosePrise));
            mTvMinPrice.setText(String.format(context.getString(R.string.min_price), minPrice));
            colorText(mTvMinPrice, R.color.stock_green);
            mTvTotal.setText(String.format(context.getString(R.string.deal_amount), dealAmount));
        }

    }

    private String formatTime(String updateDateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date format = null;
        String formatString = null;
        try {
            format = simpleDateFormat.parse(updateDateTime);
            formatString = simpleDateFormat2.format(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(formatString)) {
            formatString = updateDateTime;
        }
        return formatString;
    }

    public void colorText(TextView textView, @ColorRes int color) {
        String text = textView.getText().toString();
        int colorTextStartIndex = -1;
        if (!TextUtils.isEmpty(text) && text.contains("：")) {
            String[] split = text.split("：");
            if (!TextUtils.isEmpty(split[0])) {
                colorTextStartIndex = split[0].length() + 1;
            }
            if (colorTextStartIndex != -1) {
                SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getColor(color));
                builder.setSpan(foregroundColorSpan, colorTextStartIndex, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(builder);
            }
        }
    }
}
