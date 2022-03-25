package com.xiaoma.shop.common.util;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.shop.BuildConfig;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.impl.SoundEffDownload;
import com.xiaoma.shop.business.model.VehicleSoundDbBean;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.ui.dialog.OnViewClickListener;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.MD5Utils;
import com.xiaoma.utils.ResUtils;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/20
 * @Describe:
 */

public class VehicleSoundUtils {


    public static void setPayType(VehicleSoundEntity.SoundEffectListBean bean) {
        int scorePrice = bean.getDiscountScorePrice();
        float price = bean.getDiscountPrice();
        if (scorePrice == 0 && price == 0) {
            bean.setPay(ShopContract.Pay.DEFAULT); //免费
            if (!bean.isBuy()) {//如果后台给的是未购买，设置成已购买
                bean.setBuy(true);
            }
        } else {
            if (scorePrice > 0) {
                if (price * 100 > 0) {
                    bean.setPay(ShopContract.Pay.COIN_AND_RMB); // 人民币 + 车币
                } else {
                    bean.setPay(ShopContract.Pay.COIN); //车币
                }
            } else {
                if (price * 100 > 0) {
                    bean.setPay(ShopContract.Pay.RMB); // 人民币
                }
            }
        }
    }

    public static int getRandomNum() {
        Random random = new Random();
        return random.nextInt(4);
    }

