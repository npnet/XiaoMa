package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.ImageBean;
import com.xiaoma.assistant.utils.CommonUtils;

import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/18 0018
 * 图片二级界面
 */
public class ImageListAdapter extends BaseMultiPageAdapter<ImageBean.ImagesBean> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNum =  itemView.findViewById(R.id.tv_num);
            ivImage = itemView.findViewById(R.id.iv_photo);
        }

    }

    public ImageListAdapter(Context context, List<ImageBean.ImagesBean> list) {
        this.context = context;
        this.allList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            ImageBean.ImagesBean bean = allList.get(position);
            ViewHolder mHolder = (ViewHolder) holder;
            mHolder.tvNum.setText(String.valueOf(position + 1));
            CommonUtils.setFullItemImage(context,bean.getThumb(),mHolder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }
}
