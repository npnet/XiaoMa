package com.xiaoma.assistant.model;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/16
 * Desc：
 */
public class Serial {
    String type;
    String ref;
    String direct;
    int offset;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isSelectType(){
        return "SPOT".equalsIgnoreCase(type);
    }

    public boolean isStartByZero(){
        return "ZERO".equalsIgnoreCase(ref);
    }

    public boolean isStartByMAX(){
        return "MAX".equalsIgnoreCase(ref);
    }
}
