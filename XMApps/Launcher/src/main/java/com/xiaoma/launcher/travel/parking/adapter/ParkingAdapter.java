package com.xiaoma.launcher.travel.parking.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.parking.response.ParkingInfoBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

public class ParkingAdapter extends XMBaseAbstractBQAdapter<ParkingInfoBean, BaseViewHolder> {
    private ImageView mParkingImg;
    private TextView mParkingName;
    private TextView mParkingFeetext;
    private LinearLayout mParkingText;

    public ParkingAdapter() {
        super(R.layout.parking_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, ParkingInfoBean item) {
        initView(helper);
        initData(item);
    }

    private void initData(ParkingInfoBean item) {
        mParkingName.setText(item.getName());

        String[] time = item.getFeeText().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String text :time){
            stringBuilder.append(text+"\r\n");
        }
        mParkingFeetext.setText(stringBuilder.toString());
        ImageLoader.with(mContext)
                .load(item.getImgUrl())
                .placeholder(R.drawable.not_parking_img)
                .error(R.drawable.not_parking_img)
                .into(mParkingImg);
    }

    private void initView(BaseViewHolder helper) {
        mParkingImg = helper.getView(R.id.parking_img);
        mParkingText = helper.getView(R.id.parking_text);
        mParkingName = helper.getView(R.id.parking_name);
        mParkingFeetext = helper.getView(R.id.parking_feetext);
    }


    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}

