package com.xiaoma.shop.common.manager.update;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.fsl.android.OtaBinderPool;
import com.fsl.android.uniqueota.UniqueOtaClient;
import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.fsl.android.uniqueota.UniqueOtaListener;
import com.xiaoma.component.AppHolder;
import com.xiaoma.shop.R;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.UpdateResouceType;
import com.xiaoma.shop.common.util.UpdateOtaInfoDbUtils;
import com.xiaoma.shop.common.util.UpdateOtaUtils;
import com.xiaoma.shop.common.util.VehicleSoundUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.constant.SoundUpdate;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/19
 * @Describe:
 */

public class UpdateOtaManager {

    private static boolean bindService = false;//是否绑定服务成功
    private Context context;

    private UniqueOtaClient mUniqueOtaClient;
    private CopyOnWriteArrayList<OnUpdateCallback> callbacks;
    private ConcurrentHashMap<Integer, UpdateOtaInfo> infos;

    private UpdateOtaManager() {
        callbacks = new CopyOnWriteArrayList<>();
        infos = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, UpdateOtaInfo> getInfos() {
        return infos;
    }

    public static UpdateOtaManager getInstance() {
        return Holder.sUpdateOtaManager;
    }

    public static class Holder {
        private static UpdateOtaManager sUpdateOtaManager = new UpdateOtaManager();
    }

    public void initService(final Context context) {
        if (bindService) {
            return;
        }
        this.context = context;
        final OtaBinderPool binderPool = OtaBinderPool.getInstance(context);
        binderPool.setConnListener(new OtaBinderPool.ServiceConnListener() {
            @Override
            public void onResult(int i) {
                if (i == OtaBinderPool.BINDER_SUCCEED) {
                    KLog.i("filOut| " + "[onResult]-> BINDER_SUCCEED");
                    bindService = true;
                    registerCheckSoundUpdate(context);
                    IBinder binder = binderPool.queryClient(OtaBinderPool.UNIQUE_SERVER_OTA);
                    mUniqueOtaClient = new UniqueOtaClient(binder);
                    mUniqueOtaClient.registUniqueOtaListener(new UniqueOtaListener() {
                        /**
                         * 文件传输允许信号
                         * @param ecu    ecu: HU 主机;IC_H 高配仪表;IC_L 低配仪表;	ROBOT 3D
                         * @param state  state: ALLOW 允许;DISALLOW 不允许; INACTIVE 无动作
                         */
                        @Override
                        public void onUpdateFeedback(int ecu, int state) throws RemoteException {
                            KLog.i("filOut| " + "[onUpdateFeedback]->ecu :" + ecu + "   state: " + state);
                            if (state == UniqueOtaConstants.UpdateState.ALLOW) {//succeed
                                handleInstallState(UpdateOtaInfo.InstallState.INSTALL_PRE, ecu);
                            } else if (state == UniqueOtaConstants.UpdateState.DISALLOW) {//failure
                                handleInstallState(UpdateOtaInfo.InstallState.INSTALL_FAILED, ecu);
                                dispatchHandleFailure(ecu);
                            }
                        }

                        /**
                         * 文件接收状态信号
                         * @param ecu    ecu: HU 主机;IC_H 高配仪表;IC_L 低配仪表;	ROBOT 3D
                         * @param state  state: ERROR 错误;	FINISH 完成;IN_PROGRESS 接收中;INACTIVE 无动作
                         */
                        @Override
                        public void onTransferState(int ecu, int state) throws RemoteException {
                            KLog.i("filOut| " + "[onTransferState]->ecu :" + ecu + "   state: " + state);
                            if (state == UniqueOtaConstants.TransferState.FINISH) {//succeed
                                handleInstallState(UpdateOtaInfo.InstallState.INSTALLING, ecu, 0);
                                UpdateOtaManager.this.soundUpgrade(ecu);
                                dispatchHandleSuccess(ecu, UpdateOtaInfo.InstallState.INSTALLING);
                            } else if (state == UniqueOtaConstants.TransferState.ERROR) {
                                handleInstallState(UpdateOtaInfo.InstallState.INSTALL_FAILED, ecu);
                                dispatchHandleFailure(ecu);

                            }
                        }

                        /**
                         * 安装进度
                         */
                        @Override
                        public void onInstallProgress(int ecu, int progress) throws RemoteException {
                            UpdateOtaUtils.beginExecution();
                            handleInstallState(UpdateOtaInfo.InstallState.INSTALLING, ecu, progress);
                            KLog.i("filOut| " + "[onInstallProgress]->ecu :" + ecu + "   progress: " + progress);
                            dispatchHandleProgress(ecu, UpdateOtaInfo.InstallState.INSTALLING, progress);
                        }

                        /**
                         * 安装结果
                         * @param ecu     ecu: HU 主机;IC_H 高配仪表;IC_L 低配仪表;	ROBOT 3D
                         * @param result  result: FAIL 失败;INACTIVE 无动作;SUCCEED 成功
                         * @throws RemoteException
                         */
                        @Override
                        public void onInstallResult(int ecu, int result) throws RemoteException {
                            KLog.i("filOut| " + "[onInstallResult]->ecu :" + ecu + "   result: " + result);
                            if (result == UniqueOtaConstants.InstallResult.SUCCEED) {//succeed
                                handleInstallState(UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL, ecu);
                                dispatchHandleSuccess(ecu, UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL);
                            } else if (result == UniqueOtaConstants.InstallResult.FAIL) {
                                handleInstallState(UpdateOtaInfo.InstallState.INSTALL_FAILED, ecu);
                                dispatchHandleFailure(ecu);
                            }

                        }

                        /**
                         * 3D 个性化文件删除信号
                         * @param state  state: FAIL 失败;INACTIVE 无动作;SUCCEED 成功
                         */
                        @Override
                        public void onRobotDeleteFeedback(int state) throws RemoteException {
                            int ecu = UniqueOtaConstants.EcuId.ROBOT;
                            KLog.i("filOut| " + "[onRobotDeleteFeedback]->onRobotDeleteFeedback]:ecu :" + ecu + "   state: " + state);
                            if (state == UniqueOtaConstants.RobotDelState.SUCCEED) {//succeed
                                handleInstallState(UpdateOtaInfo.InstallState.ROBOT_DELETE_S, ecu);
                                dispatchHandleSuccess(ecu, UpdateOtaInfo.InstallState.ROBOT_DELETE_S);
                            } else if (state == UniqueOtaConstants.RobotDelState.FAIL) {
                                handleInstallState(UpdateOtaInfo.InstallState.INSTALL_FAILED, ecu);
                                dispatchHandleFailure(ecu);
                            }
                        }

                        /**
                         * 判断车辆是否处于允许升级音效的状态
                         * @param states   6位数组分别代表以上六种状态 0:满足状态 1:不满足状态
                         */
                        @Override
                        public void onVehicleStateChanged(int[] states) throws RemoteException {
                            if (states != null) {
                                for (int j = 0; j < states.length; j++) {
                                    KLog.i("filOut| " + "[onVehicleStateChanged]->j " + j + "  " + states[j]);
                                }
                            }
                        }
                    });
                } else if (i == OtaBinderPool.BINDER_FAILED) {
                    KLog.i("filOut| " + "[onResult]->BINDER_FAILED");
                }
            }
        });
        binderPool.prepare();
        KLog.i("filOut| " + "[initService]->hash prepared");
    }


    /**
     * 处理有安装进度的安装状态
     */
    private void handleInstallState(int installState, int ecu, int progress) {
        UpdateOtaInfo updateOtaInfo = infos.get(ecu);
        if (updateOtaInfo == null) {
            updateOtaInfo = UpdateOtaInfoDbUtils.query(ecu);
            if (updateOtaInfo == null) return;
            infos.put(ecu, updateOtaInfo);
        }
        updateOtaInfo.setInstallState(installState);
        updateOtaInfo.setProgress(progress);
        UpdateOtaInfoDbUtils.save(updateOtaInfo);
    }

    /**
     * 处理安装状态
     */
    private void handleInstallState(int installState, int ecu) {
        UpdateOtaInfo updateOtaInfo = infos.get(ecu);
        if (updateOtaInfo == null) {
            updateOtaInfo = UpdateOtaInfoDbUtils.query(ecu);
            if (updateOtaInfo == null) return;
            infos.put(ecu, updateOtaInfo);
        }
        updateOtaInfo.setInstallState(installState);
        UpdateOtaInfoDbUtils.save(updateOtaInfo);
        KLog.i("filOut| " + "[handleInstallState]->save install result: successful");
        if (installState == UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL) {
            VehicleSoundUtils.useThisSound(VehicleSoundUtils.getFileName(updateOtaInfo.getFileUrl()), updateOtaInfo.getFileUrl(), updateOtaInfo.getResType());
        }
        if (installState == UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL || installState == UpdateOtaInfo.InstallState.INSTALL_FAILED) {
            UpdateOtaUtils.endExecution();
        }
    }

    /**
     * 分发进度 回调
     */
    private void dispatchHandleProgress(int ecu, int installState, int progress) {
        for (OnUpdateCallback callback : callbacks) {
            if (callback.getEcu() == ecu) {
                UpdateOtaInfo info = infos.get(ecu);
                if (info == null) {
                    info = new UpdateOtaInfo();
                    infos.put(ecu, info);
                }
                info.setProgress(progress);
                info.setInstallState(installState);
                callback.onSuccess(info);
                callback.notifyDataSetChange(info);
            }
        }
    }

    private void dispatchCopyFile(int ecu, String fileUrl) {
        for (OnUpdateCallback callback : callbacks) {
            if (callback.getEcu() == ecu) {
                UpdateOtaInfo info = new UpdateOtaInfo();
                info.setEcu(ecu);
                info.setProgress(0);
                info.setFileUrl(fileUrl);
                info.setInstallState(UpdateOtaInfo.InstallState.COPY_FILE);
                infos.put(ecu, info);
                callback.onSuccess(info);
                callback.notifyDataSetChange(info);
            }
        }
    }

    /**
     * 分发 成功回调
     */
    private void dispatchHandleSuccess(int ecu, int installState) {
        for (OnUpdateCallback callback : callbacks) {
            if (callback.getEcu() == ecu) {
                UpdateOtaInfo info = infos.get(ecu);
                if (info == null) {
                    info = new UpdateOtaInfo();
                    infos.put(ecu, info);
                }
                info.setInstallState(installState);
                callback.onSuccess(info);
            }
        }
    }

    /**
     * 分发失败回调
     */
    private void dispatchHandleFailure(int ecu) {
        for (OnUpdateCallback callback : callbacks) {
            if (callback.getEcu() == ecu) {
                UpdateOtaInfo info = infos.get(ecu);
                if (info == null) {
                    info = new UpdateOtaInfo();
                    info.setInstallState(UpdateOtaInfo.InstallState.INSTALL_FAILED);
                    infos.put(ecu, info);
                }
                callback.onFailure(info);
            }
        }
    }

    private boolean checkIsNull(int ecu, boolean handleCallbacks) {
        boolean result = mUniqueOtaClient == null;
        if (result) {
            //            UpdateOtaUtils.beginExecution();
            KLog.i("filOut| " + "[checkIsNull]->the uniqueOtaClient is NULL");
            if (!handleCallbacks || ecu == -1) return result;
            handleInstallState(UpdateOtaInfo.InstallState.INSTALL_FAILED, ecu);
            dispatchHandleFailure(ecu);
        }
        return result;
    }

    public void registerCallback(OnUpdateCallback callback) {
        if (callbacks == null || callbacks.contains(callback)) return;
        callbacks.add(callback);
    }

    public void unRegisterCallback(OnUpdateCallback callback) {
        if (callbacks == null || !callbacks.contains(callback)) return;
        callbacks.remove(callback);
    }

    /**************************************************函数实现**********************************************************/

    /**
     * 删除自定义3D形象的接口
     */
    public void delete3dFile() {
        if (checkIsNull(-1, false)) return;
        mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.DELETE_ROBOT_FILE,
                UniqueOtaConstants.CommandState.ACTIVE);
    }


    /**
     * 音效文件升级
     *
     * @param ecu
     * @return
     */
    public void soundUpgrade(final int ecu) {
        if (checkIsNull(ecu, true)) return;
        switch (ecu) {
            case UniqueOtaConstants.EcuId.HU://音响
                KLog.i("filOut| " + "[soundUpgrade]->升级音响");
                mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.INSTALL_HU,
                        UniqueOtaConstants.CommandState.ACTIVE);
                break;
            case UniqueOtaConstants.EcuId.IC_H://高配仪表
                KLog.i("filOut| " + "[soundUpgrade]->升级高配仪表");
                mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.INSTALL_ICH,
                        UniqueOtaConstants.CommandState.ACTIVE);
                break;
            case UniqueOtaConstants.EcuId.IC_L://低配仪表
                KLog.i("filOut| " + "[soundUpgrade]->升级低配仪表");
                // TODO: 2019/7/25 暂时延迟，待仪表方修改完后去除
                ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.INSTALL_ICL,
                                UniqueOtaConstants.CommandState.ACTIVE);
                    }
                }, 500);

                break;
            case UniqueOtaConstants.EcuId.ROBOT:
                mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.UPDATE_INFORM_ROBOT, UniqueOtaConstants.CommandState.ACTIVE);
                break;
        }

    }

    public void pushSoundFile(final String fileUrl,
                              @ResourceType final int type,
                              @UpdateResouceType final int updateType,
                              final int ecu) {
        if (checkIsNull(ecu, true)) return;
        UpdateOtaUtils.beginExecution();
        dispatchCopyFile(ecu, fileUrl);//文件拷贝可能有点耗时，这里开始显示进度，效果更优
        handleInstallState(UpdateOtaInfo.InstallState.COPY_FILE, ecu);
        UpdateOtaUtils.pushTheFileToDst(
                fileUrl,
                type,
                updateType,
                new UpdateOtaUtils.OnHandleListener() {
                    @Override
                    public void onSucceed() {
                        KLog.i("filOut| " + "[onSucceed]->copy the file succeed ; ecu : " + ecu);
                        //文件复制成功,发送推送命令
                        doPushFileFinished(ecu, fileUrl, type, updateType);
                    }

                    @Override
                    public void onFailure() {
                        UpdateOtaUtils.endExecution();
                        KLog.i("filOut| " + "[onFailure]->copy the file failure ; ecu : " + ecu);
                        handleInstallState(UpdateOtaInfo.InstallState.INSTALL_FAILED, ecu);
                        dispatchHandleFailure(ecu);
                    }
                });
    }

    private void doPushFileFinished(int ecu, String fileUrl, @ResourceType int type, @UpdateResouceType int updateType) {
        handlePreInstall(ecu, fileUrl, type, updateType);
        pushSoundFileCommand(ecu);
    }

    /**
     * 安装前的预处理
     */
    private void handlePreInstall(int ecu, String fileUrl, @ResourceType int type, @UpdateResouceType int updateType) {
        UpdateOtaInfo queryOta = infos.get(ecu);
        if (queryOta ==null  || (queryOta =UpdateOtaInfoDbUtils.query(ecu)) == null) {
            queryOta = new UpdateOtaInfo();
        }
        queryOta.setFileUrl(fileUrl);
        queryOta.setEcu(ecu);
        queryOta.setResType(type);
        queryOta.setUpdateResType(updateType);
        queryOta.setInstallState(UpdateOtaInfo.InstallState.INSTALL_PRE);
        infos.put(ecu, queryOta);
        UpdateOtaInfoDbUtils.save(queryOta);
    }

    /**
     * 推送音效文件
     *
     * @param ecu
     * @return
     */
    public void pushSoundFileCommand(int ecu) {
        if (checkIsNull(ecu, true)) return;
        switch (ecu) {
            case UniqueOtaConstants.EcuId.HU://音响
                KLog.i("filOut| " + "[pushSoundFileCommand]->推送音响");
                // 音响 在文件复制成功后直接发送升级命令
                UpdateOtaManager.this.soundUpgrade(ecu);
                break;

            case UniqueOtaConstants.EcuId.IC_H://高配仪表
                KLog.i("filOut| " + "[pushSoundFileCommand]->推送高配仪表");
                mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.UPDATE_INFORM_ICH,
                        UniqueOtaConstants.CommandState.ACTIVE);
                break;
            case UniqueOtaConstants.EcuId.IC_L://低配仪表
                KLog.i("filOut| " + "[pushSoundFileCommand]->推送低配仪表");
                mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.UPDATE_INFORM_ICL,
                        UniqueOtaConstants.CommandState.ACTIVE);
                handleInstallState(UpdateOtaInfo.InstallState.INSTALLING, ecu, 0);//低配仪表修改安装状态
                break;
            case UniqueOtaConstants.EcuId.ROBOT://3D 全息影像
                KLog.i("filOut| " + "[pushSoundFileCommand]->推送3D全息影像");
                mUniqueOtaClient.undateCommand(UniqueOtaConstants.CommandAction.UPDATE_INFORM_ROBOT,
                        UniqueOtaConstants.CommandState.ACTIVE);
                break;
        }
    }


    /**
     * 判断车辆是否处于允许升级音效的状态
     * 包括以下状态：
     * 1、蓄电池电压＞12V（具体数字后续可能有调整）
     * 2、SOC＞99%（具体数字后续可能有调整）
     * 3、供电模式= IgnitionOn
     * 4、车速=0
     * 5、档位为P档
     * 6、EPB为LOCK状态
     */
    public boolean getVehicleState() {
        if (checkIsNull(-1, false)) return false;
        int[] vehicleState = mUniqueOtaClient.getVehicleState();
        for (int i = 0; i < vehicleState.length; i++) {
            if (vehicleState[i] != 0) {
                return false;
            }
        }
        return true;
    }


    public static class UpdateOtaBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case SoundUpdate.ACTION_CHECK_SOUND_UPDATE:
                    UpdateOtaManager.getInstance().installNotInstalled();
                    break;
            }
            context.removeStickyBroadcast(intent);
        }
    }

    private void registerCheckSoundUpdate(Context context) {
        UpdateOtaManager.UpdateOtaBroadcastReceiver receiver = new UpdateOtaManager.UpdateOtaBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SoundUpdate.ACTION_CHECK_SOUND_UPDATE);
        context.registerReceiver(receiver, intentFilter);
    }

    private void installNotInstalled() {
        List<UpdateOtaInfo> updateOtaInfos = UpdateOtaInfoDbUtils.queryContinueUpdate();
        if (!ListUtils.isEmpty(updateOtaInfos)) {
            KLog.i("filOut| " + "[installNotInstalled]-> size:" + updateOtaInfos.size() + "     " + updateOtaInfos.toString());
            for (UpdateOtaInfo updateOtaInfo : updateOtaInfos) {
                if (updateOtaInfo.getInstallState() == UpdateOtaInfo.InstallState.INSTALLING) {
                    showSoundUpdateDialog(updateOtaInfo);
                    break;
                }
            }
        }
    }

    public void showSoundUpdateDialog(final UpdateOtaInfo updateOtaInfo) {
        if (updateOtaInfo == null) return;
        if (updateOtaInfo.getEcu() == UniqueOtaConstants.EcuId.IC_L) {
            showICLUndoneUpdateDialog(updateOtaInfo);
        } else {
            showUndoneUpdateDialog(updateOtaInfo);
        }
    }

    /**
     * 未完成的高配仪表音效、音响音效升级弹窗
     */
    private void showUndoneUpdateDialog(final UpdateOtaInfo updateOtaInfo) {
        String content = ResUtils.getString(AppHolder.getInstance().getAppContext(), R.string.str_pro_undone_update_msg);
        createUpdateDialog(updateOtaInfo, content, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateOtaUtils.beginExecution();
                doPushFileFinished(updateOtaInfo.getEcu(), updateOtaInfo.getFileUrl(), updateOtaInfo.getResType(), updateOtaInfo.getUpdateResType());
            }
        });
    }

    /**
     * 未完成的低配仪表音效升级弹窗
     */
    private void showICLUndoneUpdateDialog(final UpdateOtaInfo updateOtaInfo) {
        String content_one = ResUtils.getString(AppHolder.getInstance().getAppContext(), R.string.str_undone_update_msg_one);
        String content_two = ResUtils.getString(AppHolder.getInstance().getAppContext(), R.string.str_undone_update_msg_two);
        String content  = content_one + content_two;
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new AbsoluteSizeSpan((int) ResUtils.getDimension(AppHolder.getInstance().getAppContext(), R.dimen.size_update_dialog_small)), content_one.length(), content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        createUpdateDialog(updateOtaInfo, spannableString, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateOtaManager.getInstance().getVehicleState()) {
                    UpdateOtaUtils.beginExecution();
                    doPushFileFinished(updateOtaInfo.getEcu(), updateOtaInfo.getFileUrl(), updateOtaInfo.getResType(), updateOtaInfo.getUpdateResType());
                } else {
                    XMToast.toastException(AppHolder.getInstance().getAppContext(), R.string.condition_not_satisfied);
                }
            }
        });
    }

    private void createUpdateDialog(final UpdateOtaInfo updateOtaInfo,
                                    CharSequence contentMsg,
                                    final View.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AppHolder.getInstance().getAppContext(), R.style.custom_alert_dialog);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        dialog.show();

        dialog.setContentView(R.layout.dialog_update_vehicle_sound);
        TextView tvContent = dialog.findViewById(R.id.tv_content_message);
        TextView tvConfirm = dialog.findViewById(R.id.confirm_bt);
        TextView tvCancel = dialog.findViewById(R.id.cancel_bt);
        if (updateOtaInfo.getEcu() != UniqueOtaConstants.EcuId.IC_L) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) tvContent.getLayoutParams();
            lp.bottomMargin = 30;
            lp.topMargin = 20;
            tvContent.setLayoutParams(lp);
        }
        tvContent.setText(contentMsg);
        tvConfirm.setText(R.string.state_update);
        tvCancel.setText(R.string.str_update_right);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.cancel();
            }
        });

        dialog.setCancelable(false);
    }
}
