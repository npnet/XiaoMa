package com.xiaoma.launcher.favorites;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/04/15
 *     desc   :
 * </pre>
 */
public class ToMapMessage {

    Object data;
    List list;

    public ToMapMessage(){

    }

    public ToMapMessage(Object data,List list){
        this.data=data;
        this.list=list;
    }

    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "data="+data+" list="+list+" ; ";
    }
}
