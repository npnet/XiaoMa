package com.xiaoma.shop.business.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.shop.R;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   :
 * </pre>
 */
public abstract class AbsShopAdapter<T> extends XMBaseAbstractBQAdapter<T, BaseViewHolder> {

    AbsShopAdapter() {
        super(R.layout.item_abs_shop, null);
    }

    public AbsShopAdapter(int layoutResId) {
        super(layoutResId);
    }

    public abstract T searchItemByProductId(long productId);
}
