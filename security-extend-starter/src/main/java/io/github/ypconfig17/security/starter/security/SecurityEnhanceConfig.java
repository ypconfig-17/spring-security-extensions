package io.github.ypconfig17.security.starter.security;

import io.github.ypconfig17.common.http.HttpResponseUtils;
import io.github.ypconfig17.security.extend.config.configuration.web.EnableSecurityEnhance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.EnhancedHttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author yangpan
 */

@Slf4j
@EnableSecurityEnhance
public class SecurityEnhanceConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(EnhancedHttpSecurity http) throws Exception {
        log.info("security 安全配置启动...");
        http.authorizeHttpRequests()
                .antMatchers("/").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .login()
                .loginProcessingUrl("/form/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .smsLogin()
                .mobileParameter("mobile")
                .codeParameter("code")
                .and()
                .oauth2Login()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> HttpResponseUtils.responseJson(response, "无权访问"))
                .and()
                .logout()
//                        .logoutSuccessHandler()
                .and()
        ;
        http
                .cors()
                .and()
                .csrf().disable()
        ;
        return http.build();
    }

}
