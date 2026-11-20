package com.example.controller;

import com.example.api.ApiResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangpan
 */

@RestController
public class UserController {




    @GetMapping("/user")
    public ApiResult<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        return ApiResult.ok(principal);
    }

}
