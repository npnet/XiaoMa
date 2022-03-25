package com.limpidj.android.anno;

import java.lang.annotation.Annotation;

/**
 */
public interface AnnotationMixin {
    <T extends Annotation> T getAnnotation(Class<T> clazz);
}
