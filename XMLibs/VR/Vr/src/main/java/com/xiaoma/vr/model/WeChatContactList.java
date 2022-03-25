package com.xiaoma.vr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/5/30 17:58
 * Desc:
 */
public class WeChatContactList implements Cloneable {

    public List<Contact> dictcontant = new ArrayList<>();
    private String dictname = "wechat";

    public static class Contact implements Cloneable {
        private String id = "";
        private String name;
        private String phoneNumber = "";

        public Contact(String name) {
            this.name = name;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                // this shouldn't happen, since we are Cloneable
                throw new InternalError(e);
            }
        }
    }

    @Override
    public Object clone() {
        try {
            WeChatContactList c = (WeChatContactList) super.clone();
            c.dictcontant = new ArrayList<>();
            for (int i = 0; i < dictcontant.size(); i++) {
                c.dictcontant.add(dictcontant.get(i) != null ? (Contact) dictcontant.get(i).clone() : null);
            }
            return c;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

}