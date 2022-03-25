package com.xiaoma.oilconsumption.ui.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.xiaoma.oilconsumption.R;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/05/25
 *     desc   :
 * </pre>
 */
public class CompetitionRankDialog implements View.OnClickListener {

    AlertDialog mAlertDialog;
    Button mButton;

    public void showDialog(Context context){
        View view=View.inflate(context, R.layout.dialog_competition_ranking,null);
        mButton=view.findViewById(R.id.back);
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
            case R.id.back:
                mAlertDialog.dismiss();
                break;
        }
    }
}
