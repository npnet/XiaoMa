package com.xiaoma.systemui.common.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.systemui.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppListHolder> {
    private final List<ApplicationInfo> mApplicationInfos;
    private PackageManager mPackageManager;
    private int itemWidth;

    AppListAdapter(Context context, List<ApplicationInfo> applicationInfos, int spanCount) {
        mPackageManager = context.getPackageManager();
        mApplicationInfos = applicationInfos;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        itemWidth = dm.widthPixels / spanCount;
    }

    @NonNull
    @Override
    public AppListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppListHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_launcher, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AppListHolder holder, int position) {
        final ApplicationInfo app = mApplicationInfos.get(position);
        holder.tvName.setText(app.loadLabel(mPackageManager));
        holder.ivIcon.setImageDrawable(app.loadIcon(mPackageManager));
        holder.itemView.getLayoutParams().width = itemWidth;

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = mPackageManager.getLaunchIntentForPackage(app.packageName);
                if (intent != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "无法启动应用", Toast.LENGTH_SHORT).show();
                }
            }
        };
        holder.tvName.setOnClickListener(clickListener);
        holder.ivIcon.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return mApplicationInfos != null ? mApplicationInfos.size() : 0;
    }

    static class AppListHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;

        AppListHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
