### 一个基于CAS协议的SSO登录认证企业级增强实现(PC/Android/iOS/WechatMp统一接口)，还支持QQ/Facebook等社交SNS授权认证，提供Opensaml开放API授权，内置接口级AOP二次认证实现等.

#### 一、快速入门
#####	- 1，服务端集成：
    	- 1.1，独立运行模式，使用iam的数据库表，适用于新系统集成，
    	- 1.2，依赖嵌入模式，使用外部自定义数据库表，适用于旧系统改造集成，
    	- 1.3，所有支持的yml配置，

#####	- 2，客户端集成：
    	- 2.1，PC集成(前后端分离)
    	- 2.2，[安卓端接入（全局认证拦截器）](super-devops-iam-example/src/main/java/com/wl4g/devops/iam/example/android/AndroidIamUserCoordinator.java)
		- 2.3，微信公众号集成，
		- 2.4，服务端所有支持的yml配置(以及默认值):
```
spring:
  cloud:
    devops:
      iam: # IAM configuration.
        #login-uri: /view/login.html
        login-uri: http://localhost:8080/#/login # Default, see:super-devops-view
        unauthorized-uri: /view/403.html
        success-endpoint: ${spring.application.name}@/view/index.html # Default
        acl:
          secure: false # Turn off protection will trust any same intranet IP.
          allowIpRange: ${DEVOPS_IAM_ACL_ALLOW:127.0.0.1}
          denyIpRange: ${DEVOPS_IAM_ACL_DENY}
        param: # Must be consistent with the client, otherwise authentication will never succeed
          sid: __sid
          sid-save-cookie: __cookie
          logout-forced: forced
          application: service
          grant-ticket: st
          response-type: response_type
          redirect-url: redirect_url
          which: which
          state: state
          refreshUrl: refresh_url
          agent: agent
          authorizers: authorizers
          second-auth-code: secondAuthCode
          funcId: function
          i18n-lang: lang
        matcher:
          fail-fast-match-max-attempts: 10
          fail-fast-match-delay: 3600000
          enabled-captcha-max-attempts: 3
          fail-fast-captcha-max-attempts: 20
          fail-fast-captcha-delay: 600000
          captcha-expire-ms: 60000
          fail-fast-sms-max-attempts: 3
          fail-fast-sms-max-delay: 1800000
          fail-fast-sms-delay: 90000
          sms-expire-ms: 300000
        cache:
          prefix: iam_
        session:
          global-session-timeout: 1500000
          session-validation-interval: 1500000
        cookie:
          name: IAMTOKEN_TGC
        captcha:
          #jigsaw:
            #source-dir: ${server.tomcat.basedir}/data/jigsaw-maternal
        sns: # SNS configuration.
          oauth2-connect-expire-ms: 60_000 # oauth2 connect processing expire time
          wechat-mp:
            app-id: yourappid
            app-secret: yoursecret
            redirect-url: https://iam.${DEVOPS_DOMAIN_TOP:wl4g.com}${server.contextPath}/sns/wechatmp/callback
          wechat:
            app-id: yourappid
            app-secret: yoursecret
            redirect-url: http://passport.wl4g.com${server.contextPath}/sns/wechat/callback
            #href: https://cdn.wl4g.com/static/css/wx.css
          qq:
            app-id: 101542056
            app-secret: yoursecret
            redirect-url: http://passport.wl4g.com${server.contextPath}/sns/qq/callback
```
	- 2.5，客户端所有支持的yml配置项(以及默认值):
```
spring:
  cloud:
    devops:
      iam: # IAM client configuration.
        acl:
          secure: false # Turn off protection will trust any same intranet IP.
          allowIpRange: 127.0.0.1
          denyIpRange: 
        client: # IAM client configuration.
          service-name: ${spring.application.name}
          # Authentication center api base uri
          base-uri: http://localhost:14040/devops-iam
          login-uri: ${spring.cloud.devops.iam.client.base-uri}/view/login.html
          success-uri: http://localhost:${server.port}${server.contextPath}/index.html
          unauthorized-uri: ${spring.cloud.devops.iam.client.base-uri}/view/403.html
          use-remember-redirect: false
          filter-chain:
            /public/**: anon # Public rule release
          param:
            # Must be consistent with the server, otherwise authentication will never succeed
            sid: __sid
            sid-save-cookie: __cookie
            logout-forced: forced
            application: service
            grant-ticket: st
            response-type: response_type
            redirect-url: redirect_url
            which: which
            state: state
            refreshUrl: refresh_url
            agent: agent
            authorizers: authorizers
            second-auth-code: secondAuthCode
            funcId: function
          cache:
            prefix: ${spring.application.name}
          session:
            global-session-timeout: 1500000
            session-validation-interval: 1500000
          cookie:
            name: IAMTOKEN_${spring.application.name}
```

#### 二、认证机制原理


#### 三、二次开发指南
#####	- 3.1、客户端二次开发
#####	- 3.2、服务端二次开发

###### [Reference](http://github.com/wl4g/super-devops)