package io.github.ypconfig17.security.oauth.client;


import io.github.ypconfig17.security.core.user.OauthClientUser;

/**
 * @author yangpan
 */
public interface OAuthUserMapper {


    String clientId();

    OauthClientUser map();
}
