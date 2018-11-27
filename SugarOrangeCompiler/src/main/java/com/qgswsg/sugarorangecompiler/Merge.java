package com.qgswsg.sugarorangecompiler;

import com.google.auto.common.SuperficialValidation;
import com.qgswsg.sugarorangeannotation.Api;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;


class Merge {

    private ProcessingEnvironment env;
    private String[] objMethod = new String[]{"getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait"};

    Merge(ProcessingEnvironment env) {
        this.env = env;
    }

    public void start(String name, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Api.class);
        if (elements == null) return;
        String pkName = "com.qgswsg.sugarorange";
        TypeSpec.Builder mergeApiInterfaceBuilder = TypeSpec.interfaceBuilder(name).addModifiers(Modifier.PUBLIC);
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (Element element : elements) {
            if (!SuperficialValidation.validateElement(element)) continue;
            PackageElement packageElement = env.getElementUtils().getPackageOf(element);
            String baseUrl = element.getAnnotation(Api.class).baseUrl();
            pkName = packageElement.getQualifiedName().toString();
            for (Element membersElement : env.getElementUtils().getAllMembers((TypeElement) element)) {
                if (membersElement.getKind().equals(ElementKind.METHOD)) {
                    ExecutableElement executableElement = (ExecutableElement) membersElement;
                    if (Arrays.asList(objMethod).contains(executableElement.getSimpleName().toString()))
                        continue;
                    MethodSpec methodSpec = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                            .addModifiers(executableElement.getModifiers())
                            .returns(ClassName.get(executableElement.getReturnType()))
                            .addParameters(getParameter(executableElement))
                            .addAnnotations(getElementAnnotationMirrors(membersElement, baseUrl))
                            .addJavadoc(env.getElementUtils().getDocComment(executableElement)).build();
                    methodSpecs.add(methodSpec);
                }
            }
        }
        mergeApiInterfaceBuilder.addMethods(methodSpecs);
        JavaFile javaFile = JavaFile.builder(pkName, mergeApiInterfaceBuilder.build()).build();
        note(null, mergeApiInterfaceBuilder.build().toString());
        try {
            javaFile.writeTo(env.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ParameterSpec> getParameter(ExecutableElement executableElement) {
        List<ParameterSpec> parameterSpecs = new ArrayList<>();
        for (VariableElement variableElement : executableElement.getParameters()) {
            ParameterSpec.Builder builder = ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString());
            for (Modifier modifier : variableElement.getModifiers()) {
                builder.addModifiers(modifier);
            }
            builder.addAnnotations(getParameterAnnotationMirrors(variableElement));
            parameterSpecs.add(builder.build());
        }
        return parameterSpecs;
    }

    private List<AnnotationSpec> getParameterAnnotationMirrors(Element element) {
        List<AnnotationSpec> annotationSpecList = new ArrayList<>();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            annotationSpecList.add(AnnotationSpec.get(annotationMirror));
        }
        return annotationSpecList;
    }

    private List<AnnotationSpec> getElementAnnotationMirrors(Element element, String baseUrl) {
        List<AnnotationSpec> annotationSpecList = new ArrayList<>();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            AnnotationSpec annotationSpec = AnnotationSpec.get(annotationMirror);
            if (annotationSpec.members.size() == 1 && annotationSpec.members.containsKey("value")) {
                // @Named("foo")
                List<CodeBlock> value = annotationSpec.members.get("value");
                annotationSpec = AnnotationSpec.builder(ClassName.get("retrofit2.http", "GET")).addMember("value", CodeBlock.builder().add("\"$L\" + $L", baseUrl, value.get(0).toString()).build()).build();
            }
            annotationSpecList.add(annotationSpec);
        }
        return annotationSpecList;
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        env.getMessager().printMessage(kind, message, element);
    }
}