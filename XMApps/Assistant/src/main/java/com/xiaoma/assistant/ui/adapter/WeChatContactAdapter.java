package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.view.CircleCharAvatarView;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.wechat.constant.WeChatConstants;
import com.xiaoma.wechat.manager.WeChatManagerFactory;
import com.xiaoma.wechat.model.WeChatContact;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/5/21 16:00
 * Desc:
 */
public class WeChatContactAdapter extends BaseMultiPageAdapter<WeChatContact> {

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIndex;
        public TextView tvName;
        public TextView tvNumber;
        public TextView tvLocation;
        public TextView tvOperator;
        public CircleCharAvatarView ivHead;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_contact_index);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            tvNumber = itemView.findViewById(R.id.tv_contact_num);
            ivHead = itemView.findViewById(R.id.iv_contact_head);
            tvLocation = itemView.findViewById(R.id.tv_contact_location);
            tvOperator = itemView.findViewById(R.id.tv_contact_operator);
        }

    }


    public WeChatContactAdapter(Context context, List<WeChatContact> list) {
        this.context = context;
        this.allList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_contact, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            WeChatContact bean = allList.get(position);
            if (bean == null) return;
            ((ContactViewHolder) holder).tvIndex.setText(String.valueOf(position + 1));
            ((ContactViewHolder) holder).tvName.setText(!TextUtils.isEmpty(bean.getRemark()) ? handleText(bean.getRemark()) : handleText(bean.getNick()));
            CircleCharAvatarView mIvHead = ((ContactViewHolder) holder).ivHead;
            if (!TextUtils.isEmpty(bean.getHeaderImg())) {
                GlideUrl cookie = new GlideUrl(bean.getHeaderImg(), new LazyHeaders.Builder().addHeader("Cookie", WeChatManagerFactory.getManager().getCookie()).build());
                ImageLoader.with(context)
                        .load(cookie)
                        .placeholder(R.drawable.image_place_holder)
                        .error(R.drawable.image_place_holder)
                        .into(((ContactViewHolder) holder).ivHead);
                Log.d("QBX", "onBindViewHolder: getHeaderImg=" + bean.getHeaderImg());
            } else {
                mIvHead.setImageDrawable(context.getDrawable(R.drawable.default_head));
            }
        }

    }

    private String handleText(String text) {
        //去除emoji字符
        return text.replaceAll("<[^>]*>", "").trim();
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

    public static boolean isBase64Img(String imgurl) {
        if (!TextUtils.isEmpty(imgurl) && (imgurl.startsWith("data:image/png;base64,")
                || imgurl.startsWith("data:image/*;base64,") || imgurl.startsWith("data:image/jpg;base64,")
        )) {
            return true;
        }
        return false;
    }

}
