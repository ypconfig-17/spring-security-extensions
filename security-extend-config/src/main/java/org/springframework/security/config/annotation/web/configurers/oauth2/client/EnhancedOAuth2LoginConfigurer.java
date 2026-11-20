package org.springframework.security.config.annotation.web.configurers.oauth2.client;

import io.github.ypconfig17.security.core.AuthenticationService;
import io.github.ypconfig17.security.extend.web.authentication.DefaultAuthenticationFailurelHandler;
import io.github.ypconfig17.security.extend.web.authentication.DefaultAuthenticationSucccessHandler;
import io.github.ypconfig17.security.oauth.client.ExtendOAuth2AuthenticationProvider;
import io.github.ypconfig17.security.oauth.client.web.ExtendOAuth2LoginAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * @author yangpan
 */
public class EnhancedOAuth2LoginConfigurer<B extends HttpSecurityBuilder<B>>
        extends AbstractAuthenticationFilterConfigurer<B, EnhancedOAuth2LoginConfigurer<B>, ExtendOAuth2LoginAuthenticationFilter> {

    private final AuthorizationEndpointConfig authorizationEndpointConfig = new AuthorizationEndpointConfig();

    private final TokenEndpointConfig tokenEndpointConfig = new TokenEndpointConfig();

    private final RedirectionEndpointConfig redirectionEndpointConfig = new RedirectionEndpointConfig();

    private final UserInfoEndpointConfig userInfoEndpointConfig = new UserInfoEndpointConfig();

    private String loginPage;

    private String loginProcessingUrl = ExtendOAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI;

    private AuthenticationService authenticationService;

    /**
     * Sets the repository of client registrations.
     * @param clientRegistrationRepository the repository of client registrations
     * @return the {@link EnhancedOAuth2LoginConfigurer} for further configuration
     */
    public EnhancedOAuth2LoginConfigurer<B> clientRegistrationRepository(
            ClientRegistrationRepository clientRegistrationRepository) {
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        this.getBuilder().setSharedObject(ClientRegistrationRepository.class, clientRegistrationRepository);
        return this;
    }

    /**
     * Sets the repository for authorized client(s).
     * @param authorizedClientRepository the authorized client repository
     * @return the {@link EnhancedOAuth2LoginConfigurer} for further configuration
     * @since 5.1
     */
    public EnhancedOAuth2LoginConfigurer<B> authorizedClientRepository(
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be null");
        this.getBuilder().setSharedObject(OAuth2AuthorizedClientRepository.class, authorizedClientRepository);
        return this;
    }

    /**
     * Sets the service for authorized client(s).
     * @param authorizedClientService the authorized client service
     * @return the {@link EnhancedOAuth2LoginConfigurer} for further configuration
     */
    public EnhancedOAuth2LoginConfigurer<B> authorizedClientService(OAuth2AuthorizedClientService authorizedClientService) {
        Assert.notNull(authorizedClientService, "authorizedClientService cannot be null");
        this.authorizedClientRepository(
                new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService));
        return this;
    }

    public EnhancedOAuth2LoginConfigurer<B> authorizedClientService(AuthenticationService authenticationService) {
        Assert.notNull(authenticationService, "authenticationService cannot be null");
        this.authenticationService = authenticationService;
        return this;
    }


    @Override
    public EnhancedOAuth2LoginConfigurer<B> loginPage(String loginPage) {
        Assert.hasText(loginPage, "loginPage cannot be empty");
        this.loginPage = loginPage;
        return this;
    }

    @Override
    public EnhancedOAuth2LoginConfigurer<B> loginProcessingUrl(String loginProcessingUrl) {
        Assert.hasText(loginProcessingUrl, "loginProcessingUrl cannot be empty");
        this.loginProcessingUrl = loginProcessingUrl;
        return this;
    }

    /**
     * Returns the {@link AuthorizationEndpointConfig} for configuring the Authorization
     * Server's Authorization Endpoint.
     * @return the {@link AuthorizationEndpointConfig}
     */
    public AuthorizationEndpointConfig authorizationEndpoint() {
        return this.authorizationEndpointConfig;
    }

    /**
     * Configures the Authorization Server's Authorization Endpoint.
     * @param authorizationEndpointCustomizer the {@link Customizer} to provide more
     * options for the {@link AuthorizationEndpointConfig}
     * @return the {@link EnhancedOAuth2LoginConfigurer} for further customizations
     */
    public EnhancedOAuth2LoginConfigurer<B> authorizationEndpoint(
            Customizer<AuthorizationEndpointConfig> authorizationEndpointCustomizer) {
        authorizationEndpointCustomizer.customize(this.authorizationEndpointConfig);
        return this;
    }

    /**
     * Returns the {@link TokenEndpointConfig} for configuring the Authorization Server's
     * Token Endpoint.
     * @return the {@link TokenEndpointConfig}
     */
    public TokenEndpointConfig tokenEndpoint() {
        return this.tokenEndpointConfig;
    }

    /**
     * Configures the Authorization Server's Token Endpoint.
     * @param tokenEndpointCustomizer the {@link Customizer} to provide more options for
     * the {@link TokenEndpointConfig}
     * @return the {@link EnhancedOAuth2LoginConfigurer} for further customizations
     * @throws Exception
     */
    public EnhancedOAuth2LoginConfigurer<B> tokenEndpoint(Customizer<TokenEndpointConfig> tokenEndpointCustomizer) {
        tokenEndpointCustomizer.customize(this.tokenEndpointConfig);
        return this;
    }

    /**
     * Returns the {@link RedirectionEndpointConfig} for configuring the Client's
     * Redirection Endpoint.
     * @return the {@link RedirectionEndpointConfig}
     */
    public RedirectionEndpointConfig redirectionEndpoint() {
        return this.redirectionEndpointConfig;
    }

    /**
     * Configures the Client's Redirection Endpoint.
     * @param redirectionEndpointCustomizer the {@link Customizer} to provide more options
     * for the {@link RedirectionEndpointConfig}
     * @return the {@link EnhancedOAuth2LoginConfigurer} for further customizations
     */
    public EnhancedOAuth2LoginConfigurer<B> redirectionEndpoint(
            Customizer<RedirectionEndpointConfig> redirectionEndpointCustomizer) {
        redirectionEndpointCustomizer.customize(this.redirectionEndpointConfig);
        return this;
    }

    /**
     * Returns the {@link UserInfoEndpointConfig} for configuring the Authorization
     * Server's UserInfo Endpoint.
     * @return the {@link UserInfoEndpointConfig}
     */
    public UserInfoEndpointConfig userInfoEndpoint() {
        return this.userInfoEndpointConfig;
    }

    /**
     * Configures the Authorization Server's UserInfo Endpoint.
     * @param userInfoEndpointCustomizer the {@link Customizer} to provide more options
     * for the {@link UserInfoEndpointConfig}
     * @return the {@link EnhancedOAuth2LoginConfigurer} for further customizations
     */
    public EnhancedOAuth2LoginConfigurer<B> userInfoEndpoint(Customizer<UserInfoEndpointConfig> userInfoEndpointCustomizer) {
        userInfoEndpointCustomizer.customize(this.userInfoEndpointConfig);
        return this;
    }

    @Override
    public void init(B http) throws Exception {
        ExtendOAuth2LoginAuthenticationFilter authenticationFilter = new ExtendOAuth2LoginAuthenticationFilter(
                OAuth2ClientConfigurerUtils.getClientRegistrationRepository(this.getBuilder()),
                OAuth2ClientConfigurerUtils.getAuthorizedClientRepository(this.getBuilder()), this.loginProcessingUrl);
        this.setAuthenticationFilter(authenticationFilter);
        super.loginProcessingUrl(this.loginProcessingUrl);
        if (this.loginPage != null) {
            // Set custom login page
            super.loginPage(this.loginPage);
            super.init(http);
        }
        else {
            Map<String, String> loginUrlToClientName = this.getLoginLinks();
            if (loginUrlToClientName.size() == 1) {
                // Setup auto-redirect to provider login page
                // when only 1 client is configured
                this.updateAuthenticationDefaults();
                this.updateAccessDefaults(http);
                String providerLoginPage = loginUrlToClientName.keySet().iterator().next();
                this.registerAuthenticationEntryPoint(http, this.getLoginEntryPoint(http, providerLoginPage));
            }
            else {
                super.init(http);
            }
        }
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient = this.tokenEndpointConfig.accessTokenResponseClient;
        if (accessTokenResponseClient == null) {
            accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        }
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = getOAuth2UserService();

        // customer
//        OAuth2LoginAuthenticationProvider oauth2LoginAuthenticationProvider = new OAuth2LoginAuthenticationProvider(
//                accessTokenResponseClient, oauth2UserService);
        ExtendOAuth2AuthenticationProvider oauth2LoginAuthenticationProvider = new ExtendOAuth2AuthenticationProvider(
                accessTokenResponseClient, oauth2UserService);
        if (this.authenticationService == null){
            ResolvableType type = ResolvableType.forClass(AuthenticationService.class);
            this.authenticationService = getBeanOrNull(type);

        }
        Assert.notNull(this.authenticationService, "authenticationService cannot be null");
        oauth2LoginAuthenticationProvider.setAuthenticationService(this.authenticationService);
        // end
        GrantedAuthoritiesMapper userAuthoritiesMapper = this.getGrantedAuthoritiesMapper();
        if (userAuthoritiesMapper != null) {
            oauth2LoginAuthenticationProvider.setAuthoritiesMapper(userAuthoritiesMapper);
        }
        http.authenticationProvider(this.postProcess(oauth2LoginAuthenticationProvider));
        boolean oidcAuthenticationProviderEnabled = ClassUtils
                .isPresent("org.springframework.security.oauth2.jwt.JwtDecoder", this.getClass().getClassLoader());
        if (oidcAuthenticationProviderEnabled) {
            OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService = getOidcUserService();
            OidcAuthorizationCodeAuthenticationProvider oidcAuthorizationCodeAuthenticationProvider = new OidcAuthorizationCodeAuthenticationProvider(
                    accessTokenResponseClient, oidcUserService);
            JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = this.getJwtDecoderFactoryBean();
            if (jwtDecoderFactory != null) {
                oidcAuthorizationCodeAuthenticationProvider.setJwtDecoderFactory(jwtDecoderFactory);
            }
            if (userAuthoritiesMapper != null) {
                oidcAuthorizationCodeAuthenticationProvider.setAuthoritiesMapper(userAuthoritiesMapper);
            }
            http.authenticationProvider(this.postProcess(oidcAuthorizationCodeAuthenticationProvider));
        }
        else {
            http.authenticationProvider(new OidcAuthenticationRequestChecker());
        }
        this.initDefaultLoginFilter(http);
        //设置 handler
        this.initHandlers();




    }
    //设置 handler
    void  initHandlers(){

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

        if (authenticationSuccessHandler == null){
            DefaultAuthenticationSucccessHandler defaultAuthenticationSucccessHandler = new DefaultAuthenticationSucccessHandler();
            defaultAuthenticationSucccessHandler.setMappingJackson2HttpMessageConverter(httpMessageConverter);
            authenticationSuccessHandler = defaultAuthenticationSucccessHandler;
        }

        Assert.notNull(authenticationSuccessHandler, "AuthenticationSuccessHandler can not be null");
        postProcess(authenticationSuccessHandler);
        this.successHandler(authenticationSuccessHandler);



        ResolvableType failureHandler = ResolvableType.forClass(DefaultAuthenticationFailurelHandler.class);
        AuthenticationFailureHandler authenticationFailureHandler =  this.getBeanOrNull(failureHandler);

        if (authenticationFailureHandler == null) {
            DefaultAuthenticationFailurelHandler defaultAuthenticationFailurelHandler = new DefaultAuthenticationFailurelHandler();
            defaultAuthenticationFailurelHandler.setMappingJackson2HttpMessageConverter(httpMessageConverter);
            authenticationFailureHandler =  defaultAuthenticationFailurelHandler;
        }

        Assert.notNull(authenticationFailureHandler, "AuthenticationFailureHandler can not be null");
        postProcess(authenticationFailureHandler);
        this.failureHandler(authenticationFailureHandler);

    }



    @Override
    public void configure(B http) throws Exception {
        OAuth2AuthorizationRequestRedirectFilter authorizationRequestFilter;
        if (this.authorizationEndpointConfig.authorizationRequestResolver != null) {
            authorizationRequestFilter = new OAuth2AuthorizationRequestRedirectFilter(
                    this.authorizationEndpointConfig.authorizationRequestResolver);
        }
        else {
            String authorizationRequestBaseUri = this.authorizationEndpointConfig.authorizationRequestBaseUri;
            if (authorizationRequestBaseUri == null) {
                authorizationRequestBaseUri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
            }
            authorizationRequestFilter = new OAuth2AuthorizationRequestRedirectFilter(
                    OAuth2ClientConfigurerUtils.getClientRegistrationRepository(this.getBuilder()),
                    authorizationRequestBaseUri);
        }
        if (this.authorizationEndpointConfig.authorizationRequestRepository != null) {
            authorizationRequestFilter
                    .setAuthorizationRequestRepository(this.authorizationEndpointConfig.authorizationRequestRepository);
        }
        RequestCache requestCache = http.getSharedObject(RequestCache.class);
        if (requestCache != null) {
            authorizationRequestFilter.setRequestCache(requestCache);
        }
        http.addFilter(this.postProcess(authorizationRequestFilter));
        ExtendOAuth2LoginAuthenticationFilter authenticationFilter = this.getAuthenticationFilter();
        if (this.redirectionEndpointConfig.authorizationResponseBaseUri != null) {
            authenticationFilter.setFilterProcessesUrl(this.redirectionEndpointConfig.authorizationResponseBaseUri);
        }
        if (this.authorizationEndpointConfig.authorizationRequestRepository != null) {
            authenticationFilter
                    .setAuthorizationRequestRepository(this.authorizationEndpointConfig.authorizationRequestRepository);
        }
        super.configure(http);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl);
    }

    @SuppressWarnings("unchecked")
    private JwtDecoderFactory<ClientRegistration> getJwtDecoderFactoryBean() {
        ResolvableType type = ResolvableType.forClassWithGenerics(JwtDecoderFactory.class, ClientRegistration.class);
        String[] names = this.getBuilder().getSharedObject(ApplicationContext.class).getBeanNamesForType(type);
        if (names.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, names);
        }
        if (names.length == 1) {
            return (JwtDecoderFactory<ClientRegistration>) this.getBuilder().getSharedObject(ApplicationContext.class)
                    .getBean(names[0]);
        }
        return null;
    }

    private GrantedAuthoritiesMapper getGrantedAuthoritiesMapper() {
        GrantedAuthoritiesMapper grantedAuthoritiesMapper = this.getBuilder()
                .getSharedObject(GrantedAuthoritiesMapper.class);
        if (grantedAuthoritiesMapper == null) {
            grantedAuthoritiesMapper = this.getGrantedAuthoritiesMapperBean();
            if (grantedAuthoritiesMapper != null) {
                this.getBuilder().setSharedObject(GrantedAuthoritiesMapper.class, grantedAuthoritiesMapper);
            }
        }
        return grantedAuthoritiesMapper;
    }

    private GrantedAuthoritiesMapper getGrantedAuthoritiesMapperBean() {
        Map<String, GrantedAuthoritiesMapper> grantedAuthoritiesMapperMap = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(this.getBuilder().getSharedObject(ApplicationContext.class),
                        GrantedAuthoritiesMapper.class);
        return (!grantedAuthoritiesMapperMap.isEmpty() ? grantedAuthoritiesMapperMap.values().iterator().next() : null);
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> getOidcUserService() {
        if (this.userInfoEndpointConfig.oidcUserService != null) {
            return this.userInfoEndpointConfig.oidcUserService;
        }
        ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2UserService.class, OidcUserRequest.class,
                OidcUser.class);
        OAuth2UserService<OidcUserRequest, OidcUser> bean = getBeanOrNull(type);
        return (bean != null) ? bean : new OidcUserService();
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> getOAuth2UserService() {
        if (this.userInfoEndpointConfig.userService != null) {
            return this.userInfoEndpointConfig.userService;
        }
        ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2UserService.class, OAuth2UserRequest.class,
                OAuth2User.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> bean = getBeanOrNull(type);
        if (bean != null) {
            return bean;
        }
        if (this.userInfoEndpointConfig.customUserTypes.isEmpty()) {
            return new DefaultOAuth2UserService();
        }
        List<OAuth2UserService<OAuth2UserRequest, OAuth2User>> userServices = new ArrayList<>();
        userServices.add(new CustomUserTypesOAuth2UserService(this.userInfoEndpointConfig.customUserTypes));
        userServices.add(new DefaultOAuth2UserService());
        return new DelegatingOAuth2UserService<>(userServices);
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

    private void initDefaultLoginFilter(B http) {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
                .getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if (loginPageGeneratingFilter == null || this.isCustomLoginPage()) {
            return;
        }
        loginPageGeneratingFilter.setOauth2LoginEnabled(true);
        loginPageGeneratingFilter.setOauth2AuthenticationUrlToClientName(this.getLoginLinks());
        loginPageGeneratingFilter.setLoginPageUrl(this.getLoginPage());
        loginPageGeneratingFilter.setFailureUrl(this.getFailureUrl());
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getLoginLinks() {
        Iterable<ClientRegistration> clientRegistrations = null;
        ClientRegistrationRepository clientRegistrationRepository = OAuth2ClientConfigurerUtils
                .getClientRegistrationRepository(this.getBuilder());
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }
        if (clientRegistrations == null) {
            return Collections.emptyMap();
        }
        String authorizationRequestBaseUri = (this.authorizationEndpointConfig.authorizationRequestBaseUri != null)
                ? this.authorizationEndpointConfig.authorizationRequestBaseUri
                : OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
        Map<String, String> loginUrlToClientName = new HashMap<>();
        clientRegistrations.forEach((registration) -> {
            if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(registration.getAuthorizationGrantType())) {
                String authorizationRequestUri = authorizationRequestBaseUri + "/" + registration.getRegistrationId();
                loginUrlToClientName.put(authorizationRequestUri, registration.getClientName());
            }
        });
        return loginUrlToClientName;
    }

    private AuthenticationEntryPoint getLoginEntryPoint(B http, String providerLoginPage) {
        RequestMatcher loginPageMatcher = new AntPathRequestMatcher(this.getLoginPage());
        RequestMatcher faviconMatcher = new AntPathRequestMatcher("/favicon.ico");
        RequestMatcher defaultEntryPointMatcher = this.getAuthenticationEntryPointMatcher(http);
        RequestMatcher defaultLoginPageMatcher = new AndRequestMatcher(
                new OrRequestMatcher(loginPageMatcher, faviconMatcher), defaultEntryPointMatcher);
        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(new AndRequestMatcher(notXRequestedWith, new NegatedRequestMatcher(defaultLoginPageMatcher)),
                new LoginUrlAuthenticationEntryPoint(providerLoginPage));
        DelegatingAuthenticationEntryPoint loginEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        loginEntryPoint.setDefaultEntryPoint(this.getAuthenticationEntryPoint());
        return loginEntryPoint;
    }

    /**
     * Configuration options for the Authorization Server's Authorization Endpoint.
     */
    public final class AuthorizationEndpointConfig {

        private String authorizationRequestBaseUri;

        private OAuth2AuthorizationRequestResolver authorizationRequestResolver;

        private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

        private AuthorizationEndpointConfig() {
        }

        /**
         * Sets the base {@code URI} used for authorization requests.
         * @param authorizationRequestBaseUri the base {@code URI} used for authorization
         * requests
         * @return the {@link AuthorizationEndpointConfig} for further configuration
         */
        public AuthorizationEndpointConfig baseUri(String authorizationRequestBaseUri) {
            Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
            this.authorizationRequestBaseUri = authorizationRequestBaseUri;
            return this;
        }

        /**
         * Sets the resolver used for resolving {@link OAuth2AuthorizationRequest}'s.
         * @param authorizationRequestResolver the resolver used for resolving
         * {@link OAuth2AuthorizationRequest}'s
         * @return the {@link AuthorizationEndpointConfig} for further configuration
         * @since 5.1
         */
        public AuthorizationEndpointConfig authorizationRequestResolver(
                OAuth2AuthorizationRequestResolver authorizationRequestResolver) {
            Assert.notNull(authorizationRequestResolver, "authorizationRequestResolver cannot be null");
            this.authorizationRequestResolver = authorizationRequestResolver;
            return this;
        }

        /**
         * Sets the repository used for storing {@link OAuth2AuthorizationRequest}'s.
         * @param authorizationRequestRepository the repository used for storing
         * {@link OAuth2AuthorizationRequest}'s
         * @return the {@link AuthorizationEndpointConfig} for further configuration
         */
        public AuthorizationEndpointConfig authorizationRequestRepository(
                AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
            Assert.notNull(authorizationRequestRepository, "authorizationRequestRepository cannot be null");
            this.authorizationRequestRepository = authorizationRequestRepository;
            return this;
        }

        /**
         * Returns the {@link EnhancedOAuth2LoginConfigurer} for further configuration.
         * @return the {@link EnhancedOAuth2LoginConfigurer}
         */
        public EnhancedOAuth2LoginConfigurer<B> and() {
            return EnhancedOAuth2LoginConfigurer.this;
        }

    }

    /**
     * Configuration options for the Authorization Server's Token Endpoint.
     */
    public final class TokenEndpointConfig {

        private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

        private TokenEndpointConfig() {
        }

        /**
         * Sets the client used for requesting the access token credential from the Token
         * Endpoint.
         * @param accessTokenResponseClient the client used for requesting the access
         * token credential from the Token Endpoint
         * @return the {@link TokenEndpointConfig} for further configuration
         */
        public TokenEndpointConfig accessTokenResponseClient(
                OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
            Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
            this.accessTokenResponseClient = accessTokenResponseClient;
            return this;
        }

        /**
         * Returns the {@link EnhancedOAuth2LoginConfigurer} for further configuration.
         * @return the {@link EnhancedOAuth2LoginConfigurer}
         */
        public EnhancedOAuth2LoginConfigurer<B> and() {
            return EnhancedOAuth2LoginConfigurer.this;
        }

    }

    /**
     * Configuration options for the Client's Redirection Endpoint.
     */
    public final class RedirectionEndpointConfig {

        private String authorizationResponseBaseUri;

        private RedirectionEndpointConfig() {
        }

        /**
         * Sets the {@code URI} where the authorization response will be processed.
         * @param authorizationResponseBaseUri the {@code URI} where the authorization
         * response will be processed
         * @return the {@link RedirectionEndpointConfig} for further configuration
         */
        public RedirectionEndpointConfig baseUri(String authorizationResponseBaseUri) {
            Assert.hasText(authorizationResponseBaseUri, "authorizationResponseBaseUri cannot be empty");
            this.authorizationResponseBaseUri = authorizationResponseBaseUri;
            return this;
        }

        /**
         * Returns the {@link EnhancedOAuth2LoginConfigurer} for further configuration.
         * @return the {@link EnhancedOAuth2LoginConfigurer}
         */
        public EnhancedOAuth2LoginConfigurer<B> and() {
            return EnhancedOAuth2LoginConfigurer.this;
        }

    }

    /**
     * Configuration options for the Authorization Server's UserInfo Endpoint.
     */
    public final class UserInfoEndpointConfig {

        private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

        private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

        private Map<String, Class<? extends OAuth2User>> customUserTypes = new HashMap<>();

        private UserInfoEndpointConfig() {
        }

        /**
         * Sets the OAuth 2.0 service used for obtaining the user attributes of the
         * End-User from the UserInfo Endpoint.
         * @param userService the OAuth 2.0 service used for obtaining the user attributes
         * of the End-User from the UserInfo Endpoint
         * @return the {@link UserInfoEndpointConfig} for further configuration
         */
        public UserInfoEndpointConfig userService(OAuth2UserService<OAuth2UserRequest, OAuth2User> userService) {
            Assert.notNull(userService, "userService cannot be null");
            this.userService = userService;
            return this;
        }

        /**
         * Sets the OpenID Connect 1.0 service used for obtaining the user attributes of
         * the End-User from the UserInfo Endpoint.
         * @param oidcUserService the OpenID Connect 1.0 service used for obtaining the
         * user attributes of the End-User from the UserInfo Endpoint
         * @return the {@link UserInfoEndpointConfig} for further configuration
         */
        public UserInfoEndpointConfig oidcUserService(OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) {
            Assert.notNull(oidcUserService, "oidcUserService cannot be null");
            this.oidcUserService = oidcUserService;
            return this;
        }

        /**
         * Sets a custom {@link OAuth2User} type and associates it to the provided client
         * {@link ClientRegistration#getRegistrationId() registration identifier}.
         * @param customUserType a custom {@link OAuth2User} type
         * @param clientRegistrationId the client registration identifier
         * @return the {@link UserInfoEndpointConfig} for further configuration
         * @deprecated See {@link CustomUserTypesOAuth2UserService} for alternative usage.
         */
        @Deprecated
        public UserInfoEndpointConfig customUserType(Class<? extends OAuth2User> customUserType,
                                                                           String clientRegistrationId) {
            Assert.notNull(customUserType, "customUserType cannot be null");
            Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
            this.customUserTypes.put(clientRegistrationId, customUserType);
            return this;
        }

        /**
         * Sets the {@link GrantedAuthoritiesMapper} used for mapping
         * {@link OAuth2User#getAuthorities()}.
         * @param userAuthoritiesMapper the {@link GrantedAuthoritiesMapper} used for
         * mapping the user's authorities
         * @return the {@link UserInfoEndpointConfig} for further configuration
         */
        public UserInfoEndpointConfig userAuthoritiesMapper(GrantedAuthoritiesMapper userAuthoritiesMapper) {
            Assert.notNull(userAuthoritiesMapper, "userAuthoritiesMapper cannot be null");
            EnhancedOAuth2LoginConfigurer.this.getBuilder().setSharedObject(GrantedAuthoritiesMapper.class,
                    userAuthoritiesMapper);
            return this;
        }

        /**
         * Returns the {@link EnhancedOAuth2LoginConfigurer} for further configuration.
         * @return the {@link EnhancedOAuth2LoginConfigurer}
         */
        public EnhancedOAuth2LoginConfigurer<B> and() {
            return EnhancedOAuth2LoginConfigurer.this;
        }

    }

    private static class OidcAuthenticationRequestChecker implements AuthenticationProvider {

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            OAuth2LoginAuthenticationToken authorizationCodeAuthentication = (OAuth2LoginAuthenticationToken) authentication;
            OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication.getAuthorizationExchange()
                    .getAuthorizationRequest();
            if (authorizationRequest.getScopes().contains(OidcScopes.OPENID)) {
                // Section 3.1.2.1 Authentication Request -
                // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest scope
                // REQUIRED. OpenID Connect requests MUST contain the "openid" scope
                // value.
                OAuth2Error oauth2Error = new OAuth2Error("oidc_provider_not_configured",
                        "An OpenID Connect Authentication Provider has not been configured. "
                                + "Check to ensure you include the dependency 'spring-security-oauth2-jose'.",
                        null);
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }
            return null;
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
        }

    }

}
