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

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.xiaoma.hotfix.TinkerUtils;
import com.xiaoma.hotfix.crash.TinkerUncaughtExceptionHandler;

import java.io.File;

/**
 * 步骤1: 检查补丁
 * 用于拦截Tinker收到的补丁包的修复、升级请求
 * 判断是否要唤起:patch进程去尝试补丁合成
 */
public class XMPatchListener extends DefaultPatchListener {
    private static final long NEW_PATCH_RESTRICTION_SPACE_SIZE_MIN = 60 * 1024 * 1024;
    private static final String TAG = XMPatchListener.class.getSimpleName() + "_LOG";
    private final int maxMemory;

    public XMPatchListener(Context context) {
        super(context);
        maxMemory = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * XMPatchListener Constructor");
    }

    @Override
    public int patchCheck(String path, String patchMd5) {
        File patchFile = new File(path);
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * file:" + patchFile
                + "\n * exists:" + patchFile.exists()
                + "\n * size:" + SharePatchFileUtil.getFileOrDirectorySize(patchFile)
        );
        int returnCode = super.patchCheck(path, patchMd5);

        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
            returnCode = TinkerUtils.checkForPatchRecover(NEW_PATCH_RESTRICTION_SPACE_SIZE_MIN, maxMemory);
        }

        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
            SharedPreferences sp = context.getSharedPreferences(ShareConstants.TINKER_SHARE_PREFERENCE_CONFIG, Context.MODE_MULTI_PROCESS);
            //optional, only disable this patch file with md5
            int fastCrashCount = sp.getInt(patchMd5, 0);
            if (fastCrashCount >= TinkerUncaughtExceptionHandler.MAX_CRASH_COUNT) {
                returnCode = TinkerUtils.ERROR_PATCH_CRASH_LIMIT;
            }
        }
        XMTinkerReport.onTryApply(returnCode == ShareConstants.ERROR_PATCH_OK);
        return returnCode;
    }
}
