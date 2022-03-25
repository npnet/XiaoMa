package com.xiaoma.xkan.video.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.model.UsbMediaInfo;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LKF on 2019-7-26 0026.
 */
public class VideoPlayActivityV2 extends BaseActivity {
    public static final String EXTRA_MEDIA_INFO_LIST = "media_info_list";
    public static final String EXTRA_MEDIA_INFO_INDEX = "media_info_index";

    private List<UsbMediaInfo> mMediaInfoList;
    private int mPlayIndex;

    public static void start(Context context, ArrayList<UsbMediaInfo> mediaInfoList, int index) {
        Intent intent = new Intent(context, VideoPlayActivityV2.class)
                .putExtra(EXTRA_MEDIA_INFO_LIST, mediaInfoList)
                .putExtra(EXTRA_MEDIA_INFO_INDEX, index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarDividerGone();
        getNaviBar().hideNavi();
        EventBus.getDefault().register(this);
        initByIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initByIntent(intent);
    }

    private void initByIntent(@NonNull Intent intent) {
        mMediaInfoList = intent.getParcelableArrayListExtra(EXTRA_MEDIA_INFO_LIST);
        mPlayIndex = intent.getIntExtra(EXTRA_MEDIA_INFO_INDEX, 0);
        if (mMediaInfoList != null && mPlayIndex >= 0 && mPlayIndex < mMediaInfoList.size()) {
            UsbMediaInfo info = mMediaInfoList.get(mPlayIndex);
            if (info != null && !TextUtils.isEmpty(info.getPath())
                    && new File(info.getPath()).exists()) {
                playMediaInfo(info, false);
                return;
            }
        }
        showToastException(R.string.current_file_play_failed);
        finish();
    }

    private void playMediaInfo(UsbMediaInfo info, boolean showPlayControl) {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, VideoPlayFragment.newInstance(info, showPlayControl, new VideoPlayFragment.Callback() {
                    @Override
                    public void onBack() {
                        finish();
                    }

                    @Override
                    public void onPrevious() {
                        playMediaInfo(mMediaInfoList.get(preIndex()), true);
                    }

                    @Override
                    public void onNext() {
                        playMediaInfo(mMediaInfoList.get(nextIndex()), true);
                    }

                    @Override
                    public void onPlayFailed() {
                        showToastException(R.string.current_file_play_failed);
                        finish();
                    }

                    @Override
                    public void onPlayComplete() {
                        int nextIndex = nextIndex();
                        if (nextIndex == 0) {
                            finish();
                        } else {
                            playMediaInfo(mMediaInfoList.get(nextIndex), false);
                        }
                    }

                    private int preIndex() {
                        int preIndex = mPlayIndex - 1;
                        if (preIndex < 0) {
                            preIndex = mMediaInfoList.size() - 1;
                        }
                        mPlayIndex = preIndex;
                        return preIndex;
                    }

                    private int nextIndex() {
                        int nextIndex = mPlayIndex + 1;
                        if (nextIndex >= mMediaInfoList.size()) {
                            nextIndex = 0;
                        }
                        mPlayIndex = nextIndex;
                        return nextIndex;
                    }
                }))
                .commit();
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    /**
     * 收到USB移除通知，关闭页面
     *
     * @param event
     */
    @Subscriber(tag = XkanConstants.RELEASE_MEDIAINFO)
    public void finishActivity(String event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
