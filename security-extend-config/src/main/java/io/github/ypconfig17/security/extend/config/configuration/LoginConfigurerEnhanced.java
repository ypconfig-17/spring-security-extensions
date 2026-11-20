package io.github.ypconfig17.security.extend.config.configuration;

import io.github.ypconfig17.security.core.authentication.AbstractAuthenticationProvider;
import io.github.ypconfig17.security.extend.web.authentication.LoginAuthenticationFilter;
import io.github.ypconfig17.security.core.authentication.LoginAuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.EnhancedAbstractAuthenticationConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author yangpan
 */
public class LoginConfigurerEnhanced<H extends HttpSecurityBuilder<H>> extends EnhancedAbstractAuthenticationConfigurer<H, LoginConfigurerEnhanced<H>, LoginAuthenticationFilter> {


    public LoginConfigurerEnhanced() {
        super(new LoginAuthenticationFilter(), LoginAuthenticationFilter.DEFAULT_FILTERP_ROCESSESURL);
        usernameParameter(LoginAuthenticationFilter.DEFAULT_USERNAME_PARAMETER);
        passwordParameter(LoginAuthenticationFilter.DEFAULT_PASSWORD_PARAMETER);
    }
    public LoginConfigurerEnhanced<H> usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    public LoginConfigurerEnhanced<H> passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }


    @Override
    protected AbstractAuthenticationProvider getAuthenticationProvider() {
        return new LoginAuthenticationProvider();
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    private String getUsernameParameter() {
        return getAuthenticationFilter().getUsernameParameter();
    }

    private String getPasswordParameter() {
        return getAuthenticationFilter().getPasswordParameter();
    }

}
