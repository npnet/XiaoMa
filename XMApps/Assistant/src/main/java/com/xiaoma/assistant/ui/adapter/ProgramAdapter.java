package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.XMAlbum;
import com.xiaoma.assistant.utils.CommonUtils;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/2/18 21:04
 * Desc: 节目列表适配器
 */
public class ProgramAdapter extends BaseMultiPageAdapter<XMAlbum> {

    public static class XMAlbumViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNum;
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvCount;

        public XMAlbumViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvNum = itemView.findViewById(R.id.tv_num);
        }

    }

    public ProgramAdapter(Context context, List<XMAlbum> list) {
        this.context = context;
        this.allList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_program, parent, false);
        return new XMAlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            XMAlbum bean = allList.get(position);
            ((XMAlbumViewHolder) holder).tvNum.setText(String.valueOf(position + 1));
            ((XMAlbumViewHolder) holder).tvName.setText(bean.getAlbumTitle());
            ((XMAlbumViewHolder) holder).tvCount.setText(String.format(context.getString(R.string.people_have_heard),bean.getFormatCount()));
            CommonUtils.setItemImage(context, bean.getCoverUrlMiddle(), ((XMAlbumViewHolder) holder).ivIcon);
        }
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}

