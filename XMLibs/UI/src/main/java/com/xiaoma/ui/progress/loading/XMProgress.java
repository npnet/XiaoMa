package com.xiaoma.ui.progress.loading;

import com.xiaoma.ui.UIUtils;
import com.xiaoma.ui.progress.ProgressSupport;

/**
 * Created by youthyj on 2018/9/26.
 */
public class XMProgress {

    private XMProgress() throws Exception {
        throw new Exception();
    }

    public static void showProgressDialog(final ProgressSupport support, final String content) {
        showProgressDialog(support, content, 0, 0, 0, 0);
    }

    public static void showProgressDialog(final ProgressSupport support, final String content,
                                          final int marginLeft, final int marginTop, final int
                                                  marginRight, final int marginBottom) {
        if (support == null || support.isDestroy()) {
            return;
        }
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                if (support.isDestroy()) {
                    return;
                }
                showDialog(support, content, marginLeft, marginTop, marginRight, marginBottom);
            }
        });
    }

    public static void dismissProgressDialog(final ProgressSupport support) {
        if (support == null || support.isDestroy()) {
            return;
        }
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                if (support.isDestroy()) {
                    return;
                }
                final CustomProgressDialog progressDialog = support.getProgressDialog();
                if (progressDialog != null && progressDialog.isShowing()) {
                    // Dialog相关操作加上异常保护,避免外部Activity的Window被销毁后执行显示,产生异常.
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void showDialog(ProgressSupport support, String content, int marginLeft, int
            marginTop, int marginRight, int marginBottom) {
        final CustomProgressDialog progressDialog = support.getProgressDialog();
        if (progressDialog == null) {
            return;
        }
        if (marginLeft != 0 || marginTop != 0 || marginRight != 0 || marginBottom != 0) {
            progressDialog.resetContentViewMargin(marginLeft, marginTop, marginRight, marginBottom);
        }
        // Dialog显示相关操作加上异常保护,避免外部Activity的Window被销毁后执行显示,产生异常.
        try {
            if (progressDialog.isShowing()) {
                progressDialog.setMessage(content);
            } else {
                progressDialog.setMessage(content);
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
