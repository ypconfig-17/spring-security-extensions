package io.github.ypconfig17.security.extend.config.configuration.web;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.config.annotation.web.configuration.ShimEnableWebSecurity;

import java.lang.annotation.*;

/**
 * @author yangpan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ShimEnableWebSecurity
public @interface EnableSecurityEnhance {

    @AliasFor(
            annotation = ShimEnableWebSecurity.class
    )
    boolean debug() default false;

}
