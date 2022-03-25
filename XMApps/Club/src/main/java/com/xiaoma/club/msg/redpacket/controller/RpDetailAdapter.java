package com.xiaoma.club.msg.redpacket.controller;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.club.R;
import com.xiaoma.club.msg.redpacket.model.RPDetailItemInfo;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZYao.
 * Date ：2019/4/17 0017
 */
public class RpDetailAdapter extends BaseQuickAdapter<RPDetailItemInfo, BaseViewHolder> {

    public RpDetailAdapter(@Nullable List<RPDetailItemInfo> data) {
        super(R.layout.item_rp_receiver, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RPDetailItemInfo item) {
        if (item == null) {
            return;
        }
        helper.setText(R.id.tv_receiver_user_name, StringUtil.optString(item.getReceiverUserName()));
        helper.setText(R.id.tv_receiver_money_num, String.valueOf(item.getPointNum()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mContext.getString(R.string.data_format), Locale.getDefault());
        helper.setText(R.id.tv_receiver_time, TimeUtils.millis2String(item.getReceiverTime(), simpleDateFormat));
        ImageLoader.with(mContext)
                .load(item.getReceiverUserPicPath())
                .placeholder(R.drawable.default_head_icon)
                .error(R.drawable.default_head_icon)
                .circleCrop()
                .into((ImageView) helper.getView(R.id.img_receiver_user_pic));
    }
}
