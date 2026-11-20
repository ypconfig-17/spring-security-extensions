package io.github.ypconfig17.security.starter.oauth2;

import io.github.ypconfig17.security.extend.config.configuration.web.EnableSecurityEnhance;
import io.github.ypconfig17.security.starter.security.SecurityEnhanceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;


/**
 * @author yangpan
 * @version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(value = {SecurityAutoConfiguration.class, SecurityEnhanceConfig.class})
@ConditionalOnClass({ EnableSecurityEnhance.class, ClientRegistration.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({ ExtendOAuth2ClientRegistrationRepositoryConfiguration.class, ExtendOAuth2WebSecurityConfiguration.class })
public class ExtendOAuthClientAutoConfiguration {



}
