package com.example.config;


import io.github.ypconfig17.security.core.user.GeneralUser;
import lombok.Data;

/**
 * @author yangpan
 */
@Data
public class SysUser implements GeneralUser {
    private String username;
    private String password;


    public SysUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public SysUser() {
    }
}
