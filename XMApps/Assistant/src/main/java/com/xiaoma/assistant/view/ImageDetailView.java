package com.xiaoma.assistant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.ImageBean;
import com.xiaoma.assistant.utils.CommonUtils;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/14 20:16
 * Desc:
 */
public class ImageDetailView extends RelativeLayout implements View.OnClickListener {

    private Context context;
    private List<ImageBean.ImagesBean> mImageList;
    private ImageView mIvImage;
    private int position;

    public ImageDetailView(Context context) {
        this(context, null);
    }

    public ImageDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_image_detail, this);
        mIvImage = findViewById(R.id.iv_image);
        findViewById(R.id.iv_last_page).setOnClickListener(this);
        findViewById(R.id.iv_next_page).setOnClickListener(this);
    }

    public void setData(List<ImageBean.ImagesBean> mImageList, int position) {
        this.mImageList = mImageList;
        this.position = position;
        loadImage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_last_page:
                changePage(-1);
                break;
            case R.id.iv_next_page:
                changePage(1);
                break;
        }
    }

    public void changePage(int dValue) {
        setPage(position + dValue);
    }

    public void setPage(int position) {
        if (position >= 0 && position < mImageList.size()) {
            this.position = position;
            loadImage();
        }
    }

    public void loadImage() {
        CommonUtils.setBigItemImage(context, mImageList.get(position).getThumb(), mIvImage);
    }
}
