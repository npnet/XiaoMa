package com.xiaoma.launcher.travel.car.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.parking.response.ParkInfoBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * @author taojin
 * @date 2019/5/8
 */
public class OilParkAdapter extends XMBaseAbstractBQAdapter<ParkInfoBean, BaseViewHolder> {
    private final String OIL = "加油站";
    private final String PARK = "停车场";
    private int[] bgOils = new int[]{R.drawable.bg_oil_1, R.drawable.bg_oil_2, R.drawable.bg_oil_3};
    private int[] bgParks = new int[]{R.drawable.bg_park_1, R.drawable.bg_park_2};
    private int[] bgImgs;

    public OilParkAdapter(String type) {
        super(R.layout.oilpark_item);
        if (OIL.equals(type)) {
            bgImgs = bgOils;
        } else if (PARK.equals(type)) {
            bgImgs = bgParks;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, ParkInfoBean item) {
        ImageLoader.with(mContext)
                .load(item.getPhoto())
                .placeholder(bgImgs[helper.getLayoutPosition() % bgImgs.length])
                .error(bgImgs[helper.getLayoutPosition() % bgImgs.length])
                .into((ImageView) helper.getView(R.id.oilpark_img));
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent("", "");
    }
}
