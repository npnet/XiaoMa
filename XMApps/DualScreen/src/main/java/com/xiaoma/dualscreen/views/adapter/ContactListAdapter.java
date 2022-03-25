package com.xiaoma.dualscreen.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.dualscreen.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;
import com.xiaoma.utils.log.KLog;

import java.util.List;

public class ContactListAdapter extends XMBaseAbstractRyAdapter<ContactBean> {

    public int mSelectedItemPosition = -1;

    private int mSelectedColor = R.color.simple_tv_color_yellow;

    public ContactListAdapter(Context context, List<ContactBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(XMViewHolder holder, ContactBean contactBean, int position) {
        if (position == mSelectedItemPosition) {
            ((TextView) holder.getView(R.id.tv_name)).setTextColor(mContext.getColor(mSelectedColor));
            ((TextView) holder.getView(R.id.tv_position)).setTextColor(mContext.getColor(mSelectedColor));
            ((TextView) holder.getView(R.id.tv_time)).setTextColor(mContext.getColor(mSelectedColor));
        } else {
            ((TextView) holder.getView(R.id.tv_name)).setTextColor(Color.WHITE);
            ((TextView) holder.getView(R.id.tv_position)).setTextColor(Color.WHITE);
            ((TextView) holder.getView(R.id.tv_time)).setTextColor(Color.WHITE);
        }
        ((TextView) holder.getView(R.id.tv_name)).setText(contactBean.getName());
        ((TextView) holder.getView(R.id.tv_position)).setText(contactBean.getPhoneNum());
        ((TextView) holder.getView(R.id.tv_time)).setText(contactBean.getTime());
    }

    public void setSelectedColor(int color) {
        mSelectedColor = color;
        notifyDataSetChanged();
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position).toString(), mDatas.get(position).toString());
    }

    public int getDataSize() {
        if (mDatas == null) {
            return 0;
        } else {
            return mDatas.size();
        }
    }

    public ContactBean getSelectedContactBean() {
        if (mSelectedItemPosition < 0
                || mDatas == null
                || mDatas.size() == 0
                || mSelectedItemPosition >= mDatas.size()) {
            return null;
        }
        return mDatas.get(mSelectedItemPosition);
    }

    public synchronized void next() {
        int position = mSelectedItemPosition;
        position++;
        if (position >= getDataSize()) {
            position = 0;
        }
        this.mSelectedItemPosition = position;
        notifyDataSetChanged();
        KLog.d("next select " + position);
    }

    public synchronized void pre() {
        int position = mSelectedItemPosition;
        position--;
        if (position < 0) {
            position = getDataSize() - 1;
        }
        this.mSelectedItemPosition = position;
        notifyDataSetChanged();
        KLog.d("next select " + position);
    }

    public void resetSelect(){
        this.mSelectedItemPosition = -1;
    }

    @Override
    public void setDatas(List<ContactBean> datas) {
        resetSelect();
        super.setDatas(datas);
    }
}
