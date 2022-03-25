package com.xiaoma.oilconsumption.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.oilconsumption.R;
import com.xiaoma.ui.view.XmScrollBar;

public class DetailsActivity extends BaseActivity implements View.OnClickListener {

    Button share;
    TextView date,name,rank,bonus,unmberContestants,gainMoney;
    ImageView photo;
    RecyclerView mRecyclerView;
    XmScrollBar mXmScrollBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();
    }

    public void init(){
        share=findViewById(R.id.share_button);
        date=findViewById(R.id.date);
        photo=findViewById(R.id.photo);
        name=findViewById(R.id.name);
        rank=findViewById(R.id.rank);
        bonus=findViewById(R.id.bonus);
        unmberContestants=findViewById(R.id.number_contestants);
        gainMoney=findViewById(R.id.gain_money);
        mRecyclerView=findViewById(R.id.recyclerView);
        mXmScrollBar=findViewById(R.id.scroll_bar);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.share_button:

                break;
        }
    }
}
