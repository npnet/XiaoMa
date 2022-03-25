package com.xiaoma.songname.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.songname.R;
import com.xiaoma.songname.model.SortListBean;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;

/**
 * 猜歌排行版adapter
 */
public class SongNameSortAdapter extends XMBaseAbstractRyAdapter<SortListBean.RankUserInfo> {
    private Context mContext;

    public SongNameSortAdapter(Context context, List<SortListBean.RankUserInfo> datas, int layoutId) {
        super(context, datas, layoutId);
        mContext = context;
    }

    @Override
    protected void convert(XMViewHolder holder, SortListBean.RankUserInfo sortListBean, int position) {
        TextView headColor = holder.getView(R.id.tv_head_color);
        ImageView ivUserHead = holder.getView(R.id.iv_user_head);
        TextView rankNum = holder.getView(R.id.tv_rank_num);
        TextView userName = holder.getView(R.id.tv_user_name);
        TextView point = holder.getView(R.id.tv_point);
        View itemView = holder.getView(R.id.sort_item_view);
        if (position == 0) {
            headColor.setBackgroundColor(mContext.getColor(R.color.sort_item_head_my_color));
            userName.setText(mContext.getString(R.string.sting_me));
            itemView.setEnabled(false);

        } else {
            headColor.setBackgroundColor(mContext.getColor(R.color.sort_item_head_other_color));
            userName.setText(sortListBean.getUsername());
            itemView.setEnabled(true);
        }
        ImageLoader.with(mContext)
                .load(sortListBean.getUserPic())
                .placeholder(mContext.getDrawable(R.drawable.icon_default_head))
                .into(ivUserHead);
        Drawable drawableLeft;
        if (sortListBean.getUserSex() == 1) {
            drawableLeft = mContext.getResources().getDrawable(R.drawable.sn_man);

        } else {
            drawableLeft = mContext.getResources().getDrawable(R.drawable.sn_woman);
        }
        userName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableLeft, null);
        if (sortListBean.getTotalPoint() == -1 || sortListBean.getTotalPoint() == 0) {
            point.setText(mContext.getString(R.string.not_sort));

        } else {
            point.setText(mContext.getString(R.string.sort_total_point, sortListBean.getTotalPoint()));
        }

        if (sortListBean.getRankNum() == -1) {
            rankNum.setText("100+");

        } else {
            rankNum.setText(mContext.getString(R.string.sort_rank_num, sortListBean.getRankNum()));
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
