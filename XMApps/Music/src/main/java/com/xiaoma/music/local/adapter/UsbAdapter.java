package com.xiaoma.music.local.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.view.AutoScrollTextView;

/**
 * @author zs
 * @date 2018/11/7 0007.
 */
public class UsbAdapter extends XMBaseAbstractBQAdapter<UsbMusic, BaseViewHolder> {
    private RequestManager mImageLoader;

    public UsbAdapter(RequestManager imageLoaderBuilder) {
        super(R.layout.item_gallery);
        this.mImageLoader = imageLoaderBuilder;
    }

    @Override
    protected void convert(BaseViewHolder helper, UsbMusic item) {
        try {
            mImageLoader.load(item)
                    .placeholder(R.drawable.iv_default_cover)
                    .transform(Transformations.getRoundedCorners())
                    .into((ImageView) helper.getView(R.id.cover));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String title = item.getTitleText();
        AutoScrollTextView marqueeTextView = helper.getView(R.id.title);
        setMarquee(marqueeTextView, item);
        if (TextUtils.isEmpty(title)) {
            marqueeTextView.setVisibility(View.INVISIBLE);
        } else {
            marqueeTextView.setVisibility(View.VISIBLE);
            marqueeTextView.setText(title);
        }
    }

    private void setMarquee(AutoScrollTextView textView, UsbMusic item) {
        textView.stopMarquee();
        final UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
        //被点击且playingId相等时，才开始跑马灯
        if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
            if (currUsbMusic != null && currUsbMusic.getPath().equals(item.getPath())) {
                textView.startMarquee();
            } else {
                textView.stopMarquee();
            }
        } else {
            textView.stopMarquee();
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        UsbMusic music = mData.get(position);
        return new ItemEvent(music.getTitleText(), music.getPath());
    }
}
