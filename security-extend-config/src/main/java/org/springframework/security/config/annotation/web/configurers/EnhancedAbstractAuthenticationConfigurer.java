package org.springframework.security.config.annotation.web.configurers;


import io.github.ypconfig17.security.core.AuthenticationService;
import io.github.ypconfig17.security.core.authentication.AbstractAuthenticationProvider;
import io.github.ypconfig17.security.extend.web.authentication.DefaultAuthenticationEntryPoint;
import io.github.ypconfig17.security.extend.web.authentication.DefaultAuthenticationFailurelHandler;
import io.github.ypconfig17.security.extend.web.authentication.DefaultAuthenticationSucccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.*;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author yangpan
 */
public abstract class EnhancedAbstractAuthenticationConfigurer<B extends HttpSecurityBuilder<B>, T extends EnhancedAbstractAuthenticationConfigurer<B, T, F>, F extends AbstractAuthenticationProcessingFilter> extends AbstractHttpConfigurer<T, B> {


    private F authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    protected AuthenticationSuccessHandler successHandler;

    protected AuthenticationFailureHandler failureHandler ;

    private final AuthenticationEntryPoint authenticationEntryPoint = new DefaultAuthenticationEntryPoint();

    private String loginProcessingUrl;

    protected AuthenticationProvider authenticationProvider;

    private boolean permitAll;

    protected AuthenticationService authenticationService;

    /**
     * Creates a new instance with minimal defaults
     */
    protected EnhancedAbstractAuthenticationConfigurer() {
    }

    /**
     * Creates a new instance
     *
     * @param authenticationFilter      the {@link AbstractAuthenticationProcessingFilter} to
     *                                  use
     * @param defaultLoginProcessingUrl the default URL to use for
     *                                  {@link #loginProcessingUrl(String)}
     */
    protected EnhancedAbstractAuthenticationConfigurer(F authenticationFilter, String defaultLoginProcessingUrl) {
        this();
        this.authFilter = authenticationFilter;
        if (defaultLoginProcessingUrl != null) {
            loginProcessingUrl(defaultLoginProcessingUrl);
        }
    }


