package com.xiaoma.oilconsumption.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.xiaoma.oilconsumption.R;
import com.xiaoma.oilconsumption.ui.view.ObservableScrollView;
import com.xiaoma.oilconsumption.ui.view.VerticalScrollBar;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/05/23
 *     desc   :
 * </pre>
 */

/**
 * 协议对话框
 */
public class ProtocolDialog implements View.OnClickListener {

    public AlertDialog mAlertDialog;
    public void showDialog(Context context){
        View view=View.inflate(context, R.layout.dialog_protocol,null);
        Button agreeButton=view.findViewById(R.id.agree);
        Button disagreeButton=view.findViewById(R.id.disagree);
        agreeButton.setOnClickListener(this);
        disagreeButton.setOnClickListener(this);

        ObservableScrollView observableScrollView=view.findViewById(R.id.sisclamer_scrollview);
        VerticalScrollBar verticalScrollBar=view.findViewById(R.id.sisclamer_scrollbar);
        verticalScrollBar.setScrollView(observableScrollView);

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);
        mAlertDialog=builder.create();
        mAlertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.agree:
                //todo
                mAlertDialog.dismiss();
                break;
            case R.id.disagree:
                //todo
                mAlertDialog.dismiss();
                break;
        }
    }
}
