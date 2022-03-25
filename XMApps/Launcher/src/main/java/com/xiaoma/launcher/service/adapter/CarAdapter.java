package com.xiaoma.launcher.service.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.service.model.ServiceBean;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * @author taojin
 * @date 2019/1/10
 */
public class CarAdapter extends XMBaseAbstractBQAdapter<ServiceBean, BaseViewHolder> {
    public CarAdapter(int layoutResId, @Nullable List<ServiceBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ServiceBean item) {

//        ImageLoader.with(mContext)
//                .load(item.getServiceImg())
//                .placeholder(R.drawable.icon_default_icon)
//                .into((ImageView) helper.getView(R.id.iv_car));

//        helper.setText(R.id.tv_car_name, item.getServiceName());


    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
