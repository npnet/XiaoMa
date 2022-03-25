package com.xiaoma.launcher.main.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.views.EntertainmentView;
import com.xiaoma.launcher.common.views.HotelView;
import com.xiaoma.launcher.main.model.AppModel;

import java.util.List;

/**
 * @author taojin
 * @date 2019/1/2
 */
public class AppAdapter extends BaseMultiItemQuickAdapter<AppModel, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public AppAdapter(List<AppModel> data) {
        super(data);
        addItemType(1, R.layout.item_navi);
        addItemType(2, R.layout.item_music);
        addItemType(3, R.layout.item_radio);
        addItemType(4, R.layout.item_entertainment);
        addItemType(5, R.layout.item_hotel);
    }

    @Override
    protected void convert(BaseViewHolder helper, AppModel item) {
        switch (helper.getItemViewType()) {
            case 1:
                helper.setText(R.id.tv_navi, item.getName());

                break;
            case 2:
                helper.setText(R.id.tv_music, item.getName());
                break;
            case 3:
                helper.setText(R.id.tv_radio, item.getName());
                break;
            case 4:
                ((EntertainmentView) helper.getView(R.id.ev_launcher)).setTextView(item.getName());
                ((EntertainmentView) helper.getView(R.id.ev_launcher)).setBtnText(item.getContent());
                break;
            case 5:
                ((HotelView) helper.getView(R.id.hv_launcher)).setTextView(item.getName());
                ((HotelView) helper.getView(R.id.hv_launcher)).setBtnText(item.getLeft(), item.getRight());
                break;
        }
    }
}
