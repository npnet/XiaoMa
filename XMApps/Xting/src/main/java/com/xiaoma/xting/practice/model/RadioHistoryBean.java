package com.xiaoma.xting.practice.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xting.practice.model
 *  @file_name:      RadioHistoryBean
 *  @author:         Rookie
 *  @create_time:    2019/7/8 17:05
 *  @description：   TODO             */

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("xiaoma_history_radio")
public class RadioHistoryBean {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    long DBid;

    private String name;

    public RadioHistoryBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
