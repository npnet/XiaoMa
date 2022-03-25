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

package com.xiaoma.hotfix.service;

import android.util.Log;

import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;

import java.io.File;

public class TinkerResultService extends DefaultTinkerResultService {
    private static final String TAG = TinkerResultService.class.getSimpleName() + "_LOG";

    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * TinkerResultService received null result!!!!");
            return;
        }
        Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * TinkerResultService receive result: " + result.toString());

        // 停止补丁进程
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());
        if (result.isSuccess) {
            // 删除原始补丁文件
            deleteRawPatchFile(new File(result.rawPatchFilePath));
            // 已经安装了新版本的补丁了
            TinkerLog.i(TAG, "already install the newly patch version!");
        }
    }
}
