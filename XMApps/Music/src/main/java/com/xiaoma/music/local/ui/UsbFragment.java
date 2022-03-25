package com.xiaoma.music.local.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.callback.UsbFileSearchListener;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.model.ShowHideEvent;
import com.xiaoma.music.common.model.UsbMemroySignal;
import com.xiaoma.music.common.util.UsbMusicRecordManager;
import com.xiaoma.music.common.view.RecyclerDividerItem;
import com.xiaoma.music.local.adapter.UsbAdapter;
import com.xiaoma.music.local.vm.UsbMusicVM;
import com.xiaoma.music.manager.SortUsbMusicManager;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.utils.UsbScanManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.receiver.UsbDetector;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
@PageDescComponent(EventConstants.PageDescribe.usbFragment)
public class UsbFragment extends BaseFragment implements UsbDetector.UsbDetectListener, OnUsbMusicChangedListener, UsbFileSearchListener {

    private static final String TAG = UsbFragment.class.getSimpleName();
    private RecyclerView rvUsbMusic;
    private LinearLayout unsupportedDevices;
    private LinearLayout noResponsesDevices;
    private LinearLayout connectInterruptDevices;
    private LinearLayout noDevices;
    private UsbAdapter mUsbAdapter;
    private UsbMusicVM usbMusicVM;
    private XmScrollBar scrollBar;
    private RelativeLayout emptyView;

    public static UsbFragment newInstance() {
        return new UsbFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_usb, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initVM();
        initEvent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UsbDetector.getInstance().addUsbDetectListener(this);
        UsbMusicFactory.getUsbPlayerProxy().addMusicChangeListener(this);
        EventBus.getDefault().register(this);
        UsbScanManager.getInstance().addUsbScanListener(this);
    }

    private void initVM() {
        usbMusicVM = ViewModelProviders.of(this).get(UsbMusicVM.class);
        usbMusicVM.getUsbList().observe(this, usbMusics -> {
            if (UsbDetector.getInstance().isRemoveState()) {
                showStateView(noDevices);
                dismissProgress();
                return;
            }
            if (!ListUtils.isEmpty(usbMusics)) {
                mUsbAdapter.setNewData(usbMusics);
                showStateView(rvUsbMusic);
                scrollBar.setVisibility(View.VISIBLE);
            } else {
                scrollBar.setVisibility(View.GONE);
                showStateView(emptyView);
            }
            dismissProgress();
        });
    }

