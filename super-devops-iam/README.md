### A CAS-based SSO unified authentication implementation, supporting WeChat, QQ, Facebook and other SNS authentication, Opensaml API authorization, built-in interface level AOP secondary authentication implementation.

#### Quick Start
- 1，Server integration:
    - 1.1，Independent operation mode, using Iam database tables, is suitable for new system integration.
    - 1.2，Depending on embedded mode and using external custom database tables, it is suitable for old system transformation and integration.
    - 1.3，All supported YML configurations,

- 2，Client integration:
    - 2.1，PC integration (front-end and back-end separation)
    - 2.2，[Android Access (Global Authentication Interceptor)](super-devops-iam-example/src/main/java/com/wl4g/devops/iam/example/android/AndroidIamUserCoordinator.java)
	- 2.3，Wechat Public Signal Integration,
	- 2.4，All supported YML configurations on the server side:
```
spring:
  cloud:
    devops:
      iam: # IAM server configuration.
        default-view-loader-path: classpath:/default-view/
        default-view-base-uri: /view
        login-uri: /default-view/login.html
        success-uri: /default-view/index.html
        unauthorized-uri: /default-view/403.html
        filter-chain: 
          /public/**: anon # Public rule release
          /test/**: anon # Testing rule release
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
        strategy: # Authentication api interactive strategy configuration.
          response-template: '{"code":${code},"message":"${message}","status":"${status}","data":"${data}"}'
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
        authc-internal-access:
          secure: true
          allowIp: 127.0.0.1
          denyIp:
        captcha:
          enabled: true
```
	-  2.5，All supported YML configurations on the client side:
```
spring:
  cloud:
    devops:
      iam: # IAM client configuration.
        authc-internal-access:
          enable: true
          allow-ip: 127.0.0.1
          deny-ip: 
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

#### Secondary Development
> * 3.1、Secondary development of Client
> * 3.2、Secondary development of Server

###### [Reference](https://www.zybuluo.com/mdeditor)