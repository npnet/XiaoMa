package com.xiaoma.music.search.model;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

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
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;
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
}
