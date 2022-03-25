package com.xiaoma.launcher.travel.hotel.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.adapter
 *  @file_name:      RoomUserAdapter
 *  @author:         Rookie
 *  @create_time:    2019/1/15 15:25
 *  @description：   TODO             */

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.adapter.XMBaseAbstractLvGvAdapter;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.vh.XMLvViewHolder;
import com.xiaoma.utils.StringUtil;

import java.util.List;

public class RoomUserAdapter extends XMBaseAbstractLvGvAdapter<String> {


    public interface OnItemEdit {
        void itemEdit(String name, int position);
    }

    private OnItemEdit mOnItemEdit;

    public void setOnItemEdit(OnItemEdit onItemEdit) {
        mOnItemEdit = onItemEdit;
    }

    public RoomUserAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(XMLvViewHolder viewHolder, final String name, final int position) {

        viewHolder.setText(R.id.tv_user_name, name);
        viewHolder.setText(R.id.tv_room_no, "房间 " + (position + 1));
        //编辑
        viewHolder.setOnClickListener(R.id.iv_edit, new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.HOTEL_HOME_EDIT)
            @ResId(R.id.iv_edit)
            public void onClick(View v) {
                if (mOnItemEdit != null) {
                    mOnItemEdit.itemEdit(name, position);
                }
            }
        });
        //删除
        viewHolder.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.HOTEL_HOME_DELETE)
            @ResId(R.id.iv_delete)
            public void onClick(View v) {
                if (mDatas.size() <= 1) {
                    showDeleteDialog(String.format(mContext.getString(R.string.travel_message_room_hotel)), null);
                } else {
                    showDeleteDialog(String.format(mContext.getString(R.string.travel_message_room_delete_hotel), position + 1), name);
                }
            }
        });

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent();
    }

    private void showDeleteDialog(String message, final String room) {
        String sureStr;
        boolean cancelVisibility;
        if (StringUtil.isEmpty(room)) {
            cancelVisibility = false;
            sureStr = mContext.getString(R.string.travel_left_btn_i_know);
        } else {
            cancelVisibility = true;
            sureStr = mContext.getString(R.string.travel_left_btn_delete);
        }
        ConfirmDialog dialog = new ConfirmDialog((FragmentActivity) mContext);
        dialog.setContent(message)
                .setPositiveButton(sureStr, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (!StringUtil.isEmpty(room)) {
                            mDatas.remove(room);
                            notifyDataSetChanged();
                        }
                    }
                });
               if(cancelVisibility){
                   dialog.setNegativeButton(mContext.getString(R.string.travel_right_btn), new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.dismiss();
                       }
                   });
               }else{
                   dialog.setNegativeButtonVisibility(false);
               }
               dialog.show();

//        View view = View.inflate(mContext, R.layout.dialog_travel, null);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        TextView dialogMessage = view.findViewById(R.id.dialog_message);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
//        if (StringUtil.isEmpty(room)) {
//            cancelBtn.setVisibility(View.GONE);
//            sureBtn.setText(mContext.getString(R.string.travel_left_btn_i_know));
//        } else {
//            cancelBtn.setVisibility(View.VISIBLE);
//            sureBtn.setText(mContext.getString(R.string.travel_left_btn_delete));
//            cancelBtn.setText(mContext.getString(R.string.travel_right_btn));
//        }
//        dialogTitle.setText(mContext.getString(R.string.travel_tip_str));
//        dialogMessage.setText(message);
//
//        final XmDialog builder = new XmDialog.Builder((FragmentActivity) mContext)
//                .setView(view)
//                .setWidth(mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_width))
//                .setHeight(mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_height_320))
//                .create();
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @NormalOnClick()
//            public void onClick(View v) {
//                builder.dismiss();
//                if (!StringUtil.isEmpty(room)) {
//                    mDatas.remove(room);
//                    notifyDataSetChanged();
//                }
//
//
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }
}
