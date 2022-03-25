package com.xiaoma.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xiaoma.ui.AdapterViewItemTrackProperties;
import com.xiaoma.ui.vh.XMLvViewHolder;

import java.util.List;

/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.ui.adapter
 *  @file_name:      XMBaseAbstractLvGvAdapter
 *  @author:         Rookie
 *  @create_time:    2018/12/10 11:01
 *  @description：   listview gridview adapter基类             */

public abstract class XMBaseAbstractLvGvAdapter<T> extends BaseAdapter implements AdapterViewItemTrackProperties {

    protected Context mContext;
    protected List<T> mDatas;
    protected int mLayoutId;


    public XMBaseAbstractLvGvAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        this.mDatas = datas;
        this.mLayoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        XMLvViewHolder viewHolder = null;
        if (convertView == null) {
            View itemView = LayoutInflater.from(mContext).inflate(mLayoutId, parent,
                    false);
            viewHolder = new XMLvViewHolder(mContext, itemView, parent, position);
            viewHolder.mLayoutId = mLayoutId;

        } else {
            viewHolder = (XMLvViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
        }
        convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();
    }

    protected abstract void convert(XMLvViewHolder viewHolder, T t, int position);


    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setDatas(List<T> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public List<T> getDatas(){
        return mDatas;
    }
}
