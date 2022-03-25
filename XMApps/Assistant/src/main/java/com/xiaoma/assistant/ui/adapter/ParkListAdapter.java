package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.ParkInfo;

import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/21 0021
 * 停车场列表
 */
public class ParkListAdapter extends BaseMultiPageAdapter<ParkInfo> {

    String location;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvDistance;
        TextView tvName;
        TextView tvCharges;
        TextView tvRemain;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvRemain = itemView.findViewById(R.id.tv_remain);
            this.tvCharges = itemView.findViewById(R.id.tv_charges);
            this.tvName = itemView.findViewById(R.id.tv_name);
            this.tvDistance = itemView.findViewById(R.id.tv_distance);
            this.tvNum = itemView.findViewById(R.id.tv_num);
        }

    }


    public ParkListAdapter(Context context, List<ParkInfo> list) {
        this.context = context;
        this.allList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_park, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            ParkInfo bean = allList.get(position);
            ((ViewHolder) holder).tvNum.setText(String.valueOf(position + 1));
            ((ViewHolder) holder).tvName.setText(bean.getName());
            ((ViewHolder) holder).tvDistance.setText(bean.getDistance(context, location));
            ((ViewHolder) holder).tvCharges.setText(bean.getFeeText());
            if (bean.getParkingSpotDynamicInfo().getAvailablePlaces() == null) {
                ((ViewHolder) holder).tvRemain.setText(R.string.no_data_in_the_parking_space);
            } else if (bean.getParkingSpotDynamicInfo().getAvailablePlaces() == 0) {
                ((ViewHolder) holder).tvRemain.setText(R.string.no_parking_spaces_available);
            } else {
                ((ViewHolder) holder).tvRemain.setText(getSpannableString(String.valueOf(bean.getParkingSpotDynamicInfo().getAvailablePlaces())));
            }
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private SpannableString getSpannableString(String text) {
        text = context.getString(R.string.remaining) + text + context.getString(R.string.parking_space);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.light_gold)), 1, text.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), text.length() - 2, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(24), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(28), 1, text.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(24), text.length() - 2, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
