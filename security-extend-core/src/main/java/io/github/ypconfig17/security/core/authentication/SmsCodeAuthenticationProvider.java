package io.github.ypconfig17.security.core.authentication;

import io.github.ypconfig17.security.core.authentication.AbstractAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author yangpan
 */
@Slf4j
public class SmsCodeAuthenticationProvider extends AbstractAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 1 获取token
        SmsCodeAuthenticationToken smsCodeAuthenticationToken = (SmsCodeAuthenticationToken) authentication;
        String username = determineUsername(authentication);
        String code = smsCodeAuthenticationToken.getCode();
        UserDetails userDetails;
        try {
             userDetails = authenticationService.smsAuthentication(username, code);
        } catch (AuthenticationException ex) {
            log.debug("用户认证失败:{}", username);
            throw  ex;
        }
        return createSuccessAuthentication(userDetails,authentication,userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
