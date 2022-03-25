package com.xiaoma.instructiondistribute.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.instructiondistribute.BuildConfig;
import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.adapter.PhotoFullScreenAdapter;
import com.xiaoma.instructiondistribute.adapter.PhotoListAdapter;
import com.xiaoma.instructiondistribute.xkan.common.listener.UsbConnectStateListener;
import com.xiaoma.instructiondistribute.xkan.common.listener.UsbMediaSearchListener;
import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbStatus;
import com.xiaoma.instructiondistribute.xkan.common.util.ImageUtils;
import com.xiaoma.ui.toast.XMToast;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 * 实现USB图片操作类
 *
 * @author YangGang
 * @date 2019/8/20
 */
public class PhotoActivity extends BaseActivity implements UsbConnectStateListener {

    public static final String TAG = "Photo";

    public static final String EVENT_PHOTO = "photo.event";

    public static final String ARG_RESULT = "result";

    public static final String ARG_ACTION = "action";
    public static final String ARG_PHOTO_LIST = "photo_list";
    public static final String ARG_PHOTO_INDEX = "photo_index";

    public static final String ACTION_ZOOM_IN = "zoom_in";//放大
    public static final String ACTION_ZOOM_OUT = "zoom_out";//缩小
    public static final String ACTION_ROTATE_LEFT = "rotate_left";//左旋
    public static final String ACTION_ROTATE_RIGHT = "rotate_right";
    public static final String ACTION_LIST = "list";//列表
    public static final String ACTION_FULL_SCREEN = "full_screen";//全屏
    public static final String ACTION_FULL_SCREEN_PRE = "full_screen_pre";//全屏
    public static final String ACTION_FULL_SCREEN_NEXT = "full_screen_next";//全屏

    public static final String ACTION_GET_SHOW_TYPE = "photo_show_type"; //获取图片显示type

    public static final String STATE_FULL_SCREEN = "full_screen";
    public static final String STATE_LIST = "list";


    private Group mOperateGroup;
    private RecyclerView mListRV;
    private ImageView mOriginalIV;
    private ImageView mOperateIV;
    private TextView mOperateTV;

    private String mCurOperatePath;
    private float mScaleFactorFrom = 1.0f;

    private float mDiffScale = 0.2f;

