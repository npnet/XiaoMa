package com.limpidj;

import com.limpidj.android.anno.AnnotationMixin;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareMixin;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import static com.limpidj.AllBuilderData.getOrCreateAllBuilderData;
import static com.limpidj.AnnotationUtils.InstanceHolder.annotationUtils;
import static com.limpidj.ContextUtils.getContext;
import static com.limpidj.NameUtils.DEFAULT_PARAM_NAME_0;
import static com.limpidj.NameUtils.THIS_NAME;

/**
 */
public class AnnotationBuilder {

    static final String TYPE_NAME = "typeName";

    private ExecutableElement getAnnotationExecutableElement = (ExecutableElement) getContext().elementUtils.getTypeElement(AnnotationMixin.class.getCanonicalName()).getEnclosedElements().get(0);

    public void start() {

        for (Map.Entry<Object, BuilderData> entry : getOrCreateAllBuilderData().entrySet()) {
            Object key = entry.getKey();
            if (key instanceof TypeElement) {
                eachTypeElement((TypeElement) key, entry.getValue(), true);
            }
        }
    }

    void eachTypeElement(TypeElement typeElement, BuilderData data, boolean over) {

        ArrayList<AnnotationMirror> annotationMirrors = (ArrayList<AnnotationMirror>) data.get(AnnotationExtractor.ANNOTATION_LIST);

        if (annotationMirrors == null) {
            return;
        }

        TypeSpec.Builder tb = getOrCreateType(data, typeElement);

        // 构建静态方法 getAnnotation()
        TypeVariableName t = TypeVariableName.get("T", Annotation.class);
        MethodSpec.Builder getAnnotationMethodWithStatic = MethodSpec.methodBuilder("getAnnotation")//
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//
                .addTypeVariable(t)//
                .returns(t)//
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), t), DEFAULT_PARAM_NAME_0)//
                .beginControlFlow("switch ($N.getName().hashCode())", DEFAULT_PARAM_NAME_0);
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            addFieldAndCase(tb, getAnnotationMethodWithStatic, annotationMirror);
        }
        getAnnotationMethodWithStatic.addCode("default:").addStatement("return null")//
                .endControlFlow();

        // 构建方法 createAnnotationMixin()
        MethodSpec.Builder createAnnotationMixinMethod = MethodSpec.methodBuilder("createAnnotationMixin").addAnnotation(//
                AnnotationSpec.builder(DeclareMixin.class)//
                        .addMember("value", "$S", typeElement.getQualifiedName())//
                        .build()//
        )//
                .returns(AnnotationMixin.class)//
                .addParameter(//
                        ParameterSpec.builder(ClassName.get(typeElement), THIS_NAME)//
                                .addModifiers(Modifier.FINAL)//
                                .build()//
                );

        // 构建方法 getAnnotation()
        MethodSpec.Builder getAnnotationMethod = MethodSpec.overriding(getAnnotationExecutableElement)//
                .addStatement("return $T.getAnnotation($N)", data.get(TYPE_NAME), DEFAULT_PARAM_NAME_0);

        createAnnotationMixinMethod.addStatement("return $L",//
                TypeSpec.anonymousClassBuilder("")//
                        .superclass(AnnotationMixin.class)//
                        .addMethod(getAnnotationMethod.build())//
                        .build()//
        );

        tb.addMethod(getAnnotationMethodWithStatic.build());
        tb.addMethod(createAnnotationMixinMethod.build());

        if (over) {
            saveToDisk(data);
        }
    }

    /**
     * 为每个 annotation 添加 field 和 case 代码
     *
     * @param tb
     * @param mb
     * @param annotationMirror
     */
    private void addFieldAndCase(TypeSpec.Builder tb, MethodSpec.Builder mb, AnnotationMirror annotationMirror) {

        DeclaredType annotationType = annotationMirror.getAnnotationType();

        FieldSpec field = FieldSpec.builder(TypeName.get(annotationType), NameUtils.toVariableName(annotationType), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)//
                .initializer("$L", annotationUtils.toTypeSpec(annotationMirror))//
                .build();
        tb.addField(field);


        String hashName = NameUtils.toTypeName(annotationType);
        mb.addCode("// $N\n", hashName)//
                .addCode("case $L:", hashName.hashCode()).addStatement("return (T) $N", field);
    }

    /**
     * 构建或找出已构建的类
     *
     * @param data
     * @param host
     * @return
     */
    static TypeSpec.Builder getOrCreateType(BuilderData data, TypeElement host) {

        TypeSpec.Builder aspectBuilder = data.get(TypeSpec.Builder.class);
        if (aspectBuilder == null) {
            // 构建类
            String typeName = host.getSimpleName() + "Aspect";
            aspectBuilder = TypeSpec.classBuilder(typeName)//
                    .addModifiers(Modifier.PUBLIC)//
                    .addAnnotation(Aspect.class)//
            ;

            data.add(TYPE_NAME, ClassName.get(host).peerClass(typeName));
            data.add(aspectBuilder);
        }
        return aspectBuilder;
    }

    /**
     * 保存到磁盘（自保护避免多次保存）
     *
     * @param data
     */
    static void saveToDisk(BuilderData data) {

        JavaFile javaFile = data.get(JavaFile.class);
        if (javaFile != null) {
            return;
        }

        TypeSpec.Builder tb = data.get(TypeSpec.Builder.class);

        // 构建文件
        javaFile = JavaFile.builder(getContext().elementUtils.getPackageOf(data.get(TypeElement.class)).getQualifiedName().toString(), tb.build()).build();

        // 写入磁盘
        try {
            javaFile.writeTo(getContext().filer);
            data.add(javaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
