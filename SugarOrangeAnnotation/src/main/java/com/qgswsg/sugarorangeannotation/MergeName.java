package com.qgswsg.sugarorangeannotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 指定合成后的接口文件名<br/>
 * 如果不指定，将设置为SugarOrangeApiService.java
 */
@Retention(RetentionPolicy.SOURCE)
public @interface MergeName {
    String value();
}