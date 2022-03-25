package com.xiaoma.recommend.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.xiaoma.recommend.listener.ItemClickListener;

/**
 * @author: iSun
 * @date: 2018/12/4 0004
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemClickListener {
    protected T data;
    protected ItemClickListener listener;
    protected Context context;


    public BaseViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setOnClickListener(this);
        initView(itemView);
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public BaseViewHolder<T> setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onItemLongClick(view, getPosition());
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }


    public abstract void bindViewHolder(T data);

    public void bindViewHolder() {
        bindViewHolder(data);
    }

    public abstract void initView(View itemView);


    public T getHolderData() {
        return data;
    }

}
