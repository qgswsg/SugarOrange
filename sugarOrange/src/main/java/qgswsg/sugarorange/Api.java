package qgswsg.sugarorange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface Api {
    String value();
}
