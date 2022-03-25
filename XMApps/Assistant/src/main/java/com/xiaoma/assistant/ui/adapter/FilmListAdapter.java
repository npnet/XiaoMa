package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaoma.assistant.model.FilmBean;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.utils.CommonUtils;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/13 11:57
 * Desc:
 */
public class FilmListAdapter extends BaseMultiPageAdapter<FilmBean> {

    public static class FilmBeanViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvScore;
        TextView tvName;
        TextView tvTab;

        public FilmBeanViewHolder(View itemView) {
            super(itemView);
            tvTab = itemView.findViewById(R.id.tv_tab);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }

    public FilmListAdapter(Context context, List<FilmBean> list) {
        this.context = context;
        this.allList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_film, parent, false);
        return new FilmBeanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            FilmBean bean = allList.get(position);
            FilmBeanViewHolder mHolder = (FilmBeanViewHolder) holder;
            mHolder.tvName.setText(bean.getTitle());
            mHolder.tvTab.setText(bean.getFilmType());
            mHolder.tvScore.setText(String.format(context.getString(R.string.minute),bean.getFilmScore())) ;
            CommonUtils.setFullItemImage(context,bean.getImgUrl(),mHolder.ivPhoto);
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}
