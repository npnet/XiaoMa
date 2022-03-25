package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

import cn.kuwo.base.bean.quku.BaseQukuItem;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc：音乐二级界面adapter
 */
public class MusicListAdapter extends BaseMultiPageAdapter<BaseQukuItem> {

    public static class RadioViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvNum;

        public RadioViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNum = itemView.findViewById(R.id.tv_num);
        }

    }



    public MusicListAdapter(Context context, List<BaseQukuItem> albums) {
        this.context = context;
        this.allList = albums;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_radio, parent, false);
        return new RadioListAdapter.RadioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        if (allList != null) {
            BaseQukuItem album = allList.get(position);
            ((RadioListAdapter.RadioViewHolder)holder).tvTitle.setText(album.getName());
            ((RadioListAdapter.RadioViewHolder)holder).tvNum.setText(String.valueOf(position + 1));
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }
}