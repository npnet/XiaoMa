package com.xiaoma.club.msg.chat.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.services.help.Tip;
import com.xiaoma.club.R;
import com.xiaoma.club.msg.chat.controller.viewholder.SearchLocationTipHolder;

import java.util.List;

/**
 * Created by LKF on 2019-1-9 0009.
 */
public class SearchLocationTipAdapter extends RecyclerView.Adapter<SearchLocationTipHolder> {
    private List<Tip> mTips;
    private Callback mCallback;

    public SearchLocationTipAdapter(List<Tip> tips, Callback callback) {
        mTips = tips;
        mCallback = callback;
    }

    @NonNull
    @Override
    public SearchLocationTipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchLocationTipHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_search_location_tip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchLocationTipHolder holder, int position) {
        final Tip tip = mTips.get(position);
        holder.tvPoiName.setText(tip.getName());
        holder.tvPoiAddress.setText(tip.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onTipSelect(tip);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTips != null ? mTips.size() : 0;
    }

    public interface Callback {
        void onTipSelect(Tip tip);
    }
}
