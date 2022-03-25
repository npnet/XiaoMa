package com.xiaoma.club.msg.redpacket.ui;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.ui.SendLocationActivity;
import com.xiaoma.club.msg.redpacket.datasource.RPRequest;
import com.xiaoma.club.msg.redpacket.model.RPBaseResult;
import com.xiaoma.club.msg.redpacket.model.RedPacketInfo;
import com.xiaoma.club.msg.redpacket.repo.UserWalletRepo;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.verify.FaceVerifyActivity;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

/**
 * Created by LKF on 2019-4-12 0012.
 * 发红包页面
 */
public class RPSendActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RPSendActivity";
    private static final int REQ_CODE_POI_SELECT = 100;
    public static final String EXTRA_TO_ID = "toId";
    public static final String EXTRA_IS_GROUP = "isGroup";
    public static final String EXTRA_IS_LOCATION = "isLocation";

    public static final String RESULT_RED_PACKET_INFO = "redPacketInfo";

    private TextView mTvMoney;
    private EditText mEtMoney;
    private LinearLayout mContainerCount;
    private EditText mEtCount;
    private EditText mEtGreeting;
    private TextView mTvTotalMoney;
    private Button mBtnSend;

    private long mToId;
    private boolean mIsGroup;
    private boolean mIsLocation;
    private PoiItem mPoiItem;
    private Integer mWalletMoney;
    private RedPacketParams mRedPacketParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rp_send);

        Intent intent = getIntent();
        mIsLocation = intent.getBooleanExtra(EXTRA_IS_LOCATION, false);
        mIsGroup = intent.getBooleanExtra(EXTRA_IS_GROUP, false);
        mToId = intent.getLongExtra(EXTRA_TO_ID, 0);

        mTvMoney = findViewById(R.id.tv_money);
        mEtMoney = findViewById(R.id.et_money);
        mContainerCount = findViewById(R.id.container_count);
        mEtCount = findViewById(R.id.et_count);
        mEtGreeting = findViewById(R.id.et_greeting);
        mTvTotalMoney = findViewById(R.id.tv_total_money);
        mBtnSend = findViewById(R.id.btn_send);

        if (mIsGroup) {
            mTvMoney.setText(R.string.rp_send_act_money_per);
            mContainerCount.setVisibility(View.VISIBLE);
        } else {
            mTvMoney.setText(R.string.rp_send_act_money_total);
            mContainerCount.setVisibility(View.GONE);
        }
        mBtnSend.setOnClickListener(this);
        // 监听问候语变化,限制长度
        mEtGreeting.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int maxLen = getResources().getInteger(R.integer.rp_greeting_max_len);
                int inputLen = s.length();
                if (inputLen > maxLen) {
                    s.delete(inputLen - 1, inputLen);
                }
            }
        });
        // 监听金额数,限制最大金额
        mEtMoney.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    final int maxMoney = getResources().getInteger(R.integer.rp_max_send_money_per);
                    final int money = Integer.parseInt(s.toString());
                    if (money > maxMoney) {
                        s.clear();
                        showToastException(getString(R.string.rp_send_act_error_money_too_much_format, maxMoney));
                        return;
                    }
                    // 判断字符长度
                    final int maxLen = String.valueOf(maxMoney).length();
                    if (s.length() > maxLen) {
                        s.delete(s.length() - 1, s.length());
                    }
                } catch (Exception ignored) {
                }
                bindTotalSendMoney();
            }
        });
        // 监听红包数变化,限制最大数
        mEtCount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    final int maxCount = getResources().getInteger(R.integer.rp_max_send_count);
                    final int count = Integer.parseInt(s.toString());
                    if (count > maxCount) {
                        s.clear();
                        showToastException(getString(R.string.rp_send_act_error_count_too_much_format, maxCount));
                        return;
                    }
                    // 判断字符长度
                    final int maxLen = String.valueOf(maxCount).length();
                    if (s.length() > maxLen) {
                        s.delete(s.length() - 1, s.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bindTotalSendMoney();
            }
        });
        // 拉取钱包金额
        final MutableLiveData<Integer> walletMoney = UserWalletRepo.getWalletMoney(this);
        walletMoney.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer money) {
                LogUtil.logI(TAG, "getWalletMoney # onChanged( money: %s )", money);
                mWalletMoney = money;
            }
        });
        // 如果是位置红包,要先跳转到地图里选择位置
        if (mIsLocation) {
            startActivityForResult(
                    new Intent(this, SendLocationActivity.class)
                            .putExtra(SendLocationActivity.EXTRA_SEND_BTN_TEXT, getString(R.string.rp_send_act_btn_select_location)),
                    REQ_CODE_POI_SELECT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_CODE_POI_SELECT == requestCode) {
            if (RESULT_OK == resultCode) {
                mPoiItem = data.getParcelableExtra(SendLocationActivity.RESULT_POI_ITEM);
                // 需要在window获得焦点后才能弹出输入法,所以需要延迟执行
                mEtMoney.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy())
                            return;
                        mEtMoney.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(mEtMoney, 0);
                        }
                    }
                }, 100);
                LogUtil.logI(TAG, "PoiItem: %s", mPoiItem);
            } else {
                finish();
            }
        }
        if (FaceVerifyActivity.REQUEST_CODE == requestCode) {
            if (resultCode == FaceVerifyActivity.RESULT_OK) {
                sendRedPacket(mRedPacketParams);
            } else {
                //测试让去掉异常提示
                //showToastException("识别失败,请重试");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                mRedPacketParams = ensureRedPacketParams();
                if (mRedPacketParams != null) {
                    if (XmCarConfigManager.hasFaceRecognition()) {
                        FaceVerifyActivity.newInstance(this,
                                UserManager.getInstance().getCurrentUser(),
                                FaceVerifyActivity.REQUEST_CODE);
                    } else {
                        sendRedPacket(mRedPacketParams);
                    }
                }
                break;
        }
    }

    private RedPacketParams ensureRedPacketParams() {
        if (!NetworkUtils.isConnected(this)) {
            showToastException(R.string.net_work_error);
            return null;
        }
        int money = 0;
        try {
            money = Integer.parseInt(String.valueOf(mEtMoney.getText()));
        } catch (NumberFormatException ignored) {
        }
        if (money <= 0) {
            showToastException(R.string.rp_send_act_invalid_money);
            mEtMoney.requestFocus();
            return null;
        }
        int count = 0;
        if (mIsGroup) {
            try {
                count = Integer.parseInt(mEtCount.getText().toString());
            } catch (NumberFormatException ignored) {
            }
        } else {
            count = 1;
        }
        if (count <= 0) {
            showToastException(R.string.rp_send_act_invalid_count);
            mEtCount.requestFocus();
            return null;
        }
        String greeting = mEtGreeting.getText().toString().trim();
        if (TextUtils.isEmpty(greeting)) {
            greeting = mEtGreeting.getHint().toString();
        }

        // 校验余额
        // 这里不做强校验,即:能拉取到就校验,拉取不到就交给服务器来校验
        if (mWalletMoney != null && mWalletMoney >= 0) {
            int total = money * count;
            if (mWalletMoney < total) {
                showToastException(R.string.rp_send_act_error_money_not_enough);
                return null;
            }
        }
        return new RedPacketParams(money, count, greeting);
    }

    private void sendRedPacket(RedPacketParams params) {
        showProgressDialog("");
        int money = params.money;
        int count = params.count;
        String greeting = params.greeting;
        if (mIsLocation) {
            PoiItem poi = mPoiItem;
            LatLonPoint point;
            if (poi != null && (point = poi.getLatLonPoint()) != null) {
                RPRequest.sendLocationPacket(mToId, mIsGroup, count, money, greeting,
                        point.getLatitude(), point.getLongitude(),
                        poi.getTitle(), poi.getSnippet(),
                        new RPSendCallback(money, count, greeting, poi));
            } else {
                showToastException(R.string.rp_send_act_error_invalid_location);
                dismissProgress();
            }
        } else {
            RPRequest.sendNormalPacket(mToId, mIsGroup, count, money, greeting,
                    new RPSendCallback(money, count, greeting));
        }
    }

    private void bindTotalSendMoney() {
        // 用long类型防溢出
        long perMoney = -1;
        try {
            perMoney = Long.parseLong(mEtMoney.getText().toString());
        } catch (Exception ignored) {
        }
        long count = -1;
        if (mIsGroup) {
            try {
                count = Long.parseLong(mEtCount.getText().toString());
            } catch (Exception ignored) {
            }
        } else {
            count = 1;
        }
        String totalMoney = null;
        if (perMoney > 0 && count > 0) {
            totalMoney = String.valueOf(perMoney * count);
        }
        mTvTotalMoney.setText(StringUtil.optString(totalMoney, "0"));
    }

    private class RPSendCallback implements SimpleCallback<RPBaseResult> {
        private int mMoney;
        private int mCount;
        private String greeting;
        private PoiItem mPoi;

        RPSendCallback(int money, int count, String greeting) {
            mMoney = money;
            mCount = count;
            this.greeting = greeting;
        }

        RPSendCallback(int money, int count, String greeting, PoiItem poi) {
            mMoney = money;
            mCount = count;
            this.greeting = greeting;
            mPoi = poi;
        }

        @Override
        public void onSuccess(RPBaseResult result) {
            dismissProgress();
            RedPacketInfo info;
            if (result != null && (info = result.getRedPacketInfo()) != null) {
                info.setMoney(mMoney);
                info.setCount(mCount);
                info.setGreeting(greeting);
                info.setLocation(mIsLocation);
                LatLonPoint latLonPoint;
                if (mPoi != null && (latLonPoint = mPoi.getLatLonPoint()) != null) {
                    info.setLatitude(latLonPoint.getLatitude());
                    info.setLongitude(latLonPoint.getLongitude());
                    info.setPoiName(mPoi.getTitle());
                    info.setPoiAddress(mPoi.getSnippet());
                }
                setResult(RESULT_OK, new Intent().putExtra(RESULT_RED_PACKET_INFO, info));
                finish();
            } else {
                showToastException(R.string.rp_send_act_error_send_failed);
            }
        }

        @Override
        public void onError(int code, String msg) {
            dismissProgress();
            showToastException(StringUtil.optString(msg, getString(R.string.rp_send_act_error_send_failed)));

            // 测试:模拟成功
            /*RedPacketInfo info = new RedPacketInfo();
            info.setPacketId(System.currentTimeMillis());
            info.setMoney(mMoney);
            info.setCount(mCount);
            info.setGreeting(greeting);
            info.setIsGroup(mIsGroup);
            info.setIsLocation(mIsLocation);
            LatLonPoint latLonPoint;
            if (mPoi != null && (latLonPoint = mPoi.getLatLonPoint()) != null) {
                info.setLatitude(latLonPoint.getLatitude());
                info.setLongitude(latLonPoint.getLongitude());
                info.setPoiName(mPoi.getTitle());
                info.setPoiAddress(mPoi.getSnippet());
            }
            setResult(RESULT_OK, new Intent().putExtra(RESULT_RED_PACKET_INFO, info));
            finish();*/
        }
    }

    private static class RedPacketParams {
        int money;
        int count;
        String greeting;

        RedPacketParams(int money, int count, String greeting) {
            this.money = money;
            this.count = count;
            this.greeting = greeting;
        }
    }
}