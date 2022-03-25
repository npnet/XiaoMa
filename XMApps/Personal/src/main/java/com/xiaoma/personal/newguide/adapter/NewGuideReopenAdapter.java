package com.xiaoma.personal.newguide.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.newguide.model.AppInfo;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;

public class NewGuideReopenAdapter extends XMBaseAbstractRyAdapter<AppInfo.DataBean> {
    public NewGuideReopenAdapter(Context context, List<AppInfo.DataBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(XMViewHolder holder, AppInfo.DataBean appInfo, int position) {
        holder.setText(R.id.tv_app_name,appInfo.getName());
        String icon = appInfo.getIcon();
        if (TextUtils.isEmpty(icon)) holder.setImageResource(R.id.iv_app_icon,R.drawable.default_cover);
        ImageLoader.with(holder.getConvertView().getContext()).load(icon).into((ImageView) holder.getView(R.id.iv_app_icon));
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position).getPackageName(),"");
    }
}
