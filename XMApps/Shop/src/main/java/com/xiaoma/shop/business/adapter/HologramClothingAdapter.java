package com.xiaoma.shop.business.adapter;


import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.model.HologramDress;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.Objects;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 全息影像 服装
 */
public class HologramClothingAdapter extends XMBaseAbstractBQAdapter<HologramDress, BaseViewHolder> {
    private HoloListModel mRole;
    private RoundedCorners mCorners = new RoundedCorners(10);

    public HologramClothingAdapter(HoloListModel role) {
        super(R.layout.item_hologram_detail);
        mRole = role;
    }

    @Override
    protected void convert(BaseViewHolder helper, HologramDress item) {
        helper.setText(R.id.tv_clothing_name, item.getName());
        ImageView view = helper.getView(R.id.iv_clothing_icon);
        ImageLoader.with(mContext)
                .load(item.getPicUrl())
                .transform(mCorners)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
                .into(view);
        int roleId = 0;
        try {
            roleId = Integer.parseInt(mRole.getCode());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        final boolean isSelect = Objects.equals(
                item.getCode(),
                String.valueOf(HologramRepo.getUsingClothId(mContext, roleId)));
        KLog.i("filOut| "+"[convert]- shop clothId>"+HologramRepo.getUsingClothId(mContext, roleId));
        item.setSelected(isSelect);
        helper.itemView.setSelected(item.isSelected());
        helper.getView(R.id.iv_clothing_icon_prev).setSelected(item.isSelected());
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        try {
            return new ItemEvent(getItem(position).getName(), position + "");
        } catch (Exception ignored) {
        }
        return null;
    }

    public void setSelected(HologramDress cloth) {
        if (ListUtils.isEmpty(getData())) return;
        for (HologramDress datum : getData()) {
            if (datum == cloth) {
                datum.setSelected(true);
            } else {
                datum.setSelected(false);
            }
        }
    }

    public HologramDress searchSelectedCloth() {
        if (ListUtils.isEmpty(getData())) return null;
        for (int i = 0; i < getData().size(); i++) {
            HologramDress hologramDress = getData().get(i);
            if (hologramDress.isSelected() && i != 0) {
                return hologramDress;
            }
        }
        return null;
    }
}
