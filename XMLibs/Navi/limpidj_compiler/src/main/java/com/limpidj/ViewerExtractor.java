package com.limpidj;

import com.limpidj.android.anno.Monitor;
import com.limpidj.android.anno.OnClick;
import com.limpidj.android.anno.ViewInject;
import com.limpidj.android.anno.ViewerInject;
import com.limpidj.android.page.BaseViewerConstant;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.aspectj.lang.annotation.After;

import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.limpidj.AllBuilderData.getOrCreateAllBuilderData;
import static com.limpidj.AnnotationUtils.InstanceHolder.annotationUtils;
import static com.limpidj.ContextUtils.getContext;
import static com.limpidj.NameUtils.DEFAULT_PARAM_NAME_0;
import static com.limpidj.NameUtils.THIS_NAME;

/**
 */
public class ViewerExtractor {
    private static final ClassName APPLICATION_R = ClassName.get("com.mapbar.android.mapbarnavi", "R");
    private static final ClassName viewClassName = ClassName.get("android.view", "View");
    private static final ClassName onClickListenerClassName = ClassName.get("android.view", "View", "OnClickListener");
    private static final ClassName basicManagerClassName = ClassName.get("com.mapbar.android.mapbarmap.core", "BasicManager");
    private static final ClassName eventManagerClassName = ClassName.get("com.mapbar.android.mapbarmap.core", "EventManager");
    private static final ClassName viewFinderClassName = ClassName.get("com.mapbar.android.mapbarmap.core.inject", "ViewFinder");
    private static final ClassName viewerEventReceiverClassName = ClassName.get("com.mapbar.android.mapbarmap.core.listener", "ViewerEventReceiver");
    static final ClassName injectViewListenerClassName = ClassName.get("com.mapbar.android.mapbarmap.core.listener", "InjectViewListener");

    private static final String FINDER_NAME = "finder";

