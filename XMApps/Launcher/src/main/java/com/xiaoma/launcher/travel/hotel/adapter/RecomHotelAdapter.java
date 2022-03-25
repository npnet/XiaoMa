package com.xiaoma.launcher.travel.hotel.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.adapter
 *  @file_name:      RecomHotelAdapter
 *  @author:         Rookie
 *  @create_time:    2019/1/2 15:49
 *  @description：   TODO             */

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.trip.hotel.response.ImageBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.StringUtil;

import java.util.List;

public class RecomHotelAdapter extends XMBaseAbstractBQAdapter<HotelBean, BaseViewHolder> {


    public RecomHotelAdapter() {
        super(R.layout.item_recom_hotel);
    }


    @Override
    protected void convert(BaseViewHolder helper, HotelBean hotelBean) {
        ImageLoader.with(mContext).load(getHotelImageUrl(hotelBean.getImages()))
                .placeholder(R.drawable.not_hotel_img)
                .error(R.drawable.not_hotel_img)
                .into((ImageView) helper.getView(R.id.iv_cover));

        //TODO 修改准二星级还是分数
//        helper.setText(R.id.tv_star, hotelBean.getStarName());
        helper.setText(R.id.tv_star, hotelBean.getScore().length() == 0 ? mContext.getString(R.string.no_score) : hotelBean.getScore());
        //收藏点击事件
        helper.addOnClickListener(R.id.tv_collect);

        boolean isCollect = hotelBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE;

        Drawable drawable = null;
        if (isCollect) {
            drawable = mContext.getResources().getDrawable(R.drawable.collect_star_select);
            helper.getView(R.id.tv_collect).setBackground(mContext.getResources().getDrawable(R.drawable.icon_collect_hotel));
        } else {
            drawable = mContext.getResources().getDrawable(R.drawable.collect_star_nromal);
            helper.getView(R.id.tv_collect).setBackground(mContext.getResources().getDrawable(R.drawable.icon_uncollect_hotel));
        }
        // 设置图片的大小
        drawable.setBounds(0, 0, 20, 20);
        // 设置图片的位置，左、上、右、下
        ((TextView) helper.getView(R.id.tv_collect)).setCompoundDrawables(drawable, null, null, null);
        helper.setText(R.id.tv_collect, isCollect ? mContext.getString(R.string.already_collect) : mContext.getString(R.string.collect));
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent();
    }


    private String getHotelImageUrl(List<ImageBean> imageBeans) {

        if (imageBeans == null || imageBeans.size() <= 0) {
            return "";
        }

        String hotelImageUrl = "";

        for (int i = 0; i < imageBeans.size(); i++) {

            if (mContext.getString(R.string.hotel_image).equals(imageBeans.get(i).getImageName())) {

                hotelImageUrl = imageBeans.get(i).getImageUrl();

                break;
            }
        }


        if (StringUtil.isEmpty(hotelImageUrl)) {
            hotelImageUrl = imageBeans.get(0).getImageUrl();
        }

        return hotelImageUrl;

    }

}
