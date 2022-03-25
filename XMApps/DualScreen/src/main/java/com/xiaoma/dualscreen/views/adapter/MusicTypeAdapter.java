//package com.xiaoma.dualscreen.views.adapter;
//
//import android.content.Context;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.xiaoma.dualscreen.R;
//import com.xiaoma.dualscreen.model.MusicTypeModel;
//import com.xiaoma.model.ItemEvent;
//import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
//import com.xiaoma.ui.vh.XMViewHolder;
//import com.xiaoma.utils.log.KLog;
//
//import java.util.List;
//
//public class MusicTypeAdapter extends XMBaseAbstractRyAdapter<MusicTypeModel> {
//
//    private int mSelected = -1;
//
//    public MusicTypeAdapter(Context context, List<MusicTypeModel> datas, int layoutId) {
//        super(context, datas, layoutId);
//    }
//
//    @Override
//    protected void convert(XMViewHolder holder, MusicTypeModel musicTypeModel, int position) {
//        TextView tvName = holder.getView(R.id.tv_music_type_name);
//        ImageView ivMusicIcon = holder.getView(R.id.iv_music_icon);
//        holder.getConvertView().setTag(musicTypeModel);
//        KLog.d(holder.getConvertView().getTag() + "");
//        boolean isNormalIcon = position == mSelected;
//        switch (musicTypeModel.getType()){
//            case 0:
//                if (isNormalIcon){
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_usb);
//                }else{
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_usb_small);
//                }
//                tvName.setText("USB音乐");
//                break;
//            case 1:
//                if (isNormalIcon){
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_blue);
//                }else {
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_blue_small);
//                }
//                tvName.setText("蓝牙音乐");
//                break;
//            case 2:
//                if (isNormalIcon){
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_music);
//                }else{
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_music_small);
//                }
//                tvName.setText("在线音乐");
//                break;
//            case 3:
//                if (isNormalIcon){
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_fm);
//                }else{
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_fm_small);
//                }
//                tvName.setText("FM电台");
//                break;
//            case 4:
//                if (isNormalIcon){
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_am);
//                }else {
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_am_small);
//                }
//                tvName.setText("AM电台");
//                break;
//            case 5:
//                if (isNormalIcon){
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_radio);
//                }else {
//                    ivMusicIcon.setBackgroundResource(R.drawable.bg_radio_small);
//                }
//                tvName.setText("在线电台");
//                break;
//        }
//
//    }
//
//    @Override
//    public ItemEvent returnPositionEventMsg(int position) {
//        return new ItemEvent(getDatas().get(position).toString(), mDatas.get(position).toString());
//    }
//
//    public void setSelected(int item){
//        this.mSelected = item;
//        notifyDataSetChanged();
//    }
//
//}
