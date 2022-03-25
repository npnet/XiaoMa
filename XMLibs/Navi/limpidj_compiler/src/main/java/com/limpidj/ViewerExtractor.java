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

    // 构建字段 eventManager
    static final FieldSpec EVENT_MANAGER_FIELD = FieldSpec.builder(eventManagerClassName, "eventManager", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$T.getInstance()", eventManagerClassName).build();
    // 构建字段 basicManager
    static final FieldSpec BASIC_MANAGER_FIELD = FieldSpec.builder(basicManagerClassName, "basicManager", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$T.getInstance()", basicManagerClassName).build();

    private InjectViewListenerExecutables injectViewListenerExecutables = new InjectViewListenerExecutables();
    private ExecutableElement onClickListenerExecutable = (ExecutableElement) getContext().elementUtils.getTypeElement(onClickListenerClassName.toString()).getEnclosedElements().get(0);

    private TypeElement viewerSettingTypeElement = getContext().elementUtils.getTypeElement("com.mapbar.android.mapbarmap.core.inject.anno.ViewerSetting");
    private TypeElement monitorTypeElement = getContext().elementUtils.getTypeElement(Monitor.class.getCanonicalName());

    // 该缓存为了尝试直接命中以提高执行效率
    private TypeElement keyHitCache = null;
    private Holder valueHitCache = null;

    public void start(RoundEnvironment roundEnv) {


        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();

            // 构建或找出已构建的方法
            MethodSpec.Builder mb = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;

            // 提取目标 View 的 id
            OnClick annotation = element.getAnnotation(OnClick.class);
            String[] targetIds = annotation.value();
            int[] parentIds = annotation.parentId();

            for (int i = 0; i < targetIds.length; i++) {
                // 代码
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
            // TODO: 2019/1/29 让apt自动生成import部分，临时对应，后续需要修改
            MethodSpec.Builder builder = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;//
            builder.beginControlFlow("$T.class.getName();", APPLICATION_R).endControlFlow();
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(ViewInject.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();

            // 构建或找出已构建的方法
            MethodSpec.Builder mb = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;

            // 提取目标 View 的 id
            ViewInject annotation = element.getAnnotation(ViewInject.class);
//            int targetId = annotation.value();
            int parentId = annotation.parentId();
            String idStr = annotation.id();

            // 代码
            TypeMirror fType = element.asType();
            mb.addStatement("$N.$N = ($T)$N.findViewById(" + idStr + ", $L)", THIS_NAME, element.getSimpleName(), fType, FINDER_NAME, parentId);

            // TODO: 2019/1/29 让apt自动生成import部分，临时对应，后续需要修改
            MethodSpec.Builder builder = initInjectViewListener(getOrCreateHolder(typeElement)).injectView;//
            builder.beginControlFlow("$T.class.getName();", APPLICATION_R).endControlFlow();

        }

        for (Element element : roundEnv.getElementsAnnotatedWith(ViewerInject.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();

            pretreatmentCode(initPretreatmentMethod(typeElement, getOrCreateHolder(typeElement)), BASIC_MANAGER_FIELD, element);

            injectViewToSubViewerCode(initInjectViewListener(getOrCreateHolder(typeElement)).injectViewToSubViewer, element);

            // TODO: 2019/1/29 让apt自动生成import部分，临时对应，后续需要修改
            MethodSpec.Builder builder = initInjectViewListener(getOrCreateHolder(typeElement)).injectViewToSubViewer;//
            builder.beginControlFlow("$T.class.getName();", APPLICATION_R).endControlFlow();

        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Monitor.class)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            Monitor monitorAnnotation = element.getAnnotation(Monitor.class);

            // 构建或找出已构建的方法
            MethodSpec.Builder mb = initPretreatmentMethod(typeElement, getOrCreateHolder(typeElement)).pretreatment;

            // 在方法中追加事件监听注册
//            mb.addStatement("int $N = 7","i");// 有分号
//            mb.addCode("int $N = 7","j");// 没有分号
//            mb.addStatement("$N.storeMonitorEventCode()",EVENT_MANAGER_FIELD);
            final CodeBlock.Builder storeMonitorEventCode = CodeBlock.builder()//
                    .beginControlFlow("$1N.storeMonitorEvent(new $2T<$3T>(($3T) $4N.getThis())", //
                            EVENT_MANAGER_FIELD, viewerEventReceiverClassName, typeElement, NameUtils.joinPoint);

            // 重写 doInvoke 方法
            storeMonitorEventCode.beginControlFlow("@Override\nprotected void doInvoke($T viewer)", typeElement)//
                    .addStatement("viewer.$L", element).endControlFlow();

            // 重写 isReady 方法，仅当总是接收通知时
            if (monitorAnnotation.alwaysReceive()) {
                storeMonitorEventCode.beginControlFlow("@Override\nprotected boolean isReady($T viewer)", typeElement)//
                        .addStatement("return viewer != null").endControlFlow();
            }

            storeMonitorEventCode.endControlFlow();

            // Monitor 注解的信息作为参数传入 storeMonitorEventCode 方法中
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                if (getContext().typeUtils.isSameType(annotationMirror.getAnnotationType(), monitorTypeElement.asType())) {
                    storeMonitorEventCode.add(",$L", annotationUtils.toTypeSpec(annotationMirror));
                }
            }

//            intToCode.arrayToCode(monitorAnnotation.value(), storeMonitorEventCode);
//
//            // page 事件参数传入 storeMonitorEventCode 方法中
//            storeMonitorEventCode.add(", new $T[]", PageMonitor.class);
//            pageMonitorToCode.arrayToCode(monitorAnnotation.page(), storeMonitorEventCode);

            // 收尾
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
     * 处理“预处理”的代码
     *
     * @param holder
     * @param basicManagerField
     * @param element
     */
    private void pretreatmentCode(Holder holder, FieldSpec basicManagerField, Element element) {

        TypeMirror fType = element.asType();
        holder.pretreatment.beginControlFlow("if ($N.$N == null)", THIS_NAME, element.getSimpleName());// 如果在自动注入之前用户自行赋值了，那么会尊重与保留用户的处理
        if (holder.single) {// 如果是单例
            holder.pretreatment//
                    .addStatement("$1T viewer = $2N.getViewer($1T.class)", fType, basicManagerField)//
                    .beginControlFlow("if (viewer == null)")//
                    .addStatement("viewer = new $T()", fType)//
                    .addStatement("$N.putViewer(viewer)", basicManagerField)//
                    .endControlFlow()//
                    .addStatement("$N.$N = viewer", THIS_NAME, element.getSimpleName())//
            ;
        } else {// 如果不是单例
            holder.pretreatment.addStatement("$N.$N = new $T()", THIS_NAME, element.getSimpleName(), fType);
        }
        holder.pretreatment.endControlFlow();
    }

    /**
     * 处理“给子 Viewer 注入 View ”的代码
     *
     * @param mb
     * @param element
     */
    private void injectViewToSubViewerCode(MethodSpec.Builder mb, Element element) {

        // 提取目标 View 的 id
        ViewerInject annotation = element.getAnnotation(ViewerInject.class);
//        int targetId = annotation.value();
        int parentId = annotation.parentId();
        String targetIdStr = annotation.id();

        if (isNull(targetIdStr)) {// targetId == 0 表示仅需注入 viewer 实例对象，无需注入 view
            return;
        }


        MethodSpec.Builder builder= mb//
                .beginControlFlow("if ($N.$N != null)", THIS_NAME, element.getSimpleName())
                .addStatement("$T v = " + "$N.findViewById(" + targetIdStr + ", $L)", viewClassName, FINDER_NAME, parentId)
                .addStatement("$1N.$2N.useByAssignment($1N, v)", THIS_NAME, element.getSimpleName())//
                .endControlFlow();

    }

    /**
     * 尝试初始化 InjectViewListener 接口相关一系列方法
     *
     * @param holder
     * @return
     */
    private InjectViewListenerHolder initInjectViewListener(Holder holder) {
        // 尝试直接获取
        if (holder.injectViewListener != null) {
            return holder.injectViewListener;
        }
        // 构建
        InjectViewListenerHolder h = new InjectViewListenerHolder();
        h.injectView = MethodSpec.overriding(injectViewListenerExecutables.injectView)//
                .addStatement("$1T $2N = new $1T($3N.getContentView())", viewFinderClassName, FINDER_NAME, THIS_NAME);
        h.injectViewToSubViewer = MethodSpec.overriding(injectViewListenerExecutables.injectViewToSubViewer)//
                .addStatement("$1T $2N = new $1T($3N.getContentView())", viewFinderClassName, FINDER_NAME, THIS_NAME);
        // 缓存与保存
        return holder.injectViewListener = h;
    }

    /**
     * 尝试初始化“预处理”方法
     *
     * @param typeElement
     * @param holder
     * @return
     */
    private Holder initPretreatmentMethod(TypeElement typeElement, Holder holder) {
        if (holder.pretreatment != null) {
            return holder;
        }
        // 构建方法
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

        // 初始化该方法后续构建中必须使用的，且仅需加载一次的值：
        // 是否单例
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
