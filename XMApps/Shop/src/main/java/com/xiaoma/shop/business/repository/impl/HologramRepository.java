package com.xiaoma.shop.business.repository.impl;

import com.xiaoma.shop.business.model.personalTheme.PagedHologramBean;
import com.xiaoma.shop.business.repository.AbsLoadDataRepository;
import com.xiaoma.shop.common.ShopRequestProxy;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.ThemeContract;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/20
 */
public class HologramRepository extends AbsLoadDataRepository<PagedHologramBean> {

    private @ThemeContract.SortRule
    String mCurSortRule;
    private @ShopContract.PriceType
    int mPriceType;

    public static HologramRepository newSingleton() {
        return Holder.sINSTANCE;
    }

    public boolean setSortRule(@ThemeContract.SortRule String sortRule) {
        if (sortRule == mCurSortRule) {
            return false;
        } else {
            mCurSortRule = sortRule;
            return true;
        }
    }

    public void setPriceType(@ShopContract.PriceType int type) {
        mPriceType = type;
    }

    @Override
    public boolean loadMore(ShopRequestProxy.IRequestCallback<PagedHologramBean> callback) {
        if (isValidRequest()) {
            ShopRequestProxy.requestHolograms(mCurSortRule, getCurPage() + 1, mPriceType, callback);
            return true;
        } else {
            return false;
        }
    }

    interface Holder {
        HologramRepository sINSTANCE = new HologramRepository();
    }
}
