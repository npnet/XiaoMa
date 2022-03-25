package com.xiaoma.launcher.travel.hotel.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.adapter
 *  @file_name:      HotelCollectAdapter
 *  @author:         Rookie
 *  @create_time:    2019/2/22 15:31
 *  @description：   TODO             */

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.trip.hotel.response.ImageBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.util.List;

public class HotelCollectAdapter extends XMBaseAbstractBQAdapter<HotelBean, BaseViewHolder> {


    public HotelCollectAdapter(int layoutResId, @Nullable List<HotelBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final HotelBean item) {
        String landMark = "";
        if (!ListUtils.isEmpty(item.getLandmark())) {
            HotelBean.LandmarkBean landmarkBean = item.getLandmark().get(0);
            landMark = landmarkBean.getLandName();
        }
        boolean isCollect = item.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE;
        helper.setText(R.id.tv_collection, isCollect ? mContext.getString(R.string.already_collect) : mContext.getString(R.string.collect));
        if (StringUtil.isNotEmpty(item.getScore())){
            helper.setText(R.id.tv_score, item.getScore().length()==0?"无":item.getScore());
        }
        helper.setBackgroundRes(R.id.collection_linear,
                isCollect ? R.drawable.collect_item_type_back : R.drawable.collect_item_normal);
        helper.setImageResource(R.id.iv_collection,isCollect ? R.drawable.collect_star_select : R.drawable.collect_star_nromal);
        helper.setText(R.id.tv_detail, String.format(mContext.getString(R.string.hotel_landmark), landMark.isEmpty() ? mContext.getString(R.string.hotel_no_land) : landMark, "酒店", String.valueOf(item.getDistance()), item.getStartPrice()));
        helper.setText(R.id.tv_name, item.getHotelName());
        try {
            ImageLoader.with(mContext)
                    .load(getHotelImageUrl(item.getImages()))
                    .placeholder(R.drawable.not_hotel_img)
                    .error(R.drawable.not_hotel_img)
                    .into((ImageView) helper.getView(R.id.iv_cover));
        } catch (Exception e) {
            e.printStackTrace();
        }

        helper.addOnClickListener(R.id.tv_book);
        helper.addOnClickListener(R.id.tv_guide);
        helper.addOnClickListener(R.id.tv_call);
        helper.addOnClickListener(R.id.tv_collection);
    }


    private String getHotelImageUrl(List<ImageBean> imageBeans) {

        if (imageBeans == null) {
            return "";
        }

        String hotelImageUrl = "";

        for (int i = 0; i < imageBeans.size(); i++) {

            if (mContext.getString(R.string.hotel_image).equals(imageBeans.get(i).getImageName())) {

                hotelImageUrl = imageBeans.get(i).getImageUrl();

                break;
            }
        }


        if (hotelImageUrl == "") {
            hotelImageUrl = imageBeans.get(0).getImageUrl();
        }

        return hotelImageUrl;

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

}
