package com.xiaoma.assistant.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.HotelBean;
import com.xiaoma.assistant.utils.CommonUtils;
import com.xiaoma.assistant.utils.IconTextSpan;
import com.xiaoma.utils.ListUtils;

import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/21 0021
 * 酒店列表
 */
public class HotelListAdapter extends BaseMultiPageAdapter<HotelBean> {

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvDistance;
        ImageView ivPhoto;
        TextView tvScore;
        TextView tvName;
        TextView tvTab;
        TextView tvPrice;

        public HotelViewHolder(View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTab = itemView.findViewById(R.id.tv_tab);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvNum = itemView.findViewById(R.id.tv_num);
        }
    }

    public HotelListAdapter(Context context, List<HotelBean> list) {
        this.context = context;
        this.allList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_restaurant, parent, false);
        return new HotelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            HotelBean bean = allList.get(position);
            HotelViewHolder mHolder = (HotelViewHolder) holder;
            mHolder.tvNum.setText(String.valueOf(position + 1));
            mHolder.tvDistance.setText(CommonUtils.getFormattedDistance(context, bean.getDistance()));
            if (!TextUtils.isEmpty(bean.getScore())) {
                mHolder.tvScore.setText(String.format(context.getString(R.string.minute), CommonUtils.getFormattedNumber(Double.parseDouble(bean.getScore()))));
            } else {
                mHolder.tvScore.setText(R.string.no_score);
            }
            if (!TextUtils.isEmpty(bean.getHotelName())) {
                if (!TextUtils.isEmpty(bean.getStarName())) {
                    SpannableString spannableString = new SpannableString(bean.getStarName() + bean.getHotelName());
                    spannableString.setSpan(getIconTextSpan(bean.getStarName()), 0, bean.getStarName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mHolder.tvName.setText(spannableString);
                } else {
                    mHolder.tvName.setText(bean.getHotelName());
                }
            }
            if (!TextUtils.isEmpty(bean.getStartPrice())) {
                mHolder.tvPrice.setText(CommonUtils.getPriceSpannableString(context, Double.parseDouble(bean.getStartPrice()), "¥", context.getString(R.string.privce_begin)));
            } else {
                mHolder.tvPrice.setText(R.string.no_price);
            }
            if (!ListUtils.isEmpty(bean.getImages())) {
                CommonUtils.setItemImage(context, bean.getImages().get(0).getImageUrl(), mHolder.ivPhoto);
            } else {
                CommonUtils.setItemImage(context, "", mHolder.ivPhoto);
            }
//                        mHolder.tvTab.setText(bean.getSubcate());  //TODO 显示分类Tag
        }

    }

    private IconTextSpan getIconTextSpan(String text) {
        IconTextSpan iconSpan = new IconTextSpan(context, R.color.dull_red, text);
        iconSpan.setTextSize(20);
        iconSpan.setRightMargin(8);
        iconSpan.setRadius(context.getResources().getDimension(R.dimen.radius_bg_star_level));
        iconSpan.setPadding(context.getResources().getDimension(R.dimen.height_bg_star_level_padding_top), context.getResources().getDimension(R.dimen.height_bg_star_level_padding_left));
        return iconSpan;
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}
