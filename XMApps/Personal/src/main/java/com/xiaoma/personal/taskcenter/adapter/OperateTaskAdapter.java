package com.xiaoma.personal.taskcenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.NotFullShowTipsAdapter;
import com.xiaoma.personal.taskcenter.constract.TaskStateConstract;
import com.xiaoma.personal.taskcenter.constract.TaskType;
import com.xiaoma.personal.taskcenter.model.OperateTask;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/4
 */
public class OperateTaskAdapter extends NotFullShowTipsAdapter<OperateTask, BaseViewHolder> {

    private static final int STATE_TO_FINISH = 0;
    private String[] mTaskStates;
    private Context mContext;
    @TaskType
    private int mTaskType;

    public OperateTaskAdapter(Context context, int type) {
        super(R.layout.item_operate_task);
        this.mContext = context;
        mTaskType = type;
        mTaskStates = context.getResources().getStringArray(R.array.TaskState);
    }

    @Override
    protected void convert(BaseViewHolder helper, OperateTask item) {
        int icon;
        switch (mTaskType) {
            case TaskType.GROW_UP:
                icon = R.drawable.task_grow_up;
                break;
            case TaskType.ACTIVITY:
                icon = R.drawable.task_activity;
                break;
            case TaskType.DAILY:
            default:
                icon = R.drawable.task_every_day;
                break;
        }

        ImageLoader.with(mContext)
                .load(icon)
                .into((ImageView) helper.getView(R.id.ivIcon));
        helper.setText(R.id.tvDescription, item.description);
        Button operateBtn = helper.getView(R.id.btnOperate);
        if ("去完成".equals(item.status.name)) {
            operateBtn.setText(R.string.go_to_finish);
        } else if ("已完成".equals(item.status.name)) {
            operateBtn.setText(R.string.has_finish);
        } else {
            operateBtn.setText(item.status.name);
        }
        operateBtn.setEnabled(item.status.code == STATE_TO_FINISH);
        helper.addOnClickListener(R.id.btnOperate);
        if (item.status.code == STATE_TO_FINISH) {
            if (TextUtils.isEmpty(item.getJumpUri())) {
                helper.setGone(R.id.btnOperate, false);
            } else {
                helper.setGone(R.id.btnOperate, true);
            }
        }
    }

    private void setTaskState(Button operateBtn, @TaskStateConstract.TaskState int state) {
//        if (state == TaskStateConstract.TaskState.DEFAULT)
//            throw new TaskStateConstract.TaskStateException();
        operateBtn.setText(mTaskStates[state]);
        operateBtn.setEnabled(TaskStateConstract.checkStateEnable(state));
    }

}
