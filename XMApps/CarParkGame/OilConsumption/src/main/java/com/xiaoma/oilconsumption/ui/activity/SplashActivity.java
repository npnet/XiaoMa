package com.xiaoma.oilconsumption.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.oilconsumption.R;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.NetworkUtils;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

    TextView mTextView;
    Button mButton;
    SeekBar mSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    public void init(){
        mTextView=findViewById(R.id.network_error);
        mButton=findViewById(R.id.refresh);
        mSeekBar=findViewById(R.id.process_bar);
        networkStatus();
    }

    public void networkStatus(){
        if(!NetworkUtils.isConnected(this)){
            networkError();
            return;
        }else{
            mTextView.setVisibility(View.INVISIBLE);
            mButton.setVisibility(View.INVISIBLE);
            mSeekBar.setVisibility(View.VISIBLE);
            mButton.setOnClickListener(null);
        }

        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        },2000);
    }

    public void networkError(){
        mTextView.setVisibility(View.VISIBLE);
        mButton.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.INVISIBLE);
        mButton.setOnClickListener(this);
    }

    public void networkRefresh(){
        //todo 刷新网络
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refresh:
                networkRefresh();
                break;
        }
    }

    public void startMainActivity(){
        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        finish();
    }
}
