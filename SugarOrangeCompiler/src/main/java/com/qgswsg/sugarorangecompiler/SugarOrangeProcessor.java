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
            //由于这个方法会被回调两次，这里进行一次判断，只执行一次合并逻辑
            merge = new Merge(processingEnv);
            Set<? extends Element> mergeNameElements = roundEnvironment.getElementsAnnotatedWith(MergeName.class);
            if (mergeNameElements.size() > 1) {//如果@MergeName注解存在多个，就在构建项目时报错并提示
                Element next = mergeNameElements.iterator().next();
                error(next, "Cannot have multiple @MergeName annotations at the same time");
            } else {
                if (mergeNameElements.size() == 1) {
                    //如果项目中有且仅有一个@MergeName注解时，取此注解中的值作合并后的接口的文件名
                    String value = mergeNameElements.iterator().next().getAnnotation(MergeName.class).value();
                    if (!value.isEmpty()) {
                        merge.start(value, roundEnvironment);
                    }
                } else {//如果项目中未使用@MergeName指定合并后的名字，就使用"SugarOrangeApiService"这个默认名字
                    merge.start("SugarOrangeApiService", roundEnvironment);
                }
            }
        }
        return false;
    }

    /**
    * 在构建信息中打印错误消息
    **/
    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    /**
    * 在构建信息中打印普通信息
    **/
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
