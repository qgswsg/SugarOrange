package com.qgswsg.sugarorangecompiler;

import com.google.auto.service.AutoService;
import com.qgswsg.sugarorangeannotation.Api;
import com.qgswsg.sugarorangeannotation.MergeName;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class SugarOrangeProcessor extends AbstractProcessor {

    private Merge merge;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Api.class.getCanonicalName());
        annotations.add(MergeName.class.getCanonicalName());
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (merge == null) {
            merge = new Merge(processingEnv);
            Set<? extends Element> mergeNameElements = roundEnvironment.getElementsAnnotatedWith(MergeName.class);
            if (mergeNameElements.size() > 1) {
                Element next = mergeNameElements.iterator().next();
                error(next, "Cannot have multiple @MergeName annotations at the same time");
            } else {
                if (mergeNameElements.size() == 1) {
                    String value = mergeNameElements.iterator().next().getAnnotation(MergeName.class).value();
                    if (!value.isEmpty()) {
                        merge.start(value, roundEnvironment);
                    }
                } else {
                    merge.start("SugarOrangeApiService", roundEnvironment);
                }
            }
        }
        return false;
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
        processingEnv.getMessager().printMessage(kind, message, element);
    }

}
