package com.limpidj;

import com.google.auto.service.AutoService;
import com.limpidj.android.anno.AnnotationPreload;
import com.limpidj.android.anno.Monitor;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MainProcessor extends AbstractProcessor {

    private Messager messager;

    private AnnotationExtractor annotationExtractor;
    private ViewerExtractor viewerExtractor;
    private ViewerBuilder viewerBuilder;
    private AnnotationUtilsBuilder annotationUtilsBuilder;

    private static String allClass(Class c, HashSet<Class> set) {
        if (c == null || c == Object.class) {
            return null;
        }
        boolean output = false;
        if (set == null) {
            set = new HashSet<>();
            output = true;
        }
        for (Class ic : c.getInterfaces()) {
            addClass(ic, set);
        }
        addClass(c.getSuperclass(), set);
        return output ? c.toString() + "=" + set.toString() : null;
    }

    private static void addClass(Class c, HashSet<Class> set) {
        if (c == null || c == Object.class) {
            return;
        }
        set.add(c);
        allClass(c, set);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        ContextUtils.Context context = new ContextUtils.Context();
        context.elementUtils = processingEnv.getElementUtils();
        context.typeUtils = processingEnv.getTypeUtils();
        context.messager = processingEnv.getMessager();
        context.filer = processingEnv.getFiler();
        ContextUtils.setContext(context);

//        messager.printMessage(Diagnostic.Kind.NOTE,"roundEnv.getRootElements()=" + processingEnv.getFiler().getResource(StandardLocation.locationFor(""),));

        annotationExtractor = new AnnotationExtractor();
        viewerExtractor = new ViewerExtractor();
        viewerBuilder = new ViewerBuilder();
        annotationUtilsBuilder = new AnnotationUtilsBuilder();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

        annotationExtractor.start(roundEnv);
        viewerExtractor.start(roundEnv);
        viewerBuilder.start();
        annotationUtilsBuilder.start();

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> as = new HashSet<>();
        as.add(Monitor.class.getCanonicalName());
        as.add(AnnotationPreload.class.getCanonicalName());
        return as;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        messager.printMessage(Diagnostic.Kind.NOTE, "getCompletions() element=" + element + ",annotation=" + annotation + ",member=" + member + ",userText=" + userText);
        return super.getCompletions(element, annotation, member, userText);
    }

    private static abstract class ToCode<T> {
        void arrayToCode(T[] a, CodeBlock.Builder b) {
            if (a == null) {
                b.add("null");
                return;
            }
            int iMax = a.length - 1;
            if (iMax == -1) {
                b.add("{}");
                return;
            }

            b.add("{");
            for (int i = 0; ; i++) {
                singleToCode(a[i], b);
                if (i == iMax) {
                    b.add("}");
                    return;
                }
                b.add(", ");
            }
        }

        abstract void singleToCode(T t, CodeBlock.Builder b);
    }

    private static class IntToCode {
        void arrayToCode(int[] a, CodeBlock.Builder b) {
            if (a == null) {
                b.add("null");
                return;
            }
            int iMax = a.length - 1;
            if (iMax == -1) {
                b.add("{}");
                return;
            }

            b.add("{");
            for (int i = 0; ; i++) {
                singleToCode(a[i], b);
                if (i == iMax) {
                    b.add("}");
                    return;
                }
                b.add(", ");
            }
        }

        void singleToCode(int i, CodeBlock.Builder b) {
            b.add("" + i);
        }
    }

    //    private static CodeBlock toCode(Class<? extends IPage> c){
//
//    }
//
//    private static <T> CodeBlock toCode(T c){
//        c instanceof
//    }
//
//    private static CodeBlock xx(PageMonitor pageMonitor){
//        PageProcess pageProcess = pageMonitor.value();
//        CodeBlock.of("new $T($T.$N, IndexPage.class)",PageMonitorBean.class, pageProcess.getClass(),pageProcess.name(),xxx(pageMonitor.page()));
//
//    }
//
//    private static CodeBlock xxx(Class<? extends IPage>[] page) {
//        CodeBlock.Builder b = CodeBlock.builder();
//        for (Class<? extends IPage> c : page) {
//            b.add("$")
//        }
//        return null;
//    }

//    private CodeBlock xx(){
//        CodeBlock.builder().addStatement("new ")
//    }

//    private static  TypeSpec.Builder addMemberForValue(String memberName, Object value) {
//        checkNotNull(memberName, "memberName == null");
//        checkNotNull(value, "value == null, constant non-null value expected for %s", memberName);
//        if (value instanceof Class<?>) {
//            return addMember(memberName, "$T.class", value);
//        }
//        if (value instanceof Enum) {
//            return addMember(memberName, "$T.$L", value.getClass(), ((Enum<?>) value).name());
//        }
//        if (value instanceof String) {
//            return addMember(memberName, "$S", value);
//        }
//        if (value instanceof Float) {
//            return addMember(memberName, "$Lf", value);
//        }
//        if (value instanceof Character) {
//            return addMember(memberName, "'$L'", characterLiteralWithoutSingleQuotes((char) value));
//        }
//        return addMember(memberName, "$L", value);
//    }

    private class TypeVisitor extends SimpleTypeVisitor6<TypeSpec.Builder, AnnotationValue> {
        private TypeVisitor(TypeSpec.Builder builder) {
            super(builder);
        }

        @Override
        protected TypeSpec.Builder defaultAction(TypeMirror typeMirror, AnnotationValue annotationValue) {

            messager.printMessage(Diagnostic.Kind.NOTE, "defaultAction() typeMirror=" + typeMirror + ",annotationValue=" + annotationValue);
            return super.defaultAction(typeMirror, annotationValue);
        }
    }

    private class ElementVisitor extends ElementScanner6<TypeSpec.Builder, Map<? extends ExecutableElement, ? extends AnnotationValue>> {
        private ElementVisitor(TypeSpec.Builder defaultValue) {
            super(defaultValue);
        }

        @Override
        public TypeSpec.Builder scan(Element e, Map<? extends ExecutableElement, ? extends AnnotationValue> v) {
            messager.printMessage(Diagnostic.Kind.NOTE, "scan() e=" + e + ",v=" + v);
            return super.scan(e, v);
        }

        @Override
        public TypeSpec.Builder visitPackage(PackageElement e, Map<? extends ExecutableElement, ? extends AnnotationValue> v) {
            messager.printMessage(Diagnostic.Kind.NOTE, "visitPackage() e=" + e + ",v=" + v);
            return super.visitPackage(e, v);
        }

        @Override
        public TypeSpec.Builder visitType(TypeElement e, Map<? extends ExecutableElement, ? extends AnnotationValue> v) {
            messager.printMessage(Diagnostic.Kind.NOTE, "visitType() e=" + e + ",v=" + v);
            return super.visitType(e, v);
        }

        @Override
        public TypeSpec.Builder visitVariable(VariableElement e, Map<? extends ExecutableElement, ? extends AnnotationValue> v) {
            messager.printMessage(Diagnostic.Kind.NOTE, "visitVariable() e=" + e + ",v=" + v);
            return super.visitVariable(e, v);
        }

        @Override
        public TypeSpec.Builder visitExecutable(ExecutableElement e, Map<? extends ExecutableElement, ? extends AnnotationValue> v) {
            messager.printMessage(Diagnostic.Kind.NOTE, "visitExecutable() e=" + e + ",v=" + v);
//            v.get(e).getValue()
//            e.getReturnType().
//            MethodSpec.overriding(e).addStatement("");
//            DEFAULT_VALUE.addMethod()
            return super.visitExecutable(e, v);
        }

        @Override
        public TypeSpec.Builder visitTypeParameter(TypeParameterElement e, Map<? extends ExecutableElement, ? extends AnnotationValue> v) {
            messager.printMessage(Diagnostic.Kind.NOTE, "visitTypeParameter() e=" + e + ",v=" + v);
            return super.visitTypeParameter(e, v);
        }

        @Override
        public TypeSpec.Builder visitUnknown(Element e, Map<? extends ExecutableElement, ? extends AnnotationValue> v) {
            messager.printMessage(Diagnostic.Kind.NOTE, "visitUnknown() e=" + e + ",v=" + v);
            return super.visitUnknown(e, v);
        }
    }

}
