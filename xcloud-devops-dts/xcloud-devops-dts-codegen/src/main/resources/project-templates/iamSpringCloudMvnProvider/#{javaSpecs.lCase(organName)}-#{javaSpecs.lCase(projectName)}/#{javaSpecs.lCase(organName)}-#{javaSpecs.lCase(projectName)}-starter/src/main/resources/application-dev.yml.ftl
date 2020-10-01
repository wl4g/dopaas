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

<#assign topDomain = organName?lower_case + '.debug' />
<#assign redisHost = 'redis.' + topDomain />

# #### Environment(Dev) configuration. ####
#
spring:
  cloud:
    devops:
<#if javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "cluster")>
      iam:
        cors:
          rules:
            '[/**]':
              allows-origins:
                #- http://${r'${'}X_SERVICE_ZONE:${topDomain}}
                #- http://${r'${'}X_SERVICE_ZONE:${topDomain}:${r'${'}server.port}}
                #- http://*.${r'${'}X_SERVICE_ZONE:${topDomain}}
                #- http://*.${r'${'}X_SERVICE_ZONE:${topDomain}:${r'${'}server.port}}
                #- http://localhost:8080
                #- http://127.0.0.1:8080
                - '*'
        acl:
          secure: false # Turn off protection will trust any same intranet IP.
          allowIpRange: ${r'${'}X_IAM_ACL_ALLOW:127.0.0.1}
          denyIpRange: ${r'${'}X_IAM_ACL_DENY}
        client:
          # To facilitate debugging, it is recommended to configure local hosts, wl4g.debug/wl4g.local/wl4g.dev
          # resolve to 127.0.0.1 (consistent with the server deployment structure), and the relevant front-end
          # logic is in global_variable.js:55, related database table: app_cluster_config
          server-uri: http://${topDomain}:14040/iam-server
          unauthorized-uri: ${r'${'}spring.cloud.devops.iam.client.server-uri}/view/403.html
          success-uri: http://${topDomain}:8080/#/home
<#elseif javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "local")>
      iam: # IAM server configuration.
        cors:
          rules:
            '[/**]':
              allows-origins:
                - http://${r'${'}X_SERVICE_ZONE:wl4g.dev}
                - http://${r'${'}X_SERVICE_ZONE:wl4g.dev:${r'${'}server.port}}
                - http://${r'${'}X_SERVICE_ZONE:wl4g.local}
                - http://${r'${'}X_SERVICE_ZONE:wl4g.local:${r'${'}server.port}}
                - http://${r'${'}X_SERVICE_ZONE:wl4g.debug}
                - http://${r'${'}X_SERVICE_ZONE:wl4g.debug:${r'${'}server.port}}
                - http://*.${r'${'}X_SERVICE_ZONE:wl4g.debug}
                - http://*.${r'${'}X_SERVICE_ZONE:wl4g.debug:${r'${'}server.port}}
                - http://localhost
                - http://localhost:14040
                - http://127.0.0.1
                - http://127.0.0.1:14040
                - '*'
        login-uri: http://${r'${'}X_SERVICE_ZONE:wl4g.debug}:14040${r'${'}server.servlet.contextPath}/view/login.html
        #login-uri: http://wl4g.debug/#/login # See: https://github.com/wl4g/xcloud-devops-view
        unauthorized-uri: http://${r'${'}X_SERVICE_ZONE:wl4g.debug}:14040${r'${'}server.servlet.contextPath}/view/403.html
        success-endpoint: ${r'${'}spring.application.name}@http://${r'${'}X_SERVICE_ZONE:wl4g.debug}:14040${r'${'}server.servlet.contextPath}/view/index.html
        #success-endpoint: ci@http://ci.${r'${'}X_SERVICE_ZONE:wl4g.debug}/ci-server
        acl:
          secure: false # Turn off protection will trust any same intranet IP.
          allowIpRange: ${r'${'}X_IAM_ACL_ALLOW:127.0.0.1}
          denyIpRange: ${r'${'}X_IAM_ACL_DENY}
        captcha:
          #jigsaw:
            #source-dir: ${r'${'}server.tomcat.basedir}/data/jigsaw-maternal
        sns: # SNS configuration.
          oauth2-connect-expire-ms: 60_000 # oauth2 connect processing expire time
          wechat-mp:
            app-id: yourappid
            app-secret: yoursecret
            redirect-url: https://iam.${r'${'}X_SERVICE_ZONE:wl4g.com}${r'${'}server.servlet.contextPath}/sns/wechatmp/callback
          wechat:
            app-id: yourappid
            app-secret: yoursecret
            redirect-url: http://passport.wl4g.com${r'${'}server.servlet.contextPath}/sns/wechat/callback
            href: https://${r'${'}X_SERVICE_ZONE:wl4g.com}/${r'${'}server.servlet.contextPath}/iam-jssdk/assets/css/iam-jssdk-wx.min.css
          qq:
            app-id: 101542056
            app-secret: 46b2ba9fa24c2b973abc64ec898db3b4
            redirect-url: http://passport.wl4g.com${r'${'}server.servlet.contextPath}/sns/qq/callback
        session:
          global-session-timeout: 43200_000
</#if>

  # Datasource configuration.
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.jdbc.Driver
    druid:
      url: jdbc:mysql://${r'${'}X_DB_URL:${datasource.dbhost}:${datasource.dbport}}/${r'${'}X_DB_NAME:${datasource.databaseName}}?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8
      username: ${r'${'}X_DB_USER:${datasource.dbusername}}
      password: ${r'${'}X_DB_PASSWD:${datasource.dbpassword}}
      initial-size: 10
      max-active: 100
      min-idle: 10
      max-wait: 60000
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      filters: stat,wall
      log-slow-sql: true

# Redis configuration.
redis:
  passwd: ${r'${'}X_REDIS_PASSWD:zzx!@#$%}
  connect-timeout: 10000
  max-attempts: 10
  # Redis's cluster nodes.
  nodes: ${r'${'}X_REDIS_NODES:${redisHost}:6379,${redisHost}:6380,${redisHost}:6381,${redisHost}:7379,${redisHost}:7380,${redisHost}:7381}
