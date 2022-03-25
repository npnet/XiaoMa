package com.xiaoma.launcher.mark.ui.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.mark.cluster.ClusterItem;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.mark.ui.fragment.RecordTripMomentFragment;
import com.xiaoma.launcher.mark.ui.fragment.TripAlbumFragment;
import com.xiaoma.launcher.mark.ui.fragment.TripFootPrintFragment;
import com.xiaoma.launcher.mark.vm.TripAlblumVM;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.tts.EventTtsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@PageDescComponent(EventConstants.PageDescribe.MarkMainActivityPagePathDesc)
public class MarkMainActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTripFootprint;
    private LinearLayout mTripAlbum;
    private CameraView mCameraView;
    private ImageView mPhotoBtn;
    private final static int CMAERA_OCCUPY = 3;
    private final static int LAST_FRAGMENT = 1;
    private static boolean INITIALIZATION_STATE = false;
    public MarkPhotoBean mMarkPhotoBean;
    public String photoPath;
    private TripAlbumFragment mTripAlbumFragment;
    private TripFootPrintFragment mTripFootPrintFragment;

    private ImageView mCameraOccupy;
    private TripAlblumVM mTripAlbumVM;
    private List<ClusterItem> mClusterList = new ArrayList<>();
    private List<MarkPhotoBean> mMarkPhotoList = new ArrayList<>();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private HandlerThread mHandlerThread;
    private String mAssistsntMark;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        bindView();
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCameraOccupy();

        Bundle bundleExtra = getIntent().getExtras();
        if (bundleExtra != null) {
            mAssistsntMark = bundleExtra.getString(CenterConstants.DATE);
            if (StringUtil.isNotEmpty(mAssistsntMark)) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                int count = fragmentManager.getBackStackEntryCount();
                for (int i = 0; i < count; ++i) {
                    fragmentManager.popBackStack();
                }
            }
        }
        try {
            //判断权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                mCameraView.start();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTripAlbumVM.getAllTrip();
    }

    private void initData() {
        mTripAlbumVM = ViewModelProviders.of(this).get(TripAlblumVM.class);
        mTripAlbumVM.getTripAlbumList().observe(this, new Observer<XmResource<List<MarkPhotoBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<MarkPhotoBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<MarkPhotoBean>>() {
                    @Override
                    public void onSuccess(List<MarkPhotoBean> data) {
                        mMarkPhotoList.clear();
                        if (ListUtils.isEmpty(data)) {
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.WHETHER_HAVE_MARK, false);
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.FIST_YEARS, DateUtil.getCurrentYear());
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.LAST_MONTH, 0);
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.LAST_YEARS, 0);
                        } else {
                            mMarkPhotoList.addAll(data);
                            MarkPhotoBean markPhotoBean = data.get(0);
                            MarkPhotoBean lastPhotoBean = data.get(data.size() - 1);
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.WHETHER_HAVE_MARK, true);
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.FIST_YEARS, ConvertUtils.stringToInt(markPhotoBean.getYear()));
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.LAST_MONTH, ConvertUtils.stringToInt(lastPhotoBean.getMonth()));
                            TPUtils.put(MarkMainActivity.this, LauncherConstants.LAST_YEARS, ConvertUtils.stringToInt(lastPhotoBean.getYear()));
                        }
                    }
                });
            }
        });

    }

    private void bindView() {
        mTripFootprint = findViewById(R.id.trip_footprint);
        mTripAlbum = findViewById(R.id.trip_album);
        mCameraView = findViewById(R.id.mark_surface);
        mPhotoBtn = findViewById(R.id.photo_btn);
        mCameraOccupy = findViewById(R.id.camera_occupy);
        mTripAlbumFragment = TripAlbumFragment.newInstance(this);
        mTripFootPrintFragment = TripFootPrintFragment.newInstance(this);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }


    }

    private int[] finishWord = new int[]{R.string.mark_yes, R.string.mark_ok, R.string.mark_finished};

    private int getFinishWord() {
        Random random = new Random();
        int index = random.nextInt(finishWord.length);
        return finishWord[index];
    }

    //相机监听
    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (StringUtil.isNotEmpty(mAssistsntMark)) {
                        EventTtsManager.getInstance().init(MarkMainActivity.this);
                        if (CenterConstants.ASSISTANT_MARK.equals(mAssistsntMark)) {
                            if (mMarkPhotoList.size() >= 1000) {
                                EventTtsManager.getInstance().startSpeaking(getString(R.string.save_local_aleary_finish));
                                return;
                            }
                            try {
                                if (mCameraView != null) {
                                    mCameraView.takePicture();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, 500);
        }


        @Override
        public void onCameraClosed(CameraView cameraView) {

        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            if (StringUtil.isNotEmpty(mAssistsntMark)) {
                EventTtsManager.getInstance()
                        .startSpeaking(getString(getFinishWord()) + getString(R.string.mark_share_to_circle_friends));
                mAssistsntMark = null;
            }
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveToSDCard(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });
        }
    };
    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @return imagePath 图片路径
     */
    Bundle bundle = null; // 声明一个Bundle对象，用来存储数据

    public boolean saveToSDCard(byte[] data) {
        bundle = new Bundle();
        bundle.putByteArray("bytes", data);    //将图片字节数据保存在bundle当中，实现数据交换

        //ADD 生成保存图片的路径
        String mImagePath = absolutePath(data);

        //保存到SD卡
        if (StringUtil.isEmpty(mImagePath)) {
            return false;
        }
        try {
            FileOutputStream outputStream = null; // 文件输出流
            outputStream = new FileOutputStream(mImagePath);
            outputStream.write(data); // 写入sd卡中
            outputStream.close(); // 关闭输出流
            postTakePhoto(mImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    //存储路径
    public String absolutePath(byte[] data) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String filename = format.format(date) + ".jpg";
        File fileFolder = new File(Environment.getExternalStorageDirectory() + "/mark/");
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        return jpgFile.getAbsolutePath();
    }

    private Handler mBackgroundHandler;

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            mHandlerThread = new HandlerThread("background");
            mHandlerThread.start();
            mBackgroundHandler = new Handler(mHandlerThread.getLooper());
        }
        return mBackgroundHandler;
    }


    private void setCameraOccupy() {
        if (XmMapNaviManager.getInstance().getNaviShowState() == CMAERA_OCCUPY) {
            mCameraView.setVisibility(View.GONE);
            mPhotoBtn.setVisibility(View.GONE);
            mCameraOccupy.setVisibility(View.VISIBLE);
        } else {
            INITIALIZATION_STATE = true;
        }
    }

    private void initView() {
        mTripFootprint.setOnClickListener(this);
        mTripAlbum.setOnClickListener(this);
        mPhotoBtn.setOnClickListener(this);
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
    protected void onPause() {
        super.onPause();
        try {
            mAssistsntMark = null;
            if (mCameraView != null && INITIALIZATION_STATE) {
                mCameraView.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mHandlerThread != null && mHandlerThread != null) {
                mHandlerThread.quit();
                mBackgroundHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 照完照片 提交
     */
    public void postTakePhoto(String jpgFile) {
        setPhotoPath(jpgFile);
        RecordTripMomentFragment recordTripMomentFragment = RecordTripMomentFragment.newInstance(this);
        addBackStackFragment(recordTripMomentFragment, recordTripMomentFragment.getTAG());
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.MARK_ABLUM, EventConstants.NormalClick.MARK_FOOT, EventConstants.NormalClick.MARK_TAKE_PHOTO})
//按钮对应的名称
    @ResId({R.id.trip_album, R.id.trip_footprint, R.id.photo_btn})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_album:
                addBackStackFragment(mTripAlbumFragment, mTripAlbumFragment.getTAG());
                break;
            case R.id.trip_footprint:
                addBackStackFragment(mTripFootPrintFragment, mTripFootPrintFragment.getTAG());
                break;
            case R.id.photo_btn:
                if (mMarkPhotoList.size() >= 1000) {
                    XMToast.showToast(this, getString(R.string.save_local_aleary_finish), this.getDrawable(R.drawable.toast_error));
                    return;
                }
                try {
                    if (mCameraView != null) {
                        mCameraView.takePicture();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mTripAlbumVM.getAllTrip();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment child = fragments.get(i);
                if (child.getChildFragmentManager().popBackStackImmediate()) {
                    return;
                }
            }
        }
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }

        moveTaskToBack(true);
    }


    public void addBackStackFragment(BaseFragment fragment, String tag) {
        FragmentUtils.replace(
                getSupportFragmentManager(),
                fragment,
                R.id.frame,
                tag,
                true
        );
    }

    public MarkPhotoBean getMarkPhotoBean() {
        return mMarkPhotoBean;
    }

    public void setMarkPhotoBean(MarkPhotoBean markPhotoBean) {
        mMarkPhotoBean = markPhotoBean;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setClusterItem(List<ClusterItem> clusterItems) {
        mClusterList = clusterItems;
    }

    public List<ClusterItem> getClusterItem() {
        return mClusterList;
    }
}
