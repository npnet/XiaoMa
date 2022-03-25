package com.xiaoma.shop.business.repository.impl;

import com.xiaoma.shop.business.model.personalTheme.PagedVoicesBean;
import com.xiaoma.shop.business.repository.AbsLoadDataRepository;
import com.xiaoma.shop.common.ShopRequestProxy;
import com.xiaoma.shop.common.constant.ThemeContract;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/18
 */
public class VoiceTonesRepository extends AbsLoadDataRepository<PagedVoicesBean> {
    private @ThemeContract.SortRule
    String mCurSortRule;

    private VoiceTonesRepository() {
    }

    public static VoiceTonesRepository newSingleton() {
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

    @Override
    public boolean loadMore(ShopRequestProxy.IRequestCallback<PagedVoicesBean> callback) {
        if (isValidRequest()) {
            ShopRequestProxy.requestVoices(mCurSortRule, getCurPage() + 1, callback);
            return true;
        } else {
            return false;
        }
    }

    interface Holder {
        VoiceTonesRepository sINSTANCE = new VoiceTonesRepository();
    }
}
