package com.example.config;

import io.github.ypconfig17.security.core.AuthenticationService;
import io.github.ypconfig17.security.core.user.GeneralUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author yangpan
 */



@Service
public class AuthenticationServiceImpl implements AuthenticationService {


    @Override
    public GeneralUser authentication(String username, String password) {
        if (username.equals("")){
            throw new UsernameNotFoundException("用户名未找到");
        }
        return new SysUser("18091681804","123456");
    }

    @Override
    public GeneralUser smsAuthentication(String mobile, String smsCode) {

        if (!smsCode.equals("0000")){
            throw new UsernameNotFoundException("用户名未找到");
        }
        return new SysUser("18091681804","123456");
    }

    @Override
    public GeneralUser oauthAuthentication(String registrationId, String name, Map<String, Object> attributes) {
        return new SysUser("18091681804","123456");
    }
}
