package com.xiaoma.service.order.adapter;

import android.support.constraint.ConstraintLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.service.R;
import com.xiaoma.service.order.model.ProgramBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZSH on 2018/12/11 0011.
 */
public class ProgramAdapter extends XMBaseAbstractBQAdapter<ProgramBean, BaseViewHolder> {
    private List<ProgramBean> list = new ArrayList<>();

    public ProgramAdapter(List<ProgramBean> mSelectProgramList) {
        super(R.layout.item_program);
        list.addAll(mSelectProgramList);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProgramBean item) {
        TextView tvName = helper.getView(R.id.tv_name);
        ConstraintLayout layout = helper.getView(R.id.program_layout);
        layout.setSelected(item.isSelected());
        if (mContext.getResources().getString(R.string.booking_maintain).equals(item.getName())){
            ImageLoader.with(mContext)
                   .load(R.drawable.icon_maintain)
                    .into((ImageView) helper.getView(R.id.icon_program));
        }else if (mContext.getResources().getString(R.string.booking_repair).equals(item.getName())){
            ImageLoader.with(mContext)
                    .load(R.drawable.icon_repair)
                    .into((ImageView) helper.getView(R.id.icon_program));
        }else if (mContext.getResources().getString(R.string.booking_spray).equals(item.getName())){
            ImageLoader.with(mContext)
                    .load(R.drawable.icon_spray)
                    .into((ImageView) helper.getView(R.id.icon_program));
        }



        tvName.setText(item.getName());
    }

    public List<ProgramBean> getSelectedList() {
        List<ProgramBean> selectedList = new ArrayList<>();
        for (ProgramBean programBean : getData()) {
            if (programBean.isSelected()) {
                selectedList.add(programBean);
            }
        }

        return selectedList;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getName(), getData().get(position).getId() + "");
    }
}
