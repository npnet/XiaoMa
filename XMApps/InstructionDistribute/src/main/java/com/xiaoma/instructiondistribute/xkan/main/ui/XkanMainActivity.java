package com.xiaoma.instructiondistribute.xkan.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.utils.DistributeConstants;
import com.xiaoma.instructiondistribute.xkan.common.constant.XkanConstants;
import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.instructiondistribute.xkan.picture.adapter.XmPictureAdapter;
import com.xiaoma.instructiondistribute.xkan.picture.ui.XmPhotoActivity;
import com.xiaoma.instructiondistribute.xkan.picture.vm.PictureFragmentVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2018/11/9 0009
 * index page
 */

public class XkanMainActivity extends BaseActivity {

    private Bundle arguments;
    private static final int SPAN_COUNT = 2;
    protected GridLayoutManager mGridLayoutManager;

    private XmPictureAdapter pictureAdapter;
    private PictureFragmentVM picVM;
    private TextView mUsbHintTV;
    private RecyclerView mContentRV;
    private ImageView mCloseIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_picture);
        EventBus.getDefault().register(this);

        preBindView();
        bindView();
        afterBindView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        dispatchIntent(intent);
    }

    private void preBindView() {
        picVM = ViewModelProviders.of(this).get(PictureFragmentVM.class);
        picVM.getDataList().observe(this, new Observer<List<UsbMediaInfo>>() {
            @Override
            public void onChanged(@Nullable List<UsbMediaInfo> pictureList) {
                if (pictureAdapter == null) return;
                pictureAdapter.setDatas(pictureList);
                scrollToPosition(0);
            }
        });

    }

    private void bindView() {
        mUsbHintTV = findViewById(R.id.tvUSBHint);
        mContentRV = findViewById(R.id.rvList);
        mCloseIV = findViewById(R.id.ivClose);
    }

    private void afterBindView() {
        pictureAdapter = new XmPictureAdapter(this, new ArrayList<>());
        mGridLayoutManager = new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false);
        mContentRV.setAdapter(pictureAdapter);
        mContentRV.setLayoutManager(mGridLayoutManager);
        mContentRV.setHasFixedSize(true);

        mCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dispatchIntent(getIntent());
//        UsbMediaDataManager.getInstance().syncUsbConnectState(this, new UsbConnectStateListener() {
//            @Override
//            public void onConnection(UsbStatus status, List<String> mountPaths) {
//                switch (status) {
//                    case NO_USB_MOUNTED:
//                        showUSBState("没有检测到USB挂载");
//                        break;
//                    case INSERTED:
//                        showUSBState("USB插入中");
//                        break;
//                    case MOUNTED:
//                        showUSBState("USB挂载成功");
//                        break;
//                    case MOUNT_ERROR:
//                        showUSBState("USB挂载失败!");
//                        break;
//                    case REMOVED:
//                        showUSBState("USB已移除");
//                        break;
//                }
//            }
//        });
    }

    private void showUSBState(String msg) {
        if (mUsbHintTV != null) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    mUsbHintTV.setText(msg);
                }
            });
        }
    }

    private void dispatchIntent(Intent intent) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                List<UsbMediaInfo> pictureList = UsbMediaDataManager.getInstance().getPictureList();
                if (pictureList == null || ListUtils.isEmpty(pictureList)) {
                    pictureAdapter.setDatas(new ArrayList<>());
                    mUsbHintTV.append("未检测到图片信息");
                } else {
                    picVM.setDataList(pictureList);
                    handleArguments(intent);
                }
            }
        });
    }

    private void handleArguments(Intent intent) {
        if (intent != null) {
            arguments = intent.getExtras();
        }
        if (arguments == null) {
            return;
        }
        int action = arguments.getInt("action");
        Bundle bundle = new Bundle();
        switch (action) {
            case DistributeConstants.ACTION_SET_USB_PIC_PREVIOUS_NEXT:
                intentToXmPhotoActivity(1, arguments);
                break;
            case DistributeConstants.ACTION_SET_USB_PICTURE_OPERATION:
                intentToXmPhotoActivity(0, arguments);
                break;
            case DistributeConstants.ACTION_SET_USB_PICTURE_SHOW_TYPE:
                int type = arguments.getInt("type");
                switch (type) {
                    case 1: // list
                        break;
                    case 3: // whole screen
                        intentToXmPhotoActivity(0, null);
                        break;
                }
                bundle.putInt("result", type);
                bundle.putInt("rw", 1);
                EventBus.getDefault().post(bundle, "usb_pic_show_type");
                break;
            case DistributeConstants.ACTION_GET_USB_PICTURE_SHOW_TYPE:
                bundle.putInt("result", 1); // 列表
                bundle.putInt("rw", 2);
                EventBus.getDefault().post(bundle, "usb_pic_show_type");
                break;

        }
    }

    private void intentToXmPhotoActivity(int pos, Bundle bundle) {
        Intent intent = new Intent(this, XmPhotoActivity.class);
        intent.putExtra(XkanConstants.FROM_TYPE, XkanConstants.FROM_PHOTO);
        intent.putExtra(XkanConstants.PHOTO_INDEX, pos);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    @Subscriber(tag = XkanConstants.UPDATE_DATA)
    public void updateData(String event) {
        //初始化数据和监听
        dispatchIntent(getIntent());
    }

    protected void scrollToPosition(int position) {
        mGridLayoutManager.scrollToPositionWithOffset(position, 0);
    }


    public int getTipStr() {
        return R.string.empty_no_photo_file;
    }

    public int getEmtpyImgId() {
        return R.drawable.img_no_photo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
