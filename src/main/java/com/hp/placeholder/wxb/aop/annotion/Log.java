package com.hp.placeholder.wxb.aop.annotion;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Log {
    public String value();

    public String log();
}
