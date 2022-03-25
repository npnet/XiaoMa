package com.xiaoma.launcher.travel.hotel.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.adapter
 *  @file_name:      HotelDetailAdapter
 *  @author:         Rookie
 *  @create_time:    2019/1/4 16:52
 *  @description：   TODO             */

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.hotel.response.RoomRatePlanBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

public class HotelDetailAdapter extends XMBaseAbstractBQAdapter<RoomRatePlanBean, BaseViewHolder> {

    private static final String NO_ROOM_STATE = "0";
    private static final String HAS_ROOM_STATE_ID = "026001";//有房
    private static final String NO_ROOM_STATE_ID = "026002";//没房

    private static final int ROOM_STATE_LEFT = 0;//有房
    private static final int ROOM_STATE_NONE = 1;//没房
    private static final int ROOM_STATE_MAYBE = 2;//待确认

    private static final String ROOM_WINDOW_STATE_NONE = "0";
    private static final String ROOM_WINDOW_STATE_HAS = "1";//有窗
    private static final String ROOM_WINDOW_STATE_SOME = "2";//部分

    private static final String ROOM_NET_STATE_NONE = "0";
    private static final String ROOM_NET_STATE_KUANDAI = "1";//宽带
    private static final String ROOM_NET_STATE_WIFI = "2";//wifi


    public HotelDetailAdapter(int layoutId, List<RoomRatePlanBean> datas) {
        super(layoutId, datas);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.HOTEL_SELECT_HOME,"");
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomRatePlanBean roomRatePlanBean) {
        try {
            ImageLoader.with(mContext)
                    .load(roomRatePlanBean.getRoom().getImages().get(0).getImageUrl())
                    .error(R.drawable.bg_room)
                    .into((ImageView) helper.getView(R.id.iv_cover));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        HasWindow	String	是否有窗,0:无 1:有 2:部分
//        HasNet	String	是否有网络, 0:无 1:宽带 2:WIFI

        //设置房间信息有无窗户，有无网络，有无早餐↓↓↓
        String window = roomRatePlanBean.getRoom().getHasWindow();
        String net = roomRatePlanBean.getRoom().getHasNet();
        String windowCondition = ROOM_WINDOW_STATE_NONE.equals(window) ? mContext.getString(R.string.no_window) : ROOM_WINDOW_STATE_HAS.equals(window) ? mContext.getString(R.string.has_window) : mContext.getString(R.string.some_window);
        String netCondition = ROOM_NET_STATE_NONE.equals(net) ? mContext.getString(R.string.no_net) : ROOM_NET_STATE_KUANDAI.equals(net) ? mContext.getString(R.string.has_kuandai) : mContext.getString(R.string.has_wifi);
        String isCancel = roomRatePlanBean.getRatePlan().isCancel() == true ? mContext.getString(R.string.cancel_ok) : mContext.getString(R.string.cancel_not);
        String roomMsg = String.format(mContext.getString(R.string.room_condition),
                roomRatePlanBean.getRatePlan().getBreakfastName(), windowCondition, netCondition);

        roomRatePlanBean.setRoomMsg(roomMsg);

        String roomDetail = String.format(mContext.getString(R.string.room_condition),
                roomRatePlanBean.getRatePlan().getBreakfastName(), windowCondition,isCancel);

        helper.setText(R.id.tv_detail, roomDetail);
        //设置房间信息有无窗户，有无网络，有无早餐↑↑↑

        //设置房间名字
        helper.setText(R.id.tv_room_name, roomRatePlanBean.getRoom().getRoomName());

        //设置房间单日最低价格
        helper.setText(R.id.tv_price, String.format(mContext.getString(R.string.price), roomRatePlanBean.getRatePlan().getMinPrice()));

        //判断是否可预定
        boolean hasRoom = false;
        switch (roomRatePlanBean.getRatePlan().getStatus()) {
            case HAS_ROOM_STATE_ID:
                hasRoom = true;
                break;

            case NO_ROOM_STATE_ID:
                hasRoom = false;
                break;
        }


        if (hasRoom) {
            String str = roomRatePlanBean.getRatePlan().isConfirm() ? mContext.getString(R.string.confirm_right_away) : mContext.getString(R.string.confirm_wait);
            helper.setText(R.id.tv_no_room, str);
            helper.setTextColor(R.id.tv_no_room, mContext.getColor(R.color.color_black));
            helper.setBackgroundRes(R.id.tv_no_room, R.drawable.bg_room_wait);
        } else {
            helper.setText(R.id.tv_no_room, mContext.getString(R.string.no_room));
            helper.setTextColor(R.id.tv_no_room, mContext.getColor(R.color.color_white));
            helper.setBackgroundRes(R.id.tv_no_room, R.drawable.bg_room_sold);
        }
        roomRatePlanBean.setHasRoom(hasRoom);
    }
}
