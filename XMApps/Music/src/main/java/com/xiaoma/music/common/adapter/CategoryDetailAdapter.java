package com.xiaoma.music.common.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.model.UploadMusicModel;
import com.xiaoma.music.common.util.MusicUtils;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.music.online.model.CategoryDetailModel;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.GsonHelper;

import org.simple.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class CategoryDetailAdapter<T extends IGalleryData> extends XMBaseAbstractBQAdapter<T, BaseViewHolder> {

    private WeakReference<Fragment> fragment;
    private boolean hasTrigged;

    public CategoryDetailAdapter(Fragment fragment, @Nullable List<T> data) {
        super(R.layout.item_gallery, data);
        this.fragment = new WeakReference<>(fragment);
    }


    @Override
    protected void convert(BaseViewHolder helper, T item) {
        try {
            if (fragment.get() != null && !fragment.get().isDetached()) {
                ImageLoader.with(fragment.get())
                        .load(item.getCoverUrl())
                        .placeholder(R.drawable.iv_default_cover)
                        .transform(Transformations.getRoundedCorners())
                        .into((ImageView) helper.getView(R.id.cover));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String title = item.getTitleText();
        AutoScrollTextView marqueeTextView = helper.getView(R.id.title);
        if (TextUtils.isEmpty(title)) {
            marqueeTextView.setVisibility(View.INVISIBLE);
        } else {
            marqueeTextView.setText(title);
        }
        marqueeTextView.stopMarquee();
        String id = getAlbumId(item);
        boolean marqueeStart = KwPlayInfoManager.getInstance().isCurrentPlayInfo(id, KwPlayInfoManager.AlbumType.SONG_LIST);
        if (marqueeStart) {
            marqueeTextView.startMarquee();
        } else {
            marqueeTextView.stopMarquee();
        }
        if (helper.getAdapterPosition() == 0 && !hasTrigged) {
            hasTrigged = true;
            if (GuideDataHelper.shouldShowGuide(helper.itemView.getContext(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false))
                EventBus.getDefault().post("", EventBusTags.CATEGORY_DETAIL_ITEMVIEW_LOADED);
        }
    }

    private String getAlbumId(T item) {
        CategoryDetailModel model = (CategoryDetailModel) item;
        XMSongListInfo songListInfo = model.getSongListInfo();
        if (songListInfo != null && songListInfo.getSDKBean() != null) {
            return songListInfo.getSDKBean().getId() + songListInfo.getSDKBean().getName();
        } else {
            return "";
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        CategoryDetailModel model = (CategoryDetailModel) getData().get(position);
        XMSongListInfo info = model.getSongListInfo();
        if (info == null || info.getSDKBean() == null) {
            return new ItemEvent(model.getTitleText(), String.valueOf(position));
        } else {
            UploadMusicModel musicModel = new UploadMusicModel();
            musicModel.setId(info.getSDKBean().getId());
            musicModel.setMusicName(info.getSDKBean().getName());
            musicModel.setType("kuwo");
            return new ItemEvent(model.getTitleText(), GsonHelper.toJson(musicModel));
        }
    }
}
