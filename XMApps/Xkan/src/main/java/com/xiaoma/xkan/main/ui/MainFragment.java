package com.xiaoma.xkan.main.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.ViewUtils;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.base.BaseFilterFragment;
import com.xiaoma.xkan.common.base.RVSpacesItemDecoration;
import com.xiaoma.xkan.common.comparator.DateComparator;
import com.xiaoma.xkan.common.comparator.NameComparator;
import com.xiaoma.xkan.common.comparator.SizeComparator;
import com.xiaoma.xkan.common.constant.EventConstants;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.listener.UsbMediaSearchListener;
import com.xiaoma.xkan.common.manager.AudioFocusManager;
import com.xiaoma.xkan.common.manager.CarInfoManager;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.main.adapter.XmFileAdapter;
import com.xiaoma.xkan.main.vm.MainFragmentVM;
import com.xiaoma.xkan.picture.ui.XmPhotoActivity;
import com.xiaoma.xkan.video.ui.VideoPlayActivityV2;

import org.aspectj.lang.annotation.Aspect;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas on 2018/11/9 0009
 * 全部tab
 */
@Aspect
@PageDescComponent(EventConstants.PageDescribe.MAINFRAGMENTPAGEPATHDESC)
public class MainFragment extends BaseFilterFragment {

    private static final int SPEED = 10;
    private MainFragmentVM mainVm;
    private XmFileAdapter xmFileAdapter;
    private UsbMediaSearchListener usbMediaSearchListener;
    //文件深度 最大值
    private static final int MAX_PATH_DEEPTH = 3;

    private HashMap<String, Integer> mIndexMap = new HashMap<>();

    @Override
    public void initData() {
        //初始化数据和监听
        mainVm.setDataList(UsbMediaDataManager.getInstance().getCurrPathFileList());
    }

    @Override
    public void filterName(boolean isZ) {
        filterDataByFilter(new NameComparator(isZ));
    }

    @Override
    public void filterDate(boolean isFar) {
        filterDataByFilter(new DateComparator(isFar));
    }

    @Override
    public void filterSize(boolean isBig) {
        filterDataByFilter(new SizeComparator(isBig));
    }

    private void filterDataByFilter(Comparator<UsbMediaInfo> comparator) {
        mainVm.filterList(comparator);
        Collections.sort(UsbMediaDataManager.getInstance().getCurrVideoList(), comparator);
        Collections.sort(UsbMediaDataManager.getInstance().getCurrPictList(), comparator);
    }

    /**
     * 处理onback操作
     */
    public void backToUp() {
        UsbMediaDataManager.getInstance().backUpDec();
        if (UsbMediaDataManager.getInstance().getPathList().size() == 1) {
            //如果返回到了一级路径path，就显示左边目录
            EventBus.getDefault().post(false, XkanConstants.XKAN_HIDE_MENU);
        }
        //获取当前路径下filelist
        List<UsbMediaInfo> fileList = UsbMediaDataManager.getInstance().getFileListByPath();
        if (fileList != null) {
            try {
                mainVm.setDataList(fileList);
                scrollToPosition(mIndexMap.get(UsbMediaDataManager.getInstance().getCurrPath()));
                UsbMediaDataManager.getInstance().setCurrPathFileList(fileList);
                UsbMediaDataManager.getInstance().resetCurrPicAndVideoList(fileList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //如果list为空,就重新拉取当前路径文件
            List<String> pathList = UsbMediaDataManager.getInstance().getPathList();
            UsbMediaDataManager.getInstance().fetchCurrPathMediaData(pathList.get(pathList.size() - 1), usbMediaSearchListener);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public int getTipStr() {
        return R.string.empty_no_file;
    }

    @Override
    public int getEmtpyImgId() {
        return R.drawable.img_no_file;
    }

    @Override
    public void initAdapter(View emptyView) {
        xmFileAdapter = new XmFileAdapter(
                new ArrayList<UsbMediaInfo>(),
                ImageLoader.with(this));

        xmFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ViewUtils.isFastClick()){
                    return;
                }
                UsbMediaInfo usbMediaInfo = xmFileAdapter.getData().get(position);
                switch (usbMediaInfo.getFileType()) {
                    case XkanConstants.FILE_TYPE_DEC:
                        mIndexMap.put(UsbMediaDataManager.getInstance().getCurrPath(), position);
                        //添加path到list
                        toNextLevelDir(usbMediaInfo.getPath());
                        break;
                    case XkanConstants.FILE_TYPE_VIDEO:
                        if (CarInfoManager.getInstance().getIsWatchVideoInDriving()) {
                            if (CarInfoManager.getInstance().getCurrentSpeedData() >= SPEED) {
                                XMToast.showToast(getActivity(), R.string.driving_forbid_watch_video);
                                return;
                            }
                        }
                        AudioFocusManager.getInstance().requestAudioFocus();
                        if (AudioFocusManager.getInstance().hasAudioFocus()) {
                            intentToVideoPlayActivity(usbMediaInfo);
                        } else {
                            XMToast.showToast(mContext, R.string.tip_audio_fosuc_fail);
                        }
                        break;
                    case XkanConstants.FILE_TYPE_PIC:
                        intentToXmPhotoActivity(position);
                        break;
                    default:
                        break;
                }
            }
        });
        xmFileAdapter.setEmptyView(emptyView);
        rv.addItemDecoration(new RVSpacesItemDecoration());
        rv.setAdapter(xmFileAdapter);
    }