    private float mRotateFrom = 0;
    private float mDiffRotate = 90f;
    private GridLayoutManager mListManager;
    private RecyclerView mFullScreenRV;
    private int mShowPhotoIndex;
    private List<UsbMediaInfo> mUsbList;
    private PhotoListAdapter mPhotoListAdapter;
    private PhotoFullScreenAdapter mPhotoFullScreenAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_pic_operate);

        EventBus.getDefault().register(this);
        bindView();
        afterBind();
        handleIntent(getIntent());
        UsbMediaDataManager.getInstance().syncUsbConnectState(this, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UsbMediaDataManager.getInstance().removeConnectListener();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void bindView() {
        mOperateGroup = findViewById(R.id.group_pic_operate);
        mListRV = findViewById(R.id.rvList);
        mFullScreenRV = findViewById(R.id.rvFullScreen);
        mOriginalIV = findViewById(R.id.ivOriginal);
        mOperateTV = findViewById(R.id.tvOperate);
        mOperateIV = findViewById(R.id.ivOperate);
        Group modeTest = findViewById(R.id.group_mode_test);
        modeTest.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    private void afterBind() {
        mPhotoListAdapter = new PhotoListAdapter();
        mListManager = new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false);
        mListRV.setLayoutManager(mListManager);
        mListRV.setAdapter(mPhotoListAdapter);

        mPhotoFullScreenAdapter = new PhotoFullScreenAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mFullScreenRV.setLayoutManager(linearLayoutManager);
        mFullScreenRV.setAdapter(mPhotoFullScreenAdapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mFullScreenRV);

    }

    private List<UsbMediaInfo> mockInfo() {
        List<UsbMediaInfo> list = new ArrayList<>();

        UsbMediaInfo usbMediaInfo;
        for (int i = 0; i < 20; i++) {
            usbMediaInfo = new UsbMediaInfo();
            usbMediaInfo.setPath("http://image.ijq.tv/201609/23/14-36-33-53-10.jpg");

            list.add(usbMediaInfo);
        }

        return list;
    }

    private void handleIntent(Intent intent) {
        String action = intent.getStringExtra(ARG_ACTION);
        mUsbList = UsbMediaDataManager.getInstance().getCurrPictList();
        dispatchAction(action, mUsbList);
    }

    public void dispatchAction(String action, List<UsbMediaInfo> list) {
        Intent intent = new Intent();
        intent.setAction(action);

        if (list == null || list.isEmpty()) {
            postResult(action, false);
            return;
        }
        XMToast.showToast(this, "USB图片: " + list.get(0).getPath());
        UsbMediaInfo usbMediaInfo = list.get(mShowPhotoIndex); //默认获取第一张
        String path = usbMediaInfo.getPath();
        if (!path.equals(mCurOperatePath)) {
            mRotateFrom = 0;
            mScaleFactorFrom = 1.0f;
            mCurOperatePath = path;
        }
        setView(path);
        switch (action) {
            case ACTION_ZOOM_IN:
                rollback();
                setText("放大");
                showHideView(0);
                zoom(mScaleFactorFrom, mScaleFactorFrom + mDiffScale);
                postResult(action, true);
                break;
            case ACTION_ZOOM_OUT:
                rollback();
                setText("缩小");
                showHideView(0);
                if (mScaleFactorFrom == mDiffScale) {
                    XMToast.showToast(this, "已经无法再缩小了");
                    postResult(action, false);
                } else {
                    zoom(mScaleFactorFrom, mScaleFactorFrom - mDiffScale);
                    postResult(action, true);
                }
                break;
            case ACTION_ROTATE_LEFT:
                if (ImageUtils.isGif(path)) {
                    XMToast.showToast(this, R.string.gif_no_rotate);
                    return;
                }
                setText("左旋");
                showHideView(0);
                rotate();
                postResult(action, true);
                break;
            case ACTION_ROTATE_RIGHT:
                if (ImageUtils.isGif(path)) {
                    XMToast.showToast(this, R.string.gif_no_rotate);
                    return;
                }

                setText("右旋");
                showHideView(0);
                rotate();
                postResult(action, true);
                break;
            case ACTION_LIST:
                rollback();
                showHideView(1);
                mPhotoListAdapter.setNewData(list);
                postResult(action, true);
                break;
            case ACTION_FULL_SCREEN:
                rollback();
                showHideView(2);
                mPhotoFullScreenAdapter.setNewData(list);
                postResult(action, true);
                break;
            case ACTION_FULL_SCREEN_PRE:
                rollback();
                showHideView(2);
                //如果数据表是空的,那么直接填充
                if (mPhotoFullScreenAdapter.getItemCount() == 0) {
                    mPhotoFullScreenAdapter.setNewData(list);
                    postResult(action, true);
                } else if (mShowPhotoIndex == 0) {
                    XMToast.showToast(this, "已经是第一张了");

                    postResult(action, false);
                } else {
                    --mShowPhotoIndex;
                    mFullScreenRV.smoothScrollToPosition(mShowPhotoIndex);
                    postResult(action, true);
                }
                break;
            case ACTION_FULL_SCREEN_NEXT:
                rollback();
                showHideView(2);
                //如果数据表是空的,那么直接填充
                if (mPhotoFullScreenAdapter.getItemCount() == 0) {
                    mPhotoFullScreenAdapter.setNewData(list);
                    postResult(action, true);
                } else if (mShowPhotoIndex == mUsbList.size() - 1) {
                    XMToast.showToast(this, "已经是最后一张了");
                    postResult(action, false);
                } else {
                    ++mShowPhotoIndex;
                    mFullScreenRV.smoothScrollToPosition(mShowPhotoIndex);
                    postResult(action, true);
                }
                break;
            case ACTION_GET_SHOW_TYPE:
                if (checkVisibility(mOperateGroup)) {
                    XMToast.showToast(this, "当前时页面操作模式,无法界定图片展示模式");
                } else if (checkVisibility(mFullScreenRV)) {
                    EventBus.getDefault().post(STATE_FULL_SCREEN, "eol_pic_show");
                } else if (checkVisibility(mListRV)) {
                    EventBus.getDefault().post(STATE_LIST, "eol_pic_show");
                }
                break;
        }
    }

    private boolean checkVisibility(View view) {
        if (view != null) {
            int visibility = view.getVisibility();
            return visibility == View.VISIBLE;
        }
        return false;
    }

    private void showHideView(int visible) {
        mOperateGroup.setVisibility((visible == 0) ? View.VISIBLE : View.GONE);
        mListRV.setVisibility((visible == 1) ? View.VISIBLE : View.GONE);
        mFullScreenRV.setVisibility((visible == 2) ? View.VISIBLE : View.GONE);
        if (visible > 0) {
            mRotateFrom = 0;
            mScaleFactorFrom = 1.0f;
        }
    }

    public void setView(int path) {
        showPhoto(path, mOperateIV);
        showPhoto(path, mOriginalIV);
    }

    public void setView(String path) {
        showPhoto(path, mOperateIV);
        showPhoto(path, mOriginalIV);
    }

    private void setText(String text) {
        if (mOperateTV != null) {
            mOperateTV.setText(text);
        }
    }

    private void showPhoto(int path, ImageView view) {
        if (view != null) {
            ImageLoader.with(PhotoActivity.this)
                    .asBitmap()
                    .load(path)
                    .into(view);
        }
    }

    private void showPhoto(String path, ImageView view) {
        if (view != null) {
            ImageLoader.with(PhotoActivity.this)
                    .asBitmap()
                    .load(path)
                    .into(view);
        }
    }

    private boolean zoom(float from, float to) {
        if (mOperateIV != null) {
            mScaleFactorFrom = to;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(mOperateIV, "scaleX", from, to),
                    ObjectAnimator.ofFloat(mOperateIV, "scaleY", from, to));
            animatorSet.setDuration(1000).start();

            return true;
        }
        return false;
    }

    private void rotate() {
        if (mOperateIV != null) {
            ObjectAnimator.ofFloat(mOperateIV, "rotation", mRotateFrom, mRotateFrom + mDiffRotate).setDuration(1000).start();
            mRotateFrom += mDiffRotate;
        }
    }

    private void rollback() {
        if (mOperateIV != null) {
            ObjectAnimator.ofFloat(mOperateIV, "rotation", mRotateFrom, 0).setDuration(10).start();
            mRotateFrom = 0;
        }
    }

    private void postResult(String action, boolean result) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_RESULT, result);
        bundle.putString(ARG_ACTION, action);

        EventBus.getDefault().post(bundle, EVENT_PHOTO);
    }

    public void onPre(View view) {
        startPhoto(ACTION_FULL_SCREEN_PRE);
    }

    public void onNext(View view) {
        startPhoto(ACTION_FULL_SCREEN_NEXT);
    }

    private void startPhoto(String action) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoActivity.ARG_ACTION, action);
        startActivity(intent);
    }

    public void list(View view) {
        startPhoto(ACTION_LIST);
    }

    public void fullScreen(View view) {
        startPhoto(ACTION_FULL_SCREEN);
    }

    public void zoomIn(View view) {
        startPhoto(ACTION_ZOOM_IN);
    }

    public void zoomOut(View view) {
        startPhoto(ACTION_ZOOM_OUT);
    }

    public void rotate(View view) {
        startPhoto(ACTION_ROTATE_RIGHT);
    }

    @Override
    public void onConnection(UsbStatus status, List<String> mountPaths) {
        if (status != UsbStatus.MOUNTED) {
            finish();
        } else {
            showProgressDialog("U盘扫描中");
            UsbMediaDataManager.getInstance().fetchUsbMediaData(mountPaths.get(0), new UsbMediaSearchListener() {
                @Override
                public void onUsbMediaSearchFinished() {
                    //默认进入列表模式
                    dismissProgress();
                    mUsbList = UsbMediaDataManager.getInstance().getCurrPictList();
                    mShowPhotoIndex = 0;
                    mRotateFrom = 0;
                    showHideView(1);
                    if (mUsbList == null || mUsbList.isEmpty()) {
                        XMToast.showToast(PhotoActivity.this, "未检测到可识别的图片信息");
                    } else {
                        XMToast.showToast(PhotoActivity.this, "USB图片数目 " + mUsbList.size());
                    }

                    mPhotoListAdapter.setNewData(mUsbList);
                }
            });
        }
    }

    public void showType(View view) {
        startPhoto(ACTION_GET_SHOW_TYPE);
    }
}
