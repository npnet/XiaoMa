package com.xiaoma.personal.taskcenter.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.NotFullShowTipsAdapter;
import com.xiaoma.personal.taskcenter.constract.TaskType;
import com.xiaoma.personal.taskcenter.model.TaskNote;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kaka
 * on 19-2-28 上午10:49
 * <p>
 * desc: #a
 * </p>
 */
public class TaskRecordAdapter extends NotFullShowTipsAdapter<TaskNote, BaseViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());

    public TaskRecordAdapter() {
        super(R.layout.item_task_draw);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskNote item) {

        helper.setText(R.id.tvTitle, item.action)
                .setText(R.id.tvTime, TimeUtils.millis2String(item.createDate, dateFormat))
                .setText(R.id.tvScore, StringUtil.getSignedNumber(item.score));
        int icon;
        switch (item.scoreType) {
            case TaskType.ACTIVITY:
                icon = R.drawable.task_activity;
                break;
            case TaskType.GROW_UP:
                icon = R.drawable.task_grow_up;
                break;
            case TaskType.DAILY:
            default:
                icon = R.drawable.task_every_day;
                break;
        }
        ImageLoader.with(mContext).load(icon).into((ImageView) helper.getView(R.id.ivIcon));
    }
}
