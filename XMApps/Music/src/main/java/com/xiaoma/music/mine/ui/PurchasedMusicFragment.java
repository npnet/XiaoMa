package com.xiaoma.music.mine.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.mine.vm.PurchasedVM;
import com.xiaoma.utils.log.KLog;

/**
 * Author: loren
 * Date: 2019/6/17 0017
 */
@PageDescComponent(EventConstants.PageDescribe.purchasedMusicFragment)
public class PurchasedMusicFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "PurchasedMusicFragment";

    private PurchasedVM purchasedVM;
    private ImageView qrIv;
    private TextView qrTv;
    private FrameLayout qrFl;
    private RelativeLayout listRl;

    public static PurchasedMusicFragment newInstance() {
        return new PurchasedMusicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_purchased, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OnlineMusicFactory.getKWLogin().isUserLogon()) {
            //已登录，请求已购音乐
            qrFl.setVisibility(View.GONE);
            listRl.setVisibility(View.VISIBLE);
            purchasedVM.requestPurchasedMusic();
        } else {
            //未登录，显示登录
            qrFl.setVisibility(View.VISIBLE);
            listRl.setVisibility(View.GONE);
            purchasedVM.requestQrCode();
        }
    }

    private void initView(View view) {
        view.findViewById(R.id.login_qrcode_state_btn).setOnClickListener(this);
        qrIv = view.findViewById(R.id.login_scan_qrcode_iv);
        qrTv = view.findViewById(R.id.login_qrcode_state_tv);
        qrFl = view.findViewById(R.id.login_scan_qrcode_fl);
        listRl = view.findViewById(R.id.purchased_list_rl);
    }


    private void initVM() {
        purchasedVM = ViewModelProviders.of(this).get(PurchasedVM.class);
        purchasedVM.getQrCodeResult().observe(this, qrCodeResult -> {
            if (qrCodeResult != null && qrCodeResult.isSuccess()) {
                qrIv.setImageBitmap(qrCodeResult.getBitmap());
                purchasedVM.startCheckQrCode();
            } else {
                //二维码获取失败

            }
        });
        purchasedVM.getLoginResult().observe(this, xmLoginResult -> {
            if (xmLoginResult != null) {
                KLog.d(TAG, "login kw state : " + xmLoginResult.getSDKBean().getState() + " messabe : " + xmLoginResult.getSDKBean().getMessage());
                if (xmLoginResult.isWait()) {
                    qrTv.setText(R.string.kw_login_wait);

                } else if (xmLoginResult.isLogin()) {
                    qrTv.setText(R.string.kw_login_success);
                    qrIv.setImageResource(R.drawable.iv_default_cover);
                    purchasedVM.stopCheckQrCode();
                    purchasedVM.requestPurchasedMusic();

                } else {
                    qrTv.setText(R.string.kw_login_failed);
                    purchasedVM.stopCheckQrCode();

                }
            } else {
                qrTv.setText(R.string.kw_login_failed);
                purchasedVM.stopCheckQrCode();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_qrcode_state_btn:
                purchasedVM.requestQrCode();
                break;
        }
    }
}
