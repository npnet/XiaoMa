package com.xiaoma.music.search.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.music.R;
import com.xiaoma.music.common.util.MusicUtils;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.kuwo.model.XMArtistInfo;
import com.xiaoma.music.search.ui.SingerActivity;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/12 0012
 */
public class SingleAdapter extends RecyclerView.Adapter<SingleAdapter.ViewHolder> {
    private Context mContext;
    private List<XMArtistInfo> mData;
    private WeakReference<Fragment> fragment;

    public SingleAdapter(Fragment fragment, List<XMArtistInfo> mData) {
        this.mContext = fragment.getContext();
        this.mData = mData;
        this.fragment = new WeakReference<>(fragment);
    }

    public void setData(List<XMArtistInfo> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final XMArtistInfo singerBean = mData.get(position);
        try {
            if (fragment.get() != null && !fragment.get().isDetached()) {
                ImageLoader.with(fragment.get())
                        .load(TextUtils.isEmpty(singerBean.getSDKBean().getImageUrl()) ? ""
                                : singerBean.getSDKBean().getImageUrl())
                        .placeholder(R.drawable.iv_singer_default)
                        .transform(Transformations.getCircleCrop())
                        .into(holder.mImageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mName.setText(singerBean.getSDKBean().getName());
        holder.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(holder.mName.getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, mContext.getString(R.string.net_error));
                    return;
                }
                SingerActivity.startActivity(mContext, singerBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mName;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_singer_iv);
            mName = itemView.findViewById(R.id.item_singer_tv);
        }
    }
}
