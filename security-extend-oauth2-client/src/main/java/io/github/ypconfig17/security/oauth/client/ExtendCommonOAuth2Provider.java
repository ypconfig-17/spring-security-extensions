package io.github.ypconfig17.security.oauth.client;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * @author yangpan
 */
public enum ExtendCommonOAuth2Provider {
    /**
     * 新浪微博
     */
    WEIBO {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://api.weibo.com/oauth2/authorize");
            builder.tokenUri("https://api.weibo.com/oauth2/access_token");
            builder.userInfoUri("https://api.weibo.com/2/users/show.json");
            builder.userNameAttributeName("id");
            builder.clientName("Facebook");
            return builder;
        }
    },


    /**
     * gitee
     */
    GITEE {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://gitee.com/oauth/authorize");
            builder.tokenUri("https://gitee.com/oauth/token");
            builder.userInfoUri("https://gitee.com/api/v5/user");
            builder.userNameAttributeName("id");
            builder.clientName("GITEE");
            return builder;
        }

    },

    /**
     * 钉钉
     */
    DINGTALK {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://oapi.dingtalk.com/connect/qrconnect");
            builder.userInfoUri("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
            builder.userNameAttributeName("id");
            builder.clientName("GITEE");
            return builder;
        }
    },
    /**
     * 支付宝
     */
    ALIPAY {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm");
            builder.tokenUri("https://openapi.alipay.com/gateway.do");
            builder.userInfoUri("https://openapi.alipay.com/gateway.do");
            builder.userNameAttributeName("id");
            builder.clientName("ALIPAY");
            return builder;
        }
    },

    QQ {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://graph.qq.com/oauth2.0/authorize");
            builder.tokenUri("https://graph.qq.com/oauth2.0/token");
            builder.userInfoUri("https://graph.qq.com/user/get_user_info");
            builder.userNameAttributeName("id");
            builder.clientName("QQ");
            return builder;
        }
    },
    /**
     * 微信开放平台
     */
    WECHAT_OPEN {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://open.weixin.qq.com/connect/qrconnect");
            builder.tokenUri("https://api.weixin.qq.com/sns/oauth2/access_token");
            builder.userInfoUri("https://api.weixin.qq.com/sns/userinfo");
            builder.userNameAttributeName("id");
            builder.clientName("WECHAT_OPEN");
            return builder;
        }
    },
    /**
     * 微信公众平台
     */
    WECHAT_MP {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://open.weixin.qq.com/connect/oauth2/authorize");
            builder.tokenUri("https://api.weixin.qq.com/sns/oauth2/access_token");
            builder.userInfoUri("https://api.weixin.qq.com/sns/userinfo");
            builder.userNameAttributeName("id");
            builder.clientName("WECHAT_OPEN");
            return builder;
        }
    },

    /**
     * 企业微信
     *
     * @since 1.10.0
     */
    WECHAT_ENTERPRISE {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = this.getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_BASIC, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"openid", "profile", "email"});
            builder.authorizationUri("https://open.work.weixin.qq.com/wwopen/sso/qrConnect");
            builder.tokenUri("https://qyapi.weixin.qq.com/cgi-bin/gettoken");
            builder.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
            builder.issuerUri("https://accounts.google.com");
            builder.userInfoUri("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo");
            builder.userNameAttributeName("screen_name");
            builder.clientName("Google");
            return builder;
        }

    },

    GOOGLE {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = this.getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_BASIC, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"openid", "profile", "email"});
            builder.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth");
            builder.tokenUri("https://www.googleapis.com/oauth2/v4/token");
            builder.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
            builder.issuerUri("https://accounts.google.com");
            builder.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo");
            builder.userNameAttributeName("sub");
            builder.clientName("Google");
            return builder;
        }
    },
    GITHUB {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = this.getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_BASIC, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"read:user"});
            builder.authorizationUri("https://github.com/login/oauth/authorize");
            builder.tokenUri("https://github.com/login/oauth/access_token");
            builder.userInfoUri("https://api.github.com/user");
            builder.userNameAttributeName("id");
            builder.clientName("GitHub");
            return builder;
        }
    },
    FACEBOOK {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = this.getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"public_profile", "email"});
            builder.authorizationUri("https://www.facebook.com/v2.8/dialog/oauth");
            builder.tokenUri("https://graph.facebook.com/v2.8/oauth/access_token");
            builder.userInfoUri("https://graph.facebook.com/me?fields=id,name,email");
            builder.userNameAttributeName("id");
            builder.clientName("Facebook");
            return builder;
        }
    },
    OKTA {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = this.getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_BASIC, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"openid", "profile", "email"});
            builder.userNameAttributeName("sub");
            builder.clientName("Okta");
            return builder;
        }
    };

    private static final String DEFAULT_REDIRECT_URL = "{baseUrl}/{action}/oauth2/code/{registrationId}";

    private ExtendCommonOAuth2Provider() {
    }

    protected final ClientRegistration.Builder getBuilder(String registrationId, ClientAuthenticationMethod method, String redirectUri) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(method);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUri(redirectUri);
        return builder;
    }

    public abstract ClientRegistration.Builder getBuilder(String registrationId);
}
