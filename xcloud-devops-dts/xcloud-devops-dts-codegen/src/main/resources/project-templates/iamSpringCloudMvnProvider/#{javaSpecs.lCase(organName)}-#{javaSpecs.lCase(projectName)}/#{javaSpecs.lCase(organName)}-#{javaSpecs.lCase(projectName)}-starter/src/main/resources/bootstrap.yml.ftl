# ${watermark}
# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,
# All rights reserved. Contact us <Wanglsir@gmail.com, 983708408@qq.com>
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# #### Spring cloud config server configuration. ####
#
spring:
  application.name: ${projectName?lower_case}-server
  profiles:
    include: common,support,util
    active: dev
  cloud:
    devops:
<#if javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "cluster")>
      iam: # IAM client configuration.
        cors:
          enabled: true # Default: true
          rules:
            '[/**]':
              allows-methods: [GET,HEAD,OPTIONS,POST]
              allows-headers: [X-Iam-*]
              allow-credentials: true
              #allows-origins:
                #- '*'
        xsrf:
          enabled: true
        replay:
          enabled: true
        xss:
          enabled: true # Default: true
        client: # IAM client configuration.
          filter-chain:
            /public/**: anon # Public rule release
          cipher:
            enable-data-cipher: true # Default by true
          session:
            enable-access-token-validity: true # Default by true
<#elseif javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "local")>
      iam: # IAM server configuration.
        cors:
          enabled: true # Default: true
          # Default rules: allowsOrigins=http://localhost:8080; allowsHeaders=X-Iam-*; allowsMethods=GET,HEAD,POST,OPTIONS
          rules:
            '[/**]':
              allows-methods: [GET,HEAD,POST,OPTIONS]
              allows-headers: [X-Iam-*]
              # Note: Some latest browsers, such as chrome 80.x+, only support sharing with the sub domain of the top-level domain name CORS.
              # You can also manually turn off the chrome switch(@see https://chromestatus.com/features/5088147346030592), visit chrome://flags
              # and search for samesite, find the options [SameSite by default cookies] and [Cookies without SameSite must be secure] set 
              # to disabled and restart chrome, but this is not recommended! For more solutions, please refer to: ideas like JWT/CAS/Oauth2 are extended.
              #   Fortunately, iam-jssdk solves this problem.The solution is mainly through the interface token, thereby breaking the limit of cookies,
              # and it is also very safe due to the use of a private encryption mechanism. However, the cost for such a perfect solution That is, each
              # front end has a little intrusion transformation, that is: each request needs to manually set the current token. For more information 
              # about iam-jssdk, please refer to: /default-webapps/sdk/{version}/src/js/IAM.js
              allow-credentials: true
              ##allows-origins:
                #- '*'
        xsrf:
          enabled: true # Default: false
        replay:
          enabled: true # Default: false
        xss:
          enabled: true # Default: true
          expression: execution(* com.wl4g.devops.iam.common.*.*Controller.*(..)) or
                      execution(* com.wl4g.devops.iam.sns.*.*Controller.*(..)) or
                      execution(* com.wl4g.devops.iam.*.*Controller.*(..))
          escape-translators:
            #- escapeEcmascript
            #- escapeJava
            #- escapeHtml3
            #- escapeHtml4
        filter-chain:
          '[/public/**]': anon # Public rule
        param: # Must be consistent with the client, otherwise authentication will never succeed
          sid: __sid
        matcher:
          fail-fast-match-max-attempts: 10
          fail-fast-match-delay: 3600000
          enabled-captcha-max-attempts: 3
          fail-fast-captcha-max-attempts: 100
          fail-fast-captcha-delay: 600000
          captcha-expire-ms: 60000
          fail-fast-sms-max-attempts: 3
          fail-fast-sms-max-delay: 1800000
          fail-fast-sms-delay: 90000
          sms-expire-ms: 300000
        cipher:
          enable-data-cipher: true # Default by true
        session:
          enable-access-token-validity: true # Default by true
          global-session-timeout: 1800_000
          session-validation-interval: 360_000
        cookie:
          name: _TGC
        #domain:
          #hsts-profiles-active: [pro,prod,production]
        #acl:
          #secure: false # Turn off protection will trust any same intranet IP.
          #allowIpRange: ${r'${'}X_IAM_ACL_ALLOW:127.0.0.1}
          #denyIpRange: ${r'${'}X_IAM_ACL_DENY}
        captcha:
          #kaptcha:
          #gif:
          jigsaw:
            pool-size: 64
</#if>

# ### Server configuration. ###
server:
  servlet:
    contextPath: /${r'${'}spring.application.name}
  #address: 0.0.0.0
  port: 8080
  sessionTimeout: 30
  tomcat:
    uri-encoding: UTF-8
    protocolHeader: x-forwarded-proto
    remoteIpHeader: x-forwarded-for
    basedir: /tmp/${r'${'}spring.application.name}
    access-log-enabled: false
    accesslog.directory: logs/
    backgroundProcessorDelay: 30 #seconds
    max-thread: 50 # Max worker threads(default:200)
