package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.column.RadioDetailColumnMember;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/16
 */
public class XMRadioDetailColumnMember extends XMColumnMember<RadioDetailColumnMember> {
    public XMRadioDetailColumnMember(RadioDetailColumnMember radioDetailColumnMember) {
        super(radioDetailColumnMember);
    }

    public long getRadioId() {
        return getSDKBean().getRadioId();
    }

    public int getPlayTimes() {
        return getSDKBean().getPlayTimes();
    }
}
