package com.limpidj;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import org.aspectj.lang.annotation.DeclareMixin;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.limpidj.NameUtils.THIS_NAME;
import static com.limpidj.ViewerExtractor.injectViewListenerClassName;

/**
 */
public class ViewerBuilder extends AnnotationBuilder {

    @Override
    void eachTypeElement(TypeElement typeElement, BuilderData data, boolean over) {

        ViewerExtractor.Holder holder = data.get(ViewerExtractor.Holder.class);

        if (holder == null) {
            super.eachTypeElement(typeElement, data, over);
            return;
        } else {
            super.eachTypeElement(typeElement, data, false);
        }

        TypeSpec.Builder tb = getOrCreateType(data, typeElement);

        tb.addField(ViewerExtractor.BASIC_MANAGER_FIELD)//
                .addField(ViewerExtractor.EVENT_MANAGER_FIELD)//
        ;


        // 预处理方法
        if (holder.pretreatment != null) {
            tb.addMethod(holder.pretreatment.build());
        }

        // 注入器提供方法
        ViewerExtractor.InjectViewListenerHolder iHolder = holder.injectViewListener;
        if (iHolder != null) {
            MethodSpec.Builder mb = MethodSpec.methodBuilder("createInjector")//
                    .addAnnotation(//
                            AnnotationSpec.builder(DeclareMixin.class)//
                                    .addMember("value", "$S", typeElement.getQualifiedName())//
                                    .build()//
                    )//
                    .returns(injectViewListenerClassName)//
                    .addParameter(//
                            ParameterSpec.builder(ClassName.get(typeElement), THIS_NAME)//
                                    .addModifiers(Modifier.FINAL)//
                                    .build()//
                    )//
                    .addStatement("return $L",//
                            TypeSpec.anonymousClassBuilder("")//
                                    .superclass(injectViewListenerClassName)//
                                    .addMethod(iHolder.injectView.build())//
                                    .addMethod(iHolder.injectViewToSubViewer.build())//
                                    .build()//
                    );
            tb.addMethod(mb.build());
        }

        if (over) {
            saveToDisk(data);
        }
    }
}
