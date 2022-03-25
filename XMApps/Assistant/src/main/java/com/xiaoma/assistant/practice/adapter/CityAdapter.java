package com.xiaoma.assistant.practice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.pratice.CityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市列表adapter
 * Created by zhushi.
 * Date: 2018/11/14
 */
public class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //正在定位
    public static final int LOCATING = 0;
    //定位失败
    public static final int FAILED = 1;
    //定位成功
    public static final int SUCCESS = 2;

    private LayoutInflater mInflater;
    private List<CityBean> mData = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private int locationState = LOCATING;
    private String locationCity;
    private Context mContext;
    private int ITEM_TYPE_LOCATION = 11;

    public CityAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, String city) {
        this.locationState = state;
        this.locationCity = city;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_LOCATION;

        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOCATION) {
            View view = mInflater.inflate(R.layout.item_city_location, parent, false);
            return new LocationViewHolder(view);

        } else {
            View view = mInflater.inflate(R.layout.item_city, parent, false);
            return new CityViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LocationViewHolder) {
            LocationViewHolder locationViewHolder = (LocationViewHolder) holder;
            String locationStr = "";
            switch (locationState) {
                case LOCATING:
                    locationStr = mContext.getString(R.string.location_ing);
                    locationViewHolder.mLocationImg.setVisibility(View.GONE);
                    break;

                case FAILED:
                    locationStr = mContext.getString(R.string.location_fail);
                    locationViewHolder.mLocationImg.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    locationStr = locationCity;
                    locationViewHolder.mLocationImg.setVisibility(View.VISIBLE);
                    break;
            }
            locationViewHolder.locationTv.setText(locationStr);
            if (mOnItemClickListener != null) {
                locationViewHolder.mLocationLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locationState == LOCATING || locationState == FAILED) {
                            //重新定位
                            mOnItemClickListener.onLocateClick();

                        } else if (locationState == SUCCESS) {
                            //返回定位城市
                            mOnItemClickListener.onItemClick(holder.itemView, position);
                        }
                    }
                });
            }

        } else if (holder instanceof CityViewHolder) {
            CityViewHolder cityViewHolder = (CityViewHolder) holder;
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                cityViewHolder.tvTag.setVisibility(View.VISIBLE);
                cityViewHolder.tvTag.setText(mData.get(position).getLetters().toUpperCase().substring(0, 1));

            } else {
                cityViewHolder.tvTag.setVisibility(View.GONE);
            }

            cityViewHolder.tvName.setText(this.mData.get(position).getName());
            holder.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent(mData.get(position).getName(), mData.get(position).getLetters());
                }

                @Override
                @BusinessOnClick
                public void onClick(View v) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public CityBean getItem(int position) {
        return mData.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLocateClick();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvName;

        public CityViewHolder(View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tag);
            tvName = itemView.findViewById(R.id.name);
        }
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView locationTv;
        ImageView mLocationImg;
        ImageView mLocationLayout;

        public LocationViewHolder(View itemView) {
            super(itemView);
            locationTv = itemView.findViewById(R.id.location_tv);
            mLocationImg = itemView.findViewById(R.id.iv_location);
            mLocationLayout = itemView.findViewById(R.id.iv_bg_location);
        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<CityBean> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).getLetters().toUpperCase().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 1; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters().substring(0, 1);
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }
}
