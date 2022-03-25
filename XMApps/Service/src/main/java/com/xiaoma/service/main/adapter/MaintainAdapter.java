package com.xiaoma.service.main.adapter;

import android.content.Intent;
import android.view.View;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.service.R;
import com.xiaoma.service.main.model.MaintainBean;
import com.xiaoma.service.main.ui.MaintainDetailDialog;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;

/**
 * Created by ZouShao on 2018/11/16 0016.
 */

public class MaintainAdapter extends XMBaseAbstractRyAdapter<MaintainBean> {
    private BaseActivity mContext;
    public MaintainAdapter(BaseActivity mContext, List<MaintainBean> maintainBeanList, int layoutId) {
        super(mContext, maintainBeanList, layoutId);
        this.mContext = mContext;
    }

    @Override
    protected void convert(XMViewHolder holder, final MaintainBean maintainBean, final int position) {
        holder.setText(R.id.tv_name, maintainBean.getName());
        holder.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(getDatas().get(position).getName(), getDatas().get(position).getId() + "");
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MaintainDetailDialog.class);
                intent.putExtra(MaintainDetailDialog.MAINTAINBEAN, maintainBean);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position).getName(), getDatas().get(position).getId() + "");
    }
}
