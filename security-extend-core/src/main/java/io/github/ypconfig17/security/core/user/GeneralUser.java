package io.github.ypconfig17.security.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yangpan
 */
public interface GeneralUser extends UserDetails {


    @Override
   default Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.EMPTY_LIST;
    }


    @Override
   default boolean isAccountNonExpired(){
        return true;
    }

    @Override
    default boolean isAccountNonLocked(){
        return true;
    }


    @Override
    default boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    default boolean isEnabled(){
        return true;
    }
}
