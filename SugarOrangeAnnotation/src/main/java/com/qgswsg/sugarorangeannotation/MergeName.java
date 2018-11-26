package com.qgswsg.sugarorangeannotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MergeName {
    String value();
}