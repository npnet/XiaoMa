package com.xiaoma.shop.common.util;

import android.app.DownloadManager;
import android.util.Log;

import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.component.AppHolder;
import com.xiaoma.db.DBManager;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.HologramDownload;
import com.xiaoma.shop.business.hologram.HologramUsing;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.common.constant.Hologram3DContract;
import com.xiaoma.shop.common.manager.update.UpdateOtaInfo;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;
import java.util.Objects;

/**
 * Author: loren
 * Date: 2019/8/14
 */
public class Hologram3DUtils {

    public static final int DISABLE = 1;
    public static final int ENABL = 2;

    public static final String KEY_3D_SELF_URL_DOWNLOAD = "download_url.key";
    public static final String KEY_3D_SELF_URL_USING = "using_url.key";
    public static final String KEY_3D_SELF_URL = "holo_url.key";
    public static final String DEF_NONE = "none.def";
    public static final int DEF_INT = -101;

    private Hologram3DUtils() {

    }

    /**
     * 在下载成功之后调用
     *
     * @param url
     */
    public static void saveUrl(String url) {
        TPUtils.put(AppHolder.getInstance().getAppContext(), KEY_3D_SELF_URL, url);
    }

    /**
     * 下载失败,或者删除的时候调用
     */
    public static void remove() {
        TPUtils.remove(AppHolder.getInstance().getAppContext(), KEY_3D_SELF_URL);
    }

    public static String getUrl() {
        return TPUtils.get(AppHolder.getInstance().getAppContext(), KEY_3D_SELF_URL, DEF_NONE);
    }


    /**
     * @param model
     * @return int[2] 0->状态，1，用于显示的参数，2->是否可点击（0 可点击， 1不可以）
     */
    public static int[] checkItemState(HoloListModel model) {
        int[] state = new int[3];
        String curHoloUrl = getUrl();
        if (Objects.equals(curHoloUrl, DEF_NONE)) {
            state = checkDownloadState(model, false);
        } else {
            if (Objects.equals(curHoloUrl, model.getCustomImageResourceUrl())) {
                if (HologramUsing.isRoleUsing(AppHolder.getInstance().getAppContext(), model)) {
                    state[0] = Hologram3DContract.STATE_USING;
                    state[1] = R.string.state_using;
                    state[2] = DISABLE;
                } else {
                    state[0] = Hologram3DContract.STATE_DOWNLOAD_SUCCESS;
                    state[1] = R.string.state_use;
                    state[2] = ENABL;
                }
            } else {
                state = checkDownloadState(model, true);
            }
        }
        return state;
    }


    /**
     * 删除自定义形象的时候,一定一定要调用这个
     */
    public static void remove(boolean clearAll) {
        TPUtils.remove(AppHolder.getInstance().getAppContext(), KEY_3D_SELF_URL_DOWNLOAD);
        if (clearAll) {
            TPUtils.remove(AppHolder.getInstance().getAppContext(), KEY_3D_SELF_URL_USING);
        }
    }

    private static int[] checkInstallState() {
        int[] state = new int[3];
        UpdateOtaInfo info = UpdateOtaManager.getInstance().getInfos().get(UniqueOtaConstants.EcuId.ROBOT);
        if (info == null) {
            state[0] = Hologram3DContract.STATE_DOWNLOAD_SUCCESS;
            state[1] = R.string.state_use;
            state[2] = ENABL;
        } else {
            int installState = info.getInstallState();
            switch (installState) {
                case UpdateOtaInfo.InstallState.INSTALLING:
                    state[0] = Hologram3DContract.STATE_INSTALL_PROGRESS;
                    state[1] = info.getProgress();
                    state[2] = DISABLE;
                    break;
                case UpdateOtaInfo.InstallState.INSTALL_FAILED:
                    state[0] = Hologram3DContract.STATE_INSTALL_FAIL;
                    state[1] = R.string.state_use;
                    state[2] = ENABL;
                    break;
                case UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL:
                    state[0] = Hologram3DContract.STATE_USING;
                    state[1] = R.string.state_using;
                    state[2] = DISABLE;
                    break;
            }

        }
        return state;
    }

    private static int[] checkDownloadState(HoloListModel model, boolean update) {
        int[] state = new int[3];
        DownloadStatus downloadStatus = HologramDownload.newSingleton().getDownloadStatus(model);
        if (downloadStatus == null) { //没有下载
            state[0] = Hologram3DContract.STATE_DOWNLOAD;
            state[1] = update ? R.string.state_update : R.string.state_download;
            state[2] = ENABL;
        } else {
            int status = downloadStatus.status;
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    state[0] = Hologram3DContract.STATE_DOWNLOAD_PROGRESS;
                    state[1] = (int) (downloadStatus.currentLength * 100f / downloadStatus.totalLength);
                    Log.d("HOLO", "progress: " + state[1]);
                    state[2] = DISABLE;
                    if (downloadStatus.currentLength == downloadStatus.totalLength) {
//                        saveUrl(model.getCustomImageResourceUrl());
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.d("HOLO", "fail: ");
                    HologramDownload.newSingleton().remove(model);
                    state[0] = Hologram3DContract.STATE_DOWNLOAD_FAIL;
                    state[1] = update ? R.string.state_update : R.string.state_download;
                    state[2] = ENABL;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.d("HOLO", "success: ");
                    state = checkInstallState();
                    break;
            }
        }
        return state;
    }


    /**
     * @param list
     * @param deleteIndex <0 ->all,
     */
    public static void remove(List<UpdateOtaInfo> list, int deleteIndex) {
        if (list == null || list.isEmpty()) return;
        if (deleteIndex < 0) {
            DBManager.getInstance().getDBManager().delete(list);
        } else {
            if (deleteIndex >= list.size()) {
                deleteIndex = 0;
            }
            DBManager.getInstance().getDBManager().delete(list.get(deleteIndex));
        }
    }
}
