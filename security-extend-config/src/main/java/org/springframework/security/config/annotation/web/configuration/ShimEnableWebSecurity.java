package org.springframework.security.config.annotation.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import java.lang.annotation.*;

/**
 * @author yangpan
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ WebSecurityConfiguration.class, SpringWebMvcImportSelector.class, OAuth2ImportSelector.class,
        EnhanceHttpSecurityConfiguration.class })
@EnableGlobalAuthentication
@Configuration
@EnableWebSecurity
public @interface ShimEnableWebSecurity {
    /**
     * Controls debugging support for Spring Security. Default is false.
     * @return if true, enables debug support with Spring Security
     */
    boolean debug() default false;

}
