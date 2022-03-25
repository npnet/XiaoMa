package com.xiaoma.setting.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.setting.R;
import com.xiaoma.setting.bluetooth.model.BluetoothDeviceStatusModule;
import com.xiaoma.setting.common.utils.StringUtils;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/12
 */
public class BoundDevicesAdapter extends RecyclerView.Adapter<BoundDevicesAdapter.ViewHolder> {

    private static final String TAG = "BoundDevicesAdapter";
    public static final int CONNECTED = 1;
    public static final int NOT_CONNECTED = 0;
    private Context context;
    private List<BluetoothDeviceStatusModule> items;
    private OnIconClickedListener listener;
    private static final int MAX_BT_NAME = 20;

    public BoundDevicesAdapter(Context context, List<BluetoothDeviceStatusModule> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bound_device, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BluetoothDeviceStatusModule item = items.get(position);
        final BluetoothDevice device = item.getDevice();
        holder.bltName.setText( StringUtils.subString(MAX_BT_NAME, device.getName()));
        final int connectedStatus = item.getDeviceStatus();
        if (connectedStatus == NOT_CONNECTED) {
            holder.bind.setImageResource(R.drawable.icon_disconnect);
        } else if (connectedStatus == CONNECTED) {
            holder.bind.setImageResource(R.drawable.icon_connect);
        }
        if (listener != null) {
            holder.bind.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent("连接设备", "0");
                }

                @Override
                @BusinessOnClick
                public void onClick(View v) {
                    listener.onConnectClicked(position);
                }
            });
            holder.delete.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent("删除设备", "0");
                }

                @Override
                @BusinessOnClick
                public void onClick(View v) {
                    listener.onDeleteClicked(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public void setOnIconClickedListener(OnIconClickedListener listener) {
        this.listener = listener;
    }

    public interface OnIconClickedListener {
        void onDeleteClicked(int position);

        void onConnectClicked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bltName;
        private ImageView delete;
        private ImageView bind;

        public ViewHolder(View itemView) {
            super(itemView);
            bltName = itemView.findViewById(R.id.blt_name);
            delete = itemView.findViewById(R.id.delete);
            bind = itemView.findViewById(R.id.bind);
        }
    }
}