    /**
     * show 低配更新弹窗
     */
    public static XmDialog buildUpdateDialog(FragmentActivity activity, final IOnDialogClickListener listener) {
        final int layoutId = R.layout.dialog_update_vehicle_sound;
        View contentView = View.inflate(activity, layoutId, null);
        TextView tvContent = contentView.findViewById(R.id.tv_content_message);
        TextView tvConfirm = contentView.findViewById(R.id.confirm_bt);
        TextView tvCancel = contentView.findViewById(R.id.cancel_bt);

        String content_one = ResUtils.getString(activity, R.string.str_update_msg_one);
        String content_two = ResUtils.getString(activity, R.string.str_update_msg_two);
        String content = content_one + content_two;
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new AbsoluteSizeSpan((int) ResUtils.getDimension(activity, R.dimen.size_update_dialog_small)), content_one.length(), content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


        tvContent.setText(spannableString);
        tvConfirm.setText(R.string.str_update_left);
        tvCancel.setText(R.string.str_update_right);

        XmDialog xmDialog = new XmDialog.Builder(activity)
                .setView(contentView)
                .setCancelableOutside(false)
                .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_update_dialog))
                //                .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_update_dialog))
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog tDialog) {
                        switch (view.getId()) {
                            case R.id.confirm_bt:
                                tDialog.dismiss();
                                listener.onConfirm();
                                break;
                            case R.id.cancel_bt:
                                tDialog.dismiss();
                                listener.onCancel();
                                break;
                        }
                    }
                }).create();
        return xmDialog;
    }

    /**
     * show 高配更新弹窗
     */
    public static XmDialog buildProUpdateDialog(FragmentActivity activity, final IOnDialogClickListener listener) {
        final int layoutId = R.layout.dialog_update_vehicle_sound;
        View contentView = View.inflate(activity, layoutId, null);
        TextView tvContent = contentView.findViewById(R.id.tv_content_message);
        TextView tvConfirm = contentView.findViewById(R.id.confirm_bt);
        TextView tvCancel = contentView.findViewById(R.id.cancel_bt);

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) tvContent.getLayoutParams();
        lp.bottomMargin = 30;
        lp.topMargin = 20;
        tvContent.setLayoutParams(lp);


        tvContent.setText(R.string.str_pro_update_msg);
        tvConfirm.setText(R.string.str_update_left);
        tvCancel.setText(R.string.str_update_right);

        XmDialog xmDialog = new XmDialog.Builder(activity)
                .setView(contentView)
                .setCancelableOutside(false)
                .setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.width_pro_update_dialog))
                //                .setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.height_update_dialog))
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog tDialog) {
                        switch (view.getId()) {
                            case R.id.confirm_bt:
                                listener.onConfirm();
                                break;

                            case R.id.cancel_bt:
                                listener.onCancel();
                                break;
                        }
                        tDialog.dismiss();
                    }
                }).create();
        return xmDialog;
    }

    /**
     * 是否是车机高配
     *
     * @return
     */
    public static boolean isPro() {
        return XmCarConfigManager.isHighEndLcd();
    }

    /**
     * 是否能使用仪表音效
     *
     * @return
     */
    public static boolean canUseInstrumentSound() {
        if (BuildConfig.BUILD_PLATFORM == "PAD") {//pad条件判断直接跳过
            return true;
        }
        if (VehicleSoundUtils.isPro()) {
            return true;
        } else if (!VehicleSoundUtils.isPro() && !UpdateOtaManager.getInstance().getVehicleState()) {
            return false;
        }
        return true;
    }

    /**
     * 判断资源是否在被使用
     *
     * @param url
     * @return
     */
    public static boolean theResIsUsing(String url, @ResourceType int type) {
        boolean result = false;
        String content = "";
        switch (type) {
            case ResourceType.VEHICLE_SOUND:
                content = FileUtils.read(ConfigManager.FileConfig.getGlobalAudioVehicleConfigFile());
                break;
            case ResourceType.INSTRUMENT_SOUND:
                content = FileUtils.read(ConfigManager.FileConfig.getGlobalInstrumentVehicleConfigFile());
                break;
        }
        result = !TextUtils.isEmpty(content) && url.contains(content);
        return result;
    }

    /**
     * 购买产品
     */
    public static void buyProduct(VehicleSoundEntity.SoundEffectListBean bean,
                                  @ResourceType int type,
                                  FragmentActivity activity,
                                  PaySuccessResultCallback callback) {
        if (bean == null) return;
        switch (bean.getPay()) {
            case ShopContract.Pay.DEFAULT:
            case ShopContract.Pay.COIN:
                PayHandler.getInstance().carCoinPayWindow(
                        activity,
                        type,
                        bean.getId(),
                        bean.getThemeName(),
                        (int) bean.getDiscountScorePrice(),
                        false,
                        callback);
                break;
            case ShopContract.Pay.COIN_AND_RMB:
                PayHandler.getInstance().scanCodePayWindow(
                        activity,
                        type,
                        bean.getId(),
                        bean.getThemeName(),
                        String.valueOf(bean.getDiscountPrice()),
                        (int) bean.getDiscountScorePrice(),
                        false,
                        callback);
                break;
            case ShopContract.Pay.RMB:
                PayHandler.getInstance().scanCodePayWindow(
                        activity,
                        type,
                        bean.getId(),
                        bean.getThemeName(),
                        String.valueOf(bean.getDiscountPrice()),
                        0,
                        true,
                        callback);
                break;
        }

    }

    /**
     * 使用资源文件
     *
     * @param content
     * @return
     */
    public static boolean useThisSource(String content, String filePath) {
        return FileUtils.write(content, new File(filePath), false);

    }

    public static boolean useThisSound(String content, String path, int type) {
        String filePath;
        switch (type) {
            case ResourceType.VEHICLE_SOUND:
                filePath = ConfigManager.FileConfig.getGlobalAudioVehicleConfigFile().getAbsolutePath();
                break;
            default:
            case ResourceType.INSTRUMENT_SOUND:
                filePath = ConfigManager.FileConfig.getGlobalInstrumentVehicleConfigFile().getAbsolutePath();
                break;
        }
        return FileUtils.write(content, new File(filePath), false);

    }

    public static String getFileName(String url) {
        String[] split = url.split("\\/");
        return split[split.length - 1];
    }

    public static int findPositionByUrl(List<VehicleSoundEntity.SoundEffectListBean> data, String justDownloadedUrl) {
        if (TextUtils.isEmpty(justDownloadedUrl)) return -1;
        for (int i = 0; i < data.size(); i++) {
            if (justDownloadedUrl.equals(data.get(i).getFilePath())) {
                return i;
            }
        }
        return -1;
    }

    public static @VehicleSoundType.DownloadStatus
    int theResIsDownloaded(VehicleSoundEntity.SoundEffectListBean bean,
                           @ResourceType int type,
                           SoundEffDownload download) {
        int result = VehicleSoundType.DownloadStatus.NONE;
        if (download == null || bean == null) return result;
        VehicleSoundDbBean dbBean = VehicleSoundDbUtils.query(bean.getId(), type);
        if (dbBean == null) return result;
        String currentName = VehicleSoundUtils.getFileName(bean.getFilePath());
        String historyName = VehicleSoundUtils.getFileName(dbBean.getFilePath());
        if (!TextUtils.isEmpty(currentName) && currentName.equals(historyName)) {//如果已经下载的文件名和后台文件一致
            result = download.isDownloadSuccess(bean) ? VehicleSoundType.DownloadStatus.COMPLETE : VehicleSoundType.DownloadStatus.NONE;
        } else if (!TextUtils.isEmpty(currentName)) {//文件名不一致，可能存在更新状态
            result = isDownloadSuccess(dbBean.getFilePath(), type) ? VehicleSoundType.DownloadStatus.UPDATE : VehicleSoundType.DownloadStatus.NONE;
        } else {
            result = VehicleSoundType.DownloadStatus.NONE;
        }
        return result;
    }

    public static boolean isDownloadSuccess(String fileUrl, int type) {
        if (TextUtils.isEmpty(fileUrl)) return false;
        String fileName = MD5Utils.getMD5String(fileUrl);
        if (TextUtils.isEmpty(fileName)) return false;
        File folder;
        switch (type) {
            default:
            case ResourceType.VEHICLE_SOUND:
                folder = ConfigManager.FileConfig.getHUSoundEffDownloadFolder();
                break;
            case ResourceType.INSTRUMENT_SOUND:
                folder = ConfigManager.FileConfig.getLCDSoundEffDownloadFolder();
                break;
        }

        File downFile = new File(folder, fileName);
        return downFile != null && downFile.exists();
    }

    public abstract static class IOnDialogClickListener {
        public abstract void onConfirm();

        public abstract void onCancel();


    }

}
