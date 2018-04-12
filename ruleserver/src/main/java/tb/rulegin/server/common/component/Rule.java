package tb.rulegin.server.common.component;

import tb.rulegin.server.common.data.component.ComponentScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Jary on 2018/1/19 0019.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Rule {

    String name();

    String dataSourceType() default "kafka";
    String filtersType() default "single";
    String actionsType() default "";

    ComponentScope scope() default ComponentScope.TENANT;

    Class<?> configuration() default EmptyComponentConfiguration.class;

}
