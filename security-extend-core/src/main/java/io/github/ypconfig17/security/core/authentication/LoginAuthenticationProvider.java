package io.github.ypconfig17.security.core.authentication;

import io.github.ypconfig17.security.core.DefaultPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * 不同于 {@link DaoAuthenticationProvider}
 * 该类把用户认证的能力全部外放给用户,以及密码的验证。我认为这种形式更灵活，更易于排查问题和理解
 * 用户自己判断需要抛出什么异常的类型
 * 但必须是 {@link AuthenticationException}
 * @author yangpan
 */
@Slf4j
public class LoginAuthenticationProvider extends AbstractAuthenticationProvider {

    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private volatile String userNotFoundEncodedPassword;
    private final PasswordEncoder passwordEncoder = new DefaultPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginAuthenticationToken loginAuthenticationToken = (LoginAuthenticationToken) authentication;
        String username = determineUsername(authentication);
        String presentedPassword = authentication.getCredentials().toString();
        UserDetails loadedUser = null;
        prepareTimingAttackProtection();
        try {
            loadedUser = this.getAuthenticationService().authentication(username,presentedPassword);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("AuthenticationService不应该返回null");
            }
            additionalAuthenticationChecks(loadedUser,loginAuthenticationToken);
        } catch (UsernameNotFoundException ex) {
            log.debug("用户认证失败,未找到用户名:{}", username);
            mitigateAgainstTimingAttack(loginAuthenticationToken);
            throw  ex;
        } catch (AuthenticationException ex) {
            log.debug("用户认证失败:{}", username);
            throw  ex;
        }
        return createSuccessAuthentication(loadedUser, authentication, loadedUser);
    }

    private void mitigateAgainstTimingAttack(LoginAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, LoginAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("由于没有密码，身份验证失败");
            throw new BadCredentialsException("用户名密码错误");
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!passwordEncoder.matches(presentedPassword,userDetails.getPassword())){
            log.debug("身份验证失败,密码不匹配");
            throw new BadCredentialsException("用户名密码错误");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (LoginAuthenticationToken.class.isAssignableFrom(authentication));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
