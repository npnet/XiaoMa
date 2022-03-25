package com.xiaoma.personal.feedback.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.feedback.model.MessageInfo;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * @author Gillben
 * date: 2018/12/05
 * <p>
 * 我的消息  列表适配器
 */
public class MineMessageAdapter extends XMBaseAbstractBQAdapter<MessageInfo.MessageList, BaseViewHolder> {


    public MineMessageAdapter() {
        super(R.layout.item_mine_message);
    }


    @Override
    protected void convert(BaseViewHolder helper, MessageInfo.MessageList item) {
        helper.setText(R.id.tv_message_content, item.getComments());
        Glide.with(mContext)
                .load(R.drawable.xm_avatar)
                .override(92, 92)
                .into((ImageView) helper.getView(R.id.iv_mine_message_avatar));

        //是否已读
        boolean isRead = item.isRead() == 0;
        if (isRead) {
            helper.setVisible(R.id.iv_message_point, false);
        } else {
            helper.setVisible(R.id.iv_message_point, true);
        }

        disableItem(helper, item.getIsReview());
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(mData.get(position).getChannelId(), position + "");
    }


    private void disableItem(BaseViewHolder helper, int id) {
        if (id == 1) {
            helper.itemView.setAlpha(0.7f);
            helper.itemView.setEnabled(false);
        } else {
            helper.itemView.setAlpha(1f);
            helper.itemView.setEnabled(true);
        }
    }
}
