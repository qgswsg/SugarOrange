package com.qgswsg.sugarorange;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


class Merge {

    private Elements mElementUtils;
    private Messager mMessager;
    private Filer mFiler;
    private String[] objMethod = new String[]{"getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait"};

    Merge(ProcessingEnvironment processingEnvironment) {
        this.mElementUtils = processingEnvironment.getElementUtils();
        this.mMessager = processingEnvironment.getMessager();
        this.mFiler = processingEnvironment.getFiler();
    }

    public void start(String name, RoundEnvironment roundEnvironment) {
//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Api.class);
//        if (elements == null) return;
//        Class<?> c = Api.class;
//        String pkName = c.getClass().getName();
//        TypeSpec.Builder mergeApiInterfaceBuilder = TypeSpec.interfaceBuilder(name).addModifiers(Modifier.PUBLIC);
//        List<MethodSpec> methodSpecs = new ArrayList<>();
//        for (Element element : elements) {
//            PackageElement packageElement = mElementUtils.getPackageOf(element);
//            pkName = packageElement.getQualifiedName().toString();
//            for (Element membersElement : mElementUtils.getAllMembers((TypeElement) element)) {
//                if (membersElement.getKind().equals(ElementKind.METHOD)) {
//                    ExecutableElement executableElement = (ExecutableElement) membersElement;
//                    if (Arrays.asList(objMethod).contains(executableElement.getSimpleName().toString()))
//                        continue;
//                    MethodSpec methodSpec = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
//                            .addModifiers(executableElement.getModifiers())
//                            .returns(ClassName.get(executableElement.getReturnType()))
//                            .addParameters(getParameter(executableElement))
//                            .addAnnotations(getElementAnnotationMirrors(membersElement))
//                            .addJavadoc(mElementUtils.getDocComment(executableElement)).build();
//                    methodSpecs.add(methodSpec);
//                }
//            }
//        }
//        mergeApiInterfaceBuilder.addMethods(methodSpecs);
//        JavaFile javaFile = JavaFile.builder(pkName,mergeApiInterfaceBuilder.build()).build();
//        try {
//            javaFile.writeTo(mFiler);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        note(mergeApiInterfaceBuilder.build().toString());
    }

    private void createFile(String name, String sourceCode) {
        try {
            JavaFileObject jfo = mFiler.createSourceFile(name);
            Writer writer = jfo.openWriter();
            writer.write(sourceCode);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ParameterSpec> getParameter(ExecutableElement executableElement) {
        List<ParameterSpec> parameterSpecs = new ArrayList<>();
        for (VariableElement variableElement : executableElement.getParameters()) {
            parameterSpecs.add(ParameterSpec.get(variableElement));
        }
        return parameterSpecs;
    }

    private String getFullMethod(Element element) {
        StringBuilder completeMethodStringBuilder = new StringBuilder();
        if (element.getKind() == ElementKind.METHOD) {
            completeMethodStringBuilder.append(getDocComment(element))
                    .append(getElementAnnotationMirrors(element))
                    .append(getInterfaceMethod((ExecutableElement) element));
        }
        return completeMethodStringBuilder.toString();
    }

    private String getInterfaceMethod(ExecutableElement element) {
        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append(getModifiers(element));
        methodBuilder.append(element.getReturnType().toString());
        methodBuilder.append(" ");
        methodBuilder.append(element.getSimpleName());
        methodBuilder.append("(");
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                VariableElement variableElement = parameters.get(i);
                methodBuilder.append(getElementAnnotationMirrors(variableElement));
                methodBuilder.append(getModifiers(variableElement));
                methodBuilder.append(variableElement.asType());
                methodBuilder.append(" ");
                methodBuilder.append(variableElement.getSimpleName());
                if (i < parameters.size() - 1) {
                    methodBuilder.append(", ");
                }
            }
        }
        methodBuilder.append(")");
        List<? extends TypeMirror> throwsParam = element.getThrownTypes();
        if (throwsParam != null && !throwsParam.isEmpty()) {
            methodBuilder.append("throws ");
            for (int i = 0; i < throwsParam.size(); i++) {
                TypeMirror typeMirror = throwsParam.get(i);
                methodBuilder.append(typeMirror);
                if (i < throwsParam.size() - 1) {
                    methodBuilder.append(", ");
                }
            }
        }
        methodBuilder.append(";\n");
        return methodBuilder.toString();
    }

    private String getDocComment(Element element) {
        String doc = mElementUtils.getDocComment(element);
        return doc != null ? String.format("\n/**\n*%s/\n", doc.replace("\n", "\n*")) : "";
    }

    private String getModifiers(Element element) {
        StringBuilder modifierBuilder = new StringBuilder();
        for (Modifier modifier : element.getModifiers()) {
            modifierBuilder.append(modifier);
            modifierBuilder.append(" ");
        }
        return modifierBuilder.toString();
    }

    private List<AnnotationSpec> getElementAnnotationMirrors(Element element) {
        List<AnnotationSpec> annotationSpecList = new ArrayList<>();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            annotationSpecList.add(AnnotationSpec.get(annotationMirror));
        }
        return annotationSpecList;
    }

    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
