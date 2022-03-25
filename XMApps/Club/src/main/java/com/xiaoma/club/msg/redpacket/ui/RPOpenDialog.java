package com.xiaoma.club.msg.redpacket.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.club.R;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.impl.UserRepo;
import com.xiaoma.club.common.util.LocationClientHelper;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.redpacket.constant.RPOpenResult;
import com.xiaoma.club.msg.redpacket.constant.RPOpenStatus;
import com.xiaoma.club.msg.redpacket.datasource.RPRequest;
import com.xiaoma.club.msg.redpacket.model.RPBaseResult;
import com.xiaoma.club.msg.redpacket.model.RedPacketInfo;
import com.xiaoma.club.msg.redpacket.vm.RPOpenVM;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import static com.xiaoma.club.ClubConstant.Hologram.OBTAIN_RED_PACKET_3D_ACTION_ID;

/**
 * Created by LKF on 2019-4-15 0015.
 */
public class RPOpenDialog extends AppCompatDialog {
    private static final String TAG = "RPOpenDialog";
    private FragmentActivity mActivity;
    private ImageView mIvAvatar;
    private TextView mTvSenderDisplay;
    private TextView mTvPoiDisplay;
    private TextView mTvGreeting;
    private Button mBtnOpen;
    private RPOpenVM mVM;
    private TextView mTvOpenDetails;
    private RedPacketInfo mRedPacketInfo;
    private String mSenderHxId;
    private boolean mIsGroup;
    private int mInitialOpenStatus;
    private final UserRepo mUserRepo = ClubRepo.getInstance().getUserRepo();
    private AMapLocationClient mLocationClient;

