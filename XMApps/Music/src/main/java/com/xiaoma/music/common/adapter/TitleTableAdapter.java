package com.xiaoma.music.common.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
public class TitleTableAdapter extends XMBaseAbstractRyAdapter<String> {

    private int mSelectIndex;

    public TitleTableAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setSelectIndex(int mSelectIndex) {
        this.mSelectIndex = mSelectIndex;
    }

    @Override
    protected void convert(XMViewHolder holder, String title, int position) {
        if (title != null) {
            holder.setText(R.id.tv_item_title, title);
        }
        if (position != mSelectIndex) {
            holder.setBackgroundRes(R.id.bg_cover, 0);
            holder.setTextColor(R.id.tv_item_title, mContext.getResources().getColor(R.color.color_tab_normal));
        } else {
            holder.setTextColor(R.id.tv_item_title, Color.WHITE);
            holder.setBackgroundRes(R.id.bg_cover, R.drawable.bg_item_select);
        }
        holder.getView(R.id.mine_vip_tag_iv).setVisibility(position == 2 ? View.VISIBLE : View.GONE);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position), mDatas.get(position));
    }

}
