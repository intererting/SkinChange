package com.yuliyang.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

import static javax.lang.model.element.Modifier.*;
import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(Processor.class)
public class SkinViewProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filter;
    private Map<String, Integer> resViewMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filter = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(AutoSkin.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
//        messager.printMessage(Diagnostic.Kind.ERROR, "XXXXXXXXXXXXX");
        Set<? extends Element> elesWithBind = roundEnv.getElementsAnnotatedWith(AutoSkin.class);
        for (Element element : elesWithBind) {
            TypeElement typeElement = (TypeElement) element;
            AutoSkin bindAnnotation = typeElement.getAnnotation(AutoSkin.class);
            resViewMap.put(typeElement.getQualifiedName().toString(), bindAnnotation.value());
        }

        ParameterizedTypeName observerListType = ParameterizedTypeName.get(ArrayList.class, AutoChangeable.class);

        FieldSpec fieldSpec = FieldSpec.builder(observerListType, "observers", PUBLIC, STATIC).build();

        CodeBlock staticCodeBlock = CodeBlock.builder().addStatement("observers = new $T()", observerListType).build();

        MethodSpec addObserver = MethodSpec.methodBuilder("inject")
                .addModifiers(PUBLIC, STATIC)
                .returns(void.class)
                .addParameter(AutoChangeable.class, "observer")
                .addStatement("observers.add(observer)")
                .build();

        MethodSpec applyChange = MethodSpec.methodBuilder("applyChange")
                .addModifiers(PUBLIC, STATIC)
                .returns(void.class)
                .beginControlFlow("if(!observers.isEmpty())")
                .beginControlFlow("for(AutoChangeable changeable : observers)")
                .addStatement("changeable.changeWithRes(3)")
                .endControlFlow()
                .endControlFlow()
                .build();

        TypeSpec helper = TypeSpec.classBuilder("SkinChangeHelper")
                .addModifiers(PUBLIC, FINAL)
                .addField(fieldSpec)
                .addStaticBlock(staticCodeBlock)
                .addMethod(addObserver)
                .addMethod(applyChange)
                .build();

        JavaFile javaFile = JavaFile.builder("com.yly.skinchange", helper)
                .build();
        try {
            javaFile.writeTo(filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
