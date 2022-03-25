package com.xiaoma.launcher.travel.film.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.movie.response.HallSeatsInfoBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;


public class SeatTableAdapter extends XMBaseAbstractBQAdapter<HallSeatsInfoBean.SeatsBean,BaseViewHolder>{

    private TextView mSeatRowCol;
    private TextView mSeatPrice;
    private String mPrice;
    public SeatTableAdapter() {
        super(R.layout.seat_table_item);
    }

    public void setPrice(String price){
        mPrice = price;
    }
    @Override
    protected void convert(BaseViewHolder helper, HallSeatsInfoBean.SeatsBean item) {
        initView(helper);
        intData(item);
    }

    private void intData(HallSeatsInfoBean.SeatsBean item) {
        mSeatRowCol.setText(String.format(mContext.getString(R.string.row_and_col),item.getSeatRow(),item.getSeatCol()));
        mSeatPrice.setText(String.format(mContext.getString(R.string.price),mPrice));
    }

    private void initView(BaseViewHolder helper) {
        mSeatRowCol = helper.getView(R.id.seat_row_col);
        mSeatPrice = helper.getView(R.id.seat_price);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
