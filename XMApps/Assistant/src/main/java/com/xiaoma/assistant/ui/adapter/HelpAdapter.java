package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.HelpBean;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/13
 * Desc:帮助页面
 */
public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpViewHolder> {
    private Context context;
    private List<HelpBean> helpList;
    private OnHelpItemClickListener listener;

    public static class HelpViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvExample;

        public HelpViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_help_title);
            tvExample = itemView.findViewById(R.id.tv_help_example);
        }
    }


    public HelpAdapter(Context context, List<HelpBean> helpList) {
        this.context = context;
        this.helpList = helpList;
    }


    public void setData(List<HelpBean> helpList) {
        this.helpList = helpList;
    }

    @NonNull
    @Override
    public HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        HelpViewHolder holder = new HelpViewHolder(LayoutInflater.from(context).inflate(R.layout.item_help_content,parent,false));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnItemClickListener(v);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HelpViewHolder holder, int position) {
        HelpBean bean = helpList.get(position);
        holder.tvTitle.setText(bean.getTitle());
        holder.tvExample.setText(bean.getExample());
    }

    @Override
    public int getItemCount() {
        return helpList == null ? 0 : helpList.size();
    }

    public void setOnHelpItemClickListener(OnHelpItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnHelpItemClickListener {
        void OnItemClickListener(View itemView);
    }

}
