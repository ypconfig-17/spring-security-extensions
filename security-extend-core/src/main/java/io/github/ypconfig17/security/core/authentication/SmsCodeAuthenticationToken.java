package io.github.ypconfig17.security.core.authentication;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * @author yangpan
 */
@Getter
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final String mobile;
    private final String code;

    public SmsCodeAuthenticationToken(String mobile,String code) {
        super(null);
        this.mobile = mobile;
        this.code = code;
        setAuthenticated(false);
    }


    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
