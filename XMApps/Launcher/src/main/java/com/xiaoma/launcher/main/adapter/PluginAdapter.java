package com.xiaoma.launcher.main.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.manager.LanguageUtils;
import com.xiaoma.launcher.main.model.PluginModel;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.ui.toast.XMToast;

import java.util.ArrayList;
import java.util.List;

public class PluginAdapter extends RecyclerView.Adapter<PluginAdapter.ViewHolder> {

    private Context mContext;
    private List<PluginModel> mData = new ArrayList<>();
    private ItemClickListener menuClickListener;

    public PluginAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<PluginModel> data) {
        if (data != null) {
            this.mData.clear();
            this.mData.addAll(data);
        }
    }

    public void setItemClickListener(ItemClickListener clickListener) {
        this.menuClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_control_panel_plugin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        ImageLoader.with(mContext).load(mData.get(position).getIconUrl()).into(holder.imageView);
        holder.imageView.setImageURI(Uri.parse(mData.get(position).getIconUrl()));
        holder.tvName.setText(LanguageUtils.isChinese(mContext) ? mData.get(position).getItemName() : mData.get(position).getItemNameEng());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        public TextView tvName;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            imageView = v.findViewById(R.id.iv_pic);
            v.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent(EventConstants.NormalClick.CONTROL_APP_PLUGIN, mData.get(getAdapterPosition()).getItemName());
                }

                @Override
                @BusinessOnClick
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String className = mData.get(position).getClassName();
                    if (className == null || className.length() == 0) {
                        XMToast.showToast(mContext, mContext.getString(R.string.app_no_install_tip));
                        return;
                    }

                    if (menuClickListener != null) {
                        menuClickListener.onItemClick(v, position);
                    }
                }
            });
            v.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.5f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
