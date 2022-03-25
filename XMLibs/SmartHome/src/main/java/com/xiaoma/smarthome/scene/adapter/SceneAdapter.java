package com.xiaoma.smarthome.scene.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.scene.adapter
 *  @file_name:      SceneAdapter
 *  @author:         Rookie
 *  @create_time:    2019/1/24 15:34
 *  @description：   场景adapter             */

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.EventConstants;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

public class SceneAdapter extends XMBaseAbstractBQAdapter<SceneBean, BaseViewHolder> {

    public SceneAdapter(@Nullable List<SceneBean> data) {
        super(R.layout.item_scene, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneBean item) {
        String sceneName = item.getSceneName();
        helper.setText(R.id.tv_name, sceneName);
        ImageView ivLogo = helper.getView(R.id.iv_scene_logo);
        if (sceneName.contains("回家")) {
            ivLogo.setImageResource(R.drawable.icon_backhome);
            ivLogo.setVisibility(View.VISIBLE);
            ImageLoader.with(mContext).load(R.drawable.pic_gohome).into((ImageView) helper.getView(R.id.iv_scene));
        } else if (sceneName.contains("离家")) {
            ivLogo.setImageResource(R.drawable.icon_awayfromhome);
            ivLogo.setVisibility(View.VISIBLE);
            ImageLoader.with(mContext).load(R.drawable.pic_goout).into((ImageView) helper.getView(R.id.iv_scene));
        } else {
            ivLogo.setVisibility(View.INVISIBLE);
            ImageLoader.with(mContext).load(R.drawable.icon_scene_default).placeholder(R.drawable.bg_scene).error(R.drawable.bg_scene).into((ImageView) helper.getView(R.id.iv_scene));
        }

        helper.addOnClickListener(R.id.btn_excute);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        SceneBean sceneBean = getData().get(position);
        return new ItemEvent(EventConstants.NormalClick.selectScene,sceneBean.getSceneName());
    }
}
