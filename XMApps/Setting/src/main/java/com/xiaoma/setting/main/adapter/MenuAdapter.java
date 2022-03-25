package com.xiaoma.setting.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.setting.R;
import com.xiaoma.ui.toast.XMToast;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder> {


    private Context mContext;
    private List<String> mData = new ArrayList<>();
    private IMenuClickListener menuClickListener;
    private int mSelectIndex = 0;
    private int mUserType;

    public void setSelectIndex(int selectIndex) {
        mSelectIndex = selectIndex;
    }

    public MenuAdapter(Context context, int userType, int position) {
        this.mContext = context;
        mSelectIndex = position;
    }

    public void setData(List<String> data) {
        if (data != null) {
            this.mData.clear();
            this.mData.addAll(data);
        }
    }

    public void setItemClickListener(IMenuClickListener clickListener) {
        this.menuClickListener = clickListener;
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_menu, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        if (position != mSelectIndex) {
//            holder.imageView.setBackgroundColor(Color.TRANSPARENT);
            holder.imageView.setBackgroundResource(0);
            holder.menuItem.setTextColor(mContext.getResources().getColor(R.color.color_menu_normal));
        } else {
            holder.menuItem.setTextColor(Color.WHITE);
//            holder.imageView.setBackgroundResource(R.drawable.bg_item_select);
//            holder.imageView.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.bg_item_select));
            holder.imageView.setBackgroundResource(R.drawable.bg_item_select);
        }
        if (position >= 0 && position < mData.size()) {
            holder.menuItem.setText(mData.get(position).toString());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        public TextView menuItem;
        public ImageView imageView;

        public MenuItemViewHolder(View v) {
            super(v);
            menuItem = (TextView) v.findViewById(R.id.tv_menu_name);
            imageView = v.findViewById(R.id.bg_cover);
            v.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent("点击左边菜单栏", menuItem.getText().toString());
                }

                @Override
                @BusinessOnClick
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (!ConfigManager.ApkConfig.isDebug()){
                        if (!LoginManager.getInstance().isUserLogin()) {
                            if (position != 0 && position != 1 && position != 4) {
                                XMToast.showToast(mContext, R.string.unable_to_use_this_feature);
                                return;
                            }
                        }
                    }
                    if (menuClickListener != null) {
                        menuClickListener.onItemClick(v, getAdapterPosition());
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void setCheckItem(int checkIndex){
        mSelectIndex = checkIndex;
        notifyDataSetChanged();
    }

    public interface IMenuClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
