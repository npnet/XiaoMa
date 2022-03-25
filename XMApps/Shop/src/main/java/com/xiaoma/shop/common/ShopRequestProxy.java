package com.xiaoma.shop.common;

import android.support.annotation.Nullable;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.shop.business.model.personalTheme.PagedHologramBean;
import com.xiaoma.shop.business.model.personalTheme.PagedSkinsBean;
import com.xiaoma.shop.business.model.personalTheme.PagedVoicesBean;
import com.xiaoma.shop.business.repository.impl.HologramRepository;
import com.xiaoma.shop.business.repository.impl.SystemSkinsRepository;
import com.xiaoma.shop.business.repository.impl.VoiceTonesRepository;
import com.xiaoma.shop.common.constant.ThemeContract;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/12
 */
public class ShopRequestProxy {
    private static final String TAG = ShopRequestProxy.class.getSimpleName();

    private static boolean dispatchSuccess(XMResult result, IRequestCallback callback) {
        if (result != null) {
            if (callback != null) {
                callback.onSuccess(result.getData());
            }
            return true;
        } else {
            if (callback != null) {
                callback.onFailed();
            }
            return false;
        }
    }

    private static void dispatchError(int code, String msg, IRequestCallback callback) {
        if (callback != null) {
            callback.onError(code, msg);
        }
    }

    /**
     * @param sortRule
     * @param callback 如果需要针对状态做特殊处理,就传递非null,否则传递null
     */
    public static void requestSkins(@ThemeContract.SortRule String sortRule, final int page, int type, @Nullable final IRequestCallback<PagedSkinsBean> callback) {
        SystemSkinsRepository.newSingleton().setSortRule(sortRule);
        SystemSkinsRepository.newSingleton().setPriceType(type);
        RequestManager.requestSkins(sortRule, page, type, new ResultCallback<XMResult<PagedSkinsBean>>() {
            @Override
            public void onSuccess(XMResult<PagedSkinsBean> result) {
                if (dispatchSuccess(result, callback)) {
                    SystemSkinsRepository.newSingleton().updateMinPage(0);
                    PagedSkinsBean bean = result.getData();
                    if (bean != null) {
                        PagedSkinsBean.PageInfoBean pageInfo = bean.getPageInfo();
                        SystemSkinsRepository.newSingleton().updateTotalInfo(pageInfo.getTotalPage(), pageInfo.getTotalRecord());
                        SystemSkinsRepository.newSingleton().updateCurPage(pageInfo.getPageNum());
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                dispatchError(code, msg, callback);
            }
        });
    }

    public static void requestHolograms(@ThemeContract.SortRule String sortRule, final int page, int type, @Nullable final IRequestCallback<PagedHologramBean> callback) {
        HologramRepository.newSingleton().setSortRule(sortRule);
        HologramRepository.newSingleton().setPriceType(type);
        RequestManager.requestHologram(sortRule, page, type, new ResultCallback<XMResult<PagedHologramBean>>() {
            @Override
            public void onSuccess(XMResult<PagedHologramBean> result) {
                if (dispatchSuccess(result, callback)) {
                    HologramRepository.newSingleton().updateMinPage(0);
                    PagedHologramBean bean = result.getData();
                    if (bean != null) {
                        PagedHologramBean.PageInfoBean pageInfo = bean.getPageInfo();
                        HologramRepository.newSingleton().updateTotalInfo(pageInfo.getTotalPage(), pageInfo.getTotalRecord());
                        HologramRepository.newSingleton().updateCurPage(pageInfo.getPageNum());
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                dispatchError(code, msg, callback);
            }
        });
    }

    public static void requestVoices(@ThemeContract.SortRule String sortRule, int page, @Nullable final IRequestCallback<PagedVoicesBean> callback) {
        VoiceTonesRepository.newSingleton().setSortRule(sortRule);
        RequestManager.requestVoiceTones(sortRule, page, new ResultCallback<XMResult<PagedVoicesBean>>() {
            @Override
            public void onSuccess(XMResult<PagedVoicesBean> result) {
                if (dispatchSuccess(result, callback)) {
                    VoiceTonesRepository.newSingleton().updateMinPage(0);
                    PagedVoicesBean bean = result.getData();
                    if (bean != null) {
                        PagedVoicesBean.PageInfoBean pageInfo = bean.getPageInfo();
                        VoiceTonesRepository.newSingleton().updateTotalInfo(pageInfo.getTotalPage(), pageInfo.getTotalRecord());
                        VoiceTonesRepository.newSingleton().updateCurPage(pageInfo.getPageNum());
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                dispatchError(code, msg, callback);
            }
        });
    }

    public interface IRequestCallback<T> {

        void onSuccess(T t);

        void onFailed();

        void onError(int code, String msg);
    }
}
