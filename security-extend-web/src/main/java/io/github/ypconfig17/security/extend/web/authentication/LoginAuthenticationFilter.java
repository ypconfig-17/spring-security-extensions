package io.github.ypconfig17.security.extend.web.authentication;

import io.github.ypconfig17.security.core.authentication.LoginAuthenticationToken;
import io.github.ypconfig17.security.extend.web.support.HttpRequestExtractor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author yangpan
 */
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_FILTERP_ROCESSESURL = "/login";

    public static final String DEFAULT_USERNAME_PARAMETER = "username";

    public static final String DEFAULT_PASSWORD_PARAMETER = "password";


    private String usernameParameter = DEFAULT_USERNAME_PARAMETER;
    private String passwordParameter = DEFAULT_PASSWORD_PARAMETER;

    private boolean postOnly = false;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher(DEFAULT_FILTERP_ROCESSESURL,"POST");


    public LoginAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("认证方法不支持: " + request.getMethod());
        }

        Map<String,String> requestParamters = HttpRequestExtractor.extractParamters(request);
        String username = requestParamters.get(usernameParameter);
        String password = requestParamters.get(passwordParameter);

        username = username == null? "" :  username.trim();
        password = password == null? "" :  password.trim();
        LoginAuthenticationToken authRequest = new LoginAuthenticationToken(username, password);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected void setDetails(HttpServletRequest request, LoginAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }


    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }


    public String getUsernameParameter() {
        return usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }
}
