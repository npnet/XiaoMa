package com.xiaoma.oilconsumption.ui.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.xiaoma.oilconsumption.R;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/05/24
 *     desc   :
 * </pre>
 */
public class NetworkErrorDialog {
    AlertDialog mAlertDialog;
    public void showDialog(Context context){
        View view=View.inflate(context, R.layout.dialog_network_error,null);

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        mAlertDialog=builder.create();
        mAlertDialog.show();
    }
}
