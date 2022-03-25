package com.xiaoma.autotracker.handle;

import android.view.View;

import com.xiaoma.autotracker.db.AutoTrackDBManager;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.AdapterViewItemTrackProperties;
import com.xiaoma.utils.log.KLog;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by Thomas on 2018/12/6 0006
 * RecycleView XM item点击事件切点处理
 */
public class OnItemClickRvHandle extends BaseHandle {

    //private AdapterViewItemTrackProperties adapter;
    //private View view;
    //private Integer position;

    @Override
    public void handleProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        Object adapter = proceedingJoinPoint.getArgs()[0];
        AdapterViewItemTrackProperties clickAdapter;
        if (adapter instanceof AdapterViewItemTrackProperties) {
            clickAdapter = (AdapterViewItemTrackProperties) adapter;
        } else {
            notImplementsAdapterViewItemTrackPropertiesError(proceedingJoinPoint);
            return;
        }
        Object view = proceedingJoinPoint.getArgs()[1];
        View clickView;
        if (view instanceof View) {
            clickView = (View) view;
        } else {
            doException("get proceedingJoinPoint.getArgs()[1] is no instanceof View");
            return;
        }
        Object position = proceedingJoinPoint.getArgs()[3];
        Integer clickPosition;
        if (position instanceof Integer) {
            clickPosition = (Integer) position;
            if (clickPosition < 0) {
                doException("get proceedingJoinPoint.getArgs()[3] is instanceof Integer, but this.position < 0");
                return;
            }
        } else {
            doException("get proceedingJoinPoint.getArgs()[3] is no instanceof Integer");
            return;
        }
        if (!checkPageDescFromClickView(clickView)) {
            KLog.e(TAG, "not implements annotation for component...");
            if (!checkPageDescribeIsLegal(proceedingJoinPoint)) {
                return;
            }
        }
        ItemEvent itemEvent = clickAdapter.returnPositionEventMsg(clickPosition);
        KLog.d(TAG, "OnItemClickRvHandle value id:" + itemEvent.id + " name:" + itemEvent.name
                + ", pageUIPath:" + pageUIPath + ", pageUIPathDesc:" + pageUIPathDesc);

        AutoTrackDBManager.getInstance().saveOnClickEvent(itemEvent.name, itemEvent.id, pageUIPath, pageUIPathDesc);
    }

}