    private void initEvent() {
        rvUsbMusic.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_first_margin);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0, first);
        rvUsbMusic.addItemDecoration(dividerItem);
        mUsbAdapter = new UsbAdapter(ImageLoader.with(this));
        rvUsbMusic.setItemAnimator(null);
        rvUsbMusic.setAdapter(mUsbAdapter);
        scrollBar.setRecyclerView(rvUsbMusic);
        mUsbAdapter.setOnItemClickListener((adapter, view, position) -> handleUsbMemory(position));
        if (!MusicConstants.SHOW_LOCAL_TEST) {
            initUsbListener();
        } else {
            testScanSDCard();
        }
        AudioSourceManager.getInstance().addAudioSourceListener((preAudioSource, currAudioSource) -> {
            if (mUsbAdapter != null) {
                mUsbAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (mUsbAdapter != null) {
            mUsbAdapter.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UsbDetector.getInstance().removeUsbDetectListener(this);
        UsbMusicFactory.getUsbPlayerProxy().removeMusicChangeListener(this);
        EventBus.getDefault().unregister(this);
        UsbScanManager.getInstance().removeUsbScanListener(this);
        UsbMusicRecordManager.getInstance().stopUsbMemory();
    }

    private void testScanSDCard() {
        ThreadDispatcher.getDispatcher().postNormalPriority(() -> {
            final ArrayList<UsbMusic> musicList = UsbScanManager.getInstance().scanAudioFiles(mContext);
            usbMusicVM.getUsbList().postValue(musicList);
        });
        showStateView(rvUsbMusic);
    }

    private void initUsbListener() {
//            UsbDetector.getInstance().syncUsbConnectState(mContext);
        List<UsbMusic> usbMusicList = UsbScanManager.getInstance().getUsbMusicList();
        ArrayList<UsbMusic> usbMusics = new ArrayList<>(usbMusicList);
        usbMusicVM.getUsbList().postValue(usbMusics);
    }

    private void showStateView(final View view) {
        ThreadDispatcher.getDispatcher().runOnMain(() -> {
            showContentView();
            scrollBar.setVisibility(View.GONE);
            rvUsbMusic.setVisibility(View.GONE);
            unsupportedDevices.setVisibility(View.GONE);
            noResponsesDevices.setVisibility(View.GONE);
            connectInterruptDevices.setVisibility(View.GONE);
            noDevices.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        });
    }

    private void bindView(View view) {
        rvUsbMusic = view.findViewById(R.id.rv_usb_music);
        unsupportedDevices = view.findViewById(R.id.ll_unsupported_devices);
        emptyView = view.findViewById(R.id.rl_empty_view);
        noResponsesDevices = view.findViewById(R.id.ll_no_responses_devices);
        connectInterruptDevices = view.findViewById(R.id.ll_devices_connect_interrupt);
        noDevices = view.findViewById(R.id.ll_no_devices);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUsbAdapter != null) {
            mUsbAdapter.notifyDataSetChanged();
        }
    }


    @Subscriber(tag = EventBusTags.SEARCH_USB_MEMORY_FILE)
    public void handleUsbMemoryFileSignal(UsbMemroySignal usbMemroySignal) {
        //取消切换usb tab自动播放记忆文件
//        queryUsbMusicContainsRecord(mUsbAdapter != null ? mUsbAdapter.getData() : null);
    }


    @Subscriber(tag = EventBusTags.SHOW_OR_HIDE_MINE)
    public void onHideEvent(ShowHideEvent event) {
        if (event.isShowLocal()) {
            if (mUsbAdapter != null) {
                mUsbAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void noUsbMounted() {
        dismissProgress();
        mUsbAdapter.setNewData(new ArrayList<>());
        showStateView(noDevices);
    }

    @Override
    public void inserted() {
        showStateView(rvUsbMusic);
        showProgressDialog(R.string.read_usb_media_content);
    }

    @Override
    public void mounted(List<String> mountPaths) {

    }

    @Override
    public void mountError() {
        dismissProgress();
        showStateView(unsupportedDevices);
    }

    @Override
    public void removed() {
        dismissProgress();
        mUsbAdapter.setNewData(new ArrayList<>());
        showStateView(noDevices);
        UsbMusicRecordManager.getInstance().stopUsbMemory();

        //拔掉U盘释放歌曲缓存，防止多个u盘来回切换。导致记忆上一个u盘数据，无休止切换。
        UsbScanManager.getInstance().releaseMusicList();
        UsbMusicFactory.getUsbPlayerListProxy().getDefaultUsbMusicList().clear();
        UsbMusicFactory.getUsbPlayerListProxy().getUsbMusicList().clear();
        SortUsbMusicManager.getInstance().clearAll();

    }

    @Override
    public void onBuffering(UsbMusic usbMusic) {
        if (mUsbAdapter != null) {
            ThreadDispatcher.getDispatcher().runOnMain(() -> mUsbAdapter.notifyDataSetChanged());
        }
    }

    @Override
    public void onPlay(UsbMusic usbMusic) {
    }

    @Override
    public void onProgressChange(long progressInMs, long totalInMs) {

    }

    @Override
    public void onPlayFailed(int errorCode) {

    }

    @Override
    public void onPlayStop() {
    }

    @Override
    public void onCompletion() {

    }

    @Override
    public void onUsbMusicScanFinished(ArrayList<UsbMusic> musicList) {
        ThreadDispatcher.getDispatcher().runOnMain(() -> {
            usbMusicVM.getUsbList().postValue(musicList);
            UsbMusicFactory.getUsbPlayerListProxy().addUsbMusicList(musicList);
        });
    }

    @Override
    public void onUsbMusicAnalyticFinished(ArrayList<UsbMusic> musicList) {
        usbMusicVM.getUsbList().postValue(musicList);
    }


    private void handleUsbMemory(int position) {

        if (mUsbAdapter != null) {
            List<UsbMusic> usbMusicList = mUsbAdapter.getData();
            UsbMusic curUsbMusic = usbMusicList.get(position);

            if (!queryUsbMusicContainsRecord(curUsbMusic)) {
                if (usbMusicVM != null) {
                    usbMusicVM.playMusic(position);
                    mUsbAdapter.notifyDataSetChanged();
                }
            }
        }
        // else nothing to do
    }


    private boolean queryUsbMusicContainsRecord(UsbMusic usbMusic) {
        UsbMusic usbMusicRecord = UsbMusicRecordManager.getInstance().getUsbMusicFromRecord();
        if (usbMusicRecord == null) {
            Log.d(UsbFragment.class.getSimpleName(),
                    "Usb memory file not content.");
            return false;
        }

        String recordName = usbMusicRecord.getName();
        String recordPath = usbMusicRecord.getPath();
        int playMode = UsbMusicRecordManager.getInstance().getPlayMode();
        long recordPosition = UsbMusicRecordManager.getInstance().getCurrentPositionIfExists();

        //usb记忆触发条件  名字与路径必须相同
        if (recordName.equalsIgnoreCase(usbMusic.getName()) && recordPath.equalsIgnoreCase(usbMusic.getPath())) {
            UsbMusicFactory.getUsbPlayerProxy().play(usbMusic);
            UsbMusicFactory.getUsbPlayerProxy().seekToPos(recordPosition);
            UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(playMode);
            return true;
        }

        return false;
    }
}
