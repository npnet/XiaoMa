package com.xiaoma.smarthome.scene.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.model.LocalDeviceInfo;
import com.xiaoma.utils.StringUtil;

import java.util.List;

public class XiaoBaiSceneAdapter extends BaseMultiItemQuickAdapter<LocalDeviceInfo, BaseViewHolder> {

    public XiaoBaiSceneAdapter(@Nullable List<LocalDeviceInfo> data) {
        super(data);
        //普通场景
        addItemType(LocalDeviceInfo.TYPE_OTHERS, R.layout.item_xiaobai_scene);
        //回家场景
        addItemType(LocalDeviceInfo.TYPE_ARRIVE_HOME, R.layout.item_xiaobai_scene_arrive);
        //离家场景
        addItemType(LocalDeviceInfo.TYPE_LEAVE_HOME, R.layout.item_xiaobai_scene_leave);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalDeviceInfo item) {
        String deviceName = item.getDeviceName();
        if (!StringUtil.isEmpty(deviceName)) {
            deviceName = deviceName.replace("\n", "");
        }
        helper.setText(R.id.tv_name, deviceName);

        if (item.getItemType() == LocalDeviceInfo.TYPE_ARRIVE_HOME || item.getItemType() == LocalDeviceInfo.TYPE_LEAVE_HOME) {
            helper.addOnClickListener(R.id.btn_excute);
            TextView tvAutoState = helper.getView(R.id.tv_auto_state);
            if (item.isAuto()) {
                tvAutoState.setText(R.string.open_auto_excute);
                Drawable connectDrawable = mContext.getDrawable(R.drawable.on);
                connectDrawable.setBounds(0, 0, 12, 12);
                tvAutoState.setCompoundDrawables(connectDrawable, null, null, null);

            } else {
                tvAutoState.setText(R.string.close_auto_excute);
                Drawable unConnectDrawable = mContext.getDrawable(R.drawable.off);
                unConnectDrawable.setBounds(0, 0, 12, 12);
                tvAutoState.setCompoundDrawables(unConnectDrawable, null, null, null);
            }
        }
    }
}
