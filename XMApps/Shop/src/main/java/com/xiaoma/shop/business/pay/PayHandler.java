package com.xiaoma.shop.business.pay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.model.TrackerEventType;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.verify.FaceVerifyActivity;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.BuyBean;
import com.xiaoma.shop.business.model.CoinAndSignInfo;
import com.xiaoma.shop.business.model.FetchQrCodeBean;
import com.xiaoma.shop.business.model.QrCodeBean;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.DialogCommonCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.util.LanUtils;
import com.xiaoma.shop.common.util.timer.OnTimerCallback;
import com.xiaoma.shop.common.util.timer.OrderTimer;
import com.xiaoma.ui.dialog.OnViewClickListener;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.progress.ProgressSupport;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.DoubleClickUtils;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.util.HashSet;
import java.util.Set;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/7 0007 11:37
 *       desc：支付处理
 * </pre>
 */
public class PayHandler {


    private static final String PERSONAL_PKG = "com.xiaoma.personal";
    private static final String TASK_CENTER = "com.xiaoma.personal.taskcenter.ui" + "" + ".TaskCenterActivity";
    private static final int TIMER_INTERVAL = 3000;
    private static final int QUERY_ORDER_STATUS_MAX_LOOP_NUMBERS = Integer.MAX_VALUE;// 临时处理:不限制轮询次数,先保证状态刷新
    private int currentLoopNumber;
    private Set<PayCallback> mPayCallbacks = new ArraySet<>();

    private long TIME_INTERVAL = 1200;
    private long lastTime = 0;

    private PayHandler() {
    }

    private static class Instance {
        private static final PayHandler PAY_HANDLER = new PayHandler();
    }

    public static PayHandler getInstance() {
        return Instance.PAY_HANDLER;
    }

    public void addPayCallback(PayCallback callback) {
        if (callback != null)
            mPayCallbacks.add(callback);
    }

    public void removePayCallback(PayCallback callback) {
        if (callback != null)
            mPayCallbacks.remove(callback);
    }

    /**
     * 车币付款
     *
     * @param activity    FragmentActivity
     * @param type        资源类型
     * @param productId   商品id
     * @param productName 商品名称
     * @param carCoin     车币
     */
    public void carCoinPayWindow(final FragmentActivity activity,
                                 @ResourceType final int type,
                                 final long productId,
                                 String productName,
                                 final int carCoin,
                                 final boolean isImmediately,
                                 final PaySuccessResultCallback callback) {
        if (!isValidTime() && !isImmediately) return;
        showCoinPayWindow(activity, type, productId, ""+carCoin, "", callback);
    }
    /**
     * 有订单的车币支付
     */
    public void carCoinPayWindow(final FragmentActivity activity,
                                 @ResourceType final int type,
                                 final long productId,
                                 final String carCoin,
                                 final String orderNum,
                                 final PaySuccessResultCallback callback) {
        showCoinPayWindow(activity, type, productId, carCoin, orderNum, callback);
    }