    // ???????????? eventManager
    static final FieldSpec EVENT_MANAGER_FIELD = FieldSpec.builder(eventManagerClassName, "eventManager", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$T.getInstance()", eventManagerClassName).build();
    // ???????????? basicManager
    static final FieldSpec BASIC_MANAGER_FIELD = FieldSpec.builder(basicManagerClassName, "basicManager", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$T.getInstance()", basicManagerClassName).build();

    private InjectViewListenerExecutables injectViewListenerExecutables = new InjectViewListenerExecutables();
    private ExecutableElement onClickListenerExecutable = (ExecutableElement) getContext().elementUtils.getTypeElement(onClickListenerClassName.toString()).getEnclosedElements().get(0);

    private TypeElement viewerSettingTypeElement = getContext().elementUtils.getTypeElement("com.mapbar.android.mapbarmap.core.inject.anno.ViewerSetting");
    private TypeElement monitorTypeElement = getContext().elementUtils.getTypeElement(Monitor.class.getCanonicalName());

    // ??????????????????????????????????????????????????????
    private TypeElement keyHitCache = null;
    private Holder valueHitCache = null;

    public void start(RoundEnvironment roundEnv) {


        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();

            // ?????????????????????????????????
            MethodSpec.Builder mb = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;

            // ???????????? View ??? id
            OnClick annotation = element.getAnnotation(OnClick.class);
            String[] targetIds = annotation.value();
            int[] parentIds = annotation.parentId();

            for (int i = 0; i < targetIds.length; i++) {
                // ??????
                mb.beginControlFlow("")//
                        .addStatement("$T v = $N.findViewById(" + targetIds[i] + ", $L)", viewClassName, FINDER_NAME, i < parentIds.length ? parentIds[i] : 0)//
                        .beginControlFlow("if (v != null)")//
                        .addStatement("v.setOnClickListener($L)",//
                                TypeSpec.anonymousClassBuilder("").superclass(onClickListenerClassName).addMethod(//
                                        MethodSpec.overriding(onClickListenerExecutable).addStatement("$N.$N($N)", THIS_NAME, element.getSimpleName(), DEFAULT_PARAM_NAME_0).build()).build()//
                        )//
                        .endControlFlow()//
                        .endControlFlow();
            }
            // TODO: 2019/1/29 ???apt????????????import??????????????????????????????????????????
            MethodSpec.Builder builder = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;//
            builder.beginControlFlow("$T.class.getName();", APPLICATION_R).endControlFlow();
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(ViewInject.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();

            // ?????????????????????????????????
            MethodSpec.Builder mb = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;

            // ???????????? View ??? id
            ViewInject annotation = element.getAnnotation(ViewInject.class);
//            int targetId = annotation.value();
            int parentId = annotation.parentId();
            String idStr = annotation.id();

            // ??????
            TypeMirror fType = element.asType();
            mb.addStatement("$N.$N = ($T)$N.findViewById(" + idStr + ", $L)", THIS_NAME, element.getSimpleName(), fType, FINDER_NAME, parentId);

            // TODO: 2019/1/29 ???apt????????????import??????????????????????????????????????????
            MethodSpec.Builder builder = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;//
            builder.beginControlFlow("$T.class.getName();", APPLICATION_R).endControlFlow();

        }

        for (Element element : roundEnv.getElementsAnnotatedWith(ViewerInject.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();

            pretreatmentCode(initPretreatmentMethod(typeElement, getOrCreateHolder(typeElement)), BASIC_MANAGER_FIELD, element);

            injectViewToSubViewerCode(initInjectViewListener(getOrCreateHolder(typeElement)).injectViewToSubViewer, element);

            // TODO: 2019/1/29 ???apt????????????import??????????????????????????????????????????
            MethodSpec.Builder builder = initInjectViewListener(getOrCreateHolder(typeElement)).injectViewToSubViewer;//
            builder.beginControlFlow("$T.class.getName();", APPLICATION_R).endControlFlow();

        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Monitor.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            Monitor monitorAnnotation = element.getAnnotation(Monitor.class);

            // ?????????????????????????????????
            MethodSpec.Builder mb = initPretreatmentMethod(typeElement, getOrCreateHolder(typeElement)).pretreatment;

            // ????????????????????????????????????
//            mb.addStatement("int $N = 7","i");// ?????????
//            mb.addCode("int $N = 7","j");// ????????????
//            mb.addStatement("$N.storeMonitorEventCode()",EVENT_MANAGER_FIELD);
            final CodeBlock.Builder storeMonitorEventCode = CodeBlock.builder()//
                    .beginControlFlow("$1N.storeMonitorEvent(new $2T<$3T>(($3T) $4N.getThis())", //
                            EVENT_MANAGER_FIELD, viewerEventReceiverClassName, typeElement, NameUtils.joinPoint);

            // ?????? doInvoke ??????
            storeMonitorEventCode.beginControlFlow("@Override\nprotected void doInvoke($T viewer)", typeElement)//
                    .addStatement("viewer.$L", element).endControlFlow();

            // ?????? isReady ????????????????????????????????????
            if (monitorAnnotation.alwaysReceive()) {
                storeMonitorEventCode.beginControlFlow("@Override\nprotected boolean isReady($T viewer)", typeElement)//
                        .addStatement("return viewer != null").endControlFlow();
            }

            storeMonitorEventCode.endControlFlow();

            // Monitor ????????????????????????????????? storeMonitorEventCode ?????????
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                if (getContext().typeUtils.isSameType(annotationMirror.getAnnotationType(), monitorTypeElement.asType())) {
                    storeMonitorEventCode.add(",$L", annotationUtils.toTypeSpec(annotationMirror));
                }
            }

//            intToCode.arrayToCode(monitorAnnotation.value(), storeMonitorEventCode);
//
//            // page ?????????????????? storeMonitorEventCode ?????????
//            storeMonitorEventCode.add(", new $T[]", PageMonitor.class);
//            pageMonitorToCode.arrayToCode(monitorAnnotation.page(), storeMonitorEventCode);

            // ??????
            storeMonitorEventCode.addStatement(")");
            mb.addCode(storeMonitorEventCode.build());

//            MethodSpec.methodBuilder("isReady").addAnnotation(Override.class)//
//                    .addModifiers(Modifier.PROTECTED).returns(TypeName.BOOLEAN)//
//                    .addParameter(viewerClassName, "indexViewer")//
//                    .addStatement("return indexViewer != null")//
//                    .build().toString();//
//            mb.addCode(TypeSpec.anonymousClassBuilder("($T)$N.getThis()", viewerEventReceiverClassName,joinPoint)//
//                    .superclass(viewerEventReceiverClassName)//
//                    .build().toString());

        }
    }

    /**
     * ??????????????????????????????
     *
     * @param holder
     * @param basicManagerField
     * @param element
     */
    private void pretreatmentCode(Holder holder, FieldSpec basicManagerField, Element element) {

        TypeMirror fType = element.asType();
        holder.pretreatment.beginControlFlow("if ($N.$N == null)", THIS_NAME, element.getSimpleName());// ??????????????????????????????????????????????????????????????????????????????????????????
        if (holder.single) {// ???????????????
            holder.pretreatment//
                    .addStatement("$1T viewer = $2N.getViewer($1T.class)", fType, basicManagerField)//
                    .beginControlFlow("if (viewer == null)")//
                    .addStatement("viewer = new $T()", fType)//
                    .addStatement("$N.putViewer(viewer)", basicManagerField)//
                    .endControlFlow()//
                    .addStatement("$N.$N = viewer", THIS_NAME, element.getSimpleName())//
            ;
        } else {// ??????????????????
            holder.pretreatment.addStatement("$N.$N = new $T()", THIS_NAME, element.getSimpleName(), fType);
        }
        holder.pretreatment.endControlFlow();
    }

    /**
     * ??????????????? Viewer ?????? View ????????????
     *
     * @param mb
     * @param element
     */
    private void injectViewToSubViewerCode(MethodSpec.Builder mb, Element element) {

        // ???????????? View ??? id
        ViewerInject annotation = element.getAnnotation(ViewerInject.class);
//        int targetId = annotation.value();
        int parentId = annotation.parentId();
        String targetIdStr = annotation.id();

        if (isNull(targetIdStr)) {// targetId == 0 ?????????????????? viewer ??????????????????????????? view
            return;
        }


        MethodSpec.Builder builder= mb//
                .beginControlFlow("if ($N.$N != null)", THIS_NAME, element.getSimpleName())
                .addStatement("$T v = " + "$N.findViewById(" + targetIdStr + ", $L)", viewClassName, FINDER_NAME, parentId)
                .addStatement("$1N.$2N.useByAssignment($1N, v)", THIS_NAME, element.getSimpleName())//
                .endControlFlow();

    }

    /**
     * ??????????????? InjectViewListener ???????????????????????????
     *
     * @param holder
     * @return
     */
    private InjectViewListenerHolder initInjectViewListener(Holder holder) {
        // ??????????????????
        if (holder.injectViewListener != null) {
            return holder.injectViewListener;
        }
        // ??????
        InjectViewListenerHolder h = new InjectViewListenerHolder();
        h.injectView = MethodSpec.overriding(injectViewListenerExecutables.injectView)//
                .addStatement("$1T $2N = new $1T($3N.getContentView())", viewFinderClassName, FINDER_NAME, THIS_NAME);
        h.injectViewToSubViewer = MethodSpec.overriding(injectViewListenerExecutables.injectViewToSubViewer)//
                .addStatement("$1T $2N = new $1T($3N.getContentView())", viewFinderClassName, FINDER_NAME, THIS_NAME);
        // ???????????????
        return holder.injectViewListener = h;
    }

    /**
     * ????????????????????????????????????
     *
     * @param typeElement
     * @param holder
     * @return
     */
    private Holder initPretreatmentMethod(TypeElement typeElement, Holder holder) {
        if (holder.pretreatment != null) {
            return holder;
        }
        // ????????????
        MethodSpec.Builder mb = MethodSpec.methodBuilder("pretreatment")//
                .addAnnotation(//
                        AnnotationSpec.builder(After.class)//
                                .addMember("value", "$S", "initialization(" + typeElement.getQualifiedName() + ".new(..))")//
                                .build()//
                )//
                .addModifiers(Modifier.PUBLIC)//
                .returns(TypeName.VOID)//
                .addParameter(NameUtils.joinPoint)//
                .addStatement("final $1T " + THIS_NAME + " = ($1T)$2N.getThis()", ClassName.get(typeElement), NameUtils.joinPoint);
        holder.pretreatment = mb;

        // ?????????????????????????????????????????????????????????????????????????????????
        // ????????????
        for (AnnotationMirror mirror : typeElement.getAnnotationMirrors()) {
            if (getContext().typeUtils.isSameType(mirror.getAnnotationType(), viewerSettingTypeElement.asType())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("flag")) {
                        holder.single = ((int) entry.getValue().getValue() & BaseViewerConstant.FLAG_SINGLE) == BaseViewerConstant.FLAG_SINGLE;
                    }
                }
            }
        }
        return holder;
    }

