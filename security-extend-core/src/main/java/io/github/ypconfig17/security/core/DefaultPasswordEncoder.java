package io.github.ypconfig17.security.core;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author yangpan
 */
public class DefaultPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
}
