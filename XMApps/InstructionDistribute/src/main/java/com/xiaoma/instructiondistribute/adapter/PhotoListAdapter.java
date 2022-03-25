package com.xiaoma.instructiondistribute.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/8/21
 */
public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.PhotoVH> {

    private List<UsbMediaInfo> mList;

    public PhotoListAdapter() {
        mList = new ArrayList<>();
    }

    public void setNewData(List<UsbMediaInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_list, parent, false);
        return new PhotoVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoVH holder, int position) {
        Context context = holder.itemView.getContext();
        UsbMediaInfo usbMediaInfo = mList.get(position);
        ImageLoader.with(context)
                .load(usbMediaInfo.getPath())
                .into(holder.mPhotoIV);
        holder.mIndexTV.setText(String.format("%1$s / %2$s", String.valueOf(position + 1), String.valueOf(getItemCount())));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PhotoVH extends RecyclerView.ViewHolder {

        private final ImageView mPhotoIV;
        private final TextView mIndexTV;

        public PhotoVH(View itemView) {
            super(itemView);
            mPhotoIV = itemView.findViewById(R.id.ivPhoto);
            mIndexTV = itemView.findViewById(R.id.tvIndex);
        }
    }
}
