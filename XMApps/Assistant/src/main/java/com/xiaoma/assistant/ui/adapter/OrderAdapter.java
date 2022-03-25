package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.OrderBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/2/18 21:18
 * Desc: 预约订单列表适配器
 */
public class OrderAdapter extends BaseMultiPageAdapter<OrderBean> {
    //默认分页大小
    private static final int DEFAULT_PAGE_SIZE = 4;
    private Context context;
    //总的list数据
    protected List<OrderBean> allList;
    //当前显示页面的list
    protected List<OrderBean> currentList;
    private int currentPage = 1;

    public OrderAdapter(Context context, List<OrderBean> list) {
        this.context = context;
        this.allList = list;
        setPage(1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            OrderBean bean = allList.get(position);
            ((ViewHolder) holder).tvIndex.setText(String.valueOf(position + 1));
//            ((ViewHolder) holder).tvDistance.setText();
//            ((ViewHolder) holder).tvName.setText();
            ((ViewHolder) holder).tvDuration.setText(R.string.time_duration);
            colorText(((ViewHolder) holder).tvDuration, R.color.num_color);
            ((ViewHolder) holder).tvTime.setText(R.string.appointment);
            ((ViewHolder) holder).tvPay.setText(R.string.payment);
            colorText(((ViewHolder) holder).tvPay, R.color.num_color);
        }
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

    public void colorText(TextView textView, @ColorRes int color) {
        String text = textView.getText().toString();
        if (TextUtils.isEmpty(text)) {
            int[] stringNumIndex = getStringNumIndex(text);
            if (stringNumIndex == null || stringNumIndex[0] == -1) {
                return;
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(color));
            builder.setSpan(foregroundColorSpan, stringNumIndex[0], stringNumIndex[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(builder);
        }
    }

    private int[] getStringNumIndex(String string) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        int[] indexs = new int[]{-1, -2};
        for (int i = 0; i < string.length(); i++) {
            if (Character.isDigit(string.charAt(i))) {
                if (indexs[0] == -1) {
                    indexs[0] = i;
                }
                continue;
            }

            if (i == string.length() - 1) {
                indexs[1] = string.length();
            } else {
                indexs[1] = i;
            }
            return indexs;
        }
        return null;
    }

    public void setPage(int page) {
        if (page <= 0 && allList == null || allList.size() == 0 ||
                (allList.size() + DEFAULT_PAGE_SIZE - 1) / DEFAULT_PAGE_SIZE < page) {
            return;
        }
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        this.currentPage = page;
        currentList.clear();
        currentList.addAll(allList.subList((page - 1) * DEFAULT_PAGE_SIZE,
                allList.size() / DEFAULT_PAGE_SIZE >= page ? page * DEFAULT_PAGE_SIZE : (page - 1) * DEFAULT_PAGE_SIZE + allList.size() % DEFAULT_PAGE_SIZE));
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        TextView tvDistance;
        TextView tvName;
        TextView tvDuration;
        TextView tvTime;
        TextView tvPay;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvIndex = itemView.findViewById(R.id.tv_num);
            this.tvDistance = itemView.findViewById(R.id.tv_distance);
            this.tvName = itemView.findViewById(R.id.tv_name);
            this.tvDuration = itemView.findViewById(R.id.tv_duration);
            this.tvTime = itemView.findViewById(R.id.tv_time);
            this.tvPay = itemView.findViewById(R.id.tv_pay);
        }

    }
}
