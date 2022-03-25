package com.xiaoma.smarthome.scene.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.scene.adapter
 *  @file_name:      ActionAdapter
 *  @author:         Rookie
 *  @create_time:    2019/5/5 20:06
 *  @description：   TODO             */

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

public class ActionAdapter extends XMBaseAbstractBQAdapter<SceneBean.ActionsBean, BaseViewHolder> {
    public ActionAdapter(@Nullable List<SceneBean.ActionsBean> data) {
        super(R.layout.item_device_action, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneBean.ActionsBean item) {
        ImageLoader.with(mContext).load(item.getImgUrl()).into((ImageView) helper.getView(R.id.iv_device));
        helper.setText(R.id.tv_device, item.getDeviceName());
        helper.setText(R.id.tv_action, item.getMethodName());
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
