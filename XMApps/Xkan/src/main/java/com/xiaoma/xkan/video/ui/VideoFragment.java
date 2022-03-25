package com.xiaoma.xkan.video.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.base.BaseFilterFragment;
import com.xiaoma.xkan.common.base.RVSpacesItemDecoration;
import com.xiaoma.xkan.common.comparator.DateComparator;
import com.xiaoma.xkan.common.comparator.NameComparator;
import com.xiaoma.xkan.common.comparator.SizeComparator;
import com.xiaoma.xkan.common.constant.EventConstants;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.manager.CarInfoManager;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.video.adapter.XmVideoAdapter;
import com.xiaoma.xkan.video.vm.VideoFragmentVM;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2018/11/9 0009
 * 图片tab
 */
@PageDescComponent(EventConstants.PageDescribe.VIDEOFRAGMENTPAGEPATHDESC)
public class VideoFragment extends BaseFilterFragment {
    private XmVideoAdapter videoAdapter;
    private VideoFragmentVM videoVM;
    private static final int SPEED = 10;

    @Override
    public void initData() {
        videoVM.setDataList(UsbMediaDataManager.getInstance().getVideoList());
    }

    @Override
    public void filterName(boolean isZ) {
        videoVM.filterList(new NameComparator(isZ));
    }

    @Override
    public void filterDate(boolean isFar) {
        videoVM.filterList(new DateComparator(isFar));
    }

    @Override
    public void filterSize(boolean isBig) {
        videoVM.filterList(new SizeComparator(isBig));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    public int getTipStr() {
        return R.string.empty_no_video_file;
    }

    @Override
    public int getEmtpyImgId() {
        return R.drawable.img_no_video;
    }

    @Override
    public void initAdapter(View emptyView) {
        videoAdapter = new XmVideoAdapter(
                new ArrayList<UsbMediaInfo>(),
                ImageLoader.with(this));
        videoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (CarInfoManager.getInstance().getIsWatchVideoInDriving()
                        && CarInfoManager.getInstance().getCurrentSpeedData() >= SPEED) {
                    XMToast.showToast(getActivity(), R.string.driving_forbid_watch_video);
                    return;
                }

                if (!CarInfoManager.getInstance().getIsWatchVideoInDriving() &&
                        CarInfoManager.getInstance().getCurrentSpeedData() >= SPEED) {
                    showVideoDialog(position);
                    return;
                }

                intentToVideoPlayActivity(position);
            }
        });
        videoAdapter.setEmptyView(emptyView);
        rv.addItemDecoration(new RVSpacesItemDecoration());
        rv.setAdapter(videoAdapter);
    }

    @Override
    public void initVM() {
        videoVM = ViewModelProviders.of(this).get(VideoFragmentVM.class);
        videoVM.getDataList().observe(this, new Observer<List<UsbMediaInfo>>() {
            @Override
            public void onChanged(@Nullable List<UsbMediaInfo> videoList) {
                videoAdapter.setNewData(videoList);
                rv.scrollToPosition(0);
                handlerEmptyView(videoList);
            }
        });
    }

    private void intentToVideoPlayActivity(int pos) {
        /*Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        intent.putExtra(XkanConstants.FROM_TYPE, XkanConstants.FROM_VIDEO);
        intent.putExtra(XkanConstants.VIDEO_INDEX, pos);
        startActivity(intent);*/

        VideoPlayActivityV2.start(getContext(), new ArrayList<>(videoAdapter.getData()), pos);
    }

    @Subscriber(tag = XkanConstants.XKAN_VIDEO_POS)
    public void updatePos(int position) {
        scrollToPosition(position);
    }

    /**
     * 收到USB移除通知,刷新页面
     *
     * @param event
     */
    @Subscriber(tag = XkanConstants.RELEASE_MEDIAINFO)
    public void refreshView(String event) {
        rv.stopScroll();
        videoAdapter.setNewData(null);
        filterView.reset();
    }

    private void showVideoDialog(final int pos) {
        final ConfirmDialog dialog = new ConfirmDialog(getActivity(), false);
        dialog.setCancelable(false);
        dialog.setContent(getString(R.string.driving_watch_video_message_vf) + "\n" + getString(R.string.driving_watch_video_tip))
                .setPositiveButton(getString(R.string.driving_watch_video_replayo_vf), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.CONTINUE_PLAY, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        intentToVideoPlayActivity(pos);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.driving_watch_video_stop_close_vf), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.STOP_CLOSE, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
