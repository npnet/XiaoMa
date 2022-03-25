package com.xiaoma.oilconsumption.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.oilconsumption.R;

public class ApplyPayActivity extends BaseActivity implements View.OnClickListener {

    ImageView icon;
    TextView periods,date,applyMoney,myMoney,payMoney;
    Button ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        init();
    }

    public void init(){
        icon=findViewById(R.id.icon);
        periods=findViewById(R.id.periods);
        date=findViewById(R.id.date);
        applyMoney=findViewById(R.id.apply_money);
        myMoney=findViewById(R.id.my_money);
        payMoney=findViewById(R.id.pay_money);
        ok=findViewById(R.id.ok_pay);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok_pay:

                break;
        }
    }
}
