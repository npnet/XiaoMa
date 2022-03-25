package com.xiaoma.music.welcome.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.music.R;
import com.xiaoma.music.welcome.model.PreferenceBean;
import com.xiaoma.skin.manager.XmSkinManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class PreferenceAdapter extends RecyclerView.Adapter<PreferenceAdapter.VH> {

    private List<PreferenceBean> mPreferenceTypes;
    private ArrayList<PreferenceBean> mPreferenceSelected;
    private OnItemClickListener mOnItemClickListener;
    private OnSelectIndexListener mOnSelectIndexListener;

    public PreferenceAdapter() {
        mPreferenceTypes = new ArrayList<>();
        mPreferenceSelected = new ArrayList<>();
    }

    public void inflateDates(List<PreferenceBean> list) {
        mPreferenceTypes.clear();
        mPreferenceSelected.clear();
        mPreferenceTypes.addAll(list);
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preference, parent, false);
        return new VH(inflate);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        TextView preferenceTV = holder.mPreferenceTV;
        final PreferenceBean preference = mPreferenceTypes.get(position);
        if (preference.isSelected()) {
            addToSelect(preference);
//            preferenceTV.setBackground(preferenceTV.getResources().getDrawable(R.drawable.background_preference_selected));
            preferenceTV.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.background_preference_selected));
        } else {
            mPreferenceSelected.remove(preference);
//            preferenceTV.setBackground(preferenceTV.getResources().getDrawable(R.drawable.bg_preference_item_selector));
            preferenceTV.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.bg_preference_item_selector));
        }
        if (mOnSelectIndexListener != null) {
            mOnSelectIndexListener.onSelectChange(!mPreferenceSelected.isEmpty());
        }
        preferenceTV.setText(preference.getTagName());
        preferenceTV.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString() + " " + !preference.isSelected(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                preference.setSelected(!preference.isSelected());
                notifyItemChanged(position);
            }
        });
    }

    private void addToSelect(PreferenceBean preference) {
        if (preference == null) {
            return;
        }
        if (mPreferenceSelected == null) {
            mPreferenceSelected = new ArrayList<>();
        }
        for (int i = 0; i < mPreferenceSelected.size(); i++) {
            if (mPreferenceSelected.get(i).getThridId().equals(preference.getThridId())) {
                mPreferenceSelected.remove(preference);
            }
        }
        mPreferenceSelected.add(preference);
    }

    @Override
    public int getItemCount() {
        return mPreferenceTypes.size();
    }

    public ArrayList<PreferenceBean> getSelected() {
        return mPreferenceSelected;
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView mPreferenceTV;

        public VH(View itemView) {
            super(itemView);
            mPreferenceTV = itemView.findViewById(R.id.tvPreference);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnSelectIndexListener {

        void onSelectChange(boolean enable);

    }

    public void setOnSelectIndexListener(OnSelectIndexListener listener) {
        this.mOnSelectIndexListener = listener;
    }
}
