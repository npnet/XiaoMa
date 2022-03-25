package com.xiaoma.setting.sdk.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsl.android.tbox.bean.TBoxWifiInfo;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.utils.StringUtils;
import com.xiaoma.setting.sdk.ui.WifiConnectionFragment;
import com.xiaoma.setting.wifi.model.WifiScanResultBean;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
public class WifiScanResultAdapter extends RecyclerView.Adapter<WifiScanResultAdapter.ViewHolder> {


    private List<WifiScanResultBean> items;
    private OnItemClickedListener listener;

    public WifiScanResultAdapter(List<WifiScanResultBean> list) {
        items = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_scan_result, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.getItemView().setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent("点击单个wifi", "0");
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnItemClicked(position);
                }
            }
        });
        WifiScanResultBean wifiScanResultBean = items.get(position);
        TBoxWifiInfo scanResult = wifiScanResultBean.getInfo();
//        holder.signalStrength.setText(scanResult.getScanResult().level +"");

        holder.lockSign.setVisibility(isEncrypted(scanResult.getWifiEncryption())? View.VISIBLE: View.GONE);
        holder.wifiName.setText(StringUtils.subString(WifiConnectionFragment.MAX_BT_NAME_LENGTH, scanResult.getSsid()));
        holder.saved.setVisibility(scanResult.saved? View.VISIBLE: View.GONE);
//        holder.testSingal.setText(String.valueOf(scanResult.getSigStrength()));
        holder.connectedSing.setVisibility(wifiScanResultBean.isConnected() ? View.VISIBLE : View.GONE);
    }

    private boolean isEncrypted(int encryption) {
        if (encryption == SDKConstants.WifiEncryption_NO) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickedListener {
        void OnItemClicked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView connectedSing;
        private TextView wifiName;
        private ImageView lockSign;
        private ImageView signalStrength;
        private View itemView;
        private TextView saved;
//        private TextView testSingal;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            connectedSing = itemView.findViewById(R.id.connected_sign);
            wifiName = itemView.findViewById(R.id.wifi_name);
            lockSign = itemView.findViewById(R.id.wifi_lock_sign);
            signalStrength = itemView.findViewById(R.id.wifi_signal_strength);
            saved = itemView.findViewById(R.id.saved);
//            testSingal = itemView.findViewById(R.id.test_signal);
        }

        public View getItemView() {
            return itemView;
        }
    }
}
