package com.limpidj;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

import static com.limpidj.ContextUtils.getContext;
import static java.lang.Character.isISOControl;

/**
 */
public class AnnotationUtils {

    private final AnnotationValueVisitor annotationValueVisitor = new AnnotationValueVisitor();
    private final TypeElement annotationTypeElement = getContext().elementUtils.getTypeElement(Annotation.class.getCanonicalName());

    /**
     * 禁止构造
     */
    private AnnotationUtils() {
    }

    public static AnnotationMirror getAnnotationMirror(TypeElement typeElement, TypeElement annotationElement) {
        return getAnnotationMirror(typeElement, annotationElement.asType());
    }

    public static AnnotationMirror getAnnotationMirror(TypeElement typeElement, TypeMirror annotationTypeMirror) {
        for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
            if (getContext().typeUtils.isSameType(annotationMirror.getAnnotationType(), annotationTypeMirror)) {
                return annotationMirror;
            }
        }
        return null;
    }

    private static String characterLiteralWithoutSingleQuotes(char c) {
        // see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
        if (c == '\b') {
            return "\\b"; /* \u0008: backspace (BS) */
        } else if (c == '\t') {
            return "\\t"; /* \u0009: horizontal tab (HT) */
        } else if (c == '\n') {
            return "\\n"; /* \u000a: linefeed (LF) */
        } else if (c == '\f') {
            return "\\f"; /* \u000c: form feed (FF) */
        } else if (c == '\r') {
            return "\\r"; /* \u000d: carriage return (CR) */
        } else if (c == '\"') {
            return "\"";  /* \u0022: double quote (") */
        } else if (c == '\'') {
            return "\\'"; /* \u0027: single quote (') */
        } else if (c == '\\') {
            return "\\\\";  /* \u005c: backslash (\) */
        } else {
            return isISOControl(c) ? String.format("\\u%04x", (int) c) : Character.toString(c);
        }
    }

    public TypeSpec toTypeSpec(AnnotationMirror annotationMirror) {

        DeclaredType annotationType = annotationMirror.getAnnotationType();
        TypeSpec.Builder tb = TypeSpec.anonymousClassBuilder("")//
                .superclass(TypeName.get(annotationType))//
                ;

        for (Element element : annotationTypeElement.getEnclosedElements()) {
            if (element.getSimpleName().toString().equals("annotationType")) {
                tb.addMethod(MethodSpec.overriding((ExecutableElement) element).addStatement("return this.getClass()").build());
            }
        }

        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = getContext().elementUtils.getElementValuesWithDefaults(annotationMirror);
        for (ExecutableElement executableElement : elementValues.keySet()) {
            MethodSpec.Builder mb = MethodSpec.overriding(executableElement);
            AnnotationValue value = elementValues.get(executableElement);
            value.accept(annotationValueVisitor.setMethodBuilde(mb), executableElement.getReturnType());
            tb.addMethod(mb.build());
        }
        return tb.build();

    }

    /**
     * 单例持有器
     */
    static final class InstanceHolder {
        static final AnnotationUtils annotationUtils = new AnnotationUtils();
    }

    private class AnnotationValueVisitor extends SimpleAnnotationValueVisitor7<MethodSpec.Builder, TypeMirror> {

        private MethodSpec.Builder methodBuilde;

        private AnnotationValueVisitor() {
            super();
        }

        private AnnotationValueVisitor setMethodBuilde(MethodSpec.Builder mb) {
            this.methodBuilde = mb;
            return this;
        }

        @Override
        protected MethodSpec.Builder defaultAction(Object o, TypeMirror resultType) {
            if (o instanceof String) {
                add(resultType, "$S", o);
            } else if (o instanceof Float) {
                add(resultType, "$Lf", o);
            } else if (o instanceof Character) {
                add(resultType, "'$L'", characterLiteralWithoutSingleQuotes((char) o));
            } else {
                add(resultType, "$L", o);
            }
            return DEFAULT_VALUE;
        }

        @Override
        public MethodSpec.Builder visitAnnotation(AnnotationMirror a, TypeMirror resultType) {
            MethodSpec.Builder mb = this.methodBuilde;// save
            TypeSpec typeSpec = toTypeSpec(a);
            this.methodBuilde = mb;// restore
            add(resultType, "$L", typeSpec);
            return DEFAULT_VALUE;
        }

        @Override
        public MethodSpec.Builder visitEnumConstant(VariableElement c, TypeMirror resultType) {
            add(resultType, "$T.$L", c.asType(), c.getSimpleName());
            return DEFAULT_VALUE;
        }

        @Override
        public MethodSpec.Builder visitType(TypeMirror t, TypeMirror resultType) {
            add(resultType, "$T.class", t);
            return DEFAULT_VALUE;
        }

        @Override
        public MethodSpec.Builder visitArray(List<? extends AnnotationValue> values, TypeMirror resultType) {
            methodBuilde.addCode("return new $T{", resultType);
            boolean first = true;
            for (AnnotationValue value : values) {
                if (!first) {
                    methodBuilde.addCode(",");
                }
                value.accept(this, resultType);
                first = false;
            }
            methodBuilde.addStatement("}");
            return DEFAULT_VALUE;
        }

        private void add(TypeMirror resultType, String format, Object... args) {
            if (resultType.getKind() == TypeKind.ARRAY) {
                methodBuilde.addCode(format, args);
            } else {
                methodBuilde.addStatement("return " + format, args);
            }
        }

    }
}
