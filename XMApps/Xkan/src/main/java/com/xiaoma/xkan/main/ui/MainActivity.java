package com.xiaoma.xkan.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.constant.EventConstants;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.listener.UsbMediaSearchListener;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbStatus;
import com.xiaoma.xkan.common.util.ImageUtils;
import com.xiaoma.xkan.main.service.UsbService;
import com.xiaoma.xkan.main.vm.MainMenuVM;
import com.xiaoma.xkan.picture.ui.PictureFragment;
import com.xiaoma.xkan.video.ui.VideoFragment;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Thomas on 2018/11/9 0009
 * index page
 */

@PageDescComponent(EventConstants.PageDescribe.MAINACTIVITYPAGEPATHDESC)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private MainFragment mainFragment;
    private VideoFragment videoFragment;
    private PictureFragment pictureFragment;
    private UsbConnStateFragment usbConnStateFragment;
    private MainMenuVM mMainMenuVM;
    private View mMenuView;
    private int currFragIndex = 0;
    private TextView mMainAll, mMainVideo, mMainPhto;

    private StorageManager mStorageManager;
    private static boolean isRemoteJump = false;
    private boolean isFirst = true;
    private String currentNode;

    StorageEventListener storageEventListener = new StorageEventListener() {
        @Override
        public void onUsbIfNotify(final String errmsg) {
            super.onUsbIfNotify(errmsg);
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    //不支持的usb设备
                    dismissProgress();
                    UsbMediaDataManager.getInstance().setCurrentUsbStatus(UsbStatus.MOUNT_ERROR);
                    mMenuView.setVisibility(View.GONE);
                    mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE, XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE_FRAGMENT_TAG);
                    //刷新usbfrag 页面
                    EventBus.getDefault().post(errmsg, XkanConstants.REFRESH_USB_FRAG_VIEW);
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_main);
        initView();
        initViewListener();
        initData();

    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void getAction(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        switch (msg) {
            case NodeConst.XKAN.OPEN_PICTURE:
                picture();
                break;
            case NodeConst.XKAN.OPEN_VIDEO:
                video();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    private void initView() {
        mMenuView = findViewById(R.id.main_menu);
        mMainAll = findViewById(R.id.tv_main_all);
        mMainVideo = findViewById(R.id.tv_main_video);
        mMainPhto = findViewById(R.id.tv_main_picture);
        mMainAll.setSelected(true);
        mMainVideo.setSelected(false);
        mMainPhto.setSelected(false);
    }

    private void initViewListener() {
        mMainAll.setOnClickListener(this);
        mMainVideo.setOnClickListener(this);
        mMainPhto.setOnClickListener(this);
        mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        if (mStorageManager != null) {
            mStorageManager.registerListener(storageEventListener);
        }
    }


    private void initData() {
        EventBus.getDefault().register(this);
        EventTtsManager.getInstance().init(this);
        mMainMenuVM = ViewModelProviders.of(this).get(MainMenuVM.class);
        mMainMenuVM.getMenuIndexMap().observe(this, new Observer<TreeMap<Integer, String>>() {
            @Override
            public void onChanged(@Nullable TreeMap<Integer, String> integerStringTreeMap) {
                if (integerStringTreeMap != null) {
                    currFragIndex = integerStringTreeMap.firstKey();
                    switchFragment(getFragementByIndex(integerStringTreeMap.firstKey()), integerStringTreeMap.get(integerStringTreeMap.firstKey()));
                }
            }
        });
        startService(new Intent(this, UsbService.class));
        //初始化界面为usb未挂载界面
        mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE, XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE_FRAGMENT_TAG);

    }


    private Fragment getFragementByIndex(int index) {
        switch (index) {
            case 1:
                if (videoFragment == null) {
                    videoFragment = new VideoFragment();
                }
                return videoFragment;
            case 2:
                if (pictureFragment == null) {
                    pictureFragment = new PictureFragment();
                }
                return pictureFragment;
            case 3:
                if (usbConnStateFragment == null) {
                    usbConnStateFragment = new UsbConnStateFragment();
                }
                return usbConnStateFragment;
        }
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        return mainFragment;
    }

    private void main() {
        KLog.i("filOut| " + "[main]->");
        if (mMainMenuVM == null) {
            return;
        }
        mMenuView.setVisibility(View.VISIBLE);
        mMainAll.setSelected(true);
        mMainVideo.setSelected(false);
        mMainPhto.setSelected(false);
        mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_MAIN, XkanConstants.MAIN_TAB_INDEX_MAIN_FRAGMENT_TAG);
    }

    private void video() {
        if (mMainMenuVM == null || isViewNull()) {
            return;
        }
        mMenuView.setVisibility(View.VISIBLE);
        mMainAll.setSelected(false);
        mMainVideo.setSelected(true);
        mMainPhto.setSelected(false);
        mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_VIDEO, XkanConstants.MAIN_TAB_INDEX_VIDEO_FRAGMENT_TAG);
    }

    private void picture() {
        if (mMainMenuVM == null || isViewNull()) {
            return;
        }
        mMenuView.setVisibility(View.VISIBLE);
        mMainAll.setSelected(false);
        mMainVideo.setSelected(false);
        mMainPhto.setSelected(true);
        mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_PICTURE, XkanConstants.MAIN_TAB_INDEX_PICTURE_FRAGMENT_TAG);
    }

    private boolean isViewNull() {
        return mMenuView == null
                || mMainAll == null
                || mMainVideo == null
                || mMainPhto == null;
    }

    @Subscriber(tag = XkanConstants.USB_INSERT)
    public void usbStateInsert(String event) {
        showProgressDialog(R.string.read_usb_media_content);
        mMenuView.setVisibility(View.GONE);
        mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE, XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE_FRAGMENT_TAG);
        //刷新usbfrag 页面
        EventBus.getDefault().post("", XkanConstants.REFRESH_USB_FRAG_VIEW);
    }


    @Subscriber(tag = XkanConstants.USB_MOUNTED)
    public void usbMountedSuccess(final String path) {
        //清除glide缓存
        ImageUtils.clearCacheDiskSelf(this);
        ImageUtils.clearCacheMemory(this);
        showProgressDialog(R.string.read_usb_media_content);
        EventBus.getDefault().post("", XkanConstants.REFRESH_USB_FRAG_VIEW);
        UsbMediaDataManager.getInstance().fetchUsbMediaData(path, new UsbMediaSearchListener() {
            @Override
            public void onUsbMediaSearchFinished() {
                if (UsbDetector.getInstance().isRemoveState()) {
                    dismissProgress();
                    mMenuView.setVisibility(View.GONE);
                    mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE, XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE_FRAGMENT_TAG);
                    //刷新usbfrag 页面
                    EventBus.getDefault().post("", XkanConstants.REFRESH_USB_FRAG_VIEW);
                } else {
                    showToast(R.string.complete_fetch_usb_media_data);
                    dismissProgress();
                    main();

                    UsbMediaDataManager.getInstance().setCurrPath(path);
                    UsbMediaDataManager.getInstance().addPathToList(path);
                    UsbMediaDataManager.getInstance().addFileListByPath(path);
                    EventBus.getDefault().post(XkanConstants.XKAN_EVENT, XkanConstants.UPDATE_DATA);
                }
                if (isFirst) {
                    isFirst = false;
                }
                if (isRemoteJump) {
                    isRemoteJump = false;
                    EventBus.getDefault().post(currentNode);
                }
            }
        });
    }

    @Subscriber(tag = XkanConstants.USB_NO_MOUNTED)
    public void noUsbMounted(String event) {
        dismissProgress();
        mMenuView.setVisibility(View.GONE);
        mMainMenuVM.setMenuData(XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE, XkanConstants.MAIN_TAB_INDEX_USB_CONN_STATE_FRAGMENT_TAG);
        //刷新usbfrag 页面
        EventBus.getDefault().post("", XkanConstants.REFRESH_USB_FRAG_VIEW);
    }

    public void switchFragment(Fragment fragment, String tag) {
        List<Fragment> fragments = FragmentUtils.getFragments(getSupportFragmentManager());
        Fragment fragmentByTag = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
        if (fragmentByTag != null) {
            fragments.remove(fragmentByTag);
            FragmentUtils.showHide(fragmentByTag, fragments);
        } else {
            for (Fragment fg : fragments) {
                FragmentUtils.hide(fg);
            }
            FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.fl_container, tag);
        }
    }


    @Override
    public void onBackPressed() {
        EventTtsManager.getInstance().stopSpeaking();
        //如果当前是MainFragment
        if (currFragIndex == 0 && UsbMediaDataManager.getInstance().getPathList().size() > 1) {
            ((MainFragment) getFragementByIndex(currFragIndex)).backToUp();
            return;
        }
        finish();
    }

    /**
     * 根据层级判断 是否隐藏menu
     *
     * @param hide
     */
    @Subscriber(tag = XkanConstants.XKAN_HIDE_MENU)
    public void hideMenu(boolean hide) {
        mMenuView.setVisibility(hide ? View.GONE : View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventTtsManager.getInstance().destroy();
        stopService(new Intent(this, UsbService.class));
        if (mStorageManager != null) {
            mStorageManager.unregisterListener(storageEventListener);
        }
        AppObserver.getInstance().closeAllActivitiesAndExit();
        super.onDestroy();
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.ALL, EventConstants.NormalClick.VIDEO, EventConstants.NormalClick.PICTURE})
    @ResId({R.id.tv_main_all, R.id.tv_main_video, R.id.tv_main_picture})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_all:
                main();
                break;
            case R.id.tv_main_video:
                video();
                break;
            case R.id.tv_main_picture:
                picture();
                break;
        }

    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.XKAN.OPEN_VIDEO:
            case NodeConst.XKAN.OPEN_PICTURE:
                isRemoteJump = true;
                if (isRemoteJump && isFirst) {
                    isFirst = false;
                    currentNode = nextNode;
                } else {
                    EventBus.getDefault().post(nextNode);
                }
                break;
        }
        return true;
    }

    @Command("(我想|我要)?看本地视频")
    public void switchVideo1() {
        openVideoFragment();
    }

    @Command("(打开)?本地视频")
    public void switchVideo2() {
        openVideoFragment();
    }

    @Command("(打开|启动|开启)?(本地|USB)图片")
    public void switchPic1() {
        openPicFragment();
    }

    @Command("(我想|我要)?看(本地|USB)图片")
    public void switchPic2() {
        openPicFragment();
    }


    private void openVideoFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.OPEN_MEDIA_FRAGMENT);
                if (UsbDetector.getInstance().isRemoveState()) {
                    showToast(R.string.no_usb_mounted);
                } else {
                    EventTtsManager.getInstance().startSpeaking(getString(R.string.ok));
                    video();
                }
            }
        });
    }

    private void openPicFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.OPEN_MEDIA_FRAGMENT);
                if (UsbDetector.getInstance().isRemoveState()) {
                    showToast(getString(R.string.no_usb_mounted));
                } else {
                    EventTtsManager.getInstance().startSpeaking(getString(R.string.ok));
                    picture();
                }
            }
        });
    }

    @Override
    public String getThisNode() {
        return NodeConst.XKAN.ASSISTANT_ACTIVITY;
    }
}
