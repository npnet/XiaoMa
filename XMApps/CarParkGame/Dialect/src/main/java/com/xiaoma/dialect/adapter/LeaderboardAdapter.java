package com.xiaoma.dialect.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.dialect.R;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardListItemHolder> {
    private Context mContext;
    private List mLeaderboardList = new ArrayList<>();

    public LeaderboardAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public LeaderboardListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardListItemHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardListItemHolder holder, int position) {
        if (position == 0) {
            holder.mTvranking.setText("100+");
            holder.mArrow.setVisibility(View.VISIBLE);
            holder.mTvUserName.setVisibility(View.GONE);
            holder.mTvTotalScore.setVisibility(View.GONE);
        } else {
            Log.d("", "记数：：" + position);
            holder.mTvranking.setText(" " + position);
            holder.mArrow.setVisibility(View.INVISIBLE);
            holder.mTvUserName.setVisibility(View.VISIBLE);
            holder.mTvTotalScore.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public class LeaderboardListItemHolder extends RecyclerView.ViewHolder {

        private final TextView mTvranking;
        private final ImageView mImUserAvatar;
        private final TextView mTvUserName;
        private final TextView mTvTotalScore;
        private final ImageView mArrow;

        public LeaderboardListItemHolder(View itemView, Context context) {
            super(itemView);
            mTvranking = itemView.findViewById(R.id.ranking);
            mImUserAvatar = itemView.findViewById(R.id.user_avatar);
            mTvUserName = itemView.findViewById(R.id.user_name);
            mTvTotalScore = itemView.findViewById(R.id.total_score);
            mArrow = itemView.findViewById(R.id.arrow);

        }
    }
}
