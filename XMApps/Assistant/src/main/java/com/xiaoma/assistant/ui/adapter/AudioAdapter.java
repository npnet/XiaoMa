package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.utils.CommonUtils;
import com.xiaoma.player.AudioInfo;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/19 12:01
 * Desc:
 */
public class AudioAdapter extends BaseMultiPageAdapter<AudioInfo> {

    public static class AudioViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNum;
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvCount;

        public AudioViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvNum = itemView.findViewById(R.id.tv_num);
        }

    }

    public AudioAdapter(Context context, List<AudioInfo> list) {
        this.context = context;
        this.allList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_program, parent, false);
        return new AudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            AudioInfo bean = allList.get(position);
            ((AudioViewHolder) holder).tvNum.setText(String.valueOf(position + 1));
            ((AudioViewHolder) holder).tvName.setText(bean.getTitle());
            ((AudioViewHolder) holder).tvCount.setText(String.format(context.getString(R.string.people_have_heard),bean.getPlayCount()));
            CommonUtils.setItemImage(context, bean.getCover(), ((AudioViewHolder) holder).ivIcon);
        }
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}


