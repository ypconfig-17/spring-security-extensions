# Spring Security Extensions

一个基于 Java 8、Spring Boot 2.7 和 Spring Security 5.7 的多模块认证扩展示例。项目把登录请求处理、认证逻辑和 Web 配置拆开，便于阅读自定义认证方式如何接入 Spring Security 过滤器链。

## 从哪里读代码

| 模块 | 职责 |
| --- | --- |
| [core](security-extend-core/) | 定义认证服务、认证令牌和 Provider |
| [web](security-extend-web/) | 将 HTTP 登录请求转换为认证输入 |
| [config](security-extend-config/) | 将登录过滤器和认证配置接入 Spring Security |
| [starter](security-extend-starter/) | 提供 Spring Boot 自动配置入口 |
| [oauth2-client](security-extend-oauth2-client/) | 提供 OAuth2 客户端扩展 |
| [example](security-extend-example/) | 演示登录页面、OAuth2 客户端和数据库配置 |
| [common](security-extend-common/) | HTTP 和 JSON 辅助方法 |

可以从 [LoginConfigurerEnhanced](security-extend-config/src/main/java/io/github/ypconfig17/security/extend/config/configuration/LoginConfigurerEnhanced.java) 开始，沿着 [LoginAuthenticationProvider](security-extend-core/src/main/java/io/github/ypconfig17/security/core/authentication/LoginAuthenticationProvider.java) 查看认证如何查找用户和检查凭据。

## 构建与配置

在 JDK 8 和 Maven 环境下运行：

```bash
mvn clean verify
```

示例应用使用独立的测试数据库和 OAuth2 客户端。在启动示例前设置下列环境变量。使用 Gitee 客户端配置中的实际回调地址，并把凭据保存在本机环境中。

- `GITEE_OAUTH_CLIENT_ID`
- `GITEE_OAUTH_CLIENT_SECRET`
- `DEMO_DATASOURCE_URL`
- `DEMO_DATASOURCE_USERNAME`
- `DEMO_DATASOURCE_PASSWORD`

项目使用 `io.github.ypconfig17` 作为自身 Java 包命名空间和 Maven groupId。部分 Spring Security 集成类位于框架包下，用来接入当前版本的配置机制。升级框架版本时需重新验证这些接入点。

## 许可证

[Apache-2.0](LICENSE)。第三方文件保留各自的版权和许可声明。
