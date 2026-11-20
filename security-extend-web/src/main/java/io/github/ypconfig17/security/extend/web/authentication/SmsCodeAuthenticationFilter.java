package io.github.ypconfig17.security.extend.web.authentication;

import io.github.ypconfig17.security.core.authentication.SmsCodeAuthenticationToken;
import io.github.ypconfig17.security.extend.web.support.HttpRequestExtractor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_FILTERP_ROCESSESURL = "/sms/login";

    public static final String DEFAULT_MOBILE_PARAMETER = "mobile";

    public static final String DEFAULT_CODE_PARAMETER = "code";


    private String codeParameter = DEFAULT_CODE_PARAMETER;
    private String mobileParameter = DEFAULT_MOBILE_PARAMETER;

    private boolean postOnly = false;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher(DEFAULT_FILTERP_ROCESSESURL, "POST");


    public SmsCodeAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public SmsCodeAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("认证方法不支持: " + request.getMethod());
        }

        Map<String, String> requestParamters = HttpRequestExtractor.extractParamters(request);

        String mobile = requestParamters.get(mobileParameter);
        String code = requestParamters.get(codeParameter);


        mobile = mobile == null ? "" : mobile.trim();
        code = code == null ? "" : code.trim();
        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile, code);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }


    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }


    public String getCodeParameter() {
        return codeParameter;
    }

    public void setCodeParameter(String codeParameter) {
        this.codeParameter = codeParameter;
    }

    public String getMobileParameter() {
        return mobileParameter;
    }

    public void setMobileParameter(String mobileParameter) {
        this.mobileParameter = mobileParameter;
    }
}
