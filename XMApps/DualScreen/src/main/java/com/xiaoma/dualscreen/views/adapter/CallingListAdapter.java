package com.xiaoma.dualscreen.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.model.ContactModel;
import com.xiaoma.dualscreen.utils.LastTimeFormatUtils;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;

public class CallingListAdapter extends XMBaseAbstractRyAdapter<ContactModel> {

    public int mSelectedItem = 0;

    private int mSelectedColor = R.color.simple_tv_color_yellow;
    private int handUpResId = R.drawable.btn_hung_up;
    private int answerResId = R.drawable.btn_answer;

    public CallingListAdapter(Context context, List<ContactModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(XMViewHolder holder, ContactModel contactModel, int position) {
        ImageView ivIcon = holder.getView(R.id.iv_icon);
        TextView tvName = holder.getView(R.id.tv_contact_name);
        TextView tvNumber = holder.getView(R.id.tv_contact_number);
        TextView tvTime = holder.getView(R.id.tv_time);
        ContactBean contactBean = contactModel.getContactBean();
        State state = contactModel.getState();

        if (position == mSelectedItem) {
            tvName.setTextColor(mContext.getColor(mSelectedColor));
            tvNumber.setTextColor(mContext.getColor(mSelectedColor));
            tvTime.setTextColor(mContext.getColor(mSelectedColor));
        } else {
            tvName.setTextColor(Color.WHITE);
            tvNumber.setTextColor(Color.WHITE);
            tvTime.setTextColor(Color.WHITE);
        }

        if (state == State.INCOMING){
            ivIcon.setImageResource(answerResId);
            tvName.setText(contactBean.getName());
            tvNumber.setText(contactBean.getPhoneNum());
            tvTime.setText(mContext.getString(R.string.new_caller));
        }else if (state == State.ACTIVE){
            ivIcon.setImageResource(handUpResId);
            tvName.setText(contactBean.getName());
            tvNumber.setText(contactBean.getPhoneNum());
            tvTime.setText(LastTimeFormatUtils.getCallDuration(contactBean));
        }else if (state == State.KEEP){
            ivIcon.setImageResource(handUpResId);
            tvName.setText(contactBean.getName());
            tvNumber.setText(contactBean.getPhoneNum());
            tvTime.setText(mContext.getString(R.string.keeping));
        }else if (state == State.CALL){
            ivIcon.setImageResource(handUpResId);
            tvName.setText(contactBean.getName());
            tvNumber.setText(contactBean.getPhoneNum());
            tvTime.setText(mContext.getString(R.string.calling_now));
        }

    }

    public void setSelectedColor(int color, int handUpResId, int answerResId){
        this.handUpResId = handUpResId;
        this.answerResId = answerResId;
        mSelectedColor = color;
        notifyDataSetChanged();
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position).toString(), mDatas.get(position).toString());
    }

    public void setSelectedItem(int item) {
        this.mSelectedItem = item;
        notifyDataSetChanged();
    }

    public int getSelectedItem(){
        return this.mSelectedItem;
    }


    public ContactBean getSelectedContactBean() {
       return mDatas.get(mSelectedItem).getContactBean();
    }

    public State getSelectedState(){
        return mDatas.get(mSelectedItem).getState();
    }

}
