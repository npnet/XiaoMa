package com.xiaoma.instruction.adapter;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/01
 *     desc   :
 * </pre>
 */
public class ManualCatalogBean {
    int id;
    int content;

    public ManualCatalogBean(int id,int content){
        this.id=id;
        this.content=content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ManualCatalogBean{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
