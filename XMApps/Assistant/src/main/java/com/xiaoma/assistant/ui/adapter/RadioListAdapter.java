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

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc:电台语音二级adapter
 */
public class RadioListAdapter extends BaseMultiPageAdapter<Album> {

    private static final int PAGE_SIZE = 5;

    public static class RadioViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvNum;

        public RadioViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNum = itemView.findViewById(R.id.tv_num);
        }

    }


    public RadioListAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.allList = albums;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_radio, parent, false);
        return new RadioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        if (allList != null) {
            Album album = allList.get(position);
            ((RadioViewHolder)holder).tvTitle.setText(album.getAlbumTitle());
            ((RadioViewHolder)holder).tvNum.setText(String.valueOf(position + 1));
        }

    }

    @Override
    public int getItemCount() {
        return allList == null? 0 : allList.size();
    }

}



