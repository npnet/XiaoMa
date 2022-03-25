package com.xiaoma.ad.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ad.AdManager;
import com.xiaoma.ad.R;
import com.xiaoma.ad.models.Ad;
import com.xiaoma.ad.utils.AdSpUtil;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.thread.ThreadDispatcher;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by KY
 * on 2018/8/29.
 */
public class AdDialog extends Dialog {

    private TextView countDown;
    private ImageView adPicture;
    private int showTime = 5;
    private Ad adBean;
    private String countDownText;
    private GifImageView adPictureGif;
    private AdDialogClickListener mDialogClickListener;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            countDown.setText(countDownText);
            if (showTime == 0) {
                dismiss();

            } else {
                showTime--;
                if (!adBean.isAheadClosable()) {
                    countDownText = getContext().getString(R.string.advertisement_display_countdown, showTime);
                } else {
                    countDownText = getContext().getString(R.string.advertisement_display_countdown_skip, showTime);
                }
                if (showTime >= 0) {
                    ThreadDispatcher.getDispatcher().postOnMainDelayed(this, 1000);
                }
            }
        }
    };

    public AdDialog(Context context) {
        super(context, R.style.adv_dialog);
    }

    public AdDialog(Context context, Ad adBean) {
        this(context);
        setContentView(R.layout.layout_ad_dialog);
        this.adBean = adBean;
        showTime = this.adBean.getShowTime();
        initView();
    }

    private void initView() {
        WindowManager.LayoutParams dialogLP = new WindowManager.LayoutParams();
        dialogLP.copyFrom(getWindow().getAttributes());
        dialogLP.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogLP.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(dialogLP);
        countDown = findViewById(R.id.tv_count_down);
        adPicture = findViewById(R.id.iv_launcher_ad);
        adPictureGif = findViewById(R.id.iv_launcher_ad_gif);
        locationCountDownBtn();
        findViewById(R.id.adv_contentView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogClickListener != null && !TextUtils.isEmpty(adBean.getLink())) {
                    ThreadDispatcher.getDispatcher().removeOnMain(runnable);
                    mDialogClickListener.onDialogClick(adBean.getLink());
                    dismiss();
                }
            }
        });
        if (!adBean.isAheadClosable()) {
            countDown.setEnabled(false);
            countDownText = getContext().getString(R.string.advertisement_display_countdown, showTime);

        } else {
            countDownText = getContext().getString(R.string.advertisement_display_countdown_skip, showTime);
        }
    }

    private void locationCountDownBtn() {
        countDown.setText(R.string.advertisement_display_skip);
        countDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void showAdv() {
        // glide播放gif有很大的问题，参见https://github.com/bumptech/glide/issues/2471
        // 为了更好的支持Gif播放，引入JNI实现的Gif播放库，专门用于展示GIF
        if (adBean.getImgPath().endsWith(".gif")) {
            adPictureGif.setVisibility(View.VISIBLE);
            adPicture.setVisibility(View.GONE);
            adPictureGif.setImageURI(Uri.fromFile(AdManager.getAdFile(adBean)));

        } else {
            adPicture.setVisibility(View.VISIBLE);
            adPictureGif.setVisibility(View.GONE);
            ImageLoader.with(getContext())
                    .load(adBean.getImgPath())
                    .into(adPicture);
        }
        AdSpUtil.markAdShowSync();
        ThreadDispatcher.getDispatcher().postOnMain(runnable);
    }

    @Override
    public void show() {
        if (showTime <= 0) return;
        try {
            super.show();
            showAdv();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ThreadDispatcher.getDispatcher().removeOnMain(runnable);
    }

    public interface AdDialogClickListener {
        void onDialogClick(String link);
    }

    public void setDialogClickListener(AdDialogClickListener listener) {
        this.mDialogClickListener = listener;
    }
}