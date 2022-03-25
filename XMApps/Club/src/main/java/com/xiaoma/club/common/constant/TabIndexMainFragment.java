package com.xiaoma.club.common.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: loren
 * Date: 2019/2/19 0019
 */
@Retention(RetentionPolicy.SOURCE)
public @interface TabIndexMainFragment {
    String TAB_EVENT_TAG = "tab_event_tag";
    int TAB_DISCOVER = 0;
    int TAB_MSG = 1;
    int TAB_CONTACT = 2;
}
