# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,
# All rights reserved. Contact us <Wanglsir@gmail.com, 983708408@qq.com>
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
########## The default domain names of all services in DoPaaS cluster mode. ##########

## All external web services default DNS records example.
## Notes:
## 1. The following is a sample domains resolution record of all 
##    the sub services of DoPaaS, which are forwarded by Nginx/LB and 
##    other load balancers according to the sub domain.(where 47.0.0.100 is Nginx/LB IP)
##
## 2. If you use nginx as lb, please refer to the example: {PROJECT_HOME}/nginx/conf.d/dopaas_http.conf
##
## 3. The following domain will be used outside and inside the service. If you want to deploy 
##    in the public network, you need to configure the IP to be resolved to Nginx/LB in the domain 
##    service provider (ignoring the following resolution mapping). If you only need to deploy in the intranet, 
##    you need to use the following domain resolution.
#
#47.0.0.100    dopaas.wl4g.com                  # Frontend service access domain.(unified users access URL)
#47.0.0.100    iam.wl4g.com                     # [iam-web] service access domain.
#47.0.0.100    cmdb.wl4g.com                    # [cmdb-manager] service access domain.
#47.0.0.100    home.wl4g.com                    # [home-manager] service access domain.
#47.0.0.100    lcdp.wl4g.com                    # [lcdp-manager] service access domain.
#47.0.0.100    uci.wl4g.com                     # [uci-manager] service access domain.
#47.0.0.100    ucm.wl4g.com                     # [ucm-server] service access domain.
#47.0.0.100    uds.wl4g.com                     # [uds-manager] service access domain.
#47.0.0.100    umc.wl4g.com                     # [umc-manager] service access domain.
#47.0.0.100    urm.wl4g.com                     # [urm-manager] service access domain.
#47.0.0.100    uos.wl4g.com                     # [uos-manager] service access domain.
#
#
## All dependence services default DNS records example.
## Notes:
## 1. The following is an example of DNS resolution with default configuration domain name. 
##    In terms of architecture design, Redis/MySQL/Elasticsearch/OSS etc, can be used separately 
##    for each sub service. If the number of servers is not enough, multiple services can share 
##    these services (that is, the IP configuration for resolution is the same).
##
## 2. The following example is the default production environment domain name. For other 
##    environments('fat' or 'uat'), only needs to change the top-level domain name to 'wl4g.fat' 
##    or 'wl4g.uat'
##
## 3. The following domain is only called between the sub services, as long as the intranet is valid.
#
#192.168.1.201    redis.iam.wl4g.com           # [iam-facade/iam-web] depends on redis service.
#192.168.1.202    rds.iam.wl4g.com             # [iam-data] depends on rds(mysql) service.
#
#192.168.1.203    redis.cmdb.wl4g.com          # [cmdb-facade/cmdb-manager] depends on redis service.
#192.168.1.204    rds.cmdb.wl4g.com            # [cmdb-facade] depends on rds(mysql) service.
#
#192.168.1.205    redis.home.wl4g.com          # [home-facade/home-manager] depends on redis service.
#192.168.1.206    rds.home.wl4g.com            # [home-facade] depends on rds(mysql) service.
#
#192.168.1.207    redis.lcdp.wl4g.com          # [lcdp-facade/lcdp-manager] depends on redis service.
#192.168.1.208    rds.lcdp.wl4g.com            # [lcdp-facade] depends on rds(mysql) service.
#
#192.168.1.209    redis.uci.wl4g.com           # [uci-facade/uci-server] depends on redis service.
#192.168.1.210    rds.uci.wl4g.com             # [uci-facade] depends on rds(mysql) service.
#
#192.168.1.211    redis.ucm.wl4g.com           # [ucm-facade/uds-server] depends on redis service.
#192.168.1.212    rds.ucm.wl4g.com             # [ucm-facade] depends on rds(mysql) service.
#
#192.168.1.213    redis.uds.wl4g.com           # [uds-facade/uds-manager] depends on redis service.
#192.168.1.214    rds.uds.wl4g.com             # [uds-facade] depends on rds(mysql) service.
#
#192.168.1.215    redis.umc.wl4g.com           # [umc-facade/umc-manager] depends on redis service.
#192.168.1.216    rds.umc.wl4g.com             # [umc-facade] depends on rds(mysql) service.
#192.168.1.217    elasticsearch.umc.wl4g.com   # [umc-facade] depends on elasticsearch service.
#
#192.168.1.218    redis.urm.wl4g.com           # [urm-facade/umc-manager] depends on redis service.
#192.168.1.219    rds.urm.wl4g.com             # [urm-facade] depends on rds(mysql) service.
#
#192.168.1.220    redis.uos.wl4g.com           # [uos-facade/uos-manager] depends on redis service.
#192.168.1.221    rds.uos.wl4g.com             # [uos-facade] depends on rds(mysql) service.
#192.168.1.222    oss.uos.wl4g.com             # [uos-manager] depends on oss(minio) service.

