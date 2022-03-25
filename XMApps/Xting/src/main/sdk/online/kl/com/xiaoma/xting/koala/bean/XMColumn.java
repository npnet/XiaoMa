package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.column.Column;
import com.kaolafm.opensdk.api.operation.model.column.ColumnMember;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/16
 */
public class XMColumn extends XMColumnGrp<Column> {

    public XMColumn(Column column) {
        super(column);
    }

    public int getForwardToMore() {
        return getSDKBean().getForwardToMore();
    }

    public ColumnMember getMoreColumnMember() {
        return getSDKBean().getMoreColumnMember();
    }

    public List<? extends XMColumnMember> getColumnMembers() {
        List<? extends ColumnMember> columnMembers = getSDKBean().getColumnMembers();
        if (columnMembers != null && !columnMembers.isEmpty()) {
            List<XMColumnMember> list = new ArrayList<>(columnMembers.size());
            for (ColumnMember columnMember : columnMembers) {
                if (columnMember == null) {
                    continue;
                }
                list.add(new XMColumnMember(columnMember));
            }

            return list;
        }
        return null;
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
