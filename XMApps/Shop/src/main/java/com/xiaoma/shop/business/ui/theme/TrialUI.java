package com.xiaoma.shop.business.ui.theme;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.impl.IOnDialogClickListener;
import com.xiaoma.ui.dialog.impl.XMCompatDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

/**
 * <des>
 * 试用流程
 * 1.初始化试用
 * 2.试用到期提醒
 *
 * @author YangGang
 * @date 2019/4/12
 */
public class TrialUI {

    private static void dispatchLeftSelect(ITrialSelectListener listener) {
        if (listener != null) {
            listener.onLeftSelected();
        }
    }

    public static void showTrialFirst(FragmentActivity activity, String titleBelong, String title, int dayToEnd, final ITrialSelectListener listener) {
        if (!NetworkUtils.isConnected(activity)) {
            XMToast.toastException(activity, R.string.network_anomaly);
            return;
        }
        final ConfirmDialog dialog = new ConfirmDialog(activity);
        dialog.setContent(activity.getResources().getString(R.string.skin_trial_msg, title, titleBelong, dayToEnd))
                .setPositiveButton(activity.getString(R.string.pay_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchLeftSelect(listener);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(activity.getString(R.string.pay_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();

//        View contentView = View.inflate(activity, R.layout.dialog_car_coin_pay, null);
//        TextView coinText = contentView.findViewById(R.id.tv_pay_message);
//
//        coinText.setText(activity.getResources().getString(R.string.skin_trial_msg, title, titleBelong, dayToEnd));
//
//        XmDialog xmDialog = new XmDialog.Builder(activity)
//                .setView(contentView)
//                .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
//                .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog))
//                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
//                .setOnViewClickListener(new OnViewClickListener() {
//                    @Override
//                    public void onViewClick(View view, XmDialog tDialog) {
//                        switch (view.getId()) {
//                            case R.id.confirm_bt:
//                                dispatchLeftSelect(listener);
//                                break;
//
//                            case R.id.cancel_bt:
//                                //Cancel, Nothing to do
//                                break;
//                        }
//                        tDialog.dismiss();
//                    }
//                })
//                .create();
//        xmDialog.show();
    }

    public static void showTrialEnd(final FragmentActivity activity, final SkinVersionsBean skin) {
        if (activity == null || skin == null)
            return;
        XMCompatDialog.createMiddleTextDialog()
                .setTitle(R.string.hint)
                .setMsg(activity.getString(R.string.trial_end_msg_format, skin.getAppName()))
                .setLeftClickListener(R.string.confirm_to_buy, new IOnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v) {
                        PayHandler.getInstance().scanCodePayWindow(activity,
                                ResourceType.SKIN,
                                skin.getId(),
                                String.format("【%s】系统皮肤", skin.getAppName()),
                                String.valueOf(skin.getLatestPrice()),
                                skin.getLatestScorePrice(),
                                skin.getLatestScorePrice() <= 0,
                                new PaySuccessResultCallback() {
                                    @Override
                                    public void confirm() {

                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                    }
                })
                .setRightClickListener(R.string.close, null)
                .showDialog(activity.getSupportFragmentManager());
    }

    public interface ITrialSelectListener {

        void onLeftSelected();

    }
}
