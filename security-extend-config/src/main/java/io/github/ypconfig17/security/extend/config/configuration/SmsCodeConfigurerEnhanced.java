package io.github.ypconfig17.security.extend.config.configuration;

import io.github.ypconfig17.security.extend.web.authentication.SmsCodeAuthenticationFilter;
import io.github.ypconfig17.security.core.authentication.SmsCodeAuthenticationProvider;
import org.springframework.security.config.annotation.web.configurers.EnhancedAbstractAuthenticationConfigurer;
import io.github.ypconfig17.security.core.authentication.AbstractAuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author yangpan
 */
public class SmsCodeConfigurerEnhanced<H extends HttpSecurityBuilder<H>> extends EnhancedAbstractAuthenticationConfigurer<H, SmsCodeConfigurerEnhanced<H>, SmsCodeAuthenticationFilter> {

    public SmsCodeConfigurerEnhanced() {
        super(new SmsCodeAuthenticationFilter(), SmsCodeAuthenticationFilter.DEFAULT_FILTERP_ROCESSESURL);
        mobileParameter(SmsCodeAuthenticationFilter.DEFAULT_MOBILE_PARAMETER);
        codeParameter(SmsCodeAuthenticationFilter.DEFAULT_CODE_PARAMETER);
    }

    public SmsCodeConfigurerEnhanced<H> mobileParameter(String usernameParameter) {
        getAuthenticationFilter().setMobileParameter(usernameParameter);
        return this;
    }
    public SmsCodeConfigurerEnhanced<H> codeParameter(String passwordParameter) {
        getAuthenticationFilter().setCodeParameter(passwordParameter);
        return this;
    }


    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    @Override
    protected AbstractAuthenticationProvider getAuthenticationProvider() {
        return new SmsCodeAuthenticationProvider();
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }



}
