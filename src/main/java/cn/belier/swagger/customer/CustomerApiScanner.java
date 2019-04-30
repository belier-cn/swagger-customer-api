package cn.belier.swagger.customer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author belier
 * @date 2018/10/10
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(CustomerApiRegistrar.class)
public @interface CustomerApiScanner {

    Class<?>[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

}
