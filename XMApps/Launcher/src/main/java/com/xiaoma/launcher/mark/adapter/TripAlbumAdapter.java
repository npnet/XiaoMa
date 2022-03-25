package com.xiaoma.launcher.mark.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.LauncherUtils;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.launcher.mark.ui.fragment.TripAlbumFragment;
import com.xiaoma.launcher.mark.ui.fragment.TripDateilsFragment;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.network.utils.HttpUtils;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.StringUtil;

public class TripAlbumAdapter extends XMBaseAbstractBQAdapter<MarkPhotoBean, BaseViewHolder> {

    public boolean editType = false;
    public MarkMainActivity mMarkMainActivity;
    public TripAlbumFragment mTripAlbumFragment;
    public TripAlbumAdapter(TripAlbumFragment tripAlbumFragment, MarkMainActivity markMainActivity) {
        super(R.layout.trip_album_item);
        mMarkMainActivity = markMainActivity;
        mTripAlbumFragment = tripAlbumFragment;
    }

    public void setEdit(boolean type){
        editType = type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MarkPhotoBean item) {
        TextView address = helper.getView(R.id.address);
        TextView time = helper.getView(R.id.time);
        ImageView editImg = helper.getView(R.id.edit_img);
        ImageView photoImg = helper.getView(R.id.photo_img);
        ImageLoader.with(mContext).load(item.getPhotoPath()).into(photoImg);
        if (editType){
            editImg.setVisibility(View.VISIBLE);
        }else {
            editImg.setVisibility(View.INVISIBLE);
        }

        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.MARK_RECORD_PHOTO_ITEM_DELETE)
            public void onClick(View v) {
                showClearDialog(item);
            }
        });
        if (StringUtil.isNotEmpty(item.getLocation())){
            address.setText(item.getLocation());
        }else {
            address.setText(mContext.getString(R.string.not_nvi_info));
        }
        if (ConvertUtils.stringToInt(item.getMonth())<10){
            time.setText(item.getYear()+".0"+item.getMonth()+"."+item.getDay());
        }else {
            time.setText(item.getYear()+"."+item.getMonth()+"."+item.getDay());
        }

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripDateilsFragment tripDateilsFragment  = TripDateilsFragment.newInstance(null,mTripAlbumFragment,mMarkMainActivity);
                mMarkMainActivity.setMarkPhotoBean(item);
                mMarkMainActivity.addBackStackFragment(tripDateilsFragment,tripDateilsFragment.getTAG());
            }
        });


    }
    private void showClearDialog(final MarkPhotoBean item) {
        ConfirmDialog dialog = new ConfirmDialog((FragmentActivity) mContext);
        dialog.setContent(mContext.getString(R.string.mark_clean_message))
                .setPositiveButton(mContext.getString(R.string.mark_clean_ok),new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.MARK_RECORD_PHOTO_ITEM_DELETE_SURE)
                    public void onClick(View v) {
                        if (StringUtil.isNotEmpty(item.getPhotoPath())){
                            HttpUtils.deleteFile(item.getPhotoPath());
                        }
                        LauncherUtils.getDBManager().delete(item);
                        mTripAlbumFragment.adapterDeleteListener();
                        XMToast.showToast(mContext,R.string.trip_date_delete);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.merk_clean_no),new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.MARK_RECORD_PHOTO_ITEM_DELETE_CANCEL)
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.MARK_RECORD_PHOTO_ITEM,getData().get(position).getMarkId());
    }
}
