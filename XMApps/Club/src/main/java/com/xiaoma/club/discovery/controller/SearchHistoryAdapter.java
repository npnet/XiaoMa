package com.xiaoma.club.discovery.controller;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.discovery.model.SearchHistoryInfo;
import com.xiaoma.ui.toast.XMToast;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/12 0017
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.HistoryHolder> {

    private Context context;
    private List<SearchHistoryInfo> historyInfos;

    public SearchHistoryAdapter(Context context, List<SearchHistoryInfo> historyInfos) {
        this.context = context;
        this.historyInfos = historyInfos;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_history_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new HistoryHolder(view);
        return (HistoryHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        if (historyInfos != null && !historyInfos.isEmpty()) {
            setItemData(holder, historyInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return historyInfos == null ? 0 : historyInfos.size();
    }

    private void setItemData(HistoryHolder holder, final SearchHistoryInfo historyInfo) {

        holder.searchContent.setText(historyInfo.getSearchContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMToast.showToast(context, historyInfo.getSearchContent());
            }
        });
    }


    class HistoryHolder extends RecyclerView.ViewHolder {

        private TextView searchContent;

        public HistoryHolder(View itemView) {
            super(itemView);
            searchContent = itemView.findViewById(R.id.search_content);
        }
    }

}
