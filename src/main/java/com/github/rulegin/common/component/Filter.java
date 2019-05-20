package com.github.rulegin.common.component;


import com.github.rulegin.common.data.component.ComponentScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Filter {

    String name();

    ComponentScope scope() default ComponentScope.TENANT;

    String descriptor() default "EmptyJsonDescriptor.json";

    Class<?> configuration() default EmptyComponentConfiguration.class;

}
