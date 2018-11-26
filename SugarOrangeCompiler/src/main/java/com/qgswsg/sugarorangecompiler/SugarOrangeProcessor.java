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
        merge = new Merge(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> mergeNameElements = roundEnvironment.getElementsAnnotatedWith(MergeName.class);
        String mergeName = "ApiService";
        if (mergeNameElements.size() > 1) {
            return false;
        } else if (mergeNameElements.size() == 1) {
            String value = mergeNameElements.iterator().next().getAnnotation(MergeName.class).value();
            if (!value.isEmpty()) {
                mergeName = value;
            }
            merge.start(mergeName,roundEnvironment);
            return true;
        }
        return true;
    }
}
