package com.qiming.fawcard.synthesize.core.drivescore.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.system.callback.HistoryRegistrationCenter;
import com.qiming.fawcard.synthesize.base.system.callback.IDataUpdateListener;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHistoryActivity;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHistoryDetailActivity;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScoreHistoryPresenter;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDetailDao;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ContactViewHolder>{
    public DriveScoreHistoryActivity activity;
    private final DriveScoreHistoryPresenter presenter;
    public HistoryAdapter(Context context){
        DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(context);
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(context);
        presenter = new DriveScoreHistoryPresenter(historyDao, detailDao);
        HistoryRegistrationCenter.register(new IDataUpdateListener() {
            @Override
            public void update() {
                updateForData();
            }
        });
    }
    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ContactViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
        @BindView(R.id.clDate)
        public ConstraintLayout clDate;
        @BindView(R.id.llItemClick)
        public LinearLayout llItemClick;
        @BindView(R.id.tvScore)
        public TextView tvScore;
        @BindView(R.id.tvAccNum)
        public TextView tvAccNum;
        @BindView(R.id.tvDecNum)
        public TextView tvDecNum;
        @BindView(R.id.tvTurnNum)
        public TextView tvTurnNum;
        @BindView(R.id.tvDate)
        public TextView tvDay;
        @BindView(R.id.tvTime)
        public TextView tvTime;
        @BindView(R.id.tvDelete)
        public TextView btnDelete;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_history_item, parent, false);
        return new ContactViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ContactViewHolder holder, int position) {
        if(position==0){
            holder.clDate.setVisibility(View.VISIBLE);
        }else if(presenter.getChange(position)) {
            holder.clDate.setVisibility(View.VISIBLE);
        }else {
            holder.clDate.setVisibility(View.GONE);
        }
        final DriveScoreHistoryPresenter.ViewModel viewModel = presenter.get(position);
        holder.tvScore.setText(viewModel.getScore() + activity.getResources().getString(R.string.history_score));
        holder.tvAccNum.setText(viewModel.getAccNum() + activity.getResources().getString(R.string.count));
        holder.tvDecNum.setText(viewModel.getDecNum() + activity.getResources().getString(R.string.count));
        holder.tvTurnNum.setText(viewModel.getTurnNum() + activity.getResources().getString(R.string.count));
        holder.tvTime.setText(viewModel.getTime());
        holder.tvDay.setText(viewModel.getDate());
        holder.llItemClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DriveScoreHistoryDetailActivity.class);
                intent.putExtra("id", viewModel.getId());
                intent.putExtra("startTime",viewModel.getStartTime());
                intent.putExtra("endTime",viewModel.getEndTime());
                activity.startActivity(intent);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete(viewModel.getDriveScoreHistoryEntity());
            }
        });

    }

    @Override
    public int getItemCount() {
        return presenter.getCount();
    }

    /**
     * 数据更新通知.
     */
    private void update(){
        notifyDataSetChanged();
    }

    /**
     * 数据更新
     */
    private void updateForData(){
        presenter.update();
        update();
    }
}
