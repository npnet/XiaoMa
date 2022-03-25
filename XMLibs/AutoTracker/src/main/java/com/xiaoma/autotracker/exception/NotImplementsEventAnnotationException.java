package com.xiaoma.autotracker.exception;

/**
 * Created by Thomas on 2018/12/6 0006
 * no implements annotation event exception
 */

public class NotImplementsEventAnnotationException extends Exception {

    public NotImplementsEventAnnotationException(String error) {
        super(error);
    }

    public NotImplementsEventAnnotationException(Throwable throwable) {
        super(throwable);
    }

}
