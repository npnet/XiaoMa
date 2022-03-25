package com.xiaoma.dualscreen.model;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;

public class ContactModel {

    private ContactBean contactBean;
    private State state;

    public ContactModel(ContactBean contactBean, State state){
        this.contactBean = contactBean;
        this.state = state;
    }

    public ContactBean getContactBean() {
        return contactBean;
    }

    public void setContactBean(ContactBean contactBean) {
        this.contactBean = contactBean;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
