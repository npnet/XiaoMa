package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.AssistantManager;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc：
 */
public class BaseMultiPageAdapter<T> extends RecyclerView.Adapter {

    protected Context context;
    //总的list数据
    protected List<T> allList;
    //方控position
    protected int selectPosition = 0;

    public static final int PAGE_SIZE = 20;

    private OnMultiPageItemClickListener listener;
    private RecyclerView.ViewHolder holder;

    public void setData(List<T> list) {
        if (list != null) {
            this.allList.clear();
            this.allList.addAll(list);
            notifyDataSetChanged();
            AssistantManager.getInstance().getMultiPageView().setPageNum();
        }
    }

    public List<T> getAllList() {
        return allList;
    }

    public List<T> getCurrentList() {
        return allList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        this.holder = holder;
        if (selectPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.news_selected_bg);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.news_unselected_bg);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectPosition(position);
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }


    public void setOnMultiPageItemClickListener(OnMultiPageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMultiPageItemClickListener {
        void onItemClick(int position);
    }

    public void setSelectPosition(int position) {
        this.selectPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public OnMultiPageItemClickListener getMultiPageListener() {
        return listener;
    }
}
