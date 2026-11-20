package io.github.ypconfig17.security.core.authentication;

import io.github.ypconfig17.security.core.AuthenticationService;
import io.github.ypconfig17.security.core.GeneralAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author yangpan
 */
@Slf4j
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    protected AuthenticationService authenticationService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public GrantedAuthoritiesMapper getAuthoritiesMapper() {
        return authoritiesMapper;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        GeneralAuthenticationToken result = new GeneralAuthenticationToken(principal, authentication.getCredentials(), this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());
        log.debug("认证成功的用户,username:{}",user.getUsername());
        return result;
    }

    protected String determineUsername(Authentication authentication) {
        return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
    }


}
