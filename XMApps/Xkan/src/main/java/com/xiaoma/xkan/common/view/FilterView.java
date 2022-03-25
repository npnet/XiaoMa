package com.xiaoma.xkan.common.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.skin.views.XmSkinLinearLayout;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.view.FilterToast;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.xkan.common.view
 *  @文件名:   FilterView
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/26 15:37
 *  @描述：    文件排序相关
 */
public class FilterView extends XmSkinLinearLayout implements View.OnClickListener {


    private TextView tvName;
    private ImageView ivName;
    private TextView tvDate;
    private ImageView ivDate;
    private TextView tvSize;
    private ImageView ivSize;

    //日期筛选项  默认 由远到近
    private boolean isFar = true;
    //大小筛选项  默认 大到小
    private boolean isBig = true;
    //名称筛选项  默认 A-Z
    private boolean isZ = false;


    public interface OnFilterListener {
        /**
         * 名称字母排序
         *
         * @param isZ
         */
        void filterByName(boolean isZ);

        /**
         * 日期排序
         *
         * @param isFar
         */
        void filterByDate(boolean isFar);

        /**
         * 大小排序
         *
         * @param isBig
         */
        void filterBySize(boolean isBig);
    }

    private OnFilterListener listener;

    public void setOnFilterListener(OnFilterListener listener) {
        this.listener = listener;
    }

    public FilterView(Context context) {
        this(context, null);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.layout_filter, this);

        tvName = view.findViewById(R.id.tv_name);
        ivName = view.findViewById(R.id.iv_name);
        tvDate = view.findViewById(R.id.tv_date);
        ivDate = view.findViewById(R.id.iv_date);
        tvSize = view.findViewById(R.id.tv_size);
        ivSize = view.findViewById(R.id.iv_size);

        tvName.setOnClickListener(this);
        ivName.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        ivDate.setOnClickListener(this);
        tvSize.setOnClickListener(this);
        ivSize.setOnClickListener(this);


    }

    @Override
    @SingleClick
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_name:
            case R.id.iv_name:
                isZ = !isZ;
                isBig = true;
                isFar = true;
                FilterToast.showFilterToast(mContext, mContext.getString(isZ ? R.string.filter_ztoa : R.string.filter_atoz));
                ivName.setImageResource(isZ ? R.drawable.icon_down : R.drawable.icon_up);
                ivSize.setImageResource(R.drawable.icon_default);
                ivDate.setImageResource(R.drawable.icon_default);
                tvName.setTextColor(Color.WHITE);
                tvSize.setTextColor(mContext.getColor(R.color.color_8a919d));
                tvDate.setTextColor(mContext.getColor(R.color.color_8a919d));
                if (listener != null) {
                    listener.filterByName(isZ);
                }
                break;
            case R.id.tv_date:
            case R.id.iv_date:
                isFar = !isFar;
                isZ = true;
                isBig = true;
                FilterToast.showFilterToast(mContext, mContext.getString(isFar ? R.string.filter_fartoclose : R.string.filter_closetofar));
                ivDate.setImageResource(isFar ? R.drawable.icon_down : R.drawable.icon_up);
                ivName.setImageResource(R.drawable.icon_default);
                ivSize.setImageResource(R.drawable.icon_default);
                tvDate.setTextColor(Color.WHITE);
                tvSize.setTextColor(mContext.getColor(R.color.color_8a919d));
                tvName.setTextColor(mContext.getColor(R.color.color_8a919d));
                if (listener != null) {
                    listener.filterByDate(isFar);
                }
                break;
            case R.id.tv_size:
            case R.id.iv_size:
                isBig = !isBig;
                isZ = true;
                isFar = true;
                FilterToast.showFilterToast(mContext, mContext.getString(isBig ? R.string.filter_bigtosmall : R.string.filter_smalltobig));
                ivSize.setImageResource(isBig ? R.drawable.icon_down : R.drawable.icon_up);
                ivName.setImageResource(R.drawable.icon_default);
                ivDate.setImageResource(R.drawable.icon_default);
                tvSize.setTextColor(Color.WHITE);
                tvDate.setTextColor(mContext.getColor(R.color.color_8a919d));
                tvName.setTextColor(mContext.getColor(R.color.color_8a919d));
                if (listener != null) {
                    listener.filterBySize(isBig);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 重置属性
     */
    public void reset() {
        isFar = true;
        isBig = true;
        isZ = false;
        ivName.setImageResource(R.drawable.icon_up);
        ivSize.setImageResource(R.drawable.icon_default);
        ivDate.setImageResource(R.drawable.icon_default);
        tvName.setTextColor(Color.WHITE);
        tvSize.setTextColor(mContext.getColor(R.color.color_8a919d));
        tvDate.setTextColor(mContext.getColor(R.color.color_8a919d));
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
