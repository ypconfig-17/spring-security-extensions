package io.github.ypconfig17.security.core;

import io.github.ypconfig17.security.core.exception.NotImplementedException;
import io.github.ypconfig17.security.core.user.GeneralUser;

import java.util.Map;

/**
 * @author yangpan
 */
public interface AuthenticationService {

    default GeneralUser authentication(String username, String password) {
        throw new NotImplementedException("未实现【用户名密码】认证方法.");
    }

    default GeneralUser smsAuthentication(String mobile, String smsCode) {
        throw new NotImplementedException("未实现【短信验证码】认证方法.");
    }

    default GeneralUser oauthAuthentication(String registrationId, String name, Map<String, Object> attributes) {
        throw new NotImplementedException("未实现【oauth2】认证方法.");

    }

}
