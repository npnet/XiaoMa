package com.xiaoma.vr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/5/13 19:38
 * Desc:
 */
public class ContactList implements Cloneable {

    public List<Contact> dictcontant = new ArrayList<>();
    private String dictname = "contact";

    public static class Contact implements Cloneable {
        private String id = "";
        private String name;
        private String phoneNumber = "";
        private String type = "";

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
            ContactList c = (ContactList) super.clone();
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