package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.NewsInfo;

import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/18 0018
 * 新闻二级界面
 */
public class NewsListAdapter extends BaseMultiPageAdapter<NewsInfo.DataBean.ContentlistBean> {
    private int playPostion = 0;
    private AnimationDrawable drawable;
    private boolean isStartAnim;

    public NewsListAdapter(Context context, List<NewsInfo.DataBean.ContentlistBean> list) {
        this.context = context;
        this.allList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            NewsInfo.DataBean.ContentlistBean bean = allList.get(position);
            ((ViewHolder) holder).tvNum.setText(String.valueOf(position + 1));
            ((ViewHolder) holder).tvContent.setText(allList.get(position).getTitle());
            ((ViewHolder) holder).ivIcon.setImageResource(R.drawable.speak_rhythm);
            drawable = (AnimationDrawable) ((ViewHolder) holder).ivIcon.getDrawable();

            if (position == playPostion) {
                ((ViewHolder) holder).itemParent.setBackgroundResource(R.drawable.news_selected_bg);
                ((ViewHolder) holder).tvContent.setTextColor(context.getResources().getColor(R.color.white));
                isSetMariginTop(true, ((ViewHolder) holder).numTag);
                ((ViewHolder) holder).ivIcon.setVisibility(View.VISIBLE);
                if (isStartAnim) {
                    drawable.start();
                } else {
                    if (drawable.isRunning()) {
                        drawable.stop();
                    }
                }
            } else {
                ((ViewHolder) holder).itemParent.setBackgroundResource(R.drawable.news_unselected_bg);
                ((ViewHolder) holder).ivIcon.setVisibility(View.INVISIBLE);
                ((ViewHolder) holder).tvContent.setTextColor(context.getResources().getColor(R.color.unselected_text_color));
                isSetMariginTop(false, ((ViewHolder) holder).numTag);
                if (drawable != null) {
                    if (drawable.isRunning()) {
                        drawable.stop();
                    }
                }
            }
        }
    }

    public String getDesc() {
        String result = "";
        if (allList != null && !allList.isEmpty() && playPostion >= 0 && playPostion < allList.size()) {
            result = allList.get(playPostion).getDesc();
        }
        return result;
    }

    public String getContent() {
        String result = "";
        if (allList != null && !allList.isEmpty() && playPostion >= 0 && playPostion < allList.size()) {
            result = allList.get(playPostion).getContent();
        }
        return result;
    }

    public String getTitle() {
        String result = "";
        if (allList != null && !allList.isEmpty() && playPostion >= 0 && playPostion < allList.size()) {
            result = allList.get(playPostion).getTitle();
        }
        return result;
    }

    public int getPlayPostion() {
        return playPostion;
    }

    public void setPlayPostion(int playPostion) {
        this.playPostion = playPostion;
        isStartAnim = true;
        notifyDataSetChanged();
    }

    public boolean getAnimState() {
        return isStartAnim;
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

    private void isSetMariginTop(boolean isMargin, View view) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (isMargin) {
            layoutParams.setMargins(27, 5, 0, 0);
        } else {
            layoutParams.setMargins(27, 4, 0, 0);
        }
        view.setLayoutParams(layoutParams);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvContent;
        ImageView ivIcon;
        View itemParent;
        View numTag;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvNum = itemView.findViewById(R.id.tv_num);
            itemParent = itemView.findViewById(R.id.item_news);
            numTag = itemView.findViewById(R.id.num_tag);
        }

    }
}
