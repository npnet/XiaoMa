package com.xiaoma.music.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.music.practice
 *  @file_name:      MusicHistoryBean
 *  @author:         Rookie
 *  @create_time:    2019/7/4 19:19
 *  @description：   TODO             */

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("xiaoma_history_music")
public class MusicHistoryBean {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    long dBid;

    private String name;

    public MusicHistoryBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
