package com.xiaoma.club.discovery.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Author: loren
 * Date: 2018/10/12 0017
 */
@Entity(indices = {@Index("searchContent")})
public class SearchHistoryInfo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String searchContent;

    public SearchHistoryInfo(String searchContent) {
        this.searchContent = searchContent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    @Override
    public String toString() {
        return "SearchHistoryInfo{" +
                "searchContent='" + searchContent + '\'' +
                '}';
    }
}
