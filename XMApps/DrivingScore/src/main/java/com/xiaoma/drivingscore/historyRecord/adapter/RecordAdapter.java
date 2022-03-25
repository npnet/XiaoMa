package com.xiaoma.drivingscore.historyRecord.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.drivingscore.R;
import com.xiaoma.drivingscore.historyRecord.model.DriveInfo;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/7
 */
public class RecordAdapter extends XMBaseAbstractBQAdapter<DriveInfo, BaseViewHolder> {

    public RecordAdapter() {
        super(R.layout.item_history_record, null);
    }

    @Override
    protected void convert(final BaseViewHolder helper, DriveInfo item) {
        helper.getView(R.id.groupDate).setVisibility(checkSameDate(item.getDate(), helper.getAdapterPosition()) ? View.GONE : View.VISIBLE);
        helper.setText(R.id.tvDate, item.getDate());
        helper.setText(R.id.tvClock, item.getTime());

        TextView titleTV = helper.getView(R.id.tvTitle);
        titleTV.setText(item.getDate());
        titleTV.getLayoutParams().width = mContext.getResources()
                .getDimensionPixelOffset(R.dimen.width_slide_delete);
        titleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        helper.getView(R.id.tvDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelete(helper.getAdapterPosition());
            }
        });
    }

    private boolean checkSameDate(String date, int index) {
        if (index == 0) return false;
        return getData().get(index - 1).getDate().equals(date);
    }


    public void onItemDelete(int adapterPosition) {
        getData().remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyItemChanged(adapterPosition);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
