package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.column.Column;
import com.kaolafm.opensdk.api.operation.model.column.ColumnMember;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;


/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMKoalaColumn extends XMBean<Column> {

    public XMKoalaColumn(Column column) {
        super(column);
    }

    public int getForwardToMore() {
        return getSDKBean().getForwardToMore();
    }

    public ColumnMember getMoreColumnMember() {
        return getSDKBean().getMoreColumnMember();
    }

    public List<XMColumnMember> getColumnMembers() {
        List<? extends ColumnMember> columnMembers = getSDKBean().getColumnMembers();
        if (columnMembers != null && !columnMembers.isEmpty()) {
            List<XMColumnMember> columnMemberList = new ArrayList<>(columnMembers.size());
            for (ColumnMember columnMember : columnMembers) {
                columnMemberList.add(new XMColumnMember(columnMember));
            }
            return columnMemberList;
        }

        return null;
    }
}
