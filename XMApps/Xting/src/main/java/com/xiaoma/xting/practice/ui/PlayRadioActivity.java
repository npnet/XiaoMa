package com.xiaoma.xting.practice.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      PlayRadioActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:45
 *  @description：   TODO             */

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.PlayRadioBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.constract.ScrollBarDirection;
import com.xiaoma.ui.dialog.VpRecordDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.ViewUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.local.view.tuneruler.RulerCallback;
import com.xiaoma.xting.local.view.tuneruler.TuneRuler;
import com.xiaoma.xting.practice.adapter.PlayRadioAdapter;
import com.xiaoma.xting.practice.adapter.RadioHistoryAdapter;
import com.xiaoma.xting.practice.manager.HistoryDBManager;
import com.xiaoma.xting.practice.model.RadioHistoryBean;
import com.xiaoma.xting.practice.view.CustomDividerItemDecoration;
import com.xiaoma.xting.practice.view.XmScrollBar;
import com.xiaoma.xting.practice.vm.PlayRadioVM;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayRadioActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "[PlayRadioActivity]";
    private DecimalFormat FMDecimalFormat = new DecimalFormat(".0");
    private TextView btnNative, btnOnline;
    private Button btnOk;
    //本地
    private Button btSkipPrevious, btSkipNext, btReduce, btAdd;
    private TuneRuler tuneRuler;
    private TextView tvChannelFrequency;
    private RadioGroup rgMode;
    private RadioButton rbFm, rbAm;
    private static int currentBandType = XtingConstants.FMAM.TYPE_FM;
    private long currentChannel = 87500;
    private long appointChannel = 0;
    private PlayRadioVM mPlayRadioVM;
    //在线
    private EditText etMusic;
    private Button btVoice, btnSearch, btnDelete;
    private TextView tvSearchResultTitle;
    private RecyclerView rvSearchResult;
    private XmScrollBar scrollBar;
    private PlayRadioAdapter playRadioAdapter;

    private RadioHistoryAdapter mRadioHistoryAdapter;

    private ConstraintLayout includeNative, includeOnline;

    private int mActionPosition;
    private int mRequestCode;

    private PlayRadioBean mPlayRadioBean;

    private int selectIndex = -1;

    private WeakReference<VpRecordDialog> mVoiceRecordDlgRef;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_radio);
        initView();
        initData();
        registerExit();
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        super.onDestroy();
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        }, intentFilter);
    }
    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }
    private void exit() {
        finish();
    }

    private void initView() {
        btnNative = findViewById(R.id.btn_native);
        btnNative.setOnClickListener(this);
        btnOnline = findViewById(R.id.btn_online);
        btnOnline.setOnClickListener(this);
        btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
        includeNative = findViewById(R.id.include_native);
        includeOnline = findViewById(R.id.include_online);
        initLocalView();
        initOnlineView();
    }

    private void initLocalView() {
        //本地
        rgMode = findViewById(R.id.rg_mode);
        rbFm = findViewById(R.id.rb_fm);
        rbAm = findViewById(R.id.rb_am);
//        rgMode.check(R.id.rb_fm);
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbAm.getId() == checkedId) {
                    rbAm.setTextAppearance(R.style.manual_switch_light_blue);
                    rbFm.setTextAppearance(R.style.manual_switch_normal_gray);
                    if (currentBandType != XtingConstants.FMAM.TYPE_AM) {
                        tuneRuler.setMode(XtingConstants.FMAM.TYPE_AM, false);
                        if (appointChannel == 0) {
                            Random random = new Random();
                            currentChannel = random.nextInt(1098) + 522;
                        } else {
                            currentChannel = appointChannel;
                            appointChannel = 0;
                        }
                        Log.i(TAG, "rbAm currentChannel=" + currentChannel);
                        currentBandType = XtingConstants.FMAM.TYPE_AM;
                        tuneRuler.setCurrentScale(currentChannel);
                    }
                }
                if (rbFm.getId() == checkedId) {
                    rbFm.setTextAppearance(R.style.manual_switch_light_blue);
                    rbAm.setTextAppearance(R.style.manual_switch_normal_gray);
                    if (currentBandType != XtingConstants.FMAM.TYPE_FM) {
                        tuneRuler.setMode(XtingConstants.FMAM.TYPE_FM, false);
                        Random random = new Random();
                        currentChannel = (random.nextInt(210) + 870) * 100;
                        Log.i(TAG, "rbFm currentChannel=" + currentChannel);
                        currentBandType = XtingConstants.FMAM.TYPE_FM;
                        tuneRuler.setCurrentScale(currentChannel);
                    }
                }
            }
        });
        rgMode.check(R.id.rb_fm);
        btSkipPrevious = findViewById(R.id.bt_skip_previous);
        btSkipPrevious.setOnClickListener(this);
        btSkipNext = findViewById(R.id.bt_skip_next);
        btSkipNext.setOnClickListener(this);
        btReduce = findViewById(R.id.bt_reduce);
        btReduce.setOnClickListener(this);
        btAdd = findViewById(R.id.bt_add);
        btAdd.setOnClickListener(this);
        tuneRuler = findViewById(R.id.tune_ruler);
        tvChannelFrequency = findViewById(R.id.tv_channel_frequency);
        tvChannelFrequency.setText(getString(R.string.fm_unit, FMDecimalFormat.format(XtingConstants.FMAM.getFMStart())));
        tuneRuler.setMode(XtingConstants.FMAM.TYPE_FM, true);
        tuneRuler.setCallback(new RulerCallback() {
            @Override
            public void onScaleChanging(int scale, boolean byHand) {
                Log.i(TAG, "onScaleChanging() scale=" + scale);
                // 由于调频的滑动可能持续一小段时间，
                // 所以在滑动的过程中fragment有可能被Detached掉
                currentChannel = scale;
                switch (tuneRuler.getmCurrentMode()) {
                    default:
                    case XtingConstants.FMAM.TYPE_FM:
                        tvChannelFrequency.setText(getString(R.string.fm_unit, FMDecimalFormat.format(scale / 1000f)));
                        break;
                    case XtingConstants.FMAM.TYPE_AM:
                        tvChannelFrequency.setText(getString(R.string.am_unit, String.valueOf(scale)));
                        break;
                }
            }
        });
    }

    private void initOnlineView() {
        //在线
        etMusic = findViewById(R.id.et_music);
        btVoice = findViewById(R.id.bt_voice);
        btVoice.setOnClickListener(this);
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);
        tvSearchResultTitle = findViewById(R.id.tv_search_result_title);
        rvSearchResult = findViewById(R.id.rv_search_result);
        scrollBar = findViewById(R.id.scroll_bar);
        scrollBar.setDirection(ScrollBarDirection.DIRECTION_VERTICAL);
        etMusic.setHint(getString(R.string.radio_search));
        etMusic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSearchResult.setLayoutManager(linearLayoutManager);
        playRadioAdapter = new PlayRadioAdapter(new ArrayList<>());
        playRadioAdapter.setEmptyView(R.layout.view_empty_radio, rvSearchResult);
        playRadioAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ViewUtils.isFastClick()){
                    return;
                }
                if (selectIndex != -1) {
                    playRadioAdapter.getData().get(selectIndex).setSelected(false);
                }
                selectIndex = position;
                playRadioAdapter.getData().get(selectIndex).setSelected(true);
                playRadioAdapter.notifyDataSetChanged();

                PlayRadioBean playRadioBean = playRadioAdapter.getData().get(position);
                playRadioBean.setOnline(true);
                playRadioBean.setKey(etMusic.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putString(VrPracticeConstants.ACTION_JSON, GsonHelper.toJson(playRadioBean));
                bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
                LaunchUtils.launchAppWithData(PlayRadioActivity.this, VrPracticeConstants.PACKAGE_NAME,
                        VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                finish();
            }
        });

        mRadioHistoryAdapter = new RadioHistoryAdapter(HistoryDBManager.getInstance().findRadioAll());
        mRadioHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String name = mRadioHistoryAdapter.getData().get(position).getName();
                etMusic.setText(name);
                etMusic.setSelection(name.length());
                mPlayRadioVM.fetchPlayRadioList(etMusic.getText().toString());
