package com.xiaoma.app.views;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoma.app.R;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZouShao on 2017/7/6 0006.
 */

public class PhotosViewDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private List<String> imageList;
    private List<Map<String, Object>> data;
    private ViewPager photosView;
    private PhotosViewAdapter adapter;
    private PagerIndicator pagerIndicator;
    public static final String PHOTO_URL = "photo_url";
    public static final String PHOTO_VIEW = "photo_view";

    public PhotosViewDialog(Context context) {
        super(context, R.style.dialog_transparent);
    }

    public PhotosViewDialog(Context context, List<String> imageList) {
        this(context);
        this.mContext = context;
        setContentView(R.layout.layout_photos_dialog);
        this.imageList = imageList;
        initView();
        getData();
    }

    private void initView() {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        photosView = findViewById(R.id.photo_view_pager);
        pagerIndicator = findViewById(R.id.pager_indicator);
        findViewById(R.id.layout_photo_view).setOnClickListener(this);
    }

    private void getData() {
        data = new ArrayList<>();
        if (imageList != null && !imageList.isEmpty()) {
            for (int i = 0; i < imageList.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                map.put(PHOTO_URL, imageList.get(i));
                map.put(PHOTO_VIEW, new ImageView(mContext));
                data.add(map);
            }
        }
        adapter = new PhotosViewAdapter();
        photosView.setAdapter(adapter);
        pagerIndicator.setupViewPager(photosView);

    }

    public void dismiss(Dialog dialog) {
        new SafeDismiss(dialog).dismiss();
    }

    public void setCurrentItem(int position) {
        photosView.setCurrentItem(position);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public class PhotosViewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (data != null && !data.isEmpty()) {
                return data.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = (ImageView) data.get(position).get(PHOTO_VIEW);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(1382,518));
            imageView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                @BusinessOnClick
                public void onClick(View v) {
                    dismiss();
                }

                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent(EventConstants.NormalClick.detailImageDismiss, PHOTO_URL);
                }
            });
            String url = (String) data.get(position).get(PHOTO_URL);
            ImageLoader.with(mContext)
                    .load(url)
                    .dontAnimate()
                    .error(R.drawable.iv_preview_default)
                    .into(imageView);
            container.addView(imageView);
            return data.get(position).get(PHOTO_VIEW);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView = (ImageView) data.get(position).get(PHOTO_VIEW);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            container.removeView(imageView);
        }
    }
}
