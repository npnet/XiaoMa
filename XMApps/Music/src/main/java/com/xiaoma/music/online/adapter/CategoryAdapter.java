package com.xiaoma.music.online.adapter;

import android.content.Context;
import android.view.View;

import com.xiaoma.guide.listener.ViewLayoutCallBack;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.music.online.model.Category;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */
public class CategoryAdapter extends XMBaseAbstractRyAdapter<Category> {
    private View itemView;
    private int dataUpdateCount; // 当前页面列表数据postValue的总次数
    private int currentCount;    // adapter刷新的次数
    private ViewLayoutCallBack callBack; // 最后一次刷新后的itemview 显示新手引导

    public CategoryAdapter(Context context, List<Category> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(XMViewHolder holder, Category category, int position) {
        holder.setText(R.id.item_category_tv_name, category.getName());
        if (position == 0) {
            currentCount++;
            itemView = holder.itemView;
            if (currentCount == dataUpdateCount) {
                if (callBack != null)
                    callBack.onViewLayouted(itemView);
            }
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position).getName(), getDatas().get(position).getItem().getId() + "");
    }

    public void setDataUpdateCount(int count) {
        // 只有一次数据更新的情况 拿的缓存数据
        if (currentCount == count) {
            if (callBack != null) {
                callBack.onViewLayouted(itemView);
            }
        } else {
            dataUpdateCount = count;
        }
    }

    public void setCallBack(ViewLayoutCallBack callBack) {
        this.callBack = callBack;
    }
}
