package com.limpidj;

import com.squareup.javapoet.ParameterSpec;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

/**
 */
public class NameUtils {

    // 构建 Aspect 系列方法的参数 joinPoint
    static final ParameterSpec joinPoint = ParameterSpec.builder(JoinPoint.class, "joinPoint").build();
    // 构建 Aspect 系列方法的参数 proceedingJoinPoint
    static final ParameterSpec proceedingJoinPoint = ParameterSpec.builder(ProceedingJoinPoint.class, "proceedingJoinPoint").build();

    static final String THIS_NAME = "jpThis";
    static final String DEFAULT_PARAM_NAME_0 = "arg0";
    static final String DEFAULT_PARAM_NAME_1 = "arg1";

    private static final StringBuilder stringBuilder = new StringBuilder();

    public static String toVariableName(DeclaredType typeElement) {
        return toVariableName((TypeElement) typeElement.asElement());
    }

    public static String toVariableName(TypeElement typeElement) {
        return toVariableName(typeElement.getSimpleName().toString());
    }

    public static String toVariableName(String name) {
        name = stringBuilder.append(name.substring(0, 1).toLowerCase()).append(name.substring(1)).toString();
        stringBuilder.setLength(0);
        return name;
    }

    public static String toTypeName(DeclaredType typeElement) {
        return toTypeName((TypeElement) typeElement.asElement());
    }

    public static String toTypeName(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString();
    }

}