    private Holder getOrCreateHolder(TypeElement typeElement) {
        if (typeElement.equals(keyHitCache)) {
            return valueHitCache;
        }
        BuilderData data = getOrCreateAllBuilderData().getOrCreate(typeElement);
        Holder holder = data.get(Holder.class);
        if (holder == null) {
            holder = new Holder();
            data.add(holder);
        }
        keyHitCache = typeElement;
        return valueHitCache = holder;
    }

    static class Holder {
        MethodSpec.Builder pretreatment;
        InjectViewListenerHolder injectViewListener;
        private boolean single;
    }

    static class InjectViewListenerHolder {
        MethodSpec.Builder injectView;
        MethodSpec.Builder injectViewToSubViewer;
    }

    private static class InjectViewListenerExecutables {

        private ExecutableElement injectView;
        private ExecutableElement injectViewToSubViewer;

        private InjectViewListenerExecutables() {
            List<? extends Element> enclosedElements = getContext().elementUtils.getTypeElement(injectViewListenerClassName.toString()).getEnclosedElements();
            injectView = (ExecutableElement) enclosedElements.get(0);
            injectViewToSubViewer = (ExecutableElement) enclosedElements.get(1);
        }
    }

    private static boolean isNull(Object inputStr) {
        return (null == inputStr) || "".equals(inputStr) || "null".equals(inputStr) || "".equals(inputStr.toString().trim());
    }
}
