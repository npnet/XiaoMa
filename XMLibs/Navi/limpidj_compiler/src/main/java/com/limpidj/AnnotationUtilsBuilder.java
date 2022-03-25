package com.limpidj;

import com.limpidj.android.anno.GetAnnotation;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.limpidj.AllBuilderData.getOrCreateAllBuilderData;
import static com.limpidj.AnnotationBuilder.TYPE_NAME;
import static com.limpidj.AnnotationExtractor.ANNOTATION_LIST;
import static com.limpidj.ContextUtils.getContext;
import static com.limpidj.NameUtils.DEFAULT_PARAM_NAME_0;
import static com.limpidj.NameUtils.DEFAULT_PARAM_NAME_1;

/**
 */
public class AnnotationUtilsBuilder {

    private boolean alreadyCreated = false;

    public void start() {

        if (alreadyCreated) {
            return;
        }

        ClassName annotationUtilsClassName = ClassName.get("com.mapbar.android.mapbarmap.core.util", "AnnotationUtils");
        ClassName annotationUtilsImplClassName = ClassName.get("com.mapbar.android.mapbarmap.core.util", "AnnotationUtilsImpl");
        ClassName getAnnotationClassName = ClassName.get("com.limpidj.android.anno", "GetAnnotation");

        ExecutableElement getAnnotationImplExecutable = (ExecutableElement) getContext().elementUtils.getTypeElement(annotationUtilsImplClassName.toString()).getEnclosedElements().get(0);

        ExecutableElement getAnnotationExecutable = (ExecutableElement) getContext().elementUtils.getTypeElement(getAnnotationClassName.toString()).getEnclosedElements().get(0);

        MethodSpec.Builder moduleRelation = MethodSpec.methodBuilder("moduleRelation").addAnnotation(//
                AnnotationSpec.builder(Before.class)//
                        .addMember("value", "$S",
                                "execution(public static void com.mapbar.android.NaviSupportBefore.aspectInitialzation(..))")//
                        .build()//
        )//
                .addModifiers(Modifier.PUBLIC).addStatement("$T.setAnnotationUtilsImpl(this)", annotationUtilsClassName);

        FieldSpec classGetAnnotationHashMap = FieldSpec.builder(//
                ParameterizedTypeName.get(HashMap.class, Class.class, GetAnnotation.class), //
                "classGetAnnotationHashMap"//
        )//
                .addModifiers(Modifier.PRIVATE)//
                .initializer("new $T<>()", HashMap.class)//
                .build();

        CodeBlock.Builder staticBlock = CodeBlock.builder();

        for (Map.Entry<Object, BuilderData> entry : getOrCreateAllBuilderData().entrySet()) {
            BuilderData data = entry.getValue();
            if (data.get(ANNOTATION_LIST) == null) {
                continue;
            }

            staticBlock.addStatement("classGetAnnotationHashMap.put($T.class, $L)", ClassName.get(data.get(TypeElement.class)),//
                    TypeSpec.anonymousClassBuilder("")//
                            .superclass(GetAnnotation.class)//
                            .addMethod(//
                                    MethodSpec.overriding(getAnnotationExecutable)//
                                            .addStatement("return $T.getAnnotation($N)", data.get(TYPE_NAME), DEFAULT_PARAM_NAME_0)//
                                            .build()//
                            )//
                            .build()//
            );
        }

        MethodSpec.Builder getAnnotation = MethodSpec//
                .overriding(getAnnotationImplExecutable)//
                .addStatement("return classGetAnnotationHashMap.get($N).getAnnotation($N)", DEFAULT_PARAM_NAME_0, DEFAULT_PARAM_NAME_1);

        TypeSpec.Builder tb = TypeSpec.classBuilder("AnnotationUtilsAspect")//
                .addAnnotation(Aspect.class)//
                .addSuperinterface(annotationUtilsImplClassName)//
                .addModifiers(Modifier.PUBLIC)//
                .addField(classGetAnnotationHashMap)//
                .addInitializerBlock(staticBlock.build())//
                .addMethod(moduleRelation.build())//
                .addMethod(getAnnotation.build());

        // 构建文件
        JavaFile javaFile = JavaFile.builder("com.mapbar.android.mapbarmap.core.util", tb.build()).build();

        // 写入磁盘
        try {
            javaFile.writeTo(getContext().filer);
            alreadyCreated = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
