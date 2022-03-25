package com.xiaoma.xkan.picture.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.base.BaseFilterFragment;
import com.xiaoma.xkan.common.base.RVSpacesItemDecoration;
import com.xiaoma.xkan.common.comparator.DateComparator;
import com.xiaoma.xkan.common.comparator.NameComparator;
import com.xiaoma.xkan.common.comparator.SizeComparator;
import com.xiaoma.xkan.common.constant.EventConstants;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.picture.adapter.XmPictureAdapter;
import com.xiaoma.xkan.picture.vm.PictureFragmentVM;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2018/11/9 0009
 * 图片tab
 */
@PageDescComponent(EventConstants.PageDescribe.PICTUREFRAGMENTPAGEPATHDESC)
public class PictureFragment extends BaseFilterFragment {

    private XmPictureAdapter pictureAdapter;
    private PictureFragmentVM picVM;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_picture;
    }

    @Override
    public int getTipStr() {
        return R.string.empty_no_photo_file;
    }

    @Override
    public int getEmtpyImgId() {
        return R.drawable.img_no_photo;
    }

    @Override
    public void initAdapter(View emptyView) {
        pictureAdapter = new XmPictureAdapter(
                new ArrayList<UsbMediaInfo>(),
                ImageLoader.with(this));
        pictureAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                intentToXmPhotoActivity(position);
            }
        });

        pictureAdapter.setEmptyView(emptyView);
        rv.addItemDecoration(new RVSpacesItemDecoration());
        rv.setAdapter(pictureAdapter);
    }

    @Override
    public void initVM() {
        picVM = ViewModelProviders.of(this).get(PictureFragmentVM.class);
        picVM.getDataList().observe(this, new Observer<List<UsbMediaInfo>>() {
            @Override
            public void onChanged(@Nullable List<UsbMediaInfo> pictureList) {
                pictureAdapter.setNewData(pictureList);
                rv.scrollToPosition(0);
                handlerEmptyView(pictureList);
            }
        });
    }

    @Override
    public void initData() {
        picVM.setDataList(UsbMediaDataManager.getInstance().getPictureList());
    }

    @Override
    public void filterName(boolean isZ) {
        picVM.filterList(new NameComparator(isZ));
    }

    @Override
    public void filterDate(boolean isFar) {
        picVM.filterList(new DateComparator(isFar));
    }

    @Override
    public void filterSize(boolean isBig) {
        picVM.filterList(new SizeComparator(isBig));
    }

    private void intentToXmPhotoActivity(int pos) {
        Intent intent = new Intent(getActivity(), XmPhotoActivity.class);
        intent.putExtra(XkanConstants.FROM_TYPE, XkanConstants.FROM_PHOTO);
        intent.putExtra(XkanConstants.PHOTO_INDEX, pos);
        startActivity(intent);
    }

    @Subscriber(tag = XkanConstants.XKAN_PIC_POS)
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
        pictureAdapter.setNewData(null);
        filterView.reset();
    }
}
