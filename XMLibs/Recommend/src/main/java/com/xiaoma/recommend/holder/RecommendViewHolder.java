package com.xiaoma.recommend.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.recommend.R;
import com.xiaoma.recommend.model.RecommendCategory;

/**
 * @author: iSun
 * @date: 2018/12/4 0004
 */
public class RecommendViewHolder extends BaseViewHolder<RecommendCategory> {
    private TextView title;
    private TextView name;
    private ImageView image;

    public RecommendViewHolder(View v) {
        super(v);
    }

    @Override
    public void initView(View itemView) {
        title = itemView.findViewById(R.id.tv_title);
        name = itemView.findViewById(R.id.tv_name);
        image = itemView.findViewById(R.id.iv_pic);
    }


    @Override
    public void bindViewHolder(RecommendCategory data) {
        if (data != null) {
            title.setText(data.getTitle());
            name.setText(data.getName());
            ImageLoader.with(context).load(data.getPic_url()).into(image);
        }
    }

}
