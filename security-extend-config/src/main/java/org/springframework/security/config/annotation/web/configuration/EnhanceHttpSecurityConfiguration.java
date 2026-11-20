package org.springframework.security.config.annotation.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.EnhancedHttpSecurity;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangpan
 */

@Configuration(proxyBeanMethods = false)
public class EnhanceHttpSecurityConfiguration {

    private static final String BEAN_NAME_PREFIX = "org.springframework.security.config.annotation.org.springframework.security.config.annotation.web.configuration.EnhanceHttpSecurityConfiguration.";

    private static final String HTTPSECURITY_BEAN_NAME = BEAN_NAME_PREFIX + "enhanceHttpSecurity";

    private ObjectPostProcessor<Object> objectPostProcessor;

    private AuthenticationManager authenticationManager;

    private AuthenticationConfiguration authenticationConfiguration;

    private ApplicationContext context;

    @Autowired
    void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    void setAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Autowired
    void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Bean(HTTPSECURITY_BEAN_NAME)
    @Scope("prototype")
    EnhancedHttpSecurity enhancedHttpSecurity() throws Exception {
        WebSecurityConfigurerAdapter.LazyPasswordEncoder passwordEncoder = new WebSecurityConfigurerAdapter.LazyPasswordEncoder(
                this.context);
        AuthenticationManagerBuilder authenticationBuilder = new WebSecurityConfigurerAdapter.DefaultPasswordEncoderAuthenticationManagerBuilder(
                this.objectPostProcessor, passwordEncoder);


        authenticationBuilder.parentAuthenticationManager(authenticationManager());
        EnhancedHttpSecurity http = new EnhancedHttpSecurity(this.objectPostProcessor, authenticationBuilder, createSharedObjects());
        // @formatter:off
        http
                .csrf(Customizer.withDefaults())
                .addFilter(new WebAsyncManagerIntegrationFilter())
                .exceptionHandling(Customizer.withDefaults())
                .headers(Customizer.withDefaults())
                .sessionManagement(Customizer.withDefaults())
                .securityContext(Customizer.withDefaults())
                .requestCache(Customizer.withDefaults())
                .anonymous(Customizer.withDefaults())
                .servletApi(Customizer.withDefaults())
//                .apply(new DefaultLoginPageConfigurer<>())
        ;
        http.logout(Customizer.withDefaults());
        // @formatter:on
        return http;
    }

    private AuthenticationManager authenticationManager() throws Exception {
        return (this.authenticationManager != null) ? this.authenticationManager
                : this.authenticationConfiguration.getAuthenticationManager();
    }

    private Map<Class<?>, Object> createSharedObjects() {
        Map<Class<?>, Object> sharedObjects = new HashMap<>();
        sharedObjects.put(ApplicationContext.class, this.context);
        return sharedObjects;
    }

}
