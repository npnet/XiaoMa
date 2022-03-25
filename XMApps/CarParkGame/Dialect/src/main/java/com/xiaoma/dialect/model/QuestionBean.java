package com.xiaoma.dialect.model;

import java.util.List;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.dialect.model
 *  @文件名:   QuestionBean
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/7 15:00
 *  @描述：    TODO
 */
public class QuestionBean {

    public QuestionBean(List<String> chooseList, int rightIndex) {
        this.chooseList = chooseList;
        this.rightIndex = rightIndex;
    }

    public List<String> chooseList;
    public int rightIndex;
}
