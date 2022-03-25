package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.column.LiveProgramDetailColumnMember;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMLiveProgramDetailColumnMember extends XMColumnMember<LiveProgramDetailColumnMember> {

    public XMLiveProgramDetailColumnMember(LiveProgramDetailColumnMember liveProgramDetailColumnMember) {
        super(liveProgramDetailColumnMember);
    }

    public long getLiveProgramId() {
        return getSDKBean().getLiveProgramId();
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}