//                btnDelete.setVisibility(View.GONE);
//                tvSearchResultTitle.setText(getString(R.string.search_result));
//                if (rvSearchResult.getAdapter() instanceof RadioHistoryAdapter) {
//                    rvSearchResult.setAdapter(playRadioAdapter);
//                    scrollBar.setRecyclerView(rvSearchResult);
//                }
            }
        });
        mRadioHistoryAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btn_history_delete) {
                    HistoryDBManager.getInstance().deleteRadioItem(mRadioHistoryAdapter.getData().get(position));
                    mRadioHistoryAdapter.setNewData(HistoryDBManager.getInstance().findRadioAll());
                }
            }
        });

        rvSearchResult.setAdapter(mRadioHistoryAdapter);
        scrollBar.setRecyclerView(rvSearchResult);

        CustomDividerItemDecoration dividerItemDecoration = new CustomDividerItemDecoration(this, CustomDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.audio_parting_line, null));
        rvSearchResult.addItemDecoration(dividerItemDecoration);


    }

    public void initData() {
        mPlayRadioVM = ViewModelProviders.of(this).get(PlayRadioVM.class);
        mPlayRadioVM.getPlayRadios().observe(this, new Observer<XmResource<List<PlayRadioBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<PlayRadioBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<PlayRadioBean>>() {
                    @Override
                    public void onSuccess(List<PlayRadioBean> data) {
                        btnDelete.setVisibility(View.GONE);
                        tvSearchResultTitle.setText(getString(R.string.search_result));
                        if (rvSearchResult.getAdapter() instanceof RadioHistoryAdapter) {
                            rvSearchResult.setAdapter(playRadioAdapter);
                            scrollBar.setRecyclerView(rvSearchResult);
                        }

                        playRadioAdapter.setNewData(data);
                        btnDelete.setVisibility(View.GONE);
                        selectIndex = -1;
                    }

                    @Override
                    public void onError(int code, String message) {
                        tvSearchResultTitle.setText(getString(R.string.search_result));
                        playRadioAdapter.setNewData(null);
                        btnDelete.setVisibility(View.GONE);
                        selectIndex = -1;
                    }

                    @Override
                    public void onFailure(String msg) {
                        boolean ret = NetworkUtils.isConnected(PlayRadioActivity.this);
                        if (ret) {
                            XMToast.toastException(PlayRadioActivity.this, R.string.deal_error);
                        } else {

                            XMToast.toastException(PlayRadioActivity.this, R.string.network_error);
                        }
                        tvSearchResultTitle.setText(getString(R.string.search_result));
                        playRadioAdapter.setNewData(null);
                        btnDelete.setVisibility(View.GONE);
                        selectIndex = -1;
                    }
                });
            }
        });
        mPlayRadioBean = new PlayRadioBean();
        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        String text = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        if (!TextUtils.isEmpty(text)) {
            mPlayRadioBean = GsonHelper.fromJson(text, PlayRadioBean.class);
            parseIntent(mPlayRadioBean);
        }
        mActionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION, 0);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
    }

    private void parseIntent(PlayRadioBean playRadioBean) {
        Log.i(TAG, "parseIntent() playRadioBean=" + playRadioBean);
        if (playRadioBean == null) {
            return;
        }
        if (playRadioBean.isOnline()) {
            btnNative.setTextColor(getResources().getColor(R.color.color_home_menus_unchecked, null));
            btnOnline.setTextColor(getResources().getColor(R.color.white, null));
            includeNative.setVisibility(View.GONE);
            includeOnline.setVisibility(View.VISIBLE);
            if (!StringUtil.isEmpty(playRadioBean.getKey())) {
                etMusic.setText(playRadioBean.getKey());
                etMusic.setSelection(playRadioBean.getKey().length());
                mPlayRadioVM.fetchPlayRadioList(etMusic.getText().toString());
                rvSearchResult.setAdapter(playRadioAdapter);
                scrollBar.setRecyclerView(rvSearchResult);
                tvSearchResultTitle.setText(getString(R.string.search_result));
                btnDelete.setVisibility(View.GONE);
            }
        } else {
            appointChannel = playRadioBean.getId();
            currentChannel = playRadioBean.getId();
            Log.i(TAG, "parseIntent() currentChannel=" + currentChannel);
            if (currentChannel >= VrPracticeConstants.FMAM.AM_START && currentChannel <= VrPracticeConstants.FMAM.AM_END) {
                rgMode.check(R.id.rb_am);
            }
            tuneRuler.setCurrentScale(currentChannel);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_native:
                btnNative.setTextColor(getResources().getColor(R.color.white, null));
                btnOnline.setTextColor(getResources().getColor(R.color.color_home_menus_unchecked, null));
                includeNative.setVisibility(View.VISIBLE);
                includeOnline.setVisibility(View.GONE);
                btnOk.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_online:
                btnNative.setTextColor(getResources().getColor(R.color.color_home_menus_unchecked, null));
                btnOnline.setTextColor(getResources().getColor(R.color.white, null));
                includeNative.setVisibility(View.GONE);
                includeOnline.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.GONE);
                break;
            case R.id.btn_ok:
                if (includeNative.getVisibility() == View.VISIBLE) {
                    mPlayRadioBean.setId(currentChannel);
                    mPlayRadioBean.setOnline(false);
                    mPlayRadioBean.setTitle(getString(R.string.local_fm));
                    Bundle bundle = new Bundle();
                    bundle.putString(VrPracticeConstants.ACTION_JSON, GsonHelper.toJson(mPlayRadioBean));
                    bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
                    bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                    LaunchUtils.launchAppWithData(PlayRadioActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                    finish();
                } else {
                    mPlayRadioBean = playRadioAdapter.getItem(selectIndex);
                    mPlayRadioBean.setOnline(true);
                    Bundle bundle = new Bundle();
                    bundle.putString(VrPracticeConstants.ACTION_JSON, GsonHelper.toJson(mPlayRadioBean));
                    bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
                    bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                    LaunchUtils.launchAppWithData(PlayRadioActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                    finish();
                }
                break;
            case R.id.bt_skip_previous:
                setCurrentChannel(false);
                Log.i(TAG, "bt_skip_previous currentChannel=" + currentChannel);
                tuneRuler.setCurrentScale(currentChannel);
                break;
            case R.id.bt_skip_next:
                setCurrentChannel(true);
                Log.i(TAG, "bt_skip_next currentChannel=" + currentChannel);
                tuneRuler.setCurrentScale(currentChannel);
                break;
            case R.id.bt_reduce:
                setCurrentChannel(false);
                Log.i(TAG, "bt_reduce currentChannel=" + currentChannel);
                tuneRuler.setCurrentScale(currentChannel);
                break;
            case R.id.bt_add:
                setCurrentChannel(true);
                Log.i(TAG, "bt_add currentChannel=" + currentChannel);
                tuneRuler.setCurrentScale(currentChannel);
                break;
            case R.id.btn_search:
                String word = etMusic.getText().toString();
                if (!StringUtil.isEmpty(word)) {
//                    if (rvSearchResult.getAdapter() instanceof RadioHistoryAdapter) {
//                        rvSearchResult.setAdapter(playRadioAdapter);
//                        scrollBar.setRecyclerView(rvSearchResult);
//                    }
//                    tvSearchResultTitle.setText(getString(R.string.search_result));
//                    btnDelete.setVisibility(View.GONE);
                    HistoryDBManager.getInstance().addRadio(new RadioHistoryBean(word));
                    mPlayRadioVM.fetchPlayRadioList(word);
                } else {
                    showToast("请输入搜索内容");
                }
                break;
            case R.id.btn_delete:
                if (mRadioHistoryAdapter != null) {
                    mRadioHistoryAdapter.setNewData(null);
                }
                HistoryDBManager.getInstance().deleteRadioAll();
                break;
            case R.id.bt_voice:

                if (isVoiceRecordShowing()) {
                    return;
                }

                VpRecordDialog mVpRecordDialog = new VpRecordDialog(this, new VpRecordDialog.Callback() {
                    @Override
                    public void onSend(VpRecordDialog dlg, String translation) {
                        dlg.dismiss();
                        etMusic.setText(translation);
                        if (!TextUtils.isEmpty(translation)) {
                            etMusic.setSelection(translation.length());
                        }
                    }

                    @Override
                    public void onError(VpRecordDialog dlg, int errorCode) {
                        dlg.dismiss();
                    }
                });
                mVpRecordDialog.show();

                mVoiceRecordDlgRef = new WeakReference<>(mVpRecordDialog);
                break;
            default:
                break;
        }
    }

    private boolean isVoiceRecordShowing() {
        if (mVoiceRecordDlgRef == null)
            return false;
        Dialog lastDlg = mVoiceRecordDlgRef.get();
        return lastDlg != null && lastDlg.isShowing();
    }

    public void setCurrentChannel(boolean add) {
        if (add) {
            if (currentBandType == XtingConstants.FMAM.TYPE_FM) {
                currentChannel += 100;
                if (currentChannel > XtingConstants.FMAM.getFMEnd()) {
                    currentChannel = XtingConstants.FMAM.getFMEnd();
                }
            } else {
                currentChannel += 9;
                if (currentChannel > XtingConstants.FMAM.getAMEnd()) {
                    currentChannel = XtingConstants.FMAM.getAMEnd();
                }
            }
        } else {
            if (currentBandType == XtingConstants.FMAM.TYPE_FM) {
                currentChannel -= 100;
                if (currentChannel < XtingConstants.FMAM.getFMStart()) {
                    currentChannel = XtingConstants.FMAM.getFMStart();
                }
            } else {
                currentChannel -= 9;
                if (currentChannel < XtingConstants.FMAM.getAMStart()) {
                    currentChannel = XtingConstants.FMAM.getAMStart();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentBandType = XtingConstants.FMAM.TYPE_FM;
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed()");
        finish();
        super.onBackPressed();
    }
}