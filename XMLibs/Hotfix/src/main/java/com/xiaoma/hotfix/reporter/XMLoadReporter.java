/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaoma.hotfix.reporter;

import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.File;

/**
 * 步骤2:加载补丁
 * 定义了Tinker在加载补丁时的一些回调
 */
public class XMLoadReporter extends DefaultLoadReporter {
    private static final String TAG = XMLoadReporter.class.getSimpleName() + "_TAG";

    public XMLoadReporter(Context context) {
        super(context);
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * XMLoadReporter Constructor");
    }

    @Override
    public void onLoadPatchListenerReceiveFail(final File patchFile, int errorCode) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * file path: " + patchFile
                + "\n * errorCode: " + errorCode
        );
        super.onLoadPatchListenerReceiveFail(patchFile, errorCode);
        XMTinkerReport.onTryApplyFail(errorCode);
    }

    @Override
    public void onLoadResult(File patchDirectory, int loadCode, long cost) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * file path: " + patchDirectory
                + "\n * loadCode: " + loadCode
                + "\n * cost: " + cost
        );
        super.onLoadResult(patchDirectory, loadCode, cost);
        switch (loadCode) {
            case ShareConstants.ERROR_LOAD_OK:
                XMTinkerReport.onLoaded(cost);
                break;
        }
        Looper.getMainLooper().myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                if (UpgradePatchRetry.getInstance(context).onPatchRetryLoad()) {
                    XMTinkerReport.onReportRetryPatch();
                }
                return false;
            }
        });
    }

    @Override
    public void onLoadException(Throwable e, int errorCode) {
        super.onLoadException(e, errorCode);
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * exception: " + e.getMessage()
                + "\n * errorCode: " + errorCode
        );
        XMTinkerReport.onLoadException(e, errorCode);
    }

    @Override
    public void onLoadFileMd5Mismatch(File file, int fileType) {
        super.onLoadFileMd5Mismatch(file, fileType);
        XMTinkerReport.onLoadFileMisMatch(fileType);
    }

    /**
     * try to recover patch oat file
     *
     * @param file
     * @param fileType
     * @param isDirectory
     */
    @Override
    public void onLoadFileNotFound(File file, int fileType, boolean isDirectory) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * file: " + file
                + "\n * fileType: " + fileType
                + "\n * isDirectory: " + isDirectory
        );
        super.onLoadFileNotFound(file, fileType, isDirectory);
        XMTinkerReport.onLoadFileNotFound(fileType);
    }

    @Override
    public void onLoadPackageCheckFail(File patchFile, int errorCode) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * file: " + patchFile
                + "\n * errorCode: " + errorCode
        );
        super.onLoadPackageCheckFail(patchFile, errorCode);
        XMTinkerReport.onLoadPackageCheckFail(errorCode);
    }

    @Override
    public void onLoadPatchInfoCorrupted(String oldVersion, String newVersion, File patchInfoFile) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * oldVersion: " + oldVersion
                + "\n * newVersion: " + newVersion
                + "\n * patchInfoFile: " + patchInfoFile
        );
        super.onLoadPatchInfoCorrupted(oldVersion, newVersion, patchInfoFile);
        XMTinkerReport.onLoadInfoCorrupted();
    }

    @Override
    public void onLoadInterpret(int type, Throwable e) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * type: " + type
                + "\n * exception: " + e.getMessage()
        );
        super.onLoadInterpret(type, e);
        XMTinkerReport.onLoadInterpretReport(type, e);
    }

    @Override
    public void onLoadPatchVersionChanged(String oldVersion, String newVersion, File patchDirectoryFile, String currentPatchName) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * oldVersion: " + oldVersion
                + "\n * newVersion: " + newVersion
                + "\n * patchDirectoryFile: " + patchDirectoryFile
                + "\n * currentPatchName: " + currentPatchName
        );
        super.onLoadPatchVersionChanged(oldVersion, newVersion, patchDirectoryFile, currentPatchName);
    }
}
