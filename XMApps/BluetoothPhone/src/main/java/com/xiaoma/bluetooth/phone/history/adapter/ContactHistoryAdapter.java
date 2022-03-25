package com.xiaoma.bluetooth.phone.history.adapter;

import android.content.Context;
import android.view.View;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.utils.ContactNameUtils;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;
import com.xiaoma.utils.GsonHelper;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/3
 */
public class ContactHistoryAdapter extends XMBaseAbstractRyAdapter<ContactBean> {

    private ItemClickedListener listener;

    public ContactHistoryAdapter(Context context, List<ContactBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.call, GsonHelper.toJson(getDatas().get(position)));
    }

    @Override
    protected void convert(XMViewHolder holder, final ContactBean contactBean, int position) {
        holder.setText(R.id.contact_name, ContactNameUtils.getLimitedContactName(contactBean.getName()));
        holder.setText(R.id.phone_num, ContactNameUtils.getLimitedPhoneNumber(contactBean.getPhoneNum()));
        holder.setText(R.id.date, contactBean.getDate());
        holder.setOnClickListener(R.id.item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClickedListener(contactBean);
                }
            }
        });
        int callType = contactBean.getCallType();
        int imgResource = -1;
        switch (callType) {
            case BluetoothPhoneConstants.INCOMING_CALL:
                imgResource = R.drawable.icon_incoming;
                holder.setTextColorRes(R.id.phone_num, R.color.history_text_color);
                holder.setTextColorRes(R.id.contact_name, R.color.history_text_color);
                break;
            case BluetoothPhoneConstants.OUTGOING_CALL:
                imgResource = R.drawable.icon_outgoing;
                holder.setTextColorRes(R.id.phone_num, R.color.history_text_color);
                holder.setTextColorRes(R.id.contact_name, R.color.history_text_color);
                break;
            case BluetoothPhoneConstants.MISSING_CALL:
                imgResource = R.drawable.icon_missing;
                holder.setTextColorRes(R.id.phone_num, R.color.missing_call_color);
                holder.setTextColorRes(R.id.contact_name, R.color.missing_call_color);
                break;
        }
        if (imgResource != -1)
            holder.setBackgroundRes(R.id.history_call_status, imgResource);
    }

    public void setOnItemClickedListener(ItemClickedListener listener) {
        this.listener = listener;
    }

    public interface ItemClickedListener {
        void onItemClickedListener(ContactBean item);
    }
}
