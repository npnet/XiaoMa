package com.xiaoma.dialect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaoma.dialect.R;

import java.util.List;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.dialect.adapter
 *  @文件名:   ChooseAnswerAdapter
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/7 15:06
 *  @描述：    TODO
 */
public class ChooseAnswerAdapter extends BaseAdapter {

    private List<String> list;
    private Context context;

    public ChooseAnswerAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvAnswer = convertView.findViewById(R.id.tv_answer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvAnswer.setText(list.get(position));
        return convertView;
    }

    public static class ViewHolder {
        private TextView tvAnswer;
    }
}
