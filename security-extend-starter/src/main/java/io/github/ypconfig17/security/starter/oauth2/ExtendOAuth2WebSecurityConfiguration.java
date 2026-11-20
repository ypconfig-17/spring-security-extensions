package io.github.ypconfig17.security.starter.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * @author yangpan
 */

@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnBean({ClientRegistrationRepository.class})
public class ExtendOAuth2WebSecurityConfiguration {

    ExtendOAuth2WebSecurityConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean
    OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }


    @Bean
    @ConditionalOnMissingBean
    OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

//    @Configuration(
//            proxyBeanMethods = false
//    )
//    @ConditionalOnDefaultWebSecurity
//    static class OAuth2SecurityFilterChainConfiguration {
//        OAuth2SecurityFilterChainConfiguration() {
//        }
//
//        @Bean
//        SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
//            http.authorizeRequests((requests) -> {
//                ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)requests.anyRequest()).authenticated();
//            });
//            http.oauth2Login(Customizer.withDefaults());
//            http.oauth2Client();
//            return http.build();
//        }
//    }
}
