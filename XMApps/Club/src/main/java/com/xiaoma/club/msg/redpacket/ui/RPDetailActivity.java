package com.xiaoma.club.msg.redpacket.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.msg.redpacket.controller.RpDetailAdapter;
import com.xiaoma.club.msg.redpacket.model.RPDetailItemInfo;
import com.xiaoma.club.msg.redpacket.model.RedPacketInfo;
import com.xiaoma.club.msg.redpacket.model.RpDetailInfo;
import com.xiaoma.club.msg.redpacket.vm.RPDetailVM;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.User;
import com.xiaoma.ui.view.RecycleViewDivider;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RPDetailActivity extends BaseActivity {

    private static final String EXTRA_RP_INFO = "rp_info";
    private static final String EXTRA_SEND_USER_INFO = "sender_user_info";
    private RPDetailVM mRPDetailVM;
    private RecyclerView mReceiverRv;
    private ImageView mSendIconImg;
    private TextView mSendNameTv;
    private TextView mRpSum;
    private TextView mRpTip;
    private TextView mReceiverDetailTv;
    private RpDetailAdapter mRvAdapter;
    private int mTotalCount;
    private int mMoney;

    public static void launch(Context context, User user, RedPacketInfo redPacketInfo) {
        context.startActivity(new Intent(context, RPDetailActivity.class)
                .putExtra(EXTRA_SEND_USER_INFO, (Parcelable) user)
                .putExtra(EXTRA_RP_INFO, redPacketInfo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_detail);
        getNaviBar().showBackAndHomeNavi();
        initView();
        initVM();
        fetchData();
    }

    private void fetchData() {
        Intent intent = getIntent();
        User senderUser = intent.getParcelableExtra(EXTRA_SEND_USER_INFO);
        RedPacketInfo redPacketInfo = intent.getParcelableExtra(EXTRA_RP_INFO);
        if (mRPDetailVM != null) {
            mRPDetailVM.getSenderUser().setValue(senderUser);
            mRPDetailVM.getRedPacketInfo().setValue(redPacketInfo);
        }
    }

    private void initVM() {
        mRPDetailVM = ViewModelProviders.of(this).get(RPDetailVM.class);
        mRPDetailVM.getSenderUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                String name = null;
                String picPath = null;
                if (user != null) {
                    name = user.getName();
                    picPath = user.getPicPath();
                }
                ImageLoader.with(RPDetailActivity.this)
                        .load(picPath)
                        .placeholder(R.drawable.default_head_icon)
                        .error(R.drawable.default_head_icon)
                        .circleCrop()
                        .into(mSendIconImg);
                mSendNameTv.setText(StringUtil.optString(name));

            }
        });
        mRPDetailVM.getRedPacketInfo().observe(this, new Observer<RedPacketInfo>() {
            @Override
            public void onChanged(@Nullable RedPacketInfo redPacketInfo) {
                if (redPacketInfo != null) {
                    mRPDetailVM.requestRpDetail(redPacketInfo.getPacketId());
                    refreshRpDetail(redPacketInfo);
                }
            }
        });
        mRPDetailVM.getIsReceiver().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isReceiver) {
                if (isReceiver != null) {
                    if (isReceiver) {
                        mRpTip.setVisibility(View.VISIBLE);
                    } else {
                        mRpTip.setVisibility(View.GONE);
                    }
                }
            }
        });
        mRPDetailVM.getRpDetailInfo().observe(this, new Observer<RpDetailInfo>() {
            @Override
            public void onChanged(@Nullable RpDetailInfo rpDetailInfo) {
                if (rpDetailInfo != null && rpDetailInfo.getRedEnvelopeReceivedDetailList() != null && mRvAdapter != null) {
                    final List<RPDetailItemInfo> redEnvelopeReceivedDetailList = rpDetailInfo.getRedEnvelopeReceivedDetailList();
                    mRvAdapter.setNewData(redEnvelopeReceivedDetailList);
                    final int size = redEnvelopeReceivedDetailList.size();
                    mReceiverDetailTv.setText(String.format(getString(R.string.rp_receiver_detail),
                            size, mTotalCount, size * mMoney, mTotalCount * mMoney));
                }
            }
        });
    }

    private void refreshRpDetail(RedPacketInfo redPacketInfo) {
        mTotalCount = redPacketInfo.getCount();
        mMoney = redPacketInfo.getMoney();
        mRpSum.setText(String.valueOf(mTotalCount * mMoney));
    }

    private void initView() {
        mSendIconImg = findViewById(R.id.img_send_icon);
        mSendNameTv = findViewById(R.id.tv_send_name);
        mRpSum = findViewById(R.id.tv_rp_sum);
        mRpTip = findViewById(R.id.tv_tip);
        mReceiverDetailTv = findViewById(R.id.tv_rp_receiver_detail);
        initRv();
    }

    private void initRv() {
        mReceiverRv = findViewById(R.id.rv_rp_receiver_list);
        mReceiverRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReceiverRv.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.rp_shape_devider_line, 0));
        mRvAdapter = new RpDetailAdapter(new ArrayList<RPDetailItemInfo>());
        mReceiverRv.setAdapter(mRvAdapter);
    }
}
