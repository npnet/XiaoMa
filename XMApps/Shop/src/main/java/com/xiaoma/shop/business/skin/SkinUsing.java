package com.xiaoma.shop.business.skin;

import android.app.Activity;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarSensorManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.model.GearData;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.SkinTrialResult;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.manager.TrialRestoreService;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinInfo;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by LKF on 2019-6-20 0020.
 */
public class SkinUsing {
    private static final String TAG = "SkinUsing";
    private static final int TRIAL_JOB_ID = 8605;

    public static void useSkin(final BaseFragment fragment, final SkinVersionsBean skin) {
        FragmentActivity act;
        if (fragment == null
                || (act = fragment.getActivity()) == null)
            return;
        final ConfirmDialog dialog = new ConfirmDialog(act);
        String content_one = act.getString(R.string.skin_using_condition_one);
        String content_two = act.getString(R.string.skin_using_condition_two);
        String content = content_one + content_two;
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        AbsoluteSizeSpan smallAss = new AbsoluteSizeSpan(22);
        AbsoluteSizeSpan largeAss = new AbsoluteSizeSpan(28);
        ssb.setSpan(largeAss, 0, content_one.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.setSpan(smallAss, content_one.length(), content.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);

        dialog.setContent(ssb)
                .setPositiveButton(act.getString(R.string.state_use), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 换肤达成条件: 处于P档,电子驻车拉起
                        Context context = v.getContext();
                        if (isConditionMeet()) {
                            File skinZipFile = SkinDownload.getInstance().getDownloadFile(skin);
                            if (skinZipFile != null && skinZipFile.exists()) {
                                File skinUnzipDir = SkinUnzip.unzipToUsingDir(skinZipFile);
                                if (skinUnzipDir != null && skinUnzipDir.exists()) {
                                    if (doUseSkin(context, skin, skinUnzipDir)) {
                                        XMToast.toastSuccess(context, R.string.successful_use);
                                    } else {
                                        XMToast.toastException(context, R.string.hint_switch_skin_fail);
                                    }
                                } else {
                                    XMToast.toastException(context, R.string.unpack_failed);
                                }
                            } else {
                                SkinDownload.getInstance().start(skin);
                            }
                        } else {
                            XMToast.toastException(context, R.string.condition_unsatisfied_check_retry);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(act.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        try {
            TextView tvContent = dialog.getView().findViewById(R.id.confirm_dialog_content);
            if (tvContent != null) {
                tvContent.setGravity(Gravity.START);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();
    }

    /*private static boolean useSkin(Context context, SkinVersionsBean skin, File skinDir) {
        return doUseSkin(context, skin, skinDir);
    }*/

    public static boolean isUsing(int skinId) {
        SkinInfo skinInfo = SkinUtils.getSkinMsg();
        return skinInfo != null && Objects.equals(skinInfo.skinId, String.valueOf(skinId));
    }

    public static boolean isUsing(SkinVersionsBean skin) {
        return skin != null && isUsing(skin.getId());
    }

    public static void trialSkin(final BaseFragment fragment, final SkinVersionsBean skin) {
        if (fragment == null)
            return;
        final Activity act = fragment.getActivity();
        if (act == null)
            return;
        if (!NetworkUtils.isConnected(act)) {
            XMToast.toastException(act, act.getString(R.string.no_network));
            return;
        }
        SeriesAsyncWorker.create()
                .next(new Work() {
                    @Override
                    public void doWork(Object lastResult) {
                        if (fragment.isDestroy())
                            return;
                        // 试用上报
                        RequestManager.reportSkinTrial(skin.getId(), new ResultCallback<XMResult<Object>>() {
                            @Override
                            public void onSuccess(XMResult result) {
                                if (fragment.isDestroy())
                                    return;
                                if (result != null && result.isSuccess()) {
                                    doNext();
                                } else {
                                    XMToast.toastException(act, R.string.trial_failed);
                                }
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                if (fragment.isDestroy())
                                    return;
                                XMToast.toastException(act,
                                        StringUtil.optString(msg, act.getString(R.string.trial_failed)));
                            }
                        });
                    }
                }).next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                // 试用状态查询
                RequestManager.checkSkinCanTrial(skin.getId(), new ResultCallback<XMResult<SkinTrialResult>>() {
                    @Override
                    public void onSuccess(XMResult<SkinTrialResult> result) {
                        if (fragment.isDestroy())
                            return;
                        SkinTrialResult trialResult = null;
                        if (result != null && result.isSuccess()
                                && (trialResult = result.getData()) != null
                                && trialResult.isCanTrial()) {
                            doNext();
                        } else {
                            String errMsg = trialResult != null ? trialResult.getTrialDesc() : null;
                            XMToast.toastException(act,
                                    StringUtil.optString(errMsg, act.getString(R.string.trial_failed)));
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        if (fragment.isDestroy())
                            return;
                        XMToast.toastException(act,
                                StringUtil.optString(msg, act.getString(R.string.trial_failed)));
                    }
                });
            }
        }).next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                if (fragment.isDestroy())
                    return;
                File zip = SkinDownload.getInstance().getDownloadFile(skin);
                if (zip != null && zip.exists()) {
                    File unzipDir = SkinUnzip.unzipToUsingDir(zip);
                    if (unzipDir != null && unzipDir.exists()) {
                        if (trialSkin(act, skin, unzipDir)) {
                            XMToast.toastSuccess(act, R.string.trial_success);
                        } else {
                            XMToast.toastException(act, R.string.trial_failed);
                        }
                    } else {
                        XMToast.toastException(act, R.string.unpack_failed);
                    }
                } else {
                    SkinDownload.getInstance().start(skin);
                }
            }
        }).start();
    }

    /**
     * 皮肤试用
     */
    private static boolean trialSkin(Context context, SkinVersionsBean skin, File skinDir) {
        if (skin == null
                || skin.getCanTry() < 0
                || skin.getTrialTime() <= 0)
            return false;
        if (doUseSkin(context, skin, skinDir)) {
            if (skin.getLatestScorePrice() > 0 || skin.getLatestPrice() > 0) {
                // 开启到期后自动还原默认皮肤的任务
                long trialMs = TimeUnit.DAYS.toMillis(skin.getTrialTime());
                PersistableBundle extras = new PersistableBundle();
                extras.putString("skin", GsonHelper.toJson(skin));
                JobScheduler js = (JobScheduler) context.getSystemService(Service.JOB_SCHEDULER_SERVICE);
                js.schedule(new JobInfo.Builder(TRIAL_JOB_ID, new ComponentName(context, TrialRestoreService.class))
                        .setPersisted(true)
                        .setExtras(extras)
                        .setMinimumLatency(trialMs)
                        .setOverrideDeadline(trialMs)
                        .build());
            }
            return true;
        }
        return false;
    }

    private static boolean doUseSkin(Context context, SkinVersionsBean skin, File skinDir) {
        boolean rlt = false;
        if (skin != null
                && skinDir != null
                && skinDir.exists()
                && skinDir.isDirectory()) {
            try {
                XmSkinManager.getInstance().loadSkinByPath(
                        context, String.valueOf(skin.getId()), skinDir.getPath(), skin.getSkinStyle());
                skin.setSelect(false);
                rlt = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, String.format("doUseSkin: { skinDir: %s, writeRlt: %s }", skinDir, rlt));
        if (rlt) {
            try {
                XmCarVendorExtensionManager.getInstance().setTheme(skin.getSkinStyle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 记录当前用户使用的皮肤
            saveSkinForUser(context, skin);
        }
        return rlt;
    }

    private static final String TP_SKIN_PREFIX = "using_skin_";

    private static void saveSkinForUser(Context context, SkinVersionsBean skin) {
        long curUid = Integer.MIN_VALUE;
        User u = UserManager.getInstance().getCurrentUser();
        if (u != null) {
            curUid = u.getId();
        }
        if (curUid != Integer.MIN_VALUE) {
            DownloadStatus status = SkinDownload.getInstance().getDownloadStatus(skin);
            if (status != null && new File(status.downFilePath).exists()) {
                Log.i(TAG, String.format("saveSkinForUser: uid: %s", curUid));
                DownloadedSkin downloadedSkin = new DownloadedSkin(skin, status.downFilePath);
                TPUtils.put(context, TP_SKIN_PREFIX + curUid, GsonHelper.toJson(downloadedSkin));
            } else {
                Log.e(TAG, String.format("saveSkinForUser: uid: %s, downUrl: %s",
                        curUid, status != null ? status.downFilePath : "<Null DownloadStatus>"));
            }
        } else {
            Log.e(TAG, String.format("saveSkinForUser: Invalid uid: %s", curUid));
        }
    }

    static boolean restoreSkinForUser(Context context) {
        long curUid = Integer.MIN_VALUE;
        User u = UserManager.getInstance().getCurrentUser();
        if (u != null) {
            curUid = u.getId();
        }
        if (curUid != Integer.MIN_VALUE) {
            if (isConditionMeet()) {
                String json = TPUtils.get(context, TP_SKIN_PREFIX + curUid, "");
                DownloadedSkin downloadedSkin = GsonHelper.fromJson(json, DownloadedSkin.class);
                if (downloadedSkin != null &&
                        downloadedSkin.skin != null &&
                        !TextUtils.isEmpty(downloadedSkin.skinZipPath) &&
                        new File(downloadedSkin.skinZipPath).exists()) {
                    File unZipDir = SkinUnzip.unzipToUsingDir(downloadedSkin.skinZipPath);
                    if (unZipDir != null && unZipDir.exists()) {
                        Log.i(TAG, String.format("restoreSkinForUser: Success zipPath: %s", downloadedSkin.skinZipPath));
                        return doUseSkin(context, downloadedSkin.skin, unZipDir);
                    } else {
                        // 解压失败
                        Log.e(TAG, "restoreSkinForUser: Unzip failed !!!");
                    }
                } else {
                    // 没有换肤记录或下载的zip文件不存在
                    Log.e(TAG, String.format("restoreSkinForUser: Invalid Obj = %s", json));
                }
            } else {
                // 不满足换肤条件
                Log.e(TAG, "restoreSkinForUser: Condition not meet !!!");
            }
        } else {
            // 用户ID无效
            Log.e(TAG, String.format("restoreSkinForUser: Invalid uid: %s", curUid));
        }
        return false;
    }

    public static boolean isConditionMeet() {
        // 换肤达成条件: 处于P档,电子驻车拉起
        GearData gearData = XmCarSensorManager.getInstance().getCurrentGearData();
        boolean isEPBLocked = XmCarVendorExtensionManager.getInstance().isEPBLocked();
        Log.e(TAG, String.format("isConditionMeet: gear = %s, isEPBLocked = %s",
                gearData != null ? gearData.gear : "null", isEPBLocked));
        return (gearData == null || SDKConstants.CarGearMode.GEAR_P == gearData.gear)
                && isEPBLocked;
    }

    private static class DownloadedSkin {
        SkinVersionsBean skin;
        String skinZipPath;

        DownloadedSkin(SkinVersionsBean skin, String skinZipPath) {
            this.skin = skin;
            this.skinZipPath = skinZipPath;
        }
    }
}
