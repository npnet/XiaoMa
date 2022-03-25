package com.xiaoma.personal.qrcode.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.personal.R;
import com.xiaoma.personal.qrcode.vm.QRCodeVM;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.utils.NetworkUtils;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/19 0019 11:00
 *   desc:   单个二维码基类
 * </pre>
 */
public abstract class BaseQRCodeFragment extends BaseFragment {

    private ImageView mCodeImageView;
    private TextView mCodeDescText;
    private QRCodeVM mQRCodeVM;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_number_key_remote_controller, container, false);
        return super.onCreateWrapView(contentView);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQRCodeVM = ViewModelProviders.of(this).get(QRCodeVM.class);
        initView(view);
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        XMProgress.dismissProgressDialog(this);
    }

    private void initView(View view) {
        mCodeImageView = view.findViewById(R.id.iv_key_and_remote);
        mCodeDescText = view.findViewById(R.id.tv_key_and_remote);
    }


    @Override
    protected void noNetworkOnRetry() {
        initData();
    }


    private void initData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        showContentView();
        fetchData();
    }


    protected void setCodeImageView(String url) {
        ImageLoader.with(this)
                .load(url)
                .placeholder(R.drawable.default_cover)
                .into(mCodeImageView);
    }


    protected void setCodeDescText(String text) {
        mCodeDescText.setText(text);
    }


    protected final QRCodeVM getQRCodeVM() {
        return mQRCodeVM;
    }

    protected abstract void fetchData();


}
