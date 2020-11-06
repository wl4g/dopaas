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
# #### Environment base configuration. ####

# Mybatis configuration.
mybatis:
  configLocation: mybatis/mybatis-config.xml
  # Note: for example, if depend on 'iam-core', need to use 'classpath*' to scan the DAO mapper configuration of IAM.
  mapperLocations: classpath*:mybatis/**/*Mapper.xml
  # TODO: In the future, it will definitely migrate to 'com.wl4g.iam.common.bean'
  typeAliasesPackage: com.wl4g.components.core.bean.*,com.wl4g.components.core.bean.*.*

# Logging configuration.
logging:
  file: ${r'${'}server.tomcat.basedir}/logs/${r'${'}spring.application.name}.log
  pattern:
    #console: ${r'${'}logging.pattern.file}
    #file: '%d{yy-MM-dd HH:mm:ss.SSS} ${r'${'}LOG_LEVEL_PATTERN:%4p} ${r'${'}PID} [%t] [%X{_H_:X-Request-ID}] [%X{_H_:X-Request-Seq}] [%X{_C_:${r'${'}spring.cloud.devops.iam.cookie.name}}] - %-40.40logger{39} : %m%n${r'${'}LOG_EXCEPTION_CONVERSION_WORD:%wEx}'
  root: INFO
  # Custom configuration(Non spring-boot standard).
  policy:
    maxFileSize: 1GB
    minIndex: 1
    maxIndex: 10
  level:
    de.codecentric.boot.admin: INFO
    io.swagger: ERROR
    org:
      springframework: INFO
      apache: INFO
    ${packageName}.${daoSubModPkgName}: DEBUG
