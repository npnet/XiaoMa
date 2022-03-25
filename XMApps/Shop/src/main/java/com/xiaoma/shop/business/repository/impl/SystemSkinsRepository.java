package com.xiaoma.shop.business.repository.impl;

import com.xiaoma.shop.business.model.personalTheme.PagedSkinsBean;
import com.xiaoma.shop.business.repository.AbsLoadDataRepository;
import com.xiaoma.shop.common.ShopRequestProxy;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.ThemeContract;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/10
 */
public class SystemSkinsRepository extends AbsLoadDataRepository<PagedSkinsBean> {

    private @ThemeContract.SortRule
    String mCurSortRule;
    private @ShopContract.PriceType
    int mPriceType;

    public static SystemSkinsRepository newSingleton() {
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
    public boolean loadMore(ShopRequestProxy.IRequestCallback<PagedSkinsBean> callback) {
        if (isValidRequest()) {
            ShopRequestProxy.requestSkins(mCurSortRule, getCurPage() + 1, mPriceType, callback);
            return true;
        } else {
            return false;
        }
    }

    interface Holder {
        SystemSkinsRepository sINSTANCE = new SystemSkinsRepository();
    }
}
