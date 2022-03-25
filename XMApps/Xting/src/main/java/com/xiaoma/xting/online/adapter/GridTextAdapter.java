package com.xiaoma.xting.online.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.guide.listener.ViewLayoutCallBack;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.xting.R;
import com.xiaoma.xting.online.model.INamed;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class GridTextAdapter<T extends INamed> extends XMBaseAbstractBQAdapter<T, BaseViewHolder> {
    private int currentCount;
    private ViewLayoutCallBack callBack;
    private int dataUpdateCount;
    private View itemView;

    public GridTextAdapter(@Nullable List<T> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        helper.setText(R.id.board_or_class_name, item.getName());
        if (helper.getAdapterPosition() == 0) {
            currentCount++;
            itemView = helper.itemView;
            if (currentCount == dataUpdateCount) {
                if (callBack != null)
                    callBack.onViewLayouted(itemView);
            }
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getName(), String.valueOf(position));
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

    public void setCallBack(ViewLayoutCallBack viewLayoutCallBack) {
        this.callBack = viewLayoutCallBack;
    }
}