    /**
     * @param packetInfo        {@link RedPacketInfo}
     * @param initialOpenStatus {@link RPOpenStatus}
     */
    public RPOpenDialog(FragmentActivity activity, RedPacketInfo packetInfo,
                        String senderHxId, boolean isGroup,
                        int initialOpenStatus) {
        super(activity, R.style.rp_open_dialog);
        mActivity = activity;
        mRedPacketInfo = packetInfo;
        mSenderHxId = senderHxId;
        mIsGroup = isGroup;
        mInitialOpenStatus = initialOpenStatus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rp_open);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvSenderDisplay = findViewById(R.id.tv_sender_display);
        mTvPoiDisplay = findViewById(R.id.tv_poi_display);
        mTvGreeting = findViewById(R.id.tv_greeting);
        mBtnOpen = findViewById(R.id.btn_open);
        mTvOpenDetails = findViewById(R.id.tv_open_details);
        mTvOpenDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipRpDetail();
            }
        });

        bindPacketInfo(mRedPacketInfo);

        mVM = ViewModelProviders.of(mActivity).get(RPOpenVM.class);
        // 获取红包的初始状态
        RPOpenVM.RPState initialState = null;
        switch (mInitialOpenStatus) {
            case RPOpenStatus.NOT_OPEN:
                if (mRedPacketInfo.isLocation()) {
                    // 位置红包打开前需要获取定位数据
                    initialState = RPOpenVM.RPState.RP_LOCATING;
                } else {
                    initialState = RPOpenVM.RPState.RP_CAN_OPEN;
                }
                break;
            case RPOpenStatus.HAS_OPEN:
                // 已领取过的红包,直接跳转到详情
                skipRpDetail();
                return;
            case RPOpenStatus.EXPIRED:
                initialState = RPOpenVM.RPState.RP_EXPIRED;
                break;
            case RPOpenStatus.EMPTY:
                initialState = RPOpenVM.RPState.RP_EMPTY;
                break;
        }
        if (initialState == null) {
            XMToast.toastException(mActivity, R.string.rp_open_dlg_invalid_state);
            postDismiss();
            return;
        }
        //bindRPState(mVM.getRPState().getValue());
        mVM.getRPState().observe(mActivity, new Observer<RPOpenVM.RPState>() {
            @Override
            public void onChanged(@Nullable RPOpenVM.RPState state) {
                bindRPState(state);
            }
        });
        mVM.getRPState().setValue(initialState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseLastLocationClient();
    }

    private void skipRpDetail() {
        RPDetailActivity.launch(getContext(), mUserRepo.getByKey(mSenderHxId), mRedPacketInfo);
        postDismiss();
    }

    private void postDismiss() {
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dismiss();
                        } catch (Exception ignored) {
                        }
                    }
                });
    }

    private AMapLocationClient newLocationClient() {
        releaseLastLocationClient();
        AMapLocationClient client = new AMapLocationClient(mActivity);
        AMapLocationClientOption opt = LocationClientHelper.makeLocationOption();
        opt.setOnceLocation(true);
        opt.setNeedAddress(false);
        opt.setLocationCacheEnable(false);
        client.setLocationOption(opt);
        mLocationClient = client;
        return client;
    }

    private void releaseLastLocationClient() {
        AMapLocationClient client = mLocationClient;
        if (client != null) {
            client.stopLocation();
            client.onDestroy();
        }
        mLocationClient = null;
    }

    private void bindPacketInfo(@Nullable RedPacketInfo packetInfo) {
        if (packetInfo == null) {
            XMToast.toastException(getContext(), R.string.rp_open_dlg_invalid_msg);
            postDismiss();
            return;
        }
        String poiDisplay = packetInfo.getPoiAddress();
        String greeting = packetInfo.getGreeting();
        mTvPoiDisplay.setText(poiDisplay);
        mTvGreeting.setText(greeting);
        // 发红包者信息
        User sender = mUserRepo.getByKey(mSenderHxId);
        bindSender(sender);
        if (sender == null) {
            ClubRequestManager.getUserByHxAccount(mSenderHxId, new SimpleCallback<User>() {
                @Override
                public void onSuccess(User u) {
                    if (!isShowing())
                        return;
                    bindSender(u);
                }

                @Override
                public void onError(int code, String msg) {
                }
            });
        }
    }

    private void bindSender(User u) {
        String avatar = null;
        String userName = null;
        if (u != null) {
            avatar = u.getPicPath();
            userName = u.getName();
        }
        ImageLoader.with(getContext())
                .load(avatar)
                .placeholder(R.drawable.default_head_icon)
                .error(R.drawable.default_head_icon)
                .transform(new CircleCrop())
                .into(mIvAvatar);
        mTvSenderDisplay.setText(StringUtil.optString(userName));
        if (!TextUtils.isEmpty(userName)) {
            mTvSenderDisplay.append(getString(R.string.rp_open_dlg_sender_postfix));
        }
    }

    private void bindRPState(@Nullable RPOpenVM.RPState state) {
        if (state == null) {
            XMToast.toastException(getContext(), R.string.rp_open_dlg_invalid_state);
            postDismiss();
            return;
        }
        switch (state) {
            case RP_CAN_OPEN:
                mTvOpenDetails.setVisibility(View.GONE);
                mBtnOpen.setVisibility(View.VISIBLE);
                mBtnOpen.setText(R.string.rp_open_dlg_btn_open);
                mBtnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetworkUtils.isConnected(v.getContext())) {
                            XMToast.toastException(v.getContext(), R.string.net_work_error);
                            return;
                        }
                        mVM.getRPState().setValue(RPOpenVM.RPState.RP_OPENING);
                        // 开始抢红包,发送3D动作
                        XmCarVendorExtensionManager.getInstance().setRobAction(OBTAIN_RED_PACKET_3D_ACTION_ID);
                        RPRequest.openPacket(mRedPacketInfo.getPacketId(), mIsGroup, new SimpleCallback<RPBaseResult>() {
                            @Override
                            public void onSuccess(RPBaseResult result) {
                                if (!isShowing())
                                    return;
                                try {
                                    final int rltCode = Integer.parseInt(result.getResultCode());
                                    // 红包领取成功或已领取,直接进入红包详情
                                    switch (rltCode) {
                                        case RPOpenResult.SUCCESS:
                                            enterRPDetailAndDismiss();
                                            // 成功抢到红包,发送3D动作
                                            XmCarVendorExtensionManager.getInstance().setRobAction(OBTAIN_RED_PACKET_3D_ACTION_ID);
                                            return;
                                        case RPOpenResult.HAS_OBTAIN:
                                            enterRPDetailAndDismiss();
                                            return;
                                        case RPOpenResult.EMPTY:
                                            mRedPacketInfo.setOpenStatus(RPOpenStatus.EMPTY);
                                            mVM.getRPState().postValue(RPOpenVM.RPState.RP_EMPTY);
                                            return;
                                        case RPOpenResult.EXPIRED:
                                            mRedPacketInfo.setOpenStatus(RPOpenStatus.EXPIRED);
                                            mVM.getRPState().postValue(RPOpenVM.RPState.RP_EXPIRED);
                                            return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (mCallback != null) {
                                        mCallback.onPacketOpenResult(mRedPacketInfo.getOpenStatus());
                                    }
                                }
                                XMToast.toastException(getContext(), R.string.rp_open_dlg_result_failed);
                                mVM.getRPState().postValue(RPOpenVM.RPState.RP_CAN_OPEN);
                            }

                            @Override
                            public void onError(int code, String msg) {
                                if (!isShowing())
                                    return;
                                XMToast.toastException(getContext(), StringUtil.optString(msg, getString(R.string.rp_open_dlg_result_failed)));
                                // 拆红包错误,重新置为可拆状态
                                mVM.getRPState().postValue(RPOpenVM.RPState.RP_CAN_OPEN);
                            }

                            private void enterRPDetailAndDismiss() {
                                mRedPacketInfo.setOpenStatus(RPOpenStatus.HAS_OPEN);
                                RPDetailActivity.launch(getContext(), mUserRepo.getByKey(mSenderHxId), mRedPacketInfo);
                                postDismiss();
                            }
                        });
                    }
                });
                mTvGreeting.setText(mRedPacketInfo.getGreeting());
                break;
            case RP_OPENING:
                mTvOpenDetails.setVisibility(View.GONE);
                mBtnOpen.setVisibility(View.VISIBLE);
                mBtnOpen.setText(R.string.rp_open_dlg_btn_opening);
                mBtnOpen.setOnClickListener(null);
                mTvGreeting.setText(mRedPacketInfo.getGreeting());
                break;
            case RP_LOCATING:
                mTvOpenDetails.setVisibility(View.GONE);
                mBtnOpen.setVisibility(View.VISIBLE);
                mBtnOpen.setText(R.string.rp_open_dlg_btn_locating);
                mBtnOpen.setOnClickListener(null);
                mTvGreeting.setText(mRedPacketInfo.getGreeting());
                AMapLocationClient client = newLocationClient();
                client.setLocationListener(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation location) {
                        if (!isShowing())
                            return;
                        bindLocation(location);
                    }
                });
                client.startLocation();
                break;
            case RP_LOCATED_FAIL:
                mTvOpenDetails.setVisibility(View.GONE);
                mBtnOpen.setVisibility(View.VISIBLE);
                mBtnOpen.setText(R.string.rp_open_dlg_btn_located_fail);
                mBtnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 重新尝试定位
                        mVM.getRPState().setValue(RPOpenVM.RPState.RP_LOCATING);
                    }
                });
                mTvGreeting.setText(mRedPacketInfo.getGreeting());
                break;
            case RP_OUT_OF_AREA:
                mTvOpenDetails.setVisibility(View.GONE);
                mBtnOpen.setVisibility(View.VISIBLE);
                mBtnOpen.setText(R.string.rp_open_dlg_btn_to_navi);
                mBtnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            RedPacketInfo info = mRedPacketInfo;
                            String poiName = info.getPoiName();
                            String address = info.getPoiAddress();
                            double lon = info.getLongitude();
                            double lat = info.getLatitude();
                            XmMapNaviManager.getInstance().startNaviToPoi(poiName, address, lon, lat);
                            postDismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            XMToast.toastException(v.getContext(), R.string.rp_open_dlg_invalid_poi);
                        }
                    }
                });
                break;
            case RP_EMPTY:
                mTvOpenDetails.setVisibility(View.VISIBLE);
                mBtnOpen.setVisibility(View.GONE);
                mTvGreeting.setText(R.string.rp_open_dlg_result_empty);
                break;
            case RP_EXPIRED:
                mTvOpenDetails.setVisibility(View.VISIBLE);
                mBtnOpen.setVisibility(View.GONE);
                mTvGreeting.setText(R.string.rp_open_dlg_result_expired);
                break;
        }
    }

    private void bindLocation(AMapLocation location) {
        if (location != null
                && location.getLatitude() != 0f
                && location.getLongitude() != 0f) {
            // 定位成功
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng packetLoc = new LatLng(mRedPacketInfo.getLatitude(), mRedPacketInfo.getLongitude());
            float distance = AMapUtils.calculateLineDistance(myLoc, packetLoc);
            LogUtil.logI(TAG, "onLocationChanged(){ myLoc: %s, packetLoc: %s, distance: %sm }", myLoc, packetLoc, distance);
            int maxDistance = getContext().getResources().getInteger(R.integer.rp_max_distance_can_open_metres);
            if (distance <= maxDistance) {
                mVM.getRPState().postValue(RPOpenVM.RPState.RP_CAN_OPEN);
            } else {
                mVM.getRPState().postValue(RPOpenVM.RPState.RP_OUT_OF_AREA);
            }
        } else {
            // 定位失败
            LogUtil.logI(TAG, "onLocationChanged( location: %s ) Located failed", location);
            mVM.getRPState().postValue(RPOpenVM.RPState.RP_LOCATED_FAIL);
        }
    }

    private String getString(@StringRes int strRes) {
        return mActivity != null ? mActivity.getString(strRes) : "";
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onPacketOpenResult(int rpOpenResult);
    }
}
