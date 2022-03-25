package com.xiaoma.assistant.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.LimitInfo;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/6
 * Desc:限行
 */
public class LimitView extends RelativeLayout{

    private LimitInfo limitInfo;
    private TextView weihao;
    private TextView unLimit;
    private LinearLayout weihaoContent;
    private LinearLayout limitContent;

    private TextView tvContent;

    public LimitView(Context context) {
        super(context);
        initView();
    }

    public LimitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LimitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_assistant_limit, this);
        weihao = findViewById(R.id.tv_weihao);
        unLimit = findViewById(R.id.tv_unlimit);
        weihaoContent = findViewById(R.id.ll_weihao_content);
        limitContent = findViewById(R.id.ll_limit_content);
    }


    public void setInfo(LimitInfo info) {
        if (info == null) {
            return;
        }
        this.limitInfo = info;
//        String city = TextUtils.isEmpty(limitInfo.getCityName()) ? getContext().getString(R.string.empty_string) : limitInfo.getCityName() + getContext().getString(R.string.space_string);
//        String date = TextUtils.isEmpty(limitInfo.getDate()) ? getContext().getString(R.string.empty_string) : limitInfo.getDate() + getContext().getString(R.string.space_string);
//        String titleText = city + date + getContext().getString(R.string.limit);
//        title.setText(titleText);

        if (limitInfo.isXianXing()) {
            limitContent.setVisibility(VISIBLE);
            weihaoContent.setVisibility(VISIBLE);
            unLimit.setVisibility(GONE);
            String weihaoText = "";
            for (int i = 0; i < limitInfo.getXxweihao().size(); i++) {
                if (i != limitInfo.getXxweihao().size() - 1) {
                    weihaoText += limitInfo.getXxweihao().get(i) + "、";
                } else {
                    weihaoText += limitInfo.getXxweihao().get(i);
                }
            }
            weihao.setText(TextUtils.isEmpty(weihaoText) ?getContext().getString(R.string.nothing) : weihaoText);
            limitContent.removeAllViews();
            List<LimitInfo.Des> des = info.getDes();
            for (int i = 0; i < des.size(); i++) {
                LimitDesView limitDesView = new LimitDesView(getContext());
                limitDesView.setInfo(des.get(i));
                limitContent.addView(limitDesView);
            }
        } else {
            limitContent.setVisibility(GONE);
            weihaoContent.setVisibility(GONE);
            unLimit.setVisibility(VISIBLE);
        }
    }
}
