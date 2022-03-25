package com.xiaoma.shop.common.util;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.ui.bought.BoughtActivity;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.utils.ListUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Author: Ljb
 * Time  : 2019/7/10
 * Description:
 */
public class SkinHelper {


    private ConfirmDialog downloadLimitPromptDialog;

    public static final int limit_num = 10;

    private void showCleanPrompt(final FragmentActivity activity, final boolean jumpOtherAct) {
        if (downloadLimitPromptDialog != null && downloadLimitPromptDialog.isShow()) {
            return;
        }
        downloadLimitPromptDialog = new ConfirmDialog(activity)
                .setContent(activity.getString(R.string.download_limit_prompt))
                .setPositiveButton(activity.getString(R.string.to_clean), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadLimitPromptDialog.dismiss();
                        if (jumpOtherAct) {
                            activity.startActivity(new Intent(activity, BoughtActivity.class));
                        }

                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadLimitPromptDialog.dismiss();
                    }
                });
        downloadLimitPromptDialog.show();
    }

    public boolean checkCanDownload(SkinDownload skinDownload, FragmentActivity activity, boolean jumpOtherAct) {
        if (!canDownload(skinDownload)) {
            showCleanPrompt(activity, jumpOtherAct);
            return false;
        }
        return true;
    }

    private boolean canDownload(SkinDownload skinDownload) {
        // 检测是否已经下载了十套皮肤
        File[] allDownloadFiles = skinDownload.getAllDownloadFiles();
        int downingNum = 0;
        int fileNum = allDownloadFiles != null ? allDownloadFiles.length : 0;
        for (Map.Entry<String, DownloadStatus> entry : skinDownload.getmDownloadStatusMap().entrySet()) {
            if (entry != null && entry.getValue().status == DownloadManager.STATUS_RUNNING) {
                downingNum++;
            }
        }
        int totalSize = fileNum;
        if (totalSize >= limit_num) {
            return false;
        }
        return true;
    }

    public int findPositionByUrl(List<SkinVersionsBean> beans, String url) {
        if (TextUtils.isEmpty(url) || ListUtils.isEmpty(beans)) return -1;
        for (int i = 0; i < beans.size(); i++) {
            SkinVersionsBean bean = beans.get(i);
            if (bean.getApkFilePath().equals(url)) {
                return i;
            }
        }
        return -1;
    }
}
