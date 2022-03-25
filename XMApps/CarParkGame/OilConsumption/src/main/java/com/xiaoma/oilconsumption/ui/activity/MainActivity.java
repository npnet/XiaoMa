package com.xiaoma.oilconsumption.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.oilconsumption.R;
import com.xiaoma.oilconsumption.ui.dialog.ProtocolDialog;
import com.xiaoma.oilconsumption.ui.fragment.MainFragment;

import java.util.ArrayList;

/**
 * 百公里油耗竞赛主页
 * Created by Thomas on 2019/5/23 0023
 */

public class MainActivity extends BaseActivity {

    FragmentManager mFragmentManager;
    MainFragment mMainFragment;
    ArrayList<Fragment> mFragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        new ProtocolDialog().showDialog(this);
    }

    public void init(){
        mFragmentManager=getSupportFragmentManager();
        mMainFragment=MainFragment.getInstance();
        mFragments=new ArrayList<Fragment>();
        mFragments.add(mMainFragment);
        FragmentTransaction transaction=mFragmentManager.beginTransaction();
        transaction.add(R.id.content,mMainFragment);
        transaction.show(mMainFragment).commitAllowingStateLoss();
    }



}
