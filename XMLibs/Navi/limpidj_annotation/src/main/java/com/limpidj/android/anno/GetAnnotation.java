package com.limpidj.android.anno;

import java.lang.annotation.Annotation;

/**
 */
public interface GetAnnotation {
    <T extends Annotation> T getAnnotation(Class<T> c);
}
