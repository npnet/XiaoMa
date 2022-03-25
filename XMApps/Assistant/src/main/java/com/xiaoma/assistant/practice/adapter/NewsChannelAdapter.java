package com.xiaoma.assistant.practice.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.assistant.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.pratice.NewsChannelBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * @author taojin
 * @date 2019/6/6
 */
public class NewsChannelAdapter extends XMBaseAbstractBQAdapter<NewsChannelBean, BaseViewHolder> {
    public NewsChannelAdapter(int layoutResId, @Nullable List<NewsChannelBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsChannelBean item) {
        helper.setText(R.id.tv_news_channel_name, item.getName());

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
