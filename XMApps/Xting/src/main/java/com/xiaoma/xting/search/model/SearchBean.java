package com.xiaoma.xting.search.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.xiaoma.xting.common.XtingUtils;

import java.io.Serializable;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/09
 *     desc   :
 * </pre>
 */
@Table("SearchBean")
public class SearchBean implements Serializable {
    public int id;
    @PrimaryKey(AssignType.BY_MYSELF)
    public String name;

    public SearchBean() {
    }

    public SearchBean(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SearchBean{" +
                "name='" + name + '\'' +
                '}';
    }

    public static void clearAllHistory() {
        XtingUtils.getDBManager().deleteAll(SearchBean.class);
    }

    public static void clearAllHistory(String userId) {
        XtingUtils.getDBManager(userId).deleteAll(SearchBean.class);
    }
}
