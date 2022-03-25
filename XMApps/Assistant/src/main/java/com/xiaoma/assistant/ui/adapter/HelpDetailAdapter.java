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
public class HelpDetailAdapter extends RecyclerView.Adapter<HelpDetailAdapter.HelpViewHolder> {
    private Context context;
    private List<HelpBean.MoreBean> helpList;

    public static class HelpViewHolder extends RecyclerView.ViewHolder{
        public TextView tvContent;

        public HelpViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_help_detail_content);
        }
    }


    public HelpDetailAdapter(Context context, List<HelpBean.MoreBean> helpList) {
        this.context = context;
        this.helpList = helpList;
    }


    public void setData(List<HelpBean.MoreBean> helpList) {
        this.helpList = helpList;
    }

    @NonNull
    @Override
    public HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_help_detail,parent,false);
        return new HelpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpViewHolder holder, int position) {
        HelpBean.MoreBean bean = helpList.get(position);
        holder.tvContent.setText(bean.getContent());
    }

    @Override
    public int getItemCount() {
        return helpList == null ? 0 : helpList.size();
    }

}
