package com.xiaoma.bluetooth.phone.main.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.model.ItemEvent;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/16
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder> {

    private final String[] stringArray;
    private Context context;
    private int[] items;
    private ItemClickedListener listener;
    private int selectedItemPosition = 0;

    public MenuAdapter(Context context) {
        this.context = context;
        stringArray = context.getResources().getStringArray(R.array.bluetooth_menu_string);
    }

    public void setData(int[] items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, final int position) {
        if (position != selectedItemPosition) {
            holder.lightBg.setVisibility(View.INVISIBLE);
            holder.imageButton.setSelected(false);
        } else {
            holder.lightBg.setVisibility(View.VISIBLE);
            holder.imageButton.setSelected(true);
        }
        holder.imageButton.setBackgroundResource(items[position]);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.length;
    }

    public void setOnItemClickedListener(ItemClickedListener listener) {
        this.listener = listener;
    }

    public interface ItemClickedListener {
        void onItemClickedListener(int position);
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageButton;
        private ImageView lightBg;
        private View tabButton;

        public MenuItemViewHolder(View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.image_button);
            lightBg = itemView.findViewById(R.id.light_bg);
            tabButton = itemView.findViewById(R.id.tab_button);
            tabButton.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent("点击蓝牙电话左边菜单栏", stringArray[getAdapterPosition()]);
                }

                @Override
                public void onClick(View v) {
                    selectedItemPosition = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClickedListener(getAdapterPosition());
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void autoClick() {
            tabButton.performClick();
        }
    }

    public void autoClick(RecyclerView recyclerView, int index) {
        View view = recyclerView.getChildAt(index);
        if (view != null) {
            MenuItemViewHolder childViewHolder = (MenuItemViewHolder) recyclerView.getChildViewHolder(view);
            childViewHolder.autoClick();
        }
    }
}
