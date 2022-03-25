package com.xiaoma.shop.business.repository;

import android.content.Context;
import android.text.TextUtils;

import android.util.Log;
import com.xiaoma.component.AppHolder;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.FlowBean;
import com.xiaoma.shop.business.model.FlowItemForCash;
import com.xiaoma.shop.business.model.FlowMarginBean;
import com.xiaoma.shop.business.model.ScoreProductBean;
import com.xiaoma.shop.business.model.ScoreProductBean.ProductInfoBean.ChildProductBean;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.HandleCallback;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/16
 * @Describe:
 */

public class FlowRepository {


    public void fetchTrafficMallFromUnicom( final HandleCallback<XmResource<List<FlowItemForCash>>> callbck) {
        final Context context = AppHolder.getInstance().getAppContext();
        RequestManager.fetchTrafficMallFromUnicom(
                new ResultCallback<XMResult<List<FlowItemForCash>>>() {
                    @Override
                    public void onSuccess(XMResult<List<FlowItemForCash>> result) {
                        if (result == null || ListUtils.isEmpty(result.getData())) {
                            callbck.handleResult(XmResource.<List<FlowItemForCash>>failure(context.getString(R.string.goods_info_cannot_obtain)));
                        } else {
                            callbck.handleResult(XmResource.response(handleFlowData(result.getData())));
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        callbck.handleResult(XmResource.<List<FlowItemForCash>>error(msg));
                    }
                });
    }

    private List<FlowItemForCash> handleFlowData(List<FlowItemForCash> datas) {
        for (FlowItemForCash data : datas) {
            data.setPayType(getPayType(data));
        }
        return datas;
    }

    // 获取流量购买包
    public void fetchTrafficMall(
                                 final HandleCallback<XmResource<List<ChildProductBean>>> callbck,
                                 final HandleCallback<ScoreProductBean> scoreCallbck) {
        //        final List<ChildProductBean> beans = new ArrayList<>();
        //        ChildProductBean productBean = new ChildProductBean();
        //        productBean.setPrice("10.9");
        //        productBean.setDiscountPrice("11");
        //        productBean.setNeedScore(50);
        //        productBean.setName("6G/180天");
        //        productBean.setDiscountScorePrice("100");
        //        beans.add(productBean);
        //        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                callbck.handleResult(XmResource.response(beans));
        //            }
        //        },8000);

        RequestManager.getTrafficMall(
                new ResultCallback<XMResult<ScoreProductBean>>() {
                    @Override
                    public void onSuccess(XMResult<ScoreProductBean> result) {
                        scoreCallbck.handleResult(result.getData());
                        methodHandleSoreProductBean(result, callbck);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        callbck.handleResult(XmResource.<List<ChildProductBean>>error(msg));
                    }
                });


    }

    private void methodHandleSoreProductBean(XMResult<ScoreProductBean> result,
                                             HandleCallback<XmResource<List<ChildProductBean>>>
                                                     callback) {
        Context context = AppHolder.getInstance().getAppContext();
        List<ScoreProductBean.ProductInfoBean> productInfo = result.getData().getProductInfo();
        if (productInfo != null && productInfo.size() > 0) {
            // 1. 取第一个
            ScoreProductBean.ProductInfoBean productInfoBean = productInfo.get(0);
            if (productInfoBean != null) {
                // 2. 取子产品信息
                callback.handleResult(XmResource.response(handleChildData(productInfoBean
                        .getChildProduct())));
            } else {
                callback.handleResult(XmResource.<List<ChildProductBean>>failure(context.getString(R.string.goods_info_cannot_obtain)));
            }
        } else {
            callback.handleResult(XmResource.<List<ChildProductBean>>failure(context.getString(R.string.goods_info_cannot_obtain)));
        }
    }


    private List<ChildProductBean> handleChildData(List<ChildProductBean> childProduct) {
        if (ListUtils.isEmpty(childProduct)) return new ArrayList<>();
        for (ChildProductBean productBean : childProduct) {
            productBean.setPayType(getPayType(productBean));
        }
        return childProduct;
    }

    private void changeTheList(List<ChildProductBean> childProduct) {

        for (int i = 0; i < childProduct.size(); i++) {
            childProduct.get(i).setDiscountPrice("");
            childProduct.get(i).setDiscountScorePrice("");
            childProduct.get(i).setPrice("");
            childProduct.get(i).setNeedScore(0);
            switch (i) {

                case 0:
                    childProduct.get(i).setDiscountPrice("111");
                    childProduct.get(i).setDiscountScorePrice(null);
                    childProduct.get(i).setPrice("11111");
                    childProduct.get(i).setNeedScore(1414);
                    break;
                case 1:
                    childProduct.get(i).setDiscountPrice("10000");
                    childProduct.get(i).setDiscountScorePrice("2424");
                    childProduct.get(i).setPrice("999");
                    childProduct.get(i).setNeedScore(0);
                    break;
                case 2:
                    childProduct.get(i).setDiscountPrice("3535");
                    childProduct.get(i).setDiscountScorePrice("999");
                    childProduct.get(i).setPrice("3535");
                    childProduct.get(i).setNeedScore(999);
                    break;
            }
        }
    }


    private int getPayType(ChildProductBean productBean) {
        long discountCardCoin = productBean.getNeedScore();
        if (discountCardCoin > 0) {
            return ShopContract.Pay.COIN;// 车币
        }
        return ShopContract.Pay.DEFAULT; //免费
    }

    private int getPayType(FlowItemForCash flowItemForCash) {
        float price = flowItemForCash.getMarketPrice();
        if (price > 0) {
            return ShopContract.Pay.RMB;// 人民币
        }
        return ShopContract.Pay.DEFAULT; //免费
    }

    // 获取流量余量
    public void fetchFlowMarginBean(
            final String vin,
            final HandleCallback<XmResource<FlowBean>> callbck) {
        //        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                callbck.handleResult(XmResource.response(new FlowBean("1G","500M",
        // "500M")));
        //            }
        //        },800);
        RequestManager.getFlowMargin(vin, new
                ResultCallback<XMResult<FlowMarginBean>>() {
                    @Override
                    public void onSuccess(XMResult<FlowMarginBean> result) {
                        // 处理流量余量数据
                        handleFlowMarginData(result, callbck);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        callbck.handleResult(XmResource.<FlowBean>error(msg));
                    }
                });

    }

    /**
     * 处理流量余量数据
     *
     * @param result
     * @param callbck
     */
    private void handleFlowMarginData(XMResult<FlowMarginBean> result,
                                      HandleCallback<XmResource<FlowBean>> callbck) {
        String total = "0.00M", balance = "0.00M", usage = "0.00M";
        FlowBean flowBean = new FlowBean();
        List<FlowMarginBean.LeftInfoBean> leftInfo = result.getData().getLeftInfo();
        if (leftInfo != null && leftInfo.size() > 0) {
            FlowMarginBean.LeftInfoBean leftInfoBean = leftInfo.get(0);
            String unit = TextUtils.isEmpty(leftInfoBean.getUnit()) ? "" : leftInfoBean.getUnit();
            UnitConverUtils.Unit u = null;
            switch (unit) {
                case "Kb":
                    u = UnitConverUtils.Unit.KB;
                    break;
                case "G":
                    u = UnitConverUtils.Unit.G;
                    break;
                case "M":
                    u = UnitConverUtils.Unit.M;
                    break;
            }
            if (u != null) {
                total = UnitConverUtils.toNear(u, leftInfoBean.getQuota());
                balance = UnitConverUtils.toNear(u, leftInfoBean.getQuotaBalance());
                usage = UnitConverUtils.toNear(u, leftInfoBean.getQuotaUsage());

            }
        }
        flowBean.setTotal(total);
        flowBean.setBalance(balance);
        flowBean.setUsage(usage);
        callbck.handleResult(XmResource.response(flowBean));
    }


}
