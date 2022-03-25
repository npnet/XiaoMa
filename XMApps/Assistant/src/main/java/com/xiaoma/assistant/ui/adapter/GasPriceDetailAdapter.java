package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.GasPriceBean;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/12
 */
public class GasPriceDetailAdapter extends RecyclerView.Adapter<GasPriceDetailAdapter.ViewHolder> {
    private List<GasPriceBean.GasPrice> data;
    private Context context;

    public GasPriceDetailAdapter(Context context, List<GasPriceBean.GasPrice> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_price, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        adapterPrice(holder, position);
        adapterType(holder, position);

    }

    private void adapterType(ViewHolder holder, int position) {
        if (data.get(position) == null || TextUtils.isEmpty(data.get(position).getGasType())) return;
        String gasType = data.get(position).getGasType();
        if(gasType.contains("号")){
            String[] splits = gasType.split("号");
            holder.gasType.setText(splits[0] + "#:");
        }else {
            holder.gasType.setText(gasType + "#:");
        }

    }

    private void adapterPrice(ViewHolder holder, int position) {
        if (data.get(position) == null) return;
        String price = data.get(position).getGasPrice();
        if (price.contains("暂无价格") || TextUtils.isEmpty(price)) {
            holder.gasPrice.setText("暂无价格");
            return;
        }
        if (price.contains("元")) {
            holder.gasPrice.setText(String.format(context.getString(R.string.other_yuan_liter), data.get(position).getGasPrice()));
            return;
        } else {
            holder.gasPrice.setText(String.format(context.getString(R.string.yuan_liter), data.get(position).getGasPrice()));
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView gasType;
        private TextView gasPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            gasType = itemView.findViewById(R.id.tv_gas_type);
            gasPrice = itemView.findViewById(R.id.tv_gas_price);
        }
    }
}
