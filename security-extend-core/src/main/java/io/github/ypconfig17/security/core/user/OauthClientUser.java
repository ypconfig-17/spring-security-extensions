package io.github.ypconfig17.security.core.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author yangpan
 */
@Getter
@Setter
@Builder
public class OauthClientUser {
    /**
     * 平台id
     */
    private String clientId;
    /**
     * 用户第三方系统的唯一id。在调用方集成该组件时，可以用uuid + source唯一确定一个用户
     * @since 1.3.3
     */
    private String authUserId;
    /**
     * 用户
     */
    private String username;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户网址
     */
    private String blog;
    /**
     * 所在公司
     */
    private String company;
    /**
     * 位置
     */
    private String location;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户备注（各平台中的用户个人介绍）
     */
    private String remark;
    /**
     * 性别
     */
    private String gender;

    /**
     * 第三方平台返回的原始用户信息
     */
    private Map<String, Object> rawUserInfo;

}
