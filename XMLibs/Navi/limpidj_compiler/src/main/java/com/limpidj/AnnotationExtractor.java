package com.limpidj;

import java.util.ArrayList;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.limpidj.AllBuilderData.getOrCreateAllBuilderData;
import static com.limpidj.ContextUtils.getContext;

/**
 */
public class AnnotationExtractor {
    static final String ANNOTATION_LIST = "ANNOTATION_LIST";

    public void start(RoundEnvironment roundEnv) {
//        TypeElement typeElement = getContext().elementUtils.getTypeElement("com.limpidj.android.anno.AnnotationPreload");
//        getContext().messager.printMessage(Diagnostic.Kind.NOTE,"typeElement=" + typeElement);
//        TypeElement www = getContext().elementUtils.getTypeElement("com.mapbar.android.mapbarmap.core.inject.anno.PageSetting");
//        getContext().messager.printMessage(Diagnostic.Kind.NOTE,"annotationElement.getAnnotationMirrors()=" + www.getAnnotationMirrors());
//        for (Element ae : roundEnv.getElementsAnnotatedWith(typeElement)) {
//            getContext().messager.printMessage(Diagnostic.Kind.NOTE,"ae=" + ae);
//            TypeElement annotationElement = (TypeElement) ae;
//            processAnnotation(roundEnv,annotationElement);
//        }
        processAnnotation(roundEnv, getContext().elementUtils.getTypeElement("com.mapbar.android.mapbarmap.core.inject.anno.PageSetting"));
        processAnnotation(roundEnv, getContext().elementUtils.getTypeElement("com.mapbar.android.mapbarmap.core.inject.anno.ViewerSetting"));
        processAnnotation(roundEnv, getContext().elementUtils.getTypeElement("com.mapbar.android.mapbarmap.core.scene.SceneSetting"));
    }

    private void processAnnotation(RoundEnvironment roundEnv, TypeElement annotationElement) {
        for (Element te : roundEnv.getElementsAnnotatedWith(annotationElement)) {
            BuilderData data = getOrCreateAllBuilderData().getOrCreate(te);
            ArrayList<AnnotationMirror> list = (ArrayList<AnnotationMirror>) data.get(ANNOTATION_LIST);
            if (list == null) {
                list = new ArrayList<>();
                data.add(ANNOTATION_LIST, list);
            }
            list.add(AnnotationUtils.getAnnotationMirror((TypeElement) te, annotationElement));
        }
    }
}
