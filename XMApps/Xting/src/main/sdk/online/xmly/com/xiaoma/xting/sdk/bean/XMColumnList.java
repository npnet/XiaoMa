package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.column.Column;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMColumnList extends XMBean<ColumnList> {
    public XMColumnList(ColumnList columnList) {
        super(columnList);
    }

    public int getTotalPage() {
        return getSDKBean().getTotalPage();
    }

    public void setTotalPage(int totalPage) {
        getSDKBean().setTotalPage(totalPage);
    }

    public int getTotalCount() {
        return getSDKBean().getTotalCount();
    }

    public void setTotalCount(int totalCount) {
        getSDKBean().setTotalCount(totalCount);
    }

    public int getCurrentPage() {
        return getSDKBean().getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        getSDKBean().setCurrentPage(currentPage);
    }

    public List<XMColumn> getColumns() {
        List<Column> columns = getSDKBean().getColumns();
        List<XMColumn> xmColumnsnew = new ArrayList<>();
        if (columns != null && !columns.isEmpty()) {
            for (Column column : columns) {
                if (column == null) {
                    continue;
                }
                xmColumnsnew.add(new XMColumn(column));
            }
        }
        return xmColumnsnew;
    }

    public void setColumns(List<XMColumn> xmColumns) {
        if (xmColumns == null) {
            getSDKBean().setColumns(null);
            return;
        }
        List<Column> columns = new ArrayList<>();
        for (XMColumn xmColumn : xmColumns) {
            if (xmColumn == null) {
                continue;
            }
            columns.add(xmColumn.getSDKBean());
        }
        getSDKBean().setColumns(columns);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
