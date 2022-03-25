//package com.xiaoma.shop.business.adapter;
//
//import android.app.DownloadManager;
//import android.support.annotation.IdRes;
//import android.support.annotation.StringRes;
//import android.util.Log;
//import android.widget.ImageView;
//
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
//import com.bumptech.glide.request.RequestOptions;
//import com.chad.library.adapter.base.BaseViewHolder;
//import com.xiaoma.image.ImageLoader;
//import com.xiaoma.model.ItemEvent;
//import com.xiaoma.network.okserver.OkDownload;
//import com.xiaoma.shop.R;
//import com.xiaoma.shop.business.model.SkinVersionsBean;
//import com.xiaoma.shop.business.download.impl.SkinDownload;
//import com.xiaoma.shop.business.ui.view.ProgressButton;
//import com.xiaoma.shop.common.constant.DownloadStatus;
//import com.xiaoma.shop.common.constant.ShopContract;
//import com.xiaoma.shop.common.download.DownloadHelper;
//import com.xiaoma.shop.common.util.PriceUtils;
//import com.xiaoma.shop.common.util.SkinUtils;
//
//import java.text.DecimalFormat;
//
///**
// * Created by Gillben
// * date: 2019/3/5 0005
// * <p>
// * 个性主题
// */
//public class PersonalityThemeAdapter extends AbsShopAdapter<SkinVersionsBean> {
//    private static final String TAG = PersonalityThemeAdapter.class.getSimpleName();
//
//    @Override
//    protected void convert(BaseViewHolder helper, SkinVersionsBean item) {
//        Log.d(TAG, "{convert}-[bean] : " + item.getAppName());
//        handleNormal(helper, item);
//        handlePrice(helper, item);
//        handlePbBtn(helper, item);
//    }
//
//    private void handleNormal(BaseViewHolder helper, SkinVersionsBean item) {
//        ImageView coverIV = helper.getView(R.id.iv_preview_image);
//        ImageView cornerMarkIV = helper.getView(R.id.iv_subscript_icon);
//
//        RequestOptions transform = new RequestOptions()
//                .placeholder(R.drawable.place_holder)
//                .centerCrop()
//                .transform(new RoundedCorners(10));
//
//        ImageLoader.with(mContext)
//                .load(item.getIconPathUrl())
//                .apply(transform)
//                .into(coverIV);
//        ImageLoader.with(mContext)
//                .load(item.getTagPath())
//                .placeholder(R.drawable.bg_tag_default)
//                .into(cornerMarkIV);
//        String appName = item.getAppName();
//        if (appName.length() > 6) {
//            appName = appName.substring(0, 6) + "...";
//        }
//
//        String showCount = "";
//        int showUseCountNum = item.getUsedNum() + item.getDefaultShowNum();
//        if (showUseCountNum >= 10000) {
//            showCount = (new DecimalFormat("#0.0").format(showUseCountNum * 1.0f / 10000)) + "W";
//        } else {
//            showCount = String.valueOf(showUseCountNum);
//        }
//        helper.setText(R.id.tv_preview_name, appName)
//                .setText(R.id.tv_preview_size, mContext.getString(R.string.str_buy_count, showCount))
//                .addOnClickListener(R.id.iv_preview_image)
//                .addOnClickListener(R.id.pbBtnLeft)
//                .addOnClickListener(R.id.pbBtnCenter)
//                .addOnClickListener(R.id.pbBtnRight);
//    }
//
//    private void handlePrice(BaseViewHolder helper, SkinVersionsBean item) {
//        SkinUtils.setSkinType(item);
//        setPrice(helper, item);
//    }
//
//    private void handlePbBtn(BaseViewHolder helper, SkinVersionsBean item) {
//        if (item.isIsBuy()
//                || (item.getPay() == ShopContract.Pay.DEFAULT)) { //是否已购买
//            //已购买
//            setBuyBtnState(helper, item);
//        } else {
//            //未购买
//            if (item.getCanTry() < 0) { //是否可以试用
//                //不可以试用
//                setUnTryBtnState(helper, item);
//            } else {
//                //可以试用
//                setTryBtnState(helper, item);
//            }
//        }
//    }
//
//    private void setPrice(BaseViewHolder helper, SkinVersionsBean item) {
//        switch (item.getPay()) {
//            case ShopContract.Pay
//                    .DEFAULT:
//                helper.setGone(R.id.tvOriginalRMB, false)
//                        .setGone(R.id.layout_coin, false)
//                        .setVisible(R.id.layout_rmb, true)
//                        .setText(R.id.tvDiscountRMB, R.string.state_free);
//                break;
//            case ShopContract.Pay
//                    .COIN:
//                helper.setGone(R.id.layout_rmb, false)
//                        .setVisible(R.id.layout_coin, true);
//                setCoinPrice(helper, item);
//                break;
//            case ShopContract.Pay
//                    .RMB:
//                helper.setGone(R.id.layout_coin, false)
//                        .setVisible(R.id.layout_rmb, true)
//                        .setVisible(R.id.tvDiscountRMB, true);
//                setRMBPrice(helper, item);
//                break;
//            case ShopContract.Pay
//                    .COIN_AND_RMB:
//                helper.setVisible(R.id.layout_coin, true)
//                        .setVisible(R.id.layout_rmb, true);
//                setCoinPrice(helper, item);
//                setRMBPrice(helper, item);
//                break;
//        }
//    }
//
//    private void setCoinPrice(BaseViewHolder helper, SkinVersionsBean item) {
//        int discountScorePrice = item.getDiscountScorePrice();
//        int scorePrice = item.getScorePrice();
//        if (discountScorePrice == scorePrice) {
//            helper.setGone(R.id.tvScoreOriginal, false)
//                    .setText(R.id.tvScoreDiscount, PriceUtils.formatPrice(discountScorePrice));
//        } else {
//            helper.setVisible(R.id.tvScoreOriginal, true)
//                    .setText(R.id.tvScoreDiscount, PriceUtils.formatPrice(discountScorePrice))
//                    .setText(R.id.tvScoreOriginal, PriceUtils.formatPrice(scorePrice));
//        }
//    }
//
//    private void setRMBPrice(BaseViewHolder helper, SkinVersionsBean item) {
//        double discountPrice = item.getDiscountPrice();
//        double price = item.getPrice();
//        if (discountPrice == price) {
//            helper.setGone(R.id.tvOriginalRMB, false)
//                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)));
//        } else {
//            helper.setVisible(R.id.tvOriginalRMB, true)
//                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)))
//                    .setText(R.id.tvOriginalRMB, PriceUtils.formatPrice(price));
//        }
//    }
//
//    private void setBuyBtnState(BaseViewHolder helper, SkinVersionsBean item) {
//        item.setSelect(false);
//
//        ProgressButton btn = helper.getView(R.id.pbBtnCenter);
//        btn.setEnabled(true);
//
//        helper.setVisible(R.id.group_purchase_trial, true);
//
//        boolean showDownload = false;
//        SkinDownload download = new SkinDownload(mContext, item);
//        SkinDownload.DownloadStatus downloadStatus = download.getDownloadStatus();
//        if (downloadStatus != null) {
//            final int status = downloadStatus.status;
//            if (DownloadManager.STATUS_PENDING == status) {
//                // 等待中
//                btn.setEnabled(false);
//                updatePbBtnText(R.id.pbBtnCenter, R.string.state_download_pending, true, helper);
//            } else if (DownloadManager.STATUS_RUNNING == status) {
//                // 下载中
//                helper.setVisible(R.id.pbBtnCenter, true);
//                helper.setGone(R.id.group_purchase_trial, false);
//                btn.setEnabled(false);
//                btn.updateStateAndProgress(downloadStatus.currentLength, downloadStatus.totalLength);
//            } else if (DownloadManager.STATUS_SUCCESSFUL == status) {
//                // 已下载
//                if (SkinUtils.isThisSkinUsed(item)) {
//                    btn.setEnabled(false);
//                    item.setSelect(true);
//                    updatePbBtnText(R.id.pbBtnCenter, R.string.state_using, false, helper);
//                } else {
//                    updatePbBtnText(R.id.pbBtnCenter, R.string.state_use, true, helper);
//                }
//            } else {
//                showDownload = true;
//            }
//        } else {
//            showDownload = true;
//        }
//        // 未下载
//        if (showDownload) {
//            updatePbBtnText(R.id.pbBtnCenter, R.string.state_download, true, helper);
//        }
//    }
//
//    private void setTryBtnState(BaseViewHolder helper, SkinVersionsBean item) {
//        Log.d(TAG, "{setTryBtnState}-[hasTask] : " + OkDownload.getInstance().hasTask(item.getUrl()) + " / " + status);
//        updatePbBtnText(R.id.pbBtnLeft, R.string.state_buy, true, helper);
//        if (status == DownloadStatus.ERROR || status == DownloadStatus.PAUSE) {
//            Log.d(TAG, "{setTryBtnState}-[status 1] : ");
//            updatePbBtnText(R.id.pbBtnRight, R.string.state_download_pending, true, helper);
//        } else if (status == DownloadStatus.WAITING || DownloadHelper.getInstance().checkTaskExecuting(item.getUrl())) {
//            Log.d(TAG, "{setTryBtnState}-[status 2] : ");
//            helper.setVisible(R.id.pbBtnCenter, true)
//                    .setGone(R.id.group_purchase_trial, false);
//            ProgressButton progressBtn = helper.getView(R.id.pbBtnCenter);
//            progressBtn.setEnabled(false);
//            progressBtn.updateStateAndProgress(DownloadHelper.getInstance().getDownloadSize(item.getUrl()), item.getSize());
//        } else {
//            Log.d(TAG, "{setTryBtnState}-[status 3] : ");
//            if (SkinUtils.isThisSkinUsed(item.getApkFilename())) {
//                updatePbBtnText(R.id.pbBtnRight, R.string.state_trying, false, helper);
//            } else {
//                updatePbBtnText(R.id.pbBtnRight, R.string.state_trial, true, helper);
//            }
//        }
//    }
//
//    private void setUnTryBtnState(BaseViewHolder helper, SkinVersionsBean item) {
//        if (status == DownloadStatus.ERROR || status == DownloadStatus.PAUSE) {
//            updatePbBtnText(R.id.pbBtnCenter, R.string.state_download_pending, true, helper);
//        } else {
//            updatePbBtnText(R.id.pbBtnCenter, R.string.state_buy, true, helper);
//        }
//    }
//
//    private void updatePbBtnText(@IdRes int resId, @StringRes int resStr, boolean enable, BaseViewHolder helper) {
//        ProgressButton progressBtn = helper.getView(resId);
//        progressBtn.setEnabled(enable);
//        progressBtn.updateStateAndProgress(0, mContext.getString(resStr));
//        if (resId == R.id.pbBtnCenter) {
//            helper.setVisible(R.id.pbBtnCenter, true)
//                    .setGone(R.id.group_purchase_trial, false);
//        } else {
//            helper.setVisible(R.id.group_purchase_trial, true)
//                    .setGone(R.id.pbBtnCenter, false);
//        }
//    }
//
//    /**
//     * 判断是否只显示中间的button
//     */
//    private boolean isShowCenterBtn(SkinVersionsBean bean) {
//        return bean.isIsBuy()
//                || (bean.getPay() == ShopContract.Pay.DEFAULT);
//    }
//
//    @Override
//    public ItemEvent returnPositionEventMsg(int position) {
//        return new ItemEvent(getData().get(position).getAppName(), getData().get(position).getId() + "");
//    }
//}
