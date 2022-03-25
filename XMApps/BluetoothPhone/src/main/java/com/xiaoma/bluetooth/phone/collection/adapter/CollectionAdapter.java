package com.xiaoma.bluetooth.phone.collection.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.common.utils.ContactNameUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.views.RightSlideView;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by qiuboxiang on 2018/12/4 20:07
 */
public class CollectionAdapter extends XMBaseAbstractBQAdapter<ContactBean, BaseViewHolder> {

    private RightSlideView mMenu;
    private Listener mListener;

    public CollectionAdapter(int layoutResId, @Nullable List<ContactBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder viewHolder, ContactBean item) {
        ((TextView) viewHolder.getView(R.id.text)).setText(ContactNameUtils.getLimitedContactName(item.getName()));

        RightSlideView slideView = viewHolder.getView(R.id.slideView);
        slideView.setOnSlidingListener(new RightSlideView.OnSlidingListener() {
            @Override
            public void onMenuIsOpen(RightSlideView view) {
                mMenu = view;
            }

            @Override
            public void onDown(RightSlideView view) {
                if (menuIsOpen()) {
                    closeMenu();
                } else {
                    if (mListener != null) {
                        mListener.onClick(getItem(viewHolder.getAdapterPosition()));
                    }
                }
            }

            @Override
            public void onMove(RightSlideView view) {
                if (menuIsOpen()) {
                    if (mMenu != view) {
                        closeMenu();
                    }
                }
            }

            @Override
            public void onDelete(RightSlideView view) {
                closeMenu();
                if (mListener != null) {
                    mListener.onDelete(getItem(viewHolder.getAdapterPosition()));
                }
            }
        });

      /*  boolean isLastPosition = viewHolder.getAdapterPosition() == mData.size() - 1;
        viewHolder.getView(R.id.divider).setVisibility(isLastPosition ? View.GONE : View.VISIBLE);*/
    }

    public void closeMenu() {
        if (!menuIsOpen()) return;
        mMenu.closeMenu();
        mMenu = null;
    }

    private boolean menuIsOpen() {
        if (mMenu == null) {
            return false;
        }
        if (mMenu.isOpen()) {
            return true;
        } else {
            mMenu = null;
            return false;
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getName(), String.valueOf(position));
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {

        void onClick(ContactBean bean);

        void onDelete(ContactBean bean);
    }
}
