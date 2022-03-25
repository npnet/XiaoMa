package com.xiaoma.shop.business.adapter.bought;

import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.shop.R;
import com.xiaoma.shop.common.track.EventConstant;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 */
abstract class BaseBoughtAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    private boolean cleanCache = false;
    private RequestManager mRequestManager;

    public BaseBoughtAdapter(RequestManager requestManager) {
        super(R.layout.item_base_bought);
        mRequestManager = requestManager;
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        addListener(helper);
    }

    private void addListener(BaseViewHolder helper) {
        helper.addOnClickListener(R.id.iv_select_cache);
        helper.addOnClickListener(R.id.iv_bought_test_play);
        helper.addOnClickListener(R.id.bought_operation_bt);
    }


    public final boolean isCleanCache() {
        return cleanCache;
    }

    public final void setCleanCache(boolean cleanCache) {
        this.cleanCache = cleanCache;
        notifyDataSetChanged();
    }

    final void loadRoundCircleUrl(BaseViewHolder helper, String url) {
        mRequestManager.load(url)
                .placeholder(R.drawable.place_holder)
                .transform(new RoundedCorners(10))
                .into((ImageView) helper.getView(R.id.iv_bought_icon));
    }

    final void loadTagUrl(BaseViewHolder helper, String url) {
        mRequestManager.load(url)
                .placeholder(R.drawable.bg_tag_default)
                .transform(new RoundedCorners(10))
                .into((ImageView) helper.getView(R.id.iv_bought_subscript_icon));
    }

    public boolean selectOnlyOne(int position) {
        //选择item
        return false;
    }

    public String selectAll(boolean select) {
        //选择所有item
        return null;
    }

    protected void manualUpdateTrack(String eventAction, String content,String clazz) {
        XmAutoTracker.getInstance().onEvent(eventAction, content,
                clazz,
                EventConstant.PageDesc.ACTIVITY_MY_BUY);
    }
}