    public void showCoinPayWindow(final FragmentActivity activity,
                                  final @ResourceType int type,
                                  final long productId,
                                  final String carCoin,
                                  final String orderNum,
                                  final PaySuccessResultCallback callback) {
        if (!NetworkUtils.isConnected(activity)) {
            XMToast.toastException(activity, R.string.network_anomaly);
            return;
        }
        ShopTrackManager.newSingleton().manualUpdateEvent(TrackerEventType.ONCLICK, EventConstant.NormalClick.ACTION_BUY_WITH_COIN);
        if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SHOP_CAN_USE_NOT_BUY,
                new OnBlockCallback() {
                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(activity, loginType.getPrompt(activity));
                        return true;
                    }
                })) return;

        View contentView = View.inflate(activity, R.layout.dialog_car_coin_pay, null);
        TextView coinText = contentView.findViewById(R.id.tv_pay_message);
        coinText.setText(activity.getResources().getString(R.string.confirm_use_car_coin, carCoin));

        XmDialog xmDialog = new XmDialog.Builder(activity)
                .setView(contentView)
                .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog))
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog tDialog) {
                        switch (view.getId()) {
                            case R.id.confirm_bt:
                                if (DoubleClickUtils.isFastDoubleClick(TIME_INTERVAL)) return;
                                ShopTrackManager.newSingleton().manualUpdateHint(EventConstant.NormalClick.ACTION_BUY_CONFIRM);
                                // TODO: 2019/5/14 人脸识别,先模拟高低配
                                if (XmCarConfigManager.hasFaceRecognition()) {
                                    tDialog.setOnActivityResult(new XmDialog.OnActivityResult() {
                                        @Override
                                        public void onActivityResult(XmDialog dialog, int requestCode, int resultCode, Intent data) {
                                            if (FaceVerifyActivity.REQUEST_CODE == requestCode) {
                                                if (resultCode == Activity.RESULT_OK) {
                                                    //TODO 车币购买逻辑处理
                                                    handleBuyBusiness(activity, type, productId, orderNum, callback);
                                                    dialog.dismiss();
                                                } else {
//                                                    XMToast.toastException(activity, R.string.verify_fail);
                                                }
                                            }
                                        }
                                    });
                                    FaceVerifyActivity.newInstance(tDialog.getActivity(),
                                            UserManager.getInstance().getCurrentUser(),
                                            FaceVerifyActivity.REQUEST_CODE);
                                } else {
                                    //TODO 车币购买逻辑处理
                                    handleBuyBusiness(activity, type, productId, orderNum, callback);
                                    tDialog.dismiss();
                                }
                                break;

                            case R.id.cancel_bt:
                                ShopTrackManager.newSingleton().manualUpdateHint(EventConstant.NormalClick.ACTION_BUY_CANCEL);
                                //Cancel, Nothing to do
                                tDialog.dismiss();
                                break;
                        }

                    }
                })
                .create();
        xmDialog.show();
    }






    /**
     * 扫码支付
     *
     * @param skuId        商品Id
     * @param productName  商品名称
     * @param cash         现金
     * @param carCoin      车币
     * @param onlyScanCode true 仅支持扫码支付  false 可切换车币支付
     */
    public void scanCodePayWindow(final FragmentActivity activity,
                                  @ResourceType final int type,
                                  final long skuId,
                                  final String productName,
                                  final String cash,
                                  final int carCoin,
                                  boolean onlyScanCode,
                                  final PaySuccessResultCallback callback) {
        if (!conditionIsLegal(activity)) return;
        final View contentView = buildDialogContentView(activity, cash, carCoin, onlyScanCode,true);
        final ImageView oneCodeImage = contentView.findViewById(R.id.iv_scan_code_pay);

        loading((ProgressSupport) activity);
        RequestManager.payResourceWithQRCode(type, skuId, new ResultCallback<XMResult<QrCodeBean>>() {
            @Override
            public void onSuccess(XMResult<QrCodeBean> result) {
                loadComplete((ProgressSupport) activity);
                QrCodeBean codeInfo = result.getData();
                ImageLoader.with(activity).load(codeInfo.getPayQrcode()).placeholder(R.drawable.place_holder).into(oneCodeImage);

                XmDialog xmDialog = new XmDialog.Builder(activity)
                        .setView(contentView)
                        .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                        .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog_middle))
                        .setCancelableOutside(false)
                        .addOnClickListener(R.id.go_to_car_coin_pay, R.id.iv_close_scan_code)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                endOrderTime();
                            }
                        })
                        .setOnViewClickListener(new OnViewClickListener() {
                            @Override
                            public void onViewClick(View view, XmDialog tDialog) {
                                tDialog.dismiss();
                                if (view.getId() == R.id.go_to_car_coin_pay) {
                                    endOrderTime();
                                    carCoinPayWindow(activity, type, skuId, productName, carCoin, true, callback);
                                } else if (view.getId() == R.id.iv_close_scan_code) {
                                    endOrderTime();
                                }
                            }
                        })
                        .create();
                xmDialog.show();

                //开启计时
                startOrderTime(xmDialog, skuId, codeInfo.getOrderNo(), callback);
            }

            @Override
            public void onFailure(int code, String msg) {
                loadComplete((ProgressSupport) activity);
                XMToast.toastException(activity, R.string.buy_failed);
            }
        });
    }




    /**
     * 通用Dialog
     */
    public void showCommonDialog(FragmentActivity activity, final DialogCommonCallback callback) {
        if (callback == null) return;
        final int layoutId = callback.getLayoutId() == 0 ? R.layout.dialog_car_coin_pay :
                callback.getLayoutId();
        View contentView = View.inflate(activity, layoutId, null);
        callback.prepare(contentView);

        XmDialog xmDialog = new XmDialog.Builder(activity)
                .setView(contentView)
                .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog_middle))
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog tDialog) {
                        if (layoutId == R.layout.dialog_car_coin_pay) {
                            switch (view.getId()) {
                                case R.id.confirm_bt:
                                    callback.onConfirm();
                                    break;

                                case R.id.cancel_bt:
                                    callback.onCancel();
                                    break;
                            }
                        } else {
                            callback.onClick(view, tDialog);
                        }

                        tDialog.dismiss();
                    }
                }).create();
        xmDialog.show();
    }


    /**
     * 购买实现
     */
    private void handleBuyBusiness(FragmentActivity context,
                                   @ResourceType int type,
                                   long productId,
                                   String orderNum,
                                   PaySuccessResultCallback callback) {
        String userId = LoginManager.getInstance().getLoginUserId();
        if (TextUtils.isEmpty(userId)) {
            XMToast.showToast(context, "用户不存在");
            return;
        }
        if (TextUtils.isEmpty(orderNum)) {
            buyResourceWithCarCoin(context, type, productId, callback);
        } else {
            actuallyBuy(context, productId, orderNum, callback);
        }
    }


    private void buyResourceWithCarCoin(final FragmentActivity context,
                                        @ResourceType final int type,
                                        final long skuId,
                                        final PaySuccessResultCallback callback) {
        loading((ProgressSupport) context);
        RequestManager.resourceOrderWithScore(type, skuId, new ResultCallback<XMResult<BuyBean>>() {
            @Override
            public void onSuccess(XMResult<BuyBean> result) {
                loadComplete((ProgressSupport) context);
                if (result == null || result.getData() == null) {
                    XMToast.toastException(context, R.string.buy_failed);
                    return;
                }
                BuyBean buyBean = result.getData();
                actuallyBuy(context, skuId, buyBean.getOrderNo(), callback);
            }

            @Override
            public void onFailure(int code, String msg) {
                loadComplete((ProgressSupport) context);
                XMToast.toastException(context, R.string.buy_failed);
            }
        });

    }


    private void actuallyBuy(final FragmentActivity context,
                             final long skuId,
                             String orderNumber,
                             final PaySuccessResultCallback callback) {
        loading((ProgressSupport) context);
        RequestManager.payResourceWithScore(orderNumber, new ResultCallback<XMResult<BuyBean>>() {
            @Override
            public void onSuccess(XMResult<BuyBean> result) {
                loadComplete((ProgressSupport) context);
                if (callback != null) {
                    callback.confirm();
                }
                Set<PayCallback> callbacks = new ArraySet<>(mPayCallbacks);
                for (PayCallback payCallback : callbacks) {
                    payCallback.onPaySuccess(false, skuId);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                loadComplete((ProgressSupport) context);
                if (code == 1100) {
                    //余额不足
                    getUserCoin(context);
                } else {
                    XMToast.toastException(context, R.string.buy_failed);
                    if (callback != null) {
                        callback.cancel();
                    }
                }
            }
        });
    }


    /**
     * TODO 在支付成功时调用
     *
     * @param callback 结果处理回调
     */
    public void buySuccess(FragmentActivity activity, final PaySuccessResultCallback callback) {
        View contentView = View.inflate(activity, R.layout.dialog_car_coin_pay, null);
        TextView coinText = contentView.findViewById(R.id.tv_pay_message);
        TextView confirmBt = contentView.findViewById(R.id.confirm_bt);
        TextView cancelBt = contentView.findViewById(R.id.cancel_bt);

        coinText.setText(R.string.congratulate_pay_success);
        confirmBt.setText(R.string.now_use_resource);
        cancelBt.setText(R.string.not_use_resource);

        XmDialog xmDialog = new XmDialog.Builder(activity)
                .setView(contentView)
                .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog_middle))
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog tDialog) {
                        switch (view.getId()) {
                            case R.id.confirm_bt:
                                if (callback != null) {
                                    callback.confirm();
                                }
                                break;

                            case R.id.cancel_bt:
                                if (callback != null) {
                                    callback.cancel();
                                }
                                break;
                        }
                        tDialog.dismiss();
                    }
                }).create();
        xmDialog.show();

    }


    private void getUserCoin(final FragmentActivity activity) {
        RequestManager.getUserCarCoin(new ResultCallback<XMResult<CoinAndSignInfo>>() {
            @Override
            public void onSuccess(XMResult<CoinAndSignInfo> result) {
                CoinAndSignInfo coinAndSignInfo = result.getData();
                balanceLackWindow(activity, coinAndSignInfo.getCarCoin());
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w("Obtain car coin error.");
            }
        });
    }


    /**
     * 余额不足
     */
    private void balanceLackWindow(final FragmentActivity activity, int balance) {
        View contentView = View.inflate(activity, R.layout.dialog_balance_lack, null);
        TextView earnCarCoin = contentView.findViewById(R.id.confirm_bt);
        TextView curCarCoinText = contentView.findViewById(R.id.tv_cur_car_coin);
        earnCarCoin.setText(R.string.earn_car_coin);
        curCarCoinText.setText(activity.getResources().getString(R.string
                .current_car_coin_balance, String.valueOf(balance)));

        final XmDialog xmDialog = new XmDialog.Builder(activity)
                .setView(contentView)
                .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog_middle))
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog tDialog) {
                        switch (view.getId()) {
                            //进入任务中心赚取车币
                            case R.id.confirm_bt:
                                if (!AppUtils.isAppInstalled(activity, PERSONAL_PKG)) {
                                    XMToast.toastException(activity, "应用未安装");
                                    return;
                                }
                                LaunchUtils.launchAppWithData(activity, PERSONAL_PKG,
                                        TASK_CENTER, null);
                                break;

                            case R.id.cancel_bt:
                                //Nothing to do
                                break;
                        }
                        tDialog.dismiss();
                    }
                })
                .create();
        xmDialog.show();
    }


    public void startOrderTime(final XmDialog xmDialog,
                               final long skuId,
                               final String orderNo,
                               final PaySuccessResultCallback callback) {
        currentLoopNumber = 0;
        OrderTimer.getInstance().startTimer(TIMER_INTERVAL, new OnTimerCallback() {
            @Override
            public void onTimer() {
                if (currentLoopNumber > QUERY_ORDER_STATUS_MAX_LOOP_NUMBERS) {
                    //查询订单状态， 限制最大轮询数
                    xmDialog.dismissAllowingStateLoss();
                    XMToast.toastException(xmDialog.getContext(), R.string.pay_timeout);
                } else {
                    ++currentLoopNumber;
                    queryOrderInfo(xmDialog, skuId, orderNo, callback);
                }
            }
        });
    }


    private void endOrderTime() {
        // 临时处理:不结束轮询,保证状态刷新
        //OrderTimer.getInstance().endTimer();
    }


    private void queryOrderInfo(final XmDialog xmDialog,
                                final long skuId,
                                String orderNo,
                                final PaySuccessResultCallback callback) {
        RequestManager.queryOrderInfo(orderNo, new ResultCallback<XMResult<QrCodeBean>>() {
            @Override
            public void onSuccess(XMResult<QrCodeBean> result) {
                if (result.getData() != null && result.getData().getOrderStatusId() == 3) {
                    xmDialog.dismissAllowingStateLoss();
                    // 说明购买成功
                    callback.confirm();
                    ShopTrackManager.newSingleton().manualUpdateEvent(TrackerEventType.EXPOSE, EventConstant.Expose.QR_CODE_BUY_SUCCESS);
                    Set<PayCallback> callbacks = new HashSet<>(mPayCallbacks);
                    for (final PayCallback payCallback : callbacks) {
                        payCallback.onPaySuccess(true, skuId);
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                //callback.cancel(); 扫码支付轮询处理无需处理
            }
        });
    }

    @NonNull
    private View buildDialogContentView(FragmentActivity activity,
                                        String cash,
                                        int carCoin,
                                        boolean onlyScanCode,
                                        boolean isPayIconShow) {
        final View contentView = View.inflate(activity, R.layout.dialog_scan_code_pay, null);
        TextView coinText = contentView.findViewById(R.id.tv_car_coin_text);
        TextView cashText = contentView.findViewById(R.id.tv_cash_message);

        TextView tv_suffix = contentView.findViewById(R.id.tv_buy);
        if (LanUtils.isEnglish()) {
            tv_suffix.setVisibility(View.GONE);
        } else {
            tv_suffix.setVisibility(View.VISIBLE);
        }

        LinearLayout carCoinPayLayout = contentView.findViewById(R.id.go_to_car_coin_pay);
        coinText.setText(String.valueOf(carCoin));
        cashText.setText(activity.getResources().getString(R.string.scan_code_pay_cash,
                cash));

        if (onlyScanCode) {
            carCoinPayLayout.setVisibility(View.GONE);
        }

        if(!isPayIconShow){//是否显示支付图标
            contentView.findViewById(R.id.ali_pay_icon).setVisibility(View.GONE);
            contentView.findViewById(R.id.we_chat_icon).setVisibility(View.GONE);
        }
        return contentView;
    }

    /**
     * 扫码支付
     */
    public void scanCodePayWindow(final FragmentActivity activity,
                                  final FetchQrCodeBean fetchQrCodeBean,
                                  final PaySuccessResultCallback callback) {
        if (!conditionIsLegal(activity)) return;
        String  cash =  fetchQrCodeBean.getOrderItems().get(0).getPrice();
        final View contentView = buildDialogContentView(activity, cash, 0, true,true);
        final ImageView oneCodeImage = contentView.findViewById(R.id.iv_scan_code_pay);
        loading((ProgressSupport) activity);
        RequestManager.payResourceWithQRCode(fetchQrCodeBean, new ResultCallback<XMResult<QrCodeBean>>() {
            @Override
            public void onSuccess(XMResult<QrCodeBean> result) {
                loadComplete((ProgressSupport) activity);
                QrCodeBean codeInfo = result.getData();
                ImageLoader.with(activity).load(codeInfo.getQrCode()).placeholder(R.drawable.place_holder).into(oneCodeImage);

                XmDialog xmDialog = new XmDialog.Builder(activity)
                        .setView(contentView)
                        .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                        .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog_middle))
                        .setCancelableOutside(false)
                        .addOnClickListener(R.id.iv_close_scan_code)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                endOrderTime();
                            }
                        })
                        .setOnViewClickListener(new OnViewClickListener() {
                            @Override
                            public void onViewClick(View view, XmDialog tDialog) {
                                tDialog.dismiss();
                                endOrderTime();
                            }
                        })
                        .create();
                xmDialog.show();

                //开启计时
                startOrderTime(xmDialog, codeInfo.getOrderId(), callback);
            }

            @Override
            public void onFailure(int code, String msg) {
                loadComplete((ProgressSupport) activity);
                String errorMsg = ResUtils.getString(activity, R.string.buy_failed);
                if (NetworkUtils.isConnected(activity)) {
                    errorMsg = activity.getString(R.string.buy_failuer);
                }
                XMToast.toastException(activity, errorMsg);
            }
        });
    }




    public void scanCodePayWindow(final FragmentActivity activity,
                                  final String orderNum,
                                  final String price,
                                  final long id,
                                  final boolean isFlow,
                                  final PaySuccessResultCallback callback) {
        if (!conditionIsLegal(activity)) return;
        final View contentView = buildDialogContentView(activity, price, 0, true,true);
        final ImageView oneCodeImage = contentView.findViewById(R.id.iv_scan_code_pay);
        loading((ProgressSupport) activity);
        RequestManager.payResourceWithQRCode(orderNum, new ResultCallback<XMResult<QrCodeBean>>() {
            @Override
            public void onSuccess(XMResult<QrCodeBean> result) {
                loadComplete((ProgressSupport) activity);
                QrCodeBean codeInfo = result.getData();
                ImageLoader.with(activity).load(codeInfo.getPayQrcode()).placeholder(R.drawable.place_holder).into(oneCodeImage);

                XmDialog xmDialog = new XmDialog.Builder(activity)
                        .setView(contentView)
                        .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                        .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog_middle))
                        .setCancelableOutside(false)
                        .addOnClickListener(R.id.iv_close_scan_code)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                endOrderTime();
                            }
                        })
                        .setOnViewClickListener(new OnViewClickListener() {
                            @Override
                            public void onViewClick(View view, XmDialog tDialog) {
                                tDialog.dismiss();
                                endOrderTime();
                            }
                        })
                        .create();
                xmDialog.show();

                if(isFlow){
                    startOrderTime(xmDialog, codeInfo.getId()+"", callback);
                }else{
                    startOrderTime(xmDialog,id ,orderNum, callback);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                loadComplete((ProgressSupport) activity);
                String errorMsg = ResUtils.getString(activity, R.string.buy_failed);
                if (NetworkUtils.isConnected(activity)) {
                    errorMsg = activity.getString(R.string.buy_failuer);
                }
                XMToast.toastException(activity, errorMsg);
            }
        });
    }



    private void startOrderTime(final XmDialog xmDialog,
                                final String orderNo,
                                final PaySuccessResultCallback callback) {
        currentLoopNumber = 0;
        OrderTimer.getInstance().startTimer(TIMER_INTERVAL, new OnTimerCallback() {
            @Override
            public void onTimer() {
                if (currentLoopNumber > QUERY_ORDER_STATUS_MAX_LOOP_NUMBERS) {
                    //查询订单状态， 限制最大轮询数
                    xmDialog.dismissAllowingStateLoss();
                    XMToast.toastException(xmDialog.getContext(), R.string.pay_timeout);
                } else {
                    ++currentLoopNumber;
                    queryOrderInfo(xmDialog, orderNo, callback);
                }
            }
        });
    }

    private void queryOrderInfo(final XmDialog xmDialog,
                                String orderNo,
                                final PaySuccessResultCallback callback) {
        RequestManager.queryOrderInfoNew(orderNo, new ResultCallback<XMResult<QrCodeBean>>() {
            @Override
            public void onSuccess(XMResult<QrCodeBean> result) {
                if (result.getData() != null && "1".equals(result.getData().getStatus())) {
                    xmDialog.dismissAllowingStateLoss();
                    // 说明购买成功
                    callback.confirm();
                    ShopTrackManager.newSingleton().manualUpdateEvent(TrackerEventType.EXPOSE, EventConstant.Expose.QR_CODE_BUY_SUCCESS);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                //callback.cancel(); 扫码支付轮询处理无需处理
            }
        });
    }

    private void loading(ProgressSupport support) {
        XMProgress.showProgressDialog(support, AppHolder.getInstance().getAppContext().getString(R.string.loading));
    }


    private void loadComplete(ProgressSupport support) {
        XMProgress.dismissProgressDialog(support);
    }

    public interface PayCallback {
        void onPaySuccess(boolean payWithQRCode, long skuId);
    }



    /**
     * 条件是否符合要求
     * @param activity
     * @return 返回false 条件不满足，true 条件满足
     */
    private boolean conditionIsLegal(final FragmentActivity activity) {
        if (!isValidTime()) return false; //有效时间内，防止重复点击
        if (!NetworkUtils.isConnected(activity)) {//是否有网络
            XMToast.toastException(activity, R.string.network_anomaly);
            return false;
        }
        if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SHOP_CAN_USE_NOT_BUY,
                new OnBlockCallback() {//账号限制
                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(activity, loginType.getPrompt(activity));
                        return true;
                    }
                })) return false;
        return true;
    }

    private boolean isValidTime() {
        long currentTime = System.currentTimeMillis();
        long defTime = currentTime - lastTime;
        if (defTime > TIME_INTERVAL) {
            lastTime = currentTime;
            return true;
        }
        return false;
    }
}
