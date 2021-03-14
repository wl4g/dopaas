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
  application.name: ${entryAppName}
  profiles:
    include: common,support,util
    active: dev
  cloud:
    devops:
<#if javaSpecs.isConf(extOpts, "iam.mode", "cluster")>
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
            '[/public/**]': anon # Public rule release
    <#if javaSpecs.isConf(extOpts, "swagger.ui", "bootstrapSwagger2")>
            '[/webjars/**]': anon
            '[/swagger-resources/**]': anon
            '[/swagger-resources]': anon
            '[/v2/api-docs]': anon
            '[/v2/api-docs-ext]': anon
            '[/doc.html]': anon
    <#elseif javaSpecs.isConf(extOpts, "swagger.ui", "officialOas")>
            '[/webjars/**]': anon
            '[/swagger-ui/**]': anon
            '[/swagger-resources/**]': anon
            '[/swagger-ui.html]': anon
            '[/v3/api-docs]': anon
    </#if>
          cipher:
            enable-data-cipher: true # Default by true
          session:
            enable-access-token-validity: true # Default by true
<#elseif javaSpecs.isConf(extOpts, "iam.mode", "local")>
      iam: # IAM server configuration.
        cors:
          enabled: true # Default: true
          # Default rules: allowsOrigins=http://localhost:8080; allowsHeaders=X-Iam-*; allowsMethods=GET,HEAD,POST,OPTIONS
          rules:
            '[/**]':
              allows-methods: [GET,HEAD,POST,OPTIONS]
              allows-headers: [X-Iam-*]
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
    <#if javaSpecs.isConf(extOpts, "swagger.ui", "bootstrapSwagger2")>
          '[/webjars/**]': anon
          '[/swagger-resources/**]': anon
          '[/swagger-resources]': anon
          '[/v2/api-docs]': anon
          '[/v2/api-docs-ext]': anon
          '[/doc.html]': anon
    <#elseif javaSpecs.isConf(extOpts, "swagger.ui", "officialOas")>
          '[/webjars/**]': anon
          '[/swagger-ui/**]': anon
          '[/swagger-resources/**]': anon
          '[/swagger-ui.html]': anon
          '[/v3/api-docs]': anon
    </#if>
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
          name: _${organName?upper_case}_${projectName?upper_case}
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
  port: ${entryAppPort}
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
