package cn.neucloud.server.controller.utils;

import javax.persistence.Column;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Jary on 2017/9/27 0027.
 */

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonNodeConvert {
    String toText () default "";
    @Column
    Class targetClass () default void.class;
}