    /**
     * Specifies the URL to validate the credentials.
     *
     * @param loginProcessingUrl the URL to validate username and password
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public T loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        this.authFilter.setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
        return getSelf();
    }

    /**
     * Create the {@link RequestMatcher} given a loginProcessingUrl
     *
     * @param loginProcessingUrl creates the {@link RequestMatcher} based upon the
     *                           loginProcessingUrl
     * @return the {@link RequestMatcher} to use based upon the loginProcessingUrl
     */
    protected abstract RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl);

    /**
     * Specifies a custom {@link AuthenticationDetailsSource}. The default is
     * {@link WebAuthenticationDetailsSource}.
     *
     * @param authenticationDetailsSource the custom {@link AuthenticationDetailsSource}
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T authenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return getSelf();
    }

    /**
     * Specifies the {@link AuthenticationSuccessHandler} to be used. The default is
     * {@link SavedRequestAwareAuthenticationSuccessHandler} with no additional properties
     * set.
     *
     * @param successHandler the {@link AuthenticationSuccessHandler}.
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return getSelf();
    }

    /**
     * Equivalent of invoking permitAll(true)
     *
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T permitAll() {
        return permitAll(true);
    }


    public final T permitAll(boolean permitAll) {
        this.permitAll = permitAll;
        return getSelf();
    }
    public T provider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        return getSelf();
    }

    public T authenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        return getSelf();
    }

    /**
     * Specifies the {@link AuthenticationFailureHandler} to use when authentication
     * fails. The default is redirecting to "/login?error" using
     * {@link SimpleUrlAuthenticationFailureHandler}
     *
     * @param authenticationFailureHandler the {@link AuthenticationFailureHandler} to use
     *                                     when authentication fails.
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return getSelf();
    }

    @Override
    public void init(B http) throws Exception {
        updateAuthenticationDefaults();
        updateAccessDefaults(http);
        registerDefaultAuthenticationEntryPoint(http);

        if (this.authenticationProvider == null) {
            AbstractAuthenticationProvider provider = this.getAuthenticationProvider();
            this.authenticationProvider = provider;
            Assert.notNull(this.authenticationProvider, "AbstractAuthenticationProvider can not be null");

            if (this.authenticationService == null) {
                ResolvableType type = ResolvableType.forClass(AuthenticationService.class);
                authenticationService =  this.getBeanOrNull(type);
            }
            Assert.notNull(authenticationService, "AuthenticationService can not be null");
            provider.setAuthenticationService(authenticationService);
            postProcess(this.authenticationProvider);
        }
        http.authenticationProvider(authenticationProvider);



        ResolvableType objectMapperType = ResolvableType.forClass(ObjectMapper.class);
        ObjectMapper objectMapper =  this.getBeanOrNull(objectMapperType);

        MappingJackson2HttpMessageConverter httpMessageConverter ;
        if (objectMapper != null) {
            httpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        }else {
            httpMessageConverter = new MappingJackson2HttpMessageConverter();
        }
        Assert.notNull(httpMessageConverter, "MappingJackson2HttpMessageConverter can not be null");

        ResolvableType successHandler = ResolvableType.forClass(DefaultAuthenticationSucccessHandler.class);
        AuthenticationSuccessHandler authenticationSuccessHandler = getBeanOrNull(successHandler);
        if (authenticationSuccessHandler != null) {
            this.successHandler = authenticationSuccessHandler;
        }else{
            DefaultAuthenticationSucccessHandler defaultAuthenticationSucccessHandler = new DefaultAuthenticationSucccessHandler();
            defaultAuthenticationSucccessHandler.setMappingJackson2HttpMessageConverter(httpMessageConverter);
            this.successHandler = defaultAuthenticationSucccessHandler;
        }
        Assert.notNull(this.successHandler, "AuthenticationSuccessHandler can not be null");
        postProcess(this.successHandler);

        ResolvableType failureHandler = ResolvableType.forClass(DefaultAuthenticationFailurelHandler.class);
        AuthenticationFailureHandler authenticationFailureHandler =  this.getBeanOrNull(failureHandler);

        if (authenticationFailureHandler != null) {
            this.failureHandler = authenticationFailureHandler;
        }else{
            DefaultAuthenticationFailurelHandler defaultAuthenticationFailurelHandler = new DefaultAuthenticationFailurelHandler();
            defaultAuthenticationFailurelHandler.setMappingJackson2HttpMessageConverter(httpMessageConverter);
            this.failureHandler = defaultAuthenticationFailurelHandler;
        }

        Assert.notNull(this.failureHandler, "AuthenticationFailureHandler can not be null");
        postProcess(this.failureHandler);

    }




    protected abstract AbstractAuthenticationProvider getAuthenticationProvider();


    protected final void updateAccessDefaults(B http) {
        if (this.permitAll) {
            PermitAllSupport.permitAll(http, this.loginProcessingUrl);
        }
    }


    private <T> T getBeanOrNull(ResolvableType type) {
        ApplicationContext context = getBuilder().getSharedObject(ApplicationContext.class);
        if (context != null) {
            String[] names = context.getBeanNamesForType(type);
            if (names.length == 1) {
                return (T) context.getBean(names[0]);
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    protected final void registerDefaultAuthenticationEntryPoint(B http) {
        registerAuthenticationEntryPoint(http, this.authenticationEntryPoint);
    }

    @SuppressWarnings("unchecked")
    protected final void registerAuthenticationEntryPoint(B http, AuthenticationEntryPoint authenticationEntryPoint) {
        ExceptionHandlingConfigurer<B> exceptionHandling = http.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        exceptionHandling.defaultAuthenticationEntryPointFor(postProcess(authenticationEntryPoint),
                getAuthenticationEntryPointMatcher(http));
    }

    protected final RequestMatcher getAuthenticationEntryPointMatcher(B http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher mediaMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy,
                MediaType.APPLICATION_XHTML_XML, new MediaType("image", "*"), MediaType.TEXT_HTML,
                MediaType.TEXT_PLAIN);
        mediaMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        return new AndRequestMatcher(Arrays.asList(notXRequestedWith, mediaMatcher));
    }

    @Override
    public void configure(B http) throws Exception {
        this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
        this.authFilter.setAuthenticationFailureHandler(this.failureHandler);
        if (this.authenticationDetailsSource != null) {
            this.authFilter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
        }
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            this.authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            this.authFilter.setRememberMeServices(rememberMeServices);
        }
        F filter = postProcess(this.authFilter);
        http.addFilter(filter);
    }


    /**
     * Gets the Authentication Filter
     *
     * @return the Authentication Filter
     */
    protected final F getAuthenticationFilter() {
        return this.authFilter;
    }

    /**
     * Sets the Authentication Filter
     *
     * @param authFilter the Authentication Filter
     */
    protected final void setAuthenticationFilter(F authFilter) {
        this.authFilter = authFilter;
    }


    /**
     * Gets the Authentication Entry Point
     *
     * @return the Authentication Entry Point
     */
    protected final AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.authenticationEntryPoint;
    }

    /**
     * Gets the URL to submit an authentication request to (i.e. where username/password
     * must be submitted)
     *
     * @return the URL to submit an authentication request to
     */
    protected final String getLoginProcessingUrl() {
        return this.loginProcessingUrl;
    }


    /**
     * Updates the default values for authentication.
     *
     * @throws Exception
     */
    protected final void updateAuthenticationDefaults() {
        if (this.loginProcessingUrl == null) {
            loginProcessingUrl("/login");
        }

        LogoutConfigurer<B> logoutConfigurer = getBuilder().getConfigurer(LogoutConfigurer.class);
        if (logoutConfigurer != null && !logoutConfigurer.isCustomLogoutSuccess()) {
            logoutConfigurer.logoutSuccessUrl("/logout");
        }
    }


    @SuppressWarnings("unchecked")
    private T getSelf() {
        return (T) this;
    }

}
