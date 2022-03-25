package com.xiaoma.oilconsumption.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.xiaoma.oilconsumption.R;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/05/24
 *     desc   :
 * </pre>
 */

/**
 * 比赛结果对话框
 */
class CompetitionResultDialog implements View.OnClickListener {

    public AlertDialog mAlertDialog;
    Button mButton;

    public void showDialog(Context context){
        View view=View.inflate(context, R.layout.dialog_competition_result,null);
        mButton=view.findViewById(R.id.result_ok);
        mButton.setOnClickListener(this);

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);
        mAlertDialog=builder.create();
        mAlertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.result_ok:
                //todo
                mAlertDialog.dismiss();
                break;
        }
    }
}
