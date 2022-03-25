package com.xiaoma.travel.view.citypick.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.travel.R;

/**
 * @author wutao
 * @date 2018/11/6
 */
public class CharacterHolder extends RecyclerView.ViewHolder {

    public TextView mCharater;

    public CharacterHolder(View itemView) {
        super(itemView);
        mCharater = (TextView) itemView.findViewById(R.id.character);
    }
}
