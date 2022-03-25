package com.xiaoma.carpark.main.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.carpark.R;
import com.xiaoma.carpark.main.model.XMPluginInfo;
import com.xiaoma.carpark.main.ui.MainActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * Created by zhushi.
 * Date: 2019/4/11
 */
public class TypeFragmentAdapter extends XMBaseAbstractBQAdapter<XMPluginInfo, BaseViewHolder> {

    private int mTabType;
    //游戏类型
    private static final int TYPE_GAME = 1;
    //活动类型
    private static final int TYPE_ACTIVITY = 2;

    public TypeFragmentAdapter(int tabType) {
        super(R.layout.item_plugin);
        this.mTabType = tabType;
    }

    @Override
    protected void convert(BaseViewHolder helper, XMPluginInfo item) {
        //封面
        ImageView ivCover = helper.getView(R.id.iv_cover);
        //标签
        TextView tvIconText = helper.getView(R.id.tv_icon_text);
        //类型标签
        TextView tvType = helper.getView(R.id.tv_type);
        //参与人数
        TextView tvCount = helper.getView(R.id.tv_count);
        //名称
        TextView tvName = helper.getView(R.id.tv_name);

        ImageLoader.with(mContext)
                .load(item.iconPath)
                .placeholder(R.drawable.iv_preview_default)
                .into(ivCover);

        if (TextUtils.isEmpty(item.iconText)) {
            tvIconText.setVisibility(View.INVISIBLE);

        } else {
            tvIconText.setVisibility(View.VISIBLE);
            tvIconText.setText(item.iconText);
        }

        if (mTabType == MainActivity.TYPE_RECOMMEND) {
            tvType.setVisibility(View.VISIBLE);
            if (item.type == TYPE_GAME) {
                tvType.setBackground(mContext.getDrawable(R.drawable.tag_game));

            } else if (item.type == TYPE_ACTIVITY) {
                tvType.setBackground(mContext.getDrawable(R.drawable.tag_activity));
            }

        } else {
            tvType.setVisibility(View.INVISIBLE);
        }

        tvCount.setText(mContext.getString(R.string.join_count, item.joinNumber));

        tvName.setText(item.mainTitle);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
