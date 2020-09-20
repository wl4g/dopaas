# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,
# All rights reserved. Contact us <Wanglsir@gmail.com, 983708408@qq.com>
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# #### Environment(Dev) configuration. ####
#
spring:
  cloud:
    devops:
      iam:
        cors:
          rules:
            '[/**]':
              allows-origins:
                #- http://${r'${'}X_SERVICE_ZONE:wl4g.debug}
                #- http://${r'${'}X_SERVICE_ZONE:wl4g.debug:${r'${'}server.port}}
                #- http://*.${r'${'}X_SERVICE_ZONE:wl4g.debug}
                #- http://*.${r'${'}X_SERVICE_ZONE:wl4g.debug:${r'${'}server.port}}
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
          server-uri: http://wl4g.debug:14040/iam-server
          unauthorized-uri: ${r'${'}spring.cloud.devops.iam.client.server-uri}/view/403.html
          success-uri: http://wl4g.debug:8080/#/umc/config
  # Datasource configuration.
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.jdbc.Driver
    druid:
      url: jdbc:mysql://${r'${'}X_DB_URL:wl4g.debug:3306}/${r'${'}X_DB_NAME:devops}?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8
      username: ${r'${'}X_DB_USER:gzsm}
      password: ${r'${'}X_DB_PASSWD:gzsm@%#jh?}
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
  nodes: ${r'${'}X_REDIS_NODES:wl4g.debug:6379,wl4g.debug:6380,wl4g.debug:6381,owner-node3:6379,owner-node3:6380,owner-node3:6381}
