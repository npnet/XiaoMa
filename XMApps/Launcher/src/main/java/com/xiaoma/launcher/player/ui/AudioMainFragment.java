package com.xiaoma.launcher.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.model.SelectModeBean;
import com.xiaoma.launcher.common.receiver.NetworkReceiver;
import com.xiaoma.launcher.common.views.SelectModelDialog;
import com.xiaoma.launcher.player.adapter.MainAudioAdapter;
import com.xiaoma.launcher.player.callback.LauncherPlayListener;
import com.xiaoma.launcher.player.callback.OnPlayControlListener;
import com.xiaoma.launcher.player.manager.BluetoothReceiver;
import com.xiaoma.launcher.player.manager.PlayerAudioManager;
import com.xiaoma.launcher.player.manager.PlayerConnectHelper;
import com.xiaoma.launcher.player.model.AudioInfoBean;
import com.xiaoma.launcher.player.model.LauncherAudioInfo;
import com.xiaoma.launcher.player.utils.BluetoothUtil;
import com.xiaoma.launcher.player.view.PlayerControlView;
import com.xiaoma.launcher.player.vm.AudioFragmentVM;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.PlayerConstants;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.progress.loading.CustomProgressDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.AudioMainFragmentPagePathDesc)
public class AudioMainFragment extends BaseFragment implements LauncherPlayListener, UsbDetector.UsbDetectListener,
        BluetoothReceiver.BluetoothStateCallback {

    public static final String TAG = "AudioMainFragment";
    public static final String AUDIO_INFO = "audio_info";

    //??????item id
    public static final int BT_ITEM_ID = 3;
    //usb item id
    public static final int USB_ITEM_ID = 4;
    //??????FM item id
    public static final int LOCAL_FM_ID = 6;
    //???????????? item id
    public static final int BENTEN_FM_ID = 10;

    private TabLayout mTabLayout;
    private RecyclerView mAudioRecyclerView;
    private PlayerControlView mPlayerControlView;

    private PlayerConnectHelper connectHelper;
    //tab?????????
    private String[] titles;
    //tab ViewHolder
    private ViewHolder mHolder;

    //????????????info
    private List<LauncherAudioInfo> audioInfoList;
    //????????????adapter
    private MainAudioAdapter mainAudioAdapter;
    //????????????footer width
    private static final int FOOTER_VIEW_WIDTH = 900;
    // LayoutManager
    private LinearLayoutManager mLayoutManager;
    //?????????????????????,??????tab??????
    private int xTingTitlePosition;
    //?????????????????????,??????tab??????
    private int myFavoritePosition;

    //??????????????????
    private AudioInfo mAudioInfo;
    //??????????????????
    private int mPlayState;
    //??????????????????
    private int mDataSource;

    //????????????????????????
    public static final String LAST_SOURCE = "last_source";
    //?????????????????????id
    public static final String CATEGORY_ID = "category_id";
    //????????????????????????
    public static final String LAST_AUDIO_INFO = "last_audio_info";

    //????????????item
    private LauncherAudioInfo mLauncherAudioInfo;

    private AudioFragmentVM mAudioFragmentVM;

    //usb????????????
    private int usbConnectState;
    //??????????????????
    private boolean btMusicConnected = false;
    //??????????????????
    private int btConnectState;

    //?????????????????????????????????????????????
    private boolean mShouldScroll;
    //?????????????????????
    private int mToPosition;
    //??????tab??????
    private boolean isTabClick;
    private int selectTab;

    private static final int DIALOG_TIME_OUT = 404;
    private static final int TIME_OUT = 1000 * 5;

    private TextView mTvRecom;

    private MyHandler mHandler = new MyHandler(this);
    private PlayerAudioManager.ClientOutListener clientOutListener = new PlayerAudioManager.ClientOutListener() {
        @Override
        public void onClientOut(SourceInfo sourceInfo) {
            try {
                Log.d(TAG, "onClientOut: resetPlayStatus:" + sourceInfo.getLocation());
                String location = sourceInfo.getLocation();
                boolean isXtingOut = LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE.equals(location);
                boolean isXtingAudioType = mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_KOALA_ALBUM
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_AM
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_FM
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING;
                if (isXtingAudioType && isXtingOut) {
                    resetLauncherPlayState();
                }

                boolean isMusicOut = LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE.equals(location);
                boolean isMusicAudioType = mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_KUWO_RADIO
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB
                        || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
                if (isMusicAudioType && isMusicOut) {
                    resetLauncherPlayState();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * ??????????????????????????????callBack
     */
    private SwitchResultCallBack switchResultCallBack = new SwitchResultCallBack() {
        @Override
        public void onSwitchCategoryResult(int code, int categoryId) {
            mHandler.removeMessages(DIALOG_TIME_OUT);
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    try {
                        dismissProgress();
                        if (code == AudioConstants.AudioResponseCode.SUCCESS) {
                            //?????????????????????id
                            TPUtils.put(getContext(), CATEGORY_ID, categoryId);

                        } else {
                            mPlayerControlView.setPlayState(AudioConstants.AudioStatus.PAUSING);
                            if (categoryId == USB_ITEM_ID) {
                                XMToast.toastException(getContext(), getString(R.string.audio_usb_play_error));
                                //?????????????????????????????????????????????
                                mLauncherAudioInfo = getLauncherAudioInfoById(TPUtils.get(getContext(), CATEGORY_ID, -1));
                                KLog.d("XM_LOG_" + "notifyLauncherItem: " + "onSwitchCategoryResult");
                                notifyLauncherItem();
                                updateLauncherPlayState(mPlayState);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * ??????CATEGORY_ID?????? LauncherAudioInfo
         * @param id
         * @return
         */
        private LauncherAudioInfo getLauncherAudioInfoById(int id) {
            if (audioInfoList == null) {
                return null;
            }
            try {
                for (int i = 0; i < audioInfoList.size(); i++) {
                    if (audioInfoList.get(i).getId() == id) {
                        return audioInfoList.get(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    };
    private int mAudioType;

    public static AudioMainFragment newInstance() {
        return new AudioMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_audio, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initAudio();
    }

    public void initAudio() {
        //??????????????????
        try {
            mAudioInfo = TPUtils.getObject(getContext(), LAST_AUDIO_INFO, AudioInfo.class);
            KLog.d("XM_LOG_" + "initAudio: " + (mAudioInfo == null));
            if (mAudioInfo != null) {
                KLog.d("XM_LOG_" + "initAudio: " + mAudioInfo.getAudioType());
                final int audioType = mAudioInfo.getAudioType();
                mPlayerControlView.setAudioType(audioType);
                if (audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
                    KLog.d("XM_LOG_onPlaybackStateChanged???" + btMusicConnected);
                    ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (btMusicConnected) {
                                mPlayerControlView.setAudioInfo(mAudioInfo);
                                if (!isInNewGuideMode()) {
                                    connectHelper.playAudio(audioType, AudioConstants.PlayAction.DEFAULT);
                                }
                            }
                        }
                    }, 2000);
                    return;
                }
                if (audioType != AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
                    mPlayerControlView.setAudioInfo(mAudioInfo);
                    if (audioType == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                        onPlayState(AudioConstants.AudioStatus.PLAYING);
                    }
                    if (!isInNewGuideMode()) {
                        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                            @Override
                            public void run() {

                                connectHelper.playAudio(audioType, AudioConstants.PlayAction.DEFAULT);

                            }
                        }, 2500);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isInNewGuideMode() {
        if (GuideDataHelper.shouldShowGuide(GuideConstants.LAUNCHER_SHOWED, true)) {
            return true;
        }
        if (NewGuide.isGuideShowNow()) {
            return true;
        }
        return false;
    }

    private void bindView(View view) {
        EventBus.getDefault().register(this);
        mTabLayout = view.findViewById(R.id.audio_tab);
        mAudioRecyclerView = view.findViewById(R.id.audio_rv);
        mAudioRecyclerView.setHasFixedSize(true);
        mAudioRecyclerView.setItemAnimator(null);
        mPlayerControlView = view.findViewById(R.id.player_control_view);
        mTvRecom = view.findViewById(R.id.tv_recom);
        mTabLayout.setVisibility(View.GONE);
        mTvRecom.setVisibility(View.GONE);
        mPlayerControlView.setVisibility(View.GONE);
//        setSeekbarProgressChangeListener();
        mTvRecom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????????????????
                showModelDialog();
            }
        });
    }

//    private void setSeekbarProgressChangeListener() {
//        mPlayerControlView.setSeekBarProgressChangeListener(new PlayerControlView.OnSeekBarProgressChangeListener() {
//            @Override
//            public void onSeekBarToProgress(int progress) {
//                connectHelper.seek(progress,mAudioType);
//
//            }
//        });
//    }

    private void showModelDialog() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    SelectModelDialog selectModelDialog = new SelectModelDialog(mContext);
                    selectModelDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setTvRecomText();
    }

    @Subscriber(tag = LauncherConstants.REFRESH_RECOM)
    public void refreshRecom(int recomType) {
        if (recomType == LauncherConstants.LIVE_MODEL) {
            mTvRecom.setText(R.string.life_mode);
        } else if (recomType == LauncherConstants.WORK_MODEL) {
            mTvRecom.setText(R.string.work_mode);
        } else if (recomType == LauncherConstants.TRAVEL_MODEL) {
            mTvRecom.setText(R.string.travel_mode);
        } else {
            mTvRecom.setText(R.string.need_quiet);
        }
    }

    private void setTvRecomText() {
        int model = TPUtils.get(mContext, LauncherConstants.CAR_MODEL, LauncherConstants.LIVE_MODEL);
        if (model == LauncherConstants.QUIET_MODEL) {
            //???????????????????????????????????????????????????????????????
            mTvRecom.setText(R.string.need_quiet);
        } else {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            //??????~???????????????????????????????????????~??????????????????????????????
            if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) {
                model = initModeByDay(LauncherConstants.CAR_MODEL_REST, LauncherConstants.LIVE_MODEL);
            } else {
                model = initModeByDay(LauncherConstants.CAR_MODEL_NORMAL, LauncherConstants.WORK_MODEL);
            }
            if (model == LauncherConstants.LIVE_MODEL) {
                mTvRecom.setText(R.string.life_mode);
            } else if (model == LauncherConstants.WORK_MODEL) {
                mTvRecom.setText(R.string.work_mode);
            } else {
                mTvRecom.setText(R.string.travel_mode);
            }
        }
        //????????????????????????
        TPUtils.put(mContext, LauncherConstants.CAR_MODEL, model);
    }

    private int initModeByDay(String carModel, int liveModel) {
        int model;
        SelectModeBean selectModeBean = TPUtils.getObject(mContext, carModel, SelectModeBean.class);
        if (selectModeBean == null) {
            model = liveModel;
        } else {
            //??????calendar ??????????????????????????????????????????????????????????????????????????????
            if (LauncherConstants.CAR_MODEL_NORMAL.equals(carModel)) {
                if (DateUtil.isSameWeekWithToday(selectModeBean.getCalendar(), Calendar.getInstance())) {
                    model = selectModeBean.getModel();
                } else {
                    model = liveModel;
                }
            } else {
                if (DateUtil.getTimeDistance(selectModeBean.getCalendar().getTime(), Calendar.getInstance().getTime()) <= 1) {
                    model = selectModeBean.getModel();
                } else {
                    model = liveModel;
                }
            }
        }
        return model;
    }

    private NetworkReceiver.NetWorkCallback netWorkCallback = new NetworkReceiver.NetWorkCallback() {
        @Override
        public void onNetworkDisconnect() {
        }

        @Override
        public void onNetWorkConnect() {
            if (mainAudioAdapter == null) {
                return;
            }
            if (ListUtils.isEmpty(mainAudioAdapter.getData())) {
                try {
                    initData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void initView() {
        //init Audio??????
        try {
            initAudioListener();
            //init tabLayout
            initTabLayout();
            //init RecyclerView
            initAudioRecyclerView();
            //set ?????????????????????
            setPlayerControlListener();
            //??????????????????
            initData();
            //??????????????????
            NetworkReceiver.addNetWorkListener(netWorkCallback);
            PlayerAudioManager.getInstance().addClientOutListener(clientOutListener);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * init ??????????????????
     */
    private void initAudioListener() {
        connectHelper = PlayerConnectHelper.getInstance();
        //????????????
        connectHelper.setPlayListener(this);
        //usb????????????
        UsbDetector.getInstance().addUsbDetectListener(this);
        //??????????????????
        BluetoothReceiver.setBluetoothStateCallback(this);
    }

    /**
     * init tabLayout
     */
    private void initTabLayout() {
        titles = getResources().getStringArray(R.array.home_audio_tab);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupTabView(mTabLayout);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {
            private CharSequence mTabText;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mTabText.toString(), "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mAudioRecyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    //recycleView????????????
                    mAudioRecyclerView.stopScroll();
                }
                mTabText = changeTab(tab);
                isTabClick = true;
                if (tab.getPosition() == 0) {
                    smoothMoveToPosition(mAudioRecyclerView, 0);

                } else if (tab.getPosition() == 1) {
                    smoothMoveToPosition(mAudioRecyclerView, xTingTitlePosition);

                } else {
                    smoothMoveToPosition(mAudioRecyclerView, myFavoritePosition);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                if (mAudioRecyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    //recycleView????????????
                    mAudioRecyclerView.stopScroll();
                }
                isTabClick = true;
                if (tab.getPosition() == 0) {
                    smoothMoveToPosition(mAudioRecyclerView, 0);

                } else if (tab.getPosition() == 1) {
                    smoothMoveToPosition(mAudioRecyclerView, xTingTitlePosition);

                } else {
                    smoothMoveToPosition(mAudioRecyclerView, myFavoritePosition);
                }
                mTabText = changeTab(tab);
            }
        });

    }

    /**
     * ?????????????????????
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // ?????????????????????
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // ????????????????????????
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // ???????????????:??????????????????????????????????????????
            mRecyclerView.smoothScrollToPosition(position);

        } else if (position <= lastItem) {
            // ???????????????:??????????????????????????????????????????
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int left = mRecyclerView.getChildAt(movePosition).getLeft();
                mRecyclerView.smoothScrollBy(left, 0, new LinearInterpolator());
            }

        } else {
            // ???????????????:????????????????????????????????????
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * init audio recycler
     */
    private void initAudioRecyclerView() {
        audioInfoList = new ArrayList<>();
        mainAudioAdapter = new MainAudioAdapter(audioInfoList, ImageLoader.with(this));
        View view = new View(getActivity());
        view.setLayoutParams(new ViewGroup.LayoutParams(FOOTER_VIEW_WIDTH, mAudioRecyclerView.getHeight()));
        mainAudioAdapter.addFooterView(view, -1, LinearLayout.HORIZONTAL);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mAudioRecyclerView.setLayoutManager(mLayoutManager);
        mAudioRecyclerView.setAdapter(mainAudioAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAudioRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisiblePos < xTingTitlePosition) {
                        selectTab = 0;

                    } else {
                        selectTab = 1;
                    }
                    if (firstVisiblePos == myFavoritePosition) {
                        selectTab = 2;
                    }
                    if (!isTabClick) {
                        mTabLayout.setScrollPosition(selectTab, 0, true);
                        changeTab(mTabLayout.getTabAt(selectTab));
                    }

                    if (mShouldScroll && mToPosition - mLayoutManager.findFirstVisibleItemPosition() <= 4) {
                        mShouldScroll = false;
                        smoothMoveToPosition(mAudioRecyclerView, mToPosition);
                    }
                }
            });

            mAudioRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isTabClick = false;
                    return false;
                }
            });
        }

        //???????????????
        mainAudioAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mainAudioAdapterOnItemClick(position, false);
            }
        });

        //????????????
        mainAudioAdapter.setMyFavoriteClickListener(new MainAudioAdapter.MyFavoriteClickListener() {
            @Override
            public void onMusicFavorite() {
                if (mAudioInfo == null) {
                    mAudioInfo = new AudioInfo();

                } else {
                    mAudioInfo.setPlayState(mPlayState);
                }
                mAudioInfo.setTitle(getString(R.string.favorite_music));
                toFavoriteFrame(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO, AudioListActivity.class);
            }

            @Override
            public void onXtingFavorite() {
                if (mAudioInfo == null) {
                    mAudioInfo = new AudioInfo();

                } else {
                    mAudioInfo.setPlayState(mPlayState);
                }
                toFavoriteFrame(AudioConstants.AudioTypes.XTING_NET_FM, XtingFavoriteActivity.class);
            }
        });
    }

    /**
     * ???????????????
     *
     * @param position
     */
    private void mainAudioAdapterOnItemClick(int position, boolean isRecommand) {
        try {
            LauncherAudioInfo item = mainAudioAdapter.getData().get(position);
            int itemType = item.getItemType();
            switch (itemType) {
                //title
                case MainAudioAdapter.ITEM_TYPE_XTING_TITLE:
                case MainAudioAdapter.ITEM_TYPE_MUSIC_TITLE:
                case MainAudioAdapter.ITEM_TYPE_MY_FAVORITE:
                    return;

                //????????????
                case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.AUDIO_ITEM_BT_MUSIC,
                            !btMusicConnected + "",
                            EventConstants.PagePath.AudioMainFragment,
                            EventConstants.PageDescribe.AudioMainFragmentPagePathDesc);
                    //???????????????
                    if (!btMusicConnected) {
                        jumpToSetting();
                        return;
                    }
                    break;
                //USB???????????????
                case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:

                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.AUDIO_ITEM_USB_MUSIC,
                            (usbConnectState != AudioConstants.ConnectStatus.USB_SCAN_FINISH) + "",
                            EventConstants.PagePath.AudioMainFragment,
                            EventConstants.PageDescribe.AudioMainFragmentPagePathDesc);
                    if (usbConnectState != AudioConstants.ConnectStatus.USB_SCAN_FINISH) {
                        XMToast.showToast(getContext(), getString(R.string.usb_audio_no_found));
                        return;
                    }
                    break;
            }
            XmAutoTracker.getInstance().onEvent(item.getName(),
                    item.getId() + "",
                    EventConstants.PagePath.AudioMainFragment,
                    EventConstants.PageDescribe.AudioMainFragmentPagePathDesc);

            //????????????
            if ((!NetworkUtils.isConnected(getContext())) && isNeedNetCategoryItem(item.getAudioType())) {
                XMToast.toastException(getContext(), getString(R.string.audio_no_network));
                return;
            }

            //??????????????????????????????&&??????Tab??????????????????
            if (isXtingCategoryItem(item.getAudioType()) && selectTab != 1) {
                smoothMoveToPosition(mAudioRecyclerView, xTingTitlePosition);
            }
            int playState = item.getPlayState();
            connectHelper.isRecommand = isRecommand;
            //??????????????????
            if (mLauncherAudioInfo != null && mLauncherAudioInfo.getId() == item.getId()) {
                if (playState == LauncherConstants.LOADING_STATE) {
                    return;
                }
                if (playState == LauncherConstants.PLAY_STATE) {
                    //????????????
                    if (mAudioInfo != null) {
                        connectHelper.pauseAudio(mAudioInfo.getAudioType());
                    }
                    return;
                }
            }

            mLauncherAudioInfo = item;

            //????????????
            if (item.getId() == TPUtils.get(getContext(), CATEGORY_ID, -1) && mAudioInfo != null &&
                    (mAudioInfo.getLauncherCategoryId() == mLauncherAudioInfo.getId() || mLauncherAudioInfo.isSelected())) {
                connectHelper.playAudio(mLauncherAudioInfo.getAudioType(), AudioConstants.PlayAction.DEFAULT);

            } else {
                //??????????????????
                if (mAudioInfo != null) {
                    connectHelper.pauseAudio(mAudioInfo.getAudioType());
                }
                //??????????????????
                connectHelper.playAudioCategory(item.getAudioType(), item.getId(), switchResultCallBack);
                if (item.getAudioType() == PlayerConstants.AudioTypes.XTING_LOCAL_FM) {
                    getProgressDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            getProgressDialog().setOnCancelListener(null);
                            connectHelper.cancelScan();
                        }
                    });

                    getProgressDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getProgressDialog().setOnCancelListener(null);
                            getProgressDialog().setOnDismissListener(null);
                        }
                    });
                }
                showProgressDialog("");
                //            mHandler.sendEmptyMessageDelayed(DIALOG_TIME_OUT, TIME_OUT);
            }

            //???????????????????????????
            KLog.d("XM_LOG_" + "mainAudioAdapterOnItemClick: " + "notifyLauncherItem");
            notifyLauncherItem();
            //??????????????????????????????
            updateLauncherPlayState(LauncherConstants.PAUSE_STATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isXtingCategoryItem(int type) {
        return type == AudioConstants.AudioTypes.XTING_LOCAL_FM ||
                type == AudioConstants.AudioTypes.XTING_NET_FM ||
                type == AudioConstants.AudioTypes.XTING_KOALA_ALBUM;
    }

    private boolean isNeedNetCategoryItem(int type) {
        return type == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO ||
                type == AudioConstants.AudioTypes.XTING_NET_FM ||
                type == AudioConstants.AudioTypes.XTING_KOALA_ALBUM;
    }

    private static class MyHandler extends Handler {
        private WeakReference<AudioMainFragment> weakReference;

        private MyHandler(AudioMainFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AudioMainFragment fragment = weakReference.get();
            if (fragment != null && msg.what == DIALOG_TIME_OUT) {
                fragment.disMissDialog();
            }
        }
    }

    private void disMissDialog() {
        CustomProgressDialog dialog = getProgressDialog();
        if (dialog != null && dialog.isShowing()) {
            dismissProgress();
        }
    }

    private void jumpToSetting() {
        NodeUtils.jumpTo(getContext(), CenterConstants.SETTING, "com.xiaoma.setting.main.ui.MainActivity",
                NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.BLUETOOTH_SETTINGS);
    }

    /**
     * ??????????????????
     *
     * @param audioType ???????????????
     */
    private void toFavoriteFrame(int audioType, Class className) {
        Intent intent = new Intent(mContext, className);
        intent.putExtra(AUDIO_INFO, mAudioInfo);
        mContext.startActivity(intent);
    }

    /**
     * ?????????????????????
     */
    private void setPlayerControlListener() {
        mPlayerControlView.setOnPlayControlListener(new OnPlayControlListener() {
            /**
             * ?????????
             * @param audioType ????????????
             */
            @Override
            public void onPre(int audioType) {
                connectHelper.preNextAudio(AudioConstants.Action.Option.PREVIOUS, audioType);
            }

            /**
             * ?????????
             * @param audioType ????????????
             */
            @Override
            public void onNext(int audioType) {
                connectHelper.preNextAudio(AudioConstants.Action.Option.NEXT, audioType);
            }

            /**
             * ???????????????
             * @param audioType ????????????
             * @param playState
             */
            @Override
            public void onPlayOrPause(int audioType, int playState) {
                if (playState == AudioConstants.AudioStatus.PLAYING) {
                    connectHelper.pauseAudio(audioType);

                } else {
                    connectHelper.playAudio(audioType, AudioConstants.PlayAction.DEFAULT);
                }
            }

            /**
             * ????????????
             * @param playState
             */
            @Override
            public void onStartListActivity(int playState) {
                onPlayerStartListFrame(playState);
            }

            /**
             * ??????
             * @param audioType
             * @param favoriteState
             */
            @Override
            public void onFavorite(int audioType, boolean favoriteState) {
                onPlayerFavoriteClick(audioType, favoriteState);
            }
        });
    }

    /**
     * ???????????????????????????
     *
     * @param playState
     */
    private void onPlayerStartListFrame(int playState) {
        try {
            Intent intent = new Intent(mContext, AudioListActivity.class);
            //??????????????????????????????
            mAudioInfo.setPlayState(playState);
            //???????????????????????????????????????,????????????????????????
            if (mLauncherAudioInfo != null) {
                if (mDataSource == AudioConstants.OnlineInfoSource.LAUNCHER) {
                    mAudioInfo.setTitle(mLauncherAudioInfo.getName());
                    mAudioInfo.setLauncherCategoryId(TPUtils.get(getContext(), CATEGORY_ID, -1));
                }
            }
            intent.putExtra(AUDIO_INFO, mAudioInfo);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????????
     *
     * @param audioType
     * @param favoriteState
     */
    private void onPlayerFavoriteClick(int audioType, boolean favoriteState) {
        try {
            //????????????
            if (!NetworkUtils.isConnected(getContext())) {
                XMToast.toastException(getContext(), getString(R.string.audio_no_network));
                return;
            }
            if (favoriteState) {
                //????????????
                connectHelper.favoriteAudio(audioType);
                mPlayerControlView.setFavoriteState(false);
                XMToast.showToast(getContext(), getString(R.string.un_collected_audio_success));

            } else {
                //??????
                connectHelper.favoriteAudio(audioType);
                mPlayerControlView.setFavoriteState(true);
                XMToast.showToast(getContext(), getString(R.string.collected_audio_success));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTabView(TabLayout tabLayout) {
        try {
            for (int i = 0; i < titles.length; i++) {
                mTabLayout.addTab(mTabLayout.newTab(), false);
            }

            for (int i = 0; i < titles.length; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(R.layout.audio_tab_layout_item);
                    View view = tab.getCustomView();
                    if (view != null) {
                        mHolder = new ViewHolder(view);
                        mHolder.tabTv.setText(titles[i]);
                    }
                }
                if (i == 0) {
                    mHolder.tabTv.setSelected(true);
                    mHolder.tabTv.setTextAppearance(mContext, R.style.audio_tab_text_view_selected);
                    mHolder.tabTv.setBackgroundResource(R.drawable.title_select);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String changeTab(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab1 = mTabLayout.getTabAt(i);
            View customView = tab1.getCustomView();
            mHolder = new ViewHolder(customView);
            mHolder.tabTv.setSelected(false);
            mHolder.tabTv.setBackgroundResource(R.color.transparent);
            mHolder.tabTv.setTextAppearance(getActivity(), R.style.audio_tab_text_view_normal);
        }
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ViewHolder(customView);
        mHolder.tabTv.setSelected(true);
        mHolder.tabTv.setTextAppearance(getActivity(), R.style.audio_tab_text_view_selected);
        mHolder.tabTv.setBackgroundResource(R.drawable.title_select);
        return mHolder.tabTv.getText().toString();
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }

    @Override
    public void onProgress(final ProgressInfo progressInfo) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                mPlayerControlView.setProgressInfo(progressInfo);
            }
        });
    }

    @Override
    public void onAudioInfo(final AudioInfo audioInfo) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                recoverAudio(audioInfo);
            }
        });
    }

    /**
     * ????????????
     *
     * @param audioInfo
     */
    private void recoverAudio(AudioInfo audioInfo) {
        try {
            if (audioInfo == null || audioInfo.isHistory()) {
                return;
            }
            //???????????????????????????????????????????????????,?????????????????????????????????
            if (mLauncherAudioInfo != null && mDataSource == AudioConstants.OnlineInfoSource.LAUNCHER &&
                    mAudioType != AudioConstants.AudioTypes.MUSIC_LOCAL_BT &&
                    audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
                return;
            }

            mAudioInfo = audioInfo;

            //????????????,?????????????????????,??????????????????(??????????????????)
            handleXtingMusicPlay();

            //?????????????????????????????????,???????????????????????????
            if (mLauncherAudioInfo != null) {
                if (mLauncherAudioInfo.isMusicItem() && (!audioIsMusicItem(audioInfo.getAudioType()) ||
                        mLauncherAudioInfo.getAudioType() != audioInfo.getAudioType())) {
                    mDataSource = AudioConstants.OnlineInfoSource.XMLY;

                } else if (!mLauncherAudioInfo.isMusicItem() && (audioIsMusicItem(audioInfo.getAudioType()) ||
                        mLauncherAudioInfo.getAudioType() != audioInfo.getAudioType())) {
                    mDataSource = AudioConstants.OnlineInfoSource.KUWO;
                }
                //???????????????????????????
                KLog.d("XM_LOG_" + "recoverAudio: " + "notifyLauncherItem");
                notifyLauncherItem();
            }

            //??????????????????
            if (getActivity() != null && !getActivity().isDestroyed()) {
                mPlayerControlView.setAudioInfo(audioInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????FM???,??????????????????FM??????
     * ??????????????????FM,???????????????,????????????????????????
     * ???????????????USB,?????????,???????????????????????????
     */
    private void handleXtingMusicPlay() {
        try {
            if (mAudioInfo != null && (mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM ||
                    (mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_KOALA_ALBUM && mAudioInfo.getLauncherCategoryId() > 0) ||
                    mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT ||
                    mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB)) {
                if (mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                    //??????FM?????????????????????
                    onPlayState(AudioConstants.AudioStatus.PLAYING);
                }
                for (LauncherAudioInfo info : audioInfoList) {
                    if (info.getAudioType() == mAudioInfo.getAudioType()) {
                        mLauncherAudioInfo = info;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean audioIsMusicItem(int audioType) {
        return audioType == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO ||
                audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_USB ||
                audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
    }

    /**
     * ???????????????????????????
     */
    private void notifyLauncherItem() {
        try {
            //????????????????????????
            for (LauncherAudioInfo item : audioInfoList) {
                item.setSelected(false);
            }
            //?????????????????????,???????????????????????????
            if (mDataSource == AudioConstants.OnlineInfoSource.LAUNCHER) {
                if (mLauncherAudioInfo != null) {
                    mLauncherAudioInfo.setSelected(true);
                }
            }
            notifyData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayState(final int playState) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    mPlayState = playState;
                    mPlayerControlView.setPlayState(playState);
                    updateLauncherPlayState(playState);
                    //????????????FM
                    if (mPlayState == AudioConstants.AudioStatus.EXIT && mAudioInfo == null) {
                        resetLauncherPlayState();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ?????????????????????????????????
     *
     * @param playState
     */
    private void updateLauncherPlayState(int playState) {
        try {
            //????????????????????????
            for (LauncherAudioInfo audioInfo : audioInfoList) {
                audioInfo.setPlayState(LauncherConstants.NULL_STATE);
            }
            //????????????????????????launcher
            if (mDataSource == AudioConstants.OnlineInfoSource.LAUNCHER) {
                //???????????????????????????,??????????????????
                if (mLauncherAudioInfo != null && mLauncherAudioInfo.isSelected() && mAudioInfo != null) {
                    mLauncherAudioInfo.setPlayState(playState);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyData();
    }

    private void notifyData() {
        ViewCompat.postOnAnimation(getView(), new Runnable() {
            @Override
            public void run() {
                mainAudioAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDataSource(final int dataSource) {
        this.mDataSource = dataSource;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                mPlayerControlView.setDataSource(dataSource);
            }
        });
    }

    @Override
    public void onAudioFavorite(final boolean favorite) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                mPlayerControlView.setFavoriteState(favorite);
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void initData() {
        //??????????????????
        try {
            boolean blueConnect = BluetoothUtil.getBlueToothConnectState(mContext);
            if (blueConnect) {
                onBTSinkConnected();
            } else {
                onBTSinkDisconnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //??????USB????????????
        try {
            UsbDetector.getInstance().syncUsbConnectState(getContext(), AudioMainFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mAudioFragmentVM = ViewModelProviders.of(AudioMainFragment.this).get(AudioFragmentVM.class);
            String json = TPUtils.get(getContext(), AudioFragmentVM.CACHE_AUDIO_MAIN_KEY, "");
            if (!StringUtil.isEmpty(json)) {
                //??????????????????????????????
                AudioInfoBean data = GsonHelper.fromJson(json, AudioInfoBean.class);
                showContentView();
                mTabLayout.setVisibility(View.VISIBLE);
                mTvRecom.setVisibility(View.VISIBLE);
                mPlayerControlView.setVisibility(View.VISIBLE);
                if (data != null) {
                    wrapAudioInfoData(data);
                }
            } else {
                String moreAppPath = "config/AudioList.json";
                String textFromAsset = AssetUtils.getTextFromAsset(mContext, moreAppPath);
                AudioInfoBean result = GsonHelper.fromJson(textFromAsset, AudioInfoBean.class);
                if (result != null) {
                    showContentView();
                    mTabLayout.setVisibility(View.VISIBLE);
                    mTvRecom.setVisibility(View.VISIBLE);
                    mPlayerControlView.setVisibility(View.VISIBLE);
                    wrapAudioInfoData(result);
                }
            }
            mAudioFragmentVM.fetchLauncherAudioList(StringUtil.isEmpty(json));
            mAudioFragmentVM.getAudioList().observe(AudioMainFragment.this, new Observer<XmResource<AudioInfoBean>>() {
                @Override
                public void onChanged(@Nullable XmResource<AudioInfoBean> listXmResource) {
                    if (listXmResource == null) {
                        return;
                    }
                    listXmResource.handle(new OnCallback<AudioInfoBean>() {
                        @Override
                        public void onSuccess(AudioInfoBean data) {
                            showContentView();
                            mTabLayout.setVisibility(View.VISIBLE);
                            mTvRecom.setVisibility(View.VISIBLE);
                            mPlayerControlView.setVisibility(View.VISIBLE);
                            wrapAudioInfoData(data);
                        }

                        @Override
                        public void onFailure(String msg) {
                            showNoNetView();
                            mTabLayout.setVisibility(View.GONE);
                            mTvRecom.setVisibility(View.GONE);
                            mPlayerControlView.setVisibility(View.GONE);
                        }
                    });
                }
            });
            PlayerConnectHelper.getInstance().setOnAudioTypeChangedListener(new PlayerConnectHelper.OnAudioTypeChangedListener() {
                @Override
                public void onAudioTypeChange(int audioType) {
                    mPlayerControlView.setAudioType(audioType);
                    mAudioType = audioType;
                }

                @Override
                public void onClientConnect() {
                    initAudio();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????????????????
     *
     * @param data
     */
    private void wrapAudioInfoData(AudioInfoBean data) {
        try {
            audioInfoList.clear();
            //?????????????????????
            LauncherAudioInfo musicTitle = new LauncherAudioInfo();
            musicTitle.setAudioType(MainAudioAdapter.ITEM_TYPE_MUSIC_TITLE);
            musicTitle.setLogo(R.drawable.music_card_music);
            musicTitle.setName(getString(R.string.audio_fragment_audio_music));
            musicTitle.setNameEn(getString(R.string.audio_fragment_audio_music));
            musicTitle.setSubTitle(getString(R.string.audio_fragment_audio_music_en));
            audioInfoList.add(musicTitle);
            //??????
            audioInfoList.addAll(data.getMusic());

            //?????????????????????
            LauncherAudioInfo xTingTitle = new LauncherAudioInfo();
            xTingTitle.setAudioType(MainAudioAdapter.ITEM_TYPE_XTING_TITLE);
            xTingTitle.setLogo(R.drawable.music_card_radio);
            xTingTitle.setName(getString(R.string.audio_fragment_audio_xting));
            xTingTitle.setNameEn(getString(R.string.audio_fragment_audio_xting));
            xTingTitle.setSubTitle(getString(R.string.audio_fragment_audio_xting_en));
            audioInfoList.add(xTingTitle);
            //??????
            audioInfoList.addAll(data.getRadio());

            //????????????
            LauncherAudioInfo favorite = new LauncherAudioInfo();
            favorite.setAudioType(MainAudioAdapter.ITEM_TYPE_MY_FAVORITE);
            favorite.setLogo(R.drawable.music_card_collect);
            favorite.setName(getString(R.string.audio_fragment_audio_favorite));
            favorite.setNameEn(getString(R.string.audio_fragment_audio_favorite));
            favorite.setSubTitle(getString(R.string.audio_fragment_audio_favorite_en));
            audioInfoList.add(favorite);
            //????????????item position
            xTingTitlePosition = data.getMusic().size() + 1;
            //??????item position
            myFavoritePosition = audioInfoList.size() - 1;

            selectLastPlayItem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????????????????
     */
    private void selectLastPlayItem() {
        //1.????????????????????????????????????launcher
        try {
            mDataSource = TPUtils.get(getContext(), LAST_SOURCE, -1);
            if (mDataSource == AudioConstants.OnlineInfoSource.LAUNCHER) {
                //2.???????????????????????????id
                int id = TPUtils.get(getContext(), CATEGORY_ID, -1);
                for (LauncherAudioInfo audioInfo : audioInfoList) {
                    audioInfo.setSelected(false);
                    if (audioInfo.getId() == id) {
                        if (id == LOCAL_FM_ID || connectHelper.isPlayingState()) {
                            audioInfo.setPlayState(LauncherConstants.PLAY_STATE);
                        } else {
                            audioInfo.setPlayState(LauncherConstants.PAUSE_STATE);
                        }
                        if (id == USB_ITEM_ID && usbConnectState != AudioConstants.ConnectStatus.USB_SCAN_FINISH) {
                            audioInfo.setSelected(false);
                        } else if (id == BT_ITEM_ID && !btMusicConnected) {
                            audioInfo.setSelected(false);
                        } else {
                            audioInfo.setSelected(true);
                        }
                        mLauncherAudioInfo = audioInfo;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyData();
    }

    @Subscriber(tag = LauncherConstants.RECOMMEND_PLAY)
    public void onRecommendPlayClick(int type) {
        //??????????????????
        int index = type == LauncherConstants.RECOMMEND_MUSIC ? 1 : xTingTitlePosition + 1;
        mainAudioAdapterOnItemClick(index, true);
    }

    public void showPlayerBgDefault() {
        mPlayerControlView.setBgDefault();
    }

    //-----------------------USB??????????????????--------------------------------
    @Override
    public void noUsbMounted() {
        try {
            usbConnectState = AudioConstants.ConnectStatus.USB_NOT_MOUNTED;
            mainAudioAdapter.setUsbConnectState(usbConnectState);
            KLog.d("usbConnectState:" + usbConnectState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mounted(List<String> mountPaths) {
        try {
            //USB????????????
            usbConnectState = AudioConstants.ConnectStatus.USB_SCAN_FINISH;
            mainAudioAdapter.setUsbConnectState(usbConnectState);
            KLog.d("usbConnectState:" + usbConnectState);
            AudioInfo audioInfo = TPUtils.getObject(getContext(), LAST_AUDIO_INFO, AudioInfo.class);
            if (audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
                mPlayerControlView.setAudioInfo(audioInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removed() {
        try {
            //??????usb????????????
            usbConnectState = AudioConstants.ConnectStatus.USB_REMOVE;
            mainAudioAdapter.setUsbConnectState(usbConnectState);
            //??????????????????usb???????????????
            handPlayingUsbRemove();
            KLog.d("usbConnectState:" + usbConnectState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mountError() {
        try {
            usbConnectState = AudioConstants.ConnectStatus.USB_UNSUPPORT;
            mainAudioAdapter.setUsbConnectState(usbConnectState);
            KLog.d("usbConnectState:" + usbConnectState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inserted() {
        KLog.d("usbConnectState: insert");
    }
    //-----------------------USB??????????????????--------------------------------

    /**
     * ????????????usb???????????????
     */
    private void handPlayingUsbRemove() {
        try {
            KLog.d("XM_LOG_" + "handPlayingUsbRemove: " + "");
            if (mAudioInfo != null && mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
                resetLauncherPlayState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------????????????????????????--------------------------------
    @Override
    public void onBTSinkDisconnected() {
        try {
            btConnectState = AudioConstants.ConnectStatus.BLUETOOTH_SINK_DISCONNECTED;
            btMusicConnected = false;
            mainAudioAdapter.setBTConnectState(false);
            handPlayingBtBreak();
            KLog.d("btConnectState:" + btConnectState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBTSinkConnected() {
        try {
            btConnectState = AudioConstants.ConnectStatus.BLUETOOTH_SINK_CONNECTED;
            btMusicConnected = true;
            mainAudioAdapter.setBTConnectState(true);
            KLog.d("btConnectState:" + btConnectState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBTConnected() {
        try {
            btConnectState = AudioConstants.ConnectStatus.BLUETOOTH_CONNECTED;
            btMusicConnected = true;
            mainAudioAdapter.setBTConnectState(true);
            KLog.d("btConnectState:" + btConnectState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBTDisconnected() {
        try {
            btConnectState = AudioConstants.ConnectStatus.BLUETOOTH_DISCONNECTED;
            btMusicConnected = false;
            mainAudioAdapter.setBTConnectState(false);
            handPlayingBtBreak();
            KLog.d("btConnectState:" + btConnectState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-----------------------????????????????????????--------------------------------

    /**
     * ?????????????????????????????????
     */
    private void handPlayingBtBreak() {
        try {
            if (mAudioInfo != null && mAudioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
                resetLauncherPlayState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void resetLauncherPlayState() {
        try {
            //????????????????????????
            for (LauncherAudioInfo audioInfo : audioInfoList) {
                audioInfo.setPlayState(LauncherConstants.NULL_STATE);
                audioInfo.setSelected(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                KLog.d("XM_LOG_" + "run: " + "4");
                notifyData();
                mPlayerControlView.setAudioInfo(null);
                mPlayerControlView.setBgDefault();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        UsbDetector.getInstance().removeUsbDetectListener(this);
        BluetoothReceiver.removeCallback();
        NetworkReceiver.removeNetWorkListener(netWorkCallback);
        PlayerAudioManager.getInstance().removeClientOutListener(clientOutListener);
    }

    /**
     * ??????????????????CallBack
     */
    public interface SwitchResultCallBack {
        void onSwitchCategoryResult(int code, int categoryId);
    }
}