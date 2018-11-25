package qgswsg.sugarorange;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class ApiAbstractProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Messager mMessager;
    private String[] objMethod = new String[]{"getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait"};

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Api.class.getCanonicalName());
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }

    private String getCompleteMethod(Element element) {
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

    private String getElementAnnotationMirrors(Element element) {
        StringBuilder parameterBuilder = new StringBuilder();
        if (element.getAnnotationMirrors() != null) {
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                parameterBuilder.append("@");
                parameterBuilder.append(annotationMirror.getAnnotationType());
                parameterBuilder.append("(");
                Iterator<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> iterator = annotationMirror.getElementValues().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> next = iterator.next();
                    parameterBuilder.append(next.getKey().getSimpleName());
                    parameterBuilder.append(" = ");
                    parameterBuilder.append(next.getValue());
                    if (iterator.hasNext()) {
                        parameterBuilder.append(", ");
                    }
                }
                parameterBuilder.append(element.getKind() == ElementKind.METHOD ? ")\n" : ") ");
            }
        }
        return parameterBuilder.toString();
    }
}
