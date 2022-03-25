package com.xiaoma.autotracker.handle;

import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaoma.autotracker.db.AutoTrackDBManager;
import com.xiaoma.utils.log.KLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by Thomas on 2018/12/6 0006
 * RadioGroup 点击事件切点处理
 */
public class OnRadioGroupClickHandle extends BaseHandle {

    //private RadioGroup mRadioGroup;
    //private Integer mCheckedId;

    @Override
    public void handleProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        RadioGroup clickRadioGroup;
        Object mRadioGroup = proceedingJoinPoint.getArgs()[0];
        if (mRadioGroup instanceof RadioGroup) {
            clickRadioGroup = (RadioGroup) mRadioGroup;
        } else {
            doException("get proceedingJoinPoint.getArgs()[1] is no instanceof View");
            return;
        }
        Object mCheckedId = proceedingJoinPoint.getArgs()[1];
        Integer clickCheckedId;
        if (mCheckedId instanceof Integer) {
            clickCheckedId = (Integer) mCheckedId;
        } else {
            doException("get proceedingJoinPoint.getArgs()[1] is no instanceof View");
            return;
        }
        if (!checkPageDescFromClickView(clickRadioGroup)) {
            KLog.e(TAG, "not implements annotation for component...");
            if (!checkPageDescribeIsLegal(proceedingJoinPoint)) {
                return;
            }
        }
        RadioButton radioButton = ((RadioButton) clickRadioGroup.findViewById(clickCheckedId));
        if (radioButton == null || TextUtils.isEmpty(radioButton.getText())) {
            doException("Your RadioButton must set text");
            return;
        }
        KLog.d(TAG, "text: " + radioButton.getText() + " -- " + pageUIPath + " -- " + pageUIPathDesc);
        AutoTrackDBManager.getInstance().saveOnClickEvent(radioButton.getText().toString(), null, pageUIPath, pageUIPathDesc);
    }


}
