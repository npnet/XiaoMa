package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.view.CircleCharAvatarView;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/9
 * Desc：联系人列表
 */
public class ContactListAdapter extends BaseMultiPageAdapter<ContactBean> {

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


    public ContactListAdapter(Context context, List<ContactBean> list) {
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
            ContactBean bean = allList.get(position);
            if (bean == null) return;
            ((ContactViewHolder) holder).tvIndex.setText(String.valueOf(position + 1));
            ((ContactViewHolder) holder).tvName.setText(bean.getName());
            ((ContactViewHolder) holder).tvNumber.setText(bean.getPhoneNum());
//            ((ContactViewHolder)holder).tvLocation.setText();
//            ((ContactViewHolder)holder).tvOperator.setText();
            CircleCharAvatarView mIvHead = ((ContactViewHolder) holder).ivHead;
            if (bean.getIcon() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bean.getIcon(), 0, bean.getIcon().length);
                mIvHead.setImageBitmap(bitmap);
            } else if (bean.getName().equals(context.getString(R.string.unknown_contact)) || !isLetterOrChinese(bean.getName().substring(0, 1))) {
                mIvHead.setImageDrawable(context.getDrawable(R.drawable.default_head));
            } else {
                mIvHead.setImageDrawable(context.getDrawable(R.drawable.bg_head));
                mIvHead.setText(bean.getName());
            }
        }

    }

    private static boolean isLetterOrChinese(String str) {
        String regex = "^[a-zA-Z\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}
