package com.xiaoma.music.common.pay;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.common.model.PayBusinessEvent;
import com.xiaoma.music.common.pay.timer.OrderTimer;
import com.xiaoma.music.common.pay.ui.QRCodePayDialog;
import com.xiaoma.music.kuwo.model.VIPOrderModel;
import com.xiaoma.music.kuwo.model.VIPOrderStateModel;
import com.xiaoma.music.mine.callback.OnPayResultCallback;
import com.xiaoma.music.mine.model.OrderStatus;
import com.xiaoma.music.mine.model.PayInfo;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.progress.ProgressSupport;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/25 0025 17:45
 *   desc:   支付
 * </pre>
 */
public final class PayBusiness {

    private static final String TAG = PayBusiness.class.getSimpleName();
    private static final int INTERVAL_TIME = 1_000;
    private QRCodePayDialog qrCodePayDialog;

    private PayBusiness() {
    }

    public static PayBusiness getInstance() {
        return Holder.PAY_BUSINESS;
    }


    /**
     * 普通订单（非续费）
     *
     * @param goodsId 商品id
     * @param price   商品价格
     * @param month   有效期 月
     * @param type    商品类型
     */
    public void genrateNormalOrder(FragmentActivity activity, String goodsId, double price, int month, String type) {
        generateOrder(activity, false, goodsId, price, month, type, null);
    }


    /**
     * 生成订单
     *
     * @param renewal false 非续费    true 续费
     * @param goodsId 商品id
     * @param price   商品价格
     * @param month   有效期 月
     * @param type    商品类型
     */
    public void generateOrder(final FragmentActivity activity,
                              boolean renewal,
                              String goodsId,
                              double price,
                              int month,
                              String type,
                              OnPayResultCallback callback) {
        OnlineMusicFactory.getKWLogin().getPayToken((code, message, token) -> {
            if (code == -1 || TextUtils.isEmpty(token)) {
                KLog.w(TAG, "code: " + code + "   token: " + token);
                return;
            }

            VIPOrderModel vipOrderModel = new VIPOrderModel();
            vipOrderModel.setId(goodsId);
            vipOrderModel.setCnt(month);
            vipOrderModel.setPrice(price);
            vipOrderModel.setType(type);
            vipOrderModel.setToken(token);
            vipOrderModel.setAppId(MusicConstants.KwConstants.KW_APP_ID);
            generateOrder(activity, renewal, vipOrderModel, callback);
        });
    }


    private void generateOrder(final FragmentActivity activity,
                               boolean renewal,
                               final VIPOrderModel vipOrderModel,
                               OnPayResultCallback callback) {
        XMProgress.showProgressDialog((ProgressSupport) activity, activity.getString(R.string.base_loading));
        RequestManager.getInstance().createKWVIPOrder(vipOrderModel, new ResultCallback<XMResult<PayInfo>>() {
            @Override
            public void onSuccess(XMResult<PayInfo> result) {
                XMProgress.dismissProgressDialog((ProgressSupport) activity);
                handlePay(activity, renewal, vipOrderModel, result.getData(), callback);
            }

            @Override
            public void onFailure(int code, String msg) {
                XMProgress.dismissProgressDialog((ProgressSupport) activity);
                XMToast.toastException(activity, R.string.create_order_timeout);
            }
        });
    }


    private void handlePay(FragmentActivity activity,
                           boolean renewal,
                           VIPOrderModel vipOrderModel,
                           PayInfo payInfo,
                           OnPayResultCallback callback) {
        if (payInfo == null) {
            KLog.w(TAG, "PayInfo instance is null.");
            return;
        }

        if (qrCodePayDialog != null && qrCodePayDialog.isShowing()) {
            return;
        }

        //TODO 暂且只有vip付费， 扩展歌曲付费，则在QRCodePayDialog 内部处理VIPOrderModel
        qrCodePayDialog = new QRCodePayDialog();
        qrCodePayDialog.show(activity.getSupportFragmentManager(), TAG, payInfo.getQrCode());
        qrCodePayDialog.setOnQRCodePayDismissCallback(() -> {
            OrderTimer.getInstance().endTimer();
            qrCodePayDialog = null;
        });

        //开启计时,轮询状态
        VIPOrderStateModel stateModel = new VIPOrderStateModel();
        stateModel.setId(payInfo.getId());
        stateModel.setAppId(vipOrderModel.getAppId());
        stateModel.setToken(vipOrderModel.getToken());
        OrderTimer.getInstance().startTimer(INTERVAL_TIME, () -> loopOrderStatus(activity, qrCodePayDialog, stateModel, vipOrderModel, renewal, callback));
    }


    private void loopOrderStatus(FragmentActivity activity,
                                 QRCodePayDialog dialog,
                                 VIPOrderStateModel stateModel,
                                 VIPOrderModel vipOrderModel,
                                 boolean renewal,
                                 OnPayResultCallback callback) {
        RequestManager.getInstance().checkKWVIPOrderState(stateModel, new ResultCallback<XMResult<OrderStatus>>() {
            @Override
            public void onSuccess(XMResult<OrderStatus> result) {
                showPayStatus(dialog, result.getData(), activity, vipOrderModel, renewal, callback);
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w(TAG, "code: " + code + "   msg: " + msg);
            }
        });
    }


    private void showPayStatus(QRCodePayDialog dialog,
                               OrderStatus orderStatus,
                               FragmentActivity activity,
                               VIPOrderModel vipOrderModel,
                               boolean renewal,
                               OnPayResultCallback callback) {
        if (orderStatus == null) {
            KLog.w(TAG, "OrderStatus instance is null.");
            return;
        }

        if (orderStatus.getKWOrderStatus() == null) {
            KLog.w(TAG, "KWOrderStatus is null.");
            return;
        }

        switch (orderStatus.getKWOrderStatus()) {
            case KWOrderStatus.SUCCESS:
            case KWOrderStatus.FINISHED:
                dialog.dismiss();
                if (callback != null) {
                    callback.success();
                }

                if (renewal) {
                    XMToast.showToast(activity, R.string.renewal_success);
                } else {
                    jumpOnlineMusic(activity, vipOrderModel);
                }
                break;

            case KWOrderStatus.CREATE_ORDER:
                KLog.d(TAG, "已下单");
                break;

            case KWOrderStatus.PAY_FAILED:
                XMToast.toastException(activity, R.string.bought_failed);
                break;

            case KWOrderStatus.CANCEL:
                KLog.d(TAG, "订单已取消");
                break;
        }
    }


    private void jumpOnlineMusic(FragmentActivity activity, VIPOrderModel vipOrderModel) {
        View contentView = View.inflate(activity, R.layout.view_jump_online_music, null);
        TextView vipDescText = contentView.findViewById(R.id.tv_vip_open_success_desc_content);
        vipDescText.setText(activity.getString(R.string.vip_open_success_desc, vipOrderModel.getCnt()));
        XmDialog dialog = new XmDialog.Builder(activity)
                .setView(contentView)
                .setWidth(530)
                .setHeight(320)
                .addOnClickListener(R.id.tv_jump_online_music)
                .setOnViewClickListener((view, tDialog) -> {
                    if (view.getId() == R.id.tv_jump_online_music) {
                        //TODO 跳转在线音乐
                        EventBus.getDefault().post(new PayBusinessEvent(), EventBusTags.PLAY_MUSIC_ON_BOUGHT_SUCCESSED);
                        tDialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


    private static class Holder {
        private static final PayBusiness PAY_BUSINESS = new PayBusiness();
    }

}