    @Override
    public void initVM() {
        mainVm = ViewModelProviders.of(this).get(MainFragmentVM.class);
        mainVm.getDataList().observe(this, new Observer<List<UsbMediaInfo>>() {

            @Override
            public void onChanged(@Nullable List<UsbMediaInfo> list) {
                onDataChange(list);
            }

        });
        //媒体文件扫描监听
        usbMediaSearchListener = new UsbMediaSearchListener() {
            @Override
            public void onUsbMediaSearchFinished() {
                mainVm.setDataList(UsbMediaDataManager.getInstance().getCurrPathFileList());
            }
        };
    }

    @SuppressLint("StringFormatMatches")
    private void onDataChange(@Nullable List<UsbMediaInfo> list) {
        xmFileAdapter.setNewData(list);
        rv.scrollToPosition(0);
        String currPath = UsbMediaDataManager.getInstance().getCurrPath();
        if (UsbMediaDataManager.getInstance().getPathList().size() > 1 && !StringUtil.isEmpty(currPath)) {
            tvPath.setVisibility(View.VISIBLE);
            filterView.setVisibility(View.GONE);
            String pathStr = "";
            try {
                String[] pathStrs = currPath.split(UsbMediaDataManager.getInstance().getRootPath())[1].split("/");
                if (pathStrs.length > MAX_PATH_DEEPTH) {
                    pathStr = getString(R.string.dec_path, pathStrs[0], pathStrs[pathStrs.length - 1]);
                } else {
                    pathStr = currPath.split(UsbMediaDataManager.getInstance().getRootPath())[1];
                }
            } catch (Exception ignored) {
            }
            tvPath.setText(pathStr);
        } else {
            tvPath.setVisibility(View.GONE);
            filterView.setVisibility(!ListUtils.isEmpty(list) ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 跳转图片查看
     *
     * @param pos
     */
    private void intentToXmPhotoActivity(int pos) {
        Intent intent = new Intent(getActivity(), XmPhotoActivity.class);
        intent.putExtra(XkanConstants.PHOTO_INDEX, pos - UsbMediaDataManager.getInstance().getCurrDecList().size() - UsbMediaDataManager.getInstance().getCurrVideoList().size());
        intent.putExtra(XkanConstants.FROM_TYPE, XkanConstants.FROM_ALL);
        startActivity(intent);
    }

    /**
     * 跳转视频播放
     */
    private void intentToVideoPlayActivity(final UsbMediaInfo usbMediaInfo) {
        if (usbMediaInfo == null) {
            return;
        }
        showProgressDialog("");
        SeriesAsyncWorker.create()
                .next(new Work(Priority.HIGH) {
                    @Override
                    public void doWork(Object lastResult) {
                        List<UsbMediaInfo> curVideoList = UsbMediaDataManager.getInstance().getVideoList();
                        if (curVideoList != null && !curVideoList.isEmpty()) {
                            ArrayList<UsbMediaInfo> videoList = new ArrayList<>(curVideoList);
                            for (int i = 0; i < videoList.size(); i++) {
                                UsbMediaInfo info = videoList.get(i);
                                if (Objects.equals(usbMediaInfo.getPath(), info.getPath())) {
                                    VideoPlayActivityV2.start(getContext(), videoList, i);
                                    break;
                                }
                            }
                        }
                        doNext();
                    }
                })
                .next(new Work() {
                    @Override
                    public void doWork(Object lastResult) {
                        dismissProgress();
                    }
                })
                .start();

    }

    /**
     * 下一级文件夹操作
     *
     * @param path
     */
    private void toNextLevelDir(String path) {
        UsbMediaDataManager.getInstance().addPathToList(path);
        UsbMediaDataManager.getInstance().setCurrPath(path);
        List<UsbMediaInfo> fileListByPath = UsbMediaDataManager.getInstance().getFileListByPath(path);
        if (fileListByPath != null) {
            mainVm.setDataList(fileListByPath);
            UsbMediaDataManager.getInstance().setCurrPathFileList(fileListByPath);
            UsbMediaDataManager.getInstance().resetCurrPicAndVideoList(fileListByPath);
        } else {
            UsbMediaDataManager.getInstance().fetchCurrPathMediaData(path, usbMediaSearchListener);
        }
        EventBus.getDefault().post(true, XkanConstants.XKAN_HIDE_MENU);
    }

    @Subscriber(tag = XkanConstants.XKAN_MAIN_PIC_POS)
    public void updatePicPos(int position) {
        scrollToPosition(position + UsbMediaDataManager.getInstance().getCurrDecList().size() + UsbMediaDataManager.getInstance().getCurrVideoList().size());
    }

    @Subscriber(tag = XkanConstants.XKAN_MAIN_VIDEO_POS)
    public void updateVideoPos(int position) {
        scrollToPosition(position + UsbMediaDataManager.getInstance().getCurrDecList().size());
    }

    /**
     * 收到USB移除通知,刷新页面
     *
     * @param event
     */
    @Subscriber(tag = XkanConstants.RELEASE_MEDIAINFO)
    public void refreshView(String event) {
        rv.stopScroll();
        xmFileAdapter.setNewData(null);
        filterView.reset();
        tvPath.setVisibility(View.GONE);
        filterView.setVisibility(View.VISIBLE);
    }


}
