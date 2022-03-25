package com.xiaoma.ui.progress;

import com.xiaoma.ui.UISupport;
import com.xiaoma.ui.progress.loading.CustomProgressDialog;

/**
 * Created by youthyj on 2018/9/26.
 */
public interface ProgressSupport extends UISupport {
    CustomProgressDialog getProgressDialog();
}
