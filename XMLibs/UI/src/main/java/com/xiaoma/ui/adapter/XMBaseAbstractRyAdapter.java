package com.xiaoma.ui.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.model.annotation.Ignore;
import com.xiaoma.ui.AdapterViewItemTrackProperties;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;


/*  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.xkan.main.adapter
 *  @文件名:   XmFileAdapter
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/15 18:46
 *  @描述：    抽取ry adapter基类 （仿hongyang baseadapter）             */

public abstract class XMBaseAbstractRyAdapter<T> extends RecyclerView.Adapter<XMViewHolder>  implements AdapterViewItemTrackProperties {

    public Context mContext;
    public List<T> mDatas;
    private int mLayoutId;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;


    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position);

    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        mOnItemClickListener = clickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener clickListener) {
        mOnItemLongClickListener = clickListener;
    }

    public XMBaseAbstractRyAdapter(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mDatas = datas;
        mLayoutId = layoutId;
    }

    /**
     * 初始化 hodler view
     *
     * @param holder
     * @param t
     * @param position
     */
    protected abstract void convert(XMViewHolder holder, T t, int position);

    @NonNull
    @Override
    public XMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final XMViewHolder holder = XMViewHolder.createXMViewHolder(mContext, parent, mLayoutId);
        holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            @Ignore
            public void onClick(View view) {
                onClickEvent(view,holder);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(view, holder, holder.getAdapterPosition());
                } else {
                    return false;
                }
            }
        });
        return holder;

    }

    private void onClickEvent(View view, XMViewHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(this, view, holder, holder.getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(XMViewHolder holder, int position) {
        convert(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public List<T> getDatas(){
        return mDatas;
    }

}
