package com.xiaoma.xting.welcome;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.Ignore;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.welcome.model.PreferenceBean;

import java.util.ArrayList;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class PreferenceAdapter extends XMBaseAbstractBQAdapter<PreferenceBean, BaseViewHolder> {
    private ArrayList<PreferenceBean> mPreferenceSelected;
    private OnSelectIndexListener mOnSelectIndexListener;

    public PreferenceAdapter() {
        super(R.layout.item_preference);
        mPreferenceSelected = new ArrayList<>();
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getTagName(), String.valueOf(position));
    }

    private void notifySelectedState(TextView tagTV, PreferenceBean bean) {
        tagTV.setSelected(bean.isSelected());
        if (bean.isSelected()) {
            if (!mPreferenceSelected.contains(bean)) {
                mPreferenceSelected.add(bean);
            }

        } else {
            mPreferenceSelected.remove(bean);
        }
        if (mOnSelectIndexListener != null) {
            mOnSelectIndexListener.onSelectChange(!mPreferenceSelected.isEmpty());
        }
    }

    public ArrayList<PreferenceBean> getSelected() {
        return mPreferenceSelected;
    }

    @Override
    protected void convert(BaseViewHolder helper, final PreferenceBean preference) {
        final TextView preferenceTV = helper.getView(R.id.tvPreference);
        preferenceTV.setText(preference.getTagName());
        notifySelectedState(preferenceTV, preference);
        preferenceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            @Ignore
            public void onClick(View v) {
                XmAutoTracker.getInstance().onEvent(preference.getTagName(),
                        "PreferenceSelectFragment", EventConstants.PageDescribe.FRAGMENT_PREFERENCE_SELECT);
                preference.reverseSelectState();
                notifySelectedState(preferenceTV, preference);
            }
        });

    }

    public interface OnSelectIndexListener {

        void onSelectChange(boolean enable);

    }

    public void setOnSelectIndexListener(OnSelectIndexListener listener) {
        this.mOnSelectIndexListener = listener;
    }

}
