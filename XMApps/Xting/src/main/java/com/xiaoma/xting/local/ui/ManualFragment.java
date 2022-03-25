package com.xiaoma.xting.local.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.local.view.tuneruler.RulerCallback;
import com.xiaoma.xting.local.view.tuneruler.TuneRuler;
import com.xiaoma.xting.local.vm.LocalFMVM;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.LocalFMStatusListener;
import com.xiaoma.xting.sdk.LocalFMStatusListenerImpl;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;
import com.xiaoma.xting.wheelControl.WheelCarControlHelper;

import java.text.DecimalFormat;

/**
 * @author KY
 * @date 2018/10/11
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_LOCAL_RADIO_MANUAL_SEARCH)
public class ManualFragment extends VisibilityFragment implements View.OnClickListener {

    private DecimalFormat FMDecimalFormat = new DecimalFormat(".0");
    private RadioGroup rgMode;
    private RadioButton rbFm;
    private RadioButton rbAm;
    private Button btSkipNext;
    private Button btSkipPrevious;
    private TextView tvChannelFrequency;
    private Button btSaveChannel;
    private TuneRuler tuneRuler;
    private Button btAdd;
    private Button btReduce;
    private LocalFMVM localFMVM;
    private boolean initViewDone;
    private Handler mHandler = new Handler();
    private LocalFMStatusListener mFMListener = new FMListener() {
        @Override
        public void onNewStation(XMRadioStation station) {
            Log.d("kaka", "onNewStation1: " + station.getChannel());
            if (tuneRuler.isScrollingOrTouched()) return;
            if (station != null) {
                int radioBand = station.getRadioBand();
                Log.d("kaka", "onNewStation2: " + station.getChannel());
                tuneRuler.setCurrentScale(station.getChannel());
                if (tuneRuler.getmCurrentMode() != radioBand) {
                    tuneRuler.setMode(radioBand, true);
                }
            }
            if (mScanTask != null) {
                mHandler.removeCallbacks(mScanTask);
                mHandler.postDelayed(mScanTask, 500);
            }
        }

        @Override
        public void onBandChanged(BandType band) {
            super.onBandChanged(band);
            switch (band) {
                case AM:
                    tuneRuler.setMode(XtingConstants.FMAM.TYPE_AM, false);
                    break;
                case FM:
                    tuneRuler.setMode(XtingConstants.FMAM.TYPE_FM, false);
                    break;
            }
        }
    };
    private ScanOverTask mScanTask;
    private WheelCarControlHelper.OnWheelHandle mWheelHandle;
    private boolean isScanning;

    public static ManualFragment newInstance() {
        return new ManualFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual_fm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initListener();
        initData();
    }

    private void bindView(View view) {
        rgMode = view.findViewById(R.id.rg_mode);
        rbFm = view.findViewById(R.id.rb_fm);
        rbAm = view.findViewById(R.id.rb_am);
        btSkipNext = view.findViewById(R.id.bt_skip_next);
        btSkipPrevious = view.findViewById(R.id.bt_skip_previous);
        tvChannelFrequency = view.findViewById(R.id.tv_channel_frequency);
        btSaveChannel = view.findViewById(R.id.bt_save_channel);
        tuneRuler = view.findViewById(R.id.tune_ruler);
        btAdd = view.findViewById(R.id.bt_add);
        btReduce = view.findViewById(R.id.bt_reduce);
    }

    private void initView() {
        btSkipNext.setOnClickListener(this);
        btSkipPrevious.setOnClickListener(this);
        btSaveChannel.setOnClickListener(this);
        btAdd.setOnClickListener(this);
        btReduce.setOnClickListener(this);
        refreshFrequency();
    }

    private void refreshFrequency() {
        BandType currentBand = LocalFMFactory.getSDK().getCurrentBand();
        XMRadioStation currentStation = LocalFMFactory.getSDK().getCurrentStation();
        if (currentBand == null) currentBand = BandType.FM;
        if (currentStation == null) {
            if (currentBand == BandType.FM) {
                currentStation = new XMRadioStation(XtingConstants.FMAM.getFMStart(),
                        0, XtingConstants.FMAM.TYPE_FM, null);
            } else {
                currentStation = new XMRadioStation(XtingConstants.FMAM.getAMStart(),
                        0, XtingConstants.FMAM.TYPE_AM, null);
            }
        }
        if (currentBand == BandType.AM) {
            rgMode.check(R.id.rb_am);
            tvChannelFrequency.setText(getString(R.string.am_unit, String.valueOf(currentStation.getChannel())));
        } else {
            rgMode.check(R.id.rb_fm);
            tvChannelFrequency.setText(getString(R.string.fm_unit, FMDecimalFormat.format(currentStation.getChannel() / 1000f)));
        }
        int band = currentBand == BandType.FM ? XtingConstants.FMAM.TYPE_FM : XtingConstants.FMAM.TYPE_AM;
        if (tuneRuler.getmCurrentMode() != band) {
            tuneRuler.setMode(band, true);
        }
        tuneRuler.setCurrentScale(currentStation.getChannel());
    }

    private void initData() {
        localFMVM = ViewModelProviders.of(getActivity()).get(LocalFMVM.class);

        tuneRuler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tuneRuler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initViewDone = true;
                refreshFrequency();
            }
        });
    }

    private void initListener() {
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbAm.getId() == checkedId) {
                    rbAm.setTextAppearance(R.style.manual_switch_light_blue);
                    rbFm.setTextAppearance(R.style.manual_switch_normal_gray);
                    if (LocalFMFactory.getSDK().getCurrentBand() != BandType.AM) {
                        boolean b = LocalFMFactory.getSDK().switchBand(BandType.AM);
                        if (!b) {
                            rgMode.setOnCheckedChangeListener(null);
                            rgMode.check(rbFm.getId());
                            rgMode.setOnCheckedChangeListener(this);
                        }
                    }
                }
                if (rbFm.getId() == checkedId) {
                    rbFm.setTextAppearance(R.style.manual_switch_light_blue);
                    rbAm.setTextAppearance(R.style.manual_switch_normal_gray);
                    if (LocalFMFactory.getSDK().getCurrentBand() != BandType.FM) {
                        boolean b = LocalFMFactory.getSDK().switchBand(BandType.FM);
                        if (!b) {
                            rgMode.setOnCheckedChangeListener(null);
                            rgMode.check(rbAm.getId());
                            rgMode.setOnCheckedChangeListener(this);
                        }
                    }
                }
            }
        });

        tuneRuler.setCallback(new RulerCallback() {
            @Override
            public void onScaleChanging(int scale, boolean byHand) {
                // 由于调频的滑动可能持续一小段时间，
                // 所以在滑动的过程中fragment有可能被Detached掉
                if (!isDetached() && getContext() != null) {
                    int fmType = tuneRuler.getmCurrentMode();
                    btSaveChannel.setEnabled(!LocalFMOperateManager.newSingleton().contains(fmType, scale));
                    switch (fmType) {
                        default:
                        case XtingConstants.FMAM.TYPE_FM:

                            tvChannelFrequency.setText(getString(R.string.fm_unit, FMDecimalFormat.format(scale / 1000f)));
                            if (initViewDone && byHand) {
                                LocalFMFactory.getSDK().tuneFM(scale);
                            }
                            break;
                        case XtingConstants.FMAM.TYPE_AM:
                            tvChannelFrequency.setText(getString(R.string.am_unit, String.valueOf(scale)));
                            if (initViewDone && byHand) {
                                LocalFMFactory.getSDK().tuneAM(scale);
                            }
                            break;
                    }
                }
            }
        });

        // 在scan期间只要按到 TuneRuler 就调用tuneFM/AM阻断继续scan，避免UI错乱
        tuneRuler.setRulerOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (LocalFMFactory.getSDK().getCurrentBand() == BandType.FM) {
                        LocalFMFactory.getSDK().tuneFM(tuneRuler.getCurrentScale());
                    } else {
                        LocalFMFactory.getSDK().tuneAM(tuneRuler.getCurrentScale());
                    }
                }
                return false;
            }
        });

        WheelCarControlHelper.newSingleton().addWheelHandle(mWheelHandle = new WheelCarControlHelper.OnWheelHandle() {
            @Override
            public boolean onHandle(boolean isNext) {
                if (LocalFMFactory.getSDK().isRadioOpen()) {
                    if (isScanning) {
                        LocalFMFactory.getSDK().cancel();
                        return true;
                    }
                    if (isNext) {
                        scanUp();
                    } else {
                        scanDown();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalFMFactory.getSDK().addLocalFMStatusListener(mFMListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalFMFactory.getSDK().removeLocalFMStatusListener(mFMListener);
        WheelCarControlHelper.newSingleton().removeWheelHandle(mWheelHandle);
    }

    @Override
    public boolean onBackPressed() {
        XmAutoTracker.getInstance().onEvent(
                EventConstants.PageDescribe.FRAGMENT_LOCAL_RADIO_MANUAL_SEARCH,
                this.getClass().getName(),
                EventConstants.NormalClick.ACTION_CLOSE_MANUAL_SEARCH_CHANNEL);
        return super.onBackPressed();
    }

    @ResId({R.id.bt_skip_next, R.id.bt_skip_previous, R.id.bt_save_channel, R.id.bt_add, R.id.bt_reduce})
    @NormalOnClick({EventConstants.NormalClick.ACTION_AUTO_SEARCH_NEXT_CHANNEL,
            EventConstants.NormalClick.ACTION_AUTO_SEARCH_PREVIOUS_CHANNEL,
            EventConstants.NormalClick.ACTION_SAVE_SEARCH_RESULT,
            EventConstants.NormalClick.ACTION_MANUAL_SEARCH_CHANNEL_ADD,
            EventConstants.NormalClick.ACTION_MANUAL_SEARCH_CHANNEL_REDUCE})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_skip_next:
                scanUp();
                break;
            case R.id.bt_skip_previous:
                scanDown();
                break;
            case R.id.bt_save_channel:
                boolean saveSuccess = false;
                localFMVM.getLocation();
                switch (tuneRuler.getmCurrentMode()) {
                    case XtingConstants.FMAM.TYPE_FM:
                        saveSuccess = localFMVM.saveFMChannel(new FMChannelBean(tuneRuler.getCurrentScale()));
                        break;
                    case XtingConstants.FMAM.TYPE_AM:
                        saveSuccess = localFMVM.saveAMChannel(new AMChannelBean(tuneRuler.getCurrentScale()));
                        break;
                    default:
                }
                if (saveSuccess) {
                    btSaveChannel.setEnabled(false);
                    XMToast.showToast(mContext, R.string.add_success);
                } else {
                    XMToast.showToast(mContext, R.string.have_added);
                }
                break;
            case R.id.bt_add:
                XMRadioStation currentStation = LocalFMFactory.getSDK().getCurrentStation();
                if (currentStation != null) {
                    int radioBand = currentStation.getRadioBand();
                    int channel = currentStation.getChannel();
                    if ((radioBand == XtingConstants.FMAM.TYPE_FM && channel >= XtingConstants.FMAM.getFMEnd())
                            || (radioBand == XtingConstants.FMAM.TYPE_AM && channel >= XtingConstants.FMAM.getAMEnd())) {
                        XMToast.toastException(getContext(), R.string.out_of_range);
                        return;
                    }
                }
                LocalFMFactory.getSDK().stepNext();
                break;
            case R.id.bt_reduce:
                XMRadioStation fmchannel = LocalFMFactory.getSDK().getCurrentStation();
                if (fmchannel != null) {
                    int radioBand = fmchannel.getRadioBand();
                    int channel = fmchannel.getChannel();
                    if ((radioBand == XtingConstants.FMAM.TYPE_FM && channel <= XtingConstants.FMAM.getFMStart())
                            || (radioBand == XtingConstants.FMAM.TYPE_AM && channel <= XtingConstants.FMAM.getAMStart())) {
                        XMToast.showToast(getContext(), R.string.out_of_range);
                        return;
                    }
                }
                LocalFMFactory.getSDK().stepPrevious();
                break;
            default:
        }
    }

    private void scanDown() {
        isScanning = true;
        LocalFMFactory.getSDK().scanDown();
        setCancelListener();
        showProgressDialog(R.string.scanning);
        if (mScanTask == null) mScanTask = new ScanOverTask();
        mHandler.postDelayed(mScanTask, 500);
    }

    private void scanUp() {
        isScanning = true;
        LocalFMFactory.getSDK().scanUp();
        setCancelListener();
        showProgressDialog(R.string.scanning);
        if (mScanTask == null) mScanTask = new ScanOverTask();
        mHandler.postDelayed(mScanTask, 500);
    }

    private void setCancelListener() {
        getProgressDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getProgressDialog().setOnCancelListener(null);
                mHandler.removeCallbacks(mScanTask);
                LocalFMFactory.getSDK().cancel();
            }
        });
    }

    class ScanOverTask implements Runnable {

        @Override
        public void run() {
            isScanning = true;
            dismissProgress();
        }
    }

    public static abstract class FMListener extends LocalFMStatusListenerImpl {

    }
}
