package com.xiaoma.smarthome.scene.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.scene.adapter
 *  @file_name:      DeviceAdapter
 *  @author:         Rookie
 *  @create_time:    2019/4/25 11:06
 *  @description：   TODO             */

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.miot.common.abstractdevice.AbstractDevice;
import com.viomi.devicelib.manager.DeviceCentre;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.smarthome.R;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

public class DeviceAdapter extends XMBaseAbstractBQAdapter<AbstractDevice, BaseViewHolder> {

    public DeviceAdapter(@Nullable List<AbstractDevice> data) {
        super(R.layout.item_device, data);
    }


    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

    @Override
    protected void convert(BaseViewHolder helper, AbstractDevice item) {
        helper.setVisible(R.id.top_line, helper.getAdapterPosition() == 0);

        ImageLoader.with(mContext).load(DeviceCentre.getInstance().getDeviceIcon(item)).into((ImageView) helper.getView(R.id.iv_device));
        helper.setText(R.id.tv_device_name, item.getName());
        boolean online = item.isOnline();
        helper.setText(R.id.tv_state, online ? mContext.getString(R.string.online) : mContext.getString(R.string.off_line));
        helper.setTextColor(R.id.tv_state, Color.parseColor(online ? "#fbd3a4" : "#8a919d"));
    }
}
