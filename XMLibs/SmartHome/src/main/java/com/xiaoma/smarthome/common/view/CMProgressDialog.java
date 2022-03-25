package com.xiaoma.smarthome.common.view;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common.view
 *  @file_name:      CMProgressDialog
 *  @author:         Rookie
 *  @create_time:    2019/5/8 14:24
 *  @description：   TODO             */

import android.app.Dialog;
import android.content.Context;

import com.xiaoma.smarthome.R;

public class CMProgressDialog extends Dialog {
    public CMProgressDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setContentView(R.layout.view_cm_progress);
    }
}
