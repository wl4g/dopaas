## XCloud DoPaaS
![XCloud DoPaaS](shots/logo.jpg)

[中文文档](README_CN.md) | English version goes [here](README.md)

### One stop solution of PaaS platform based on DevSecOps
> Based on SpringCloud/Docker/ServiceMesh(istio), the main modules are: unified asset management center (Cmdb), unified continuous delivery center (distributed compilation CI/CD), Iam Certification Center(Rbac/Oidc/[Saml2])), unified monitoring center (SBA/Zipkin/Promethous), unified configuration center, unified operation center (Elasticjob/Spark/Flink/MR), unified Document Center (Swagger/Rap), unified development center (Lcdp/Autogen), unified private object storage management, unified library management (Git/Nexus(Maven/Image)), Shell-Cli, multiple tool chains (Hdfs/HBase/Phoenix/OSS), instant messaging, lightweight risk control, etc

[![Build Status](https://travis-ci.org/wl4g/xcloud-dopaas.svg)](https://travis-ci.org/wl4g/xcloud-dopaas)
![License](https://img.shields.io/badge/license-Apache2.0+-green.svg)
[![Maven](https://img.shields.io/badge/Maven-3.5+-green.svg)](https://github.com/wl4g/xcloud-dopaas)
[![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)](https://github.com/wl4g/xcloud-dopaas)
[![MySQL](https://img.shields.io/badge/MySQL-5.6+-green.svg)](https://github.com/wl4g/xcloud-dopaas)
[![Redis](https://img.shields.io/badge/RedisCluster-3+-green.svg)](https://github.com/wl4g/xcloud-dopaas)
[![Kafka](https://img.shields.io/badge/Kafka-0.10.0+-green.svg)](https://github.com/wl4g/xcloud-dopaas)
[![Zookeeper](https://img.shields.io/badge/Zookeeper-3.4.6+-green.svg)](https://github.com/wl4g/xcloud-dopaas)
[![Docker-ce](https://img.shields.io/badge/DockerCE-18.06+-green.svg)](https://github.com/wl4g/xcloud-dopaas)
[![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
[![GithubStars](https://img.shields.io/github/stars/wl4g/xcloud-dopaas)](https://github.com/wl4g/xcloud-dopaas)
[![GiteeStars](https://gitee.com/wl4g/xcloud-dopaas/badge/star.svg)](https://gitee.com/wl4g/xcloud-dopaas)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-16+-green.svg)](https://gitee.com/wl4g/xcloud-dopaas)
[![CentOS](https://img.shields.io/badge/CentOS-6.5+-green.svg)](https://gitee.com/wl4g/xcloud-dopaas)


### Development and runtime on technology stack(primary)
> This project is mainly based on the development of springboot/cloud/dubbo, which supports the operation of traditional monomer (`standalone`) and fully distributed micro service (Springcloud/Dubbo). The source structure is carefully designed as &nbsp;<b>It's both a platform and a framework demonstration</b>.

- Required dependencies:
<pre>
Spring Boot:2.2 +
Spring Cloud:2.2 +
Eureka:1.10 +
Zipkin:2.15 +
Jdk:8 +
Maven:3.5 +
Mysql:5.6 +
</pre>

- Optional dependencies:
<pre>
Kafka:0.10.0 +
Zookeeper:3.4.6 +
DockerCE:18.06 +
CoreDNS:1.7.0 +
MinIO:latest
Elasticsearch(EFK):6.2.3 +
Shardingsphere-Elasticjob:3.0.0 +
Mesos:1.11.0 +
</pre>

- [Developments](README_DEVEL_CN.md)


### One click automatic deployment:
> It is suitable for rapid deployment in server environment, Note: only backend spring services (including Eureka) deployment are temporarily supported, For other dependent service deployment, please refer to [frontend deploy](#Frontend compiling), It is not recommended to use in high concurrency production environment. If necessary, please replace nginx with ELB/SLB/LVS.

```
bash -c "$(curl -L https://raw.githubusercontent.com/wl4g/xcloud-dopaas/master/script/deploy/deploy-boot.sh)"
# or
bash -c "$(curl -L https://gitee.com/wl4g/xcloud-dopaas/raw/master/script/deploy/deploy-boot.sh)"
```

- Server specs requirements

| Runtime Mode | Min specs requirements(Suggested) | Description |
| ---- | ---- | ---- |
| cluster | CentOS7+ / Ubuntu18+ (4Core+ 8GB+) | Only pseudo clusters can be deployed when there is only one host |
| standalone | 2Core+ 2GB+ | Monomer application |


### Manual compilation and build:
> It is suitable for build secondary development environment

- vim .m2/settings.xml
```xml
<mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public<url>
</mirror>
```

- Backend compiling
> Compile according to the order of project dependency. The `mvn -P` options are: `springExecJar` (packaged as a single executable jar) and `mvnAssTar` (packaged as a general software release package), default. 

```
git clone https://github.com/wl4g/xcloud-component.git # Relatively new upstream (recommended)
# git clone https://gitee.com/wl4g/xcloud-component.git # or
mvn -f xcloud-component -U clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-iam.git # Relatively new upstream (recommended)
# git clone https://gitee.com/wl4g/xcloud-iam.git # or
mvn -f xcloud-iam -U clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-dopaas.git # Relatively new upstream (recommended)
# git clone https://gitee.com/wl4g/xcloud-dopaas.git # or
mvn -f xcloud-dopaas -U clean install -DskipTests -T 2C
```

- Frontend compiling
```
git clone https://github.com/wl4g/xcloud-dopaas-view.git # Relatively new upstream (recommended)
或者 git clone https://gitee.com/wl4g/xcloud-dopaas-view.git
npm run dev # Development debugging
npm run build # Production building
```

- Initial DB: first, prepare a MySQL5.6+ instance and create a database named `dopaas`(utf8/utf8)_bin), and then [Initial DB sql](../../../xcloud-dopaas-db). (Notes: the SQL script should correspond to the source code version. We will update it regularly. It is recommended to use the latest version)

- Configure local DNS: add local virtual domain name resolution （C:\Windows\System32\drivers\etc 或 vim /etc/hosts）：
```
# Notes: The default virtual domain names of various environment databases are different. In rare cases, it may not correspond to the document due to the change of version module. Please check the retention table (sys_cluster_config.extranet_base_uri) The configuration of hosts is as follows:

# dev:
127.0.0.1 wl4g.debug
# fat:
127.0.0.1 wl4g.fat dopaas.wl4g.fat iam.wl4g.fat erm.wl4g.fat ci.wl4g.fat dts.wl4g.fat vcs.wl4g.fat umc.wl4g.fat
# uat:
127.0.0.1 wl4g.uat dopaas.wl4g.uat iam.wl4g.uat erm.wl4g.uat ci.wl4g.uat dts.wl4g.uat vcs.wl4g.uat umc.wl4g.uat
# pro:
127.0.0.1 wl4g.com dopaas.wl4g.com iam.wl4g.com erm.wl4g.com ci.wl4g.com dts.wl4g.com vcs.wl4g.com umc.wl4g.com
```

- Quickly build a redis cluster/docker (optional)
> [https://github.com/wl4g/docker-redis-cluster](https://github.com/wl4g/docker-redis-cluster) 或者 [https://gitee.com/wl4g/docker-redis-cluster](https://github.com/wl4g/docker-redis-cluster)

- Browser Access (Chrome recommended)
> http://wl4g.debug &nbsp;&nbsp; Default account password: root/wl4g.com

- Deployed successful screenshot:
> Tips: the screenshot may be slightly different due to the version evolution, if you have any questions, please join the communication group (see the end section)

![registered-eureka-apps](shots/registered-eureka-apps.png)
- [More shots](shots/)


### Submodule documents
- [UCI](../../blob/master/xcloud-dopaas-uci/README.md)  Unified Continuous Integration Service(CI/CD)
- [UMC](../../blob/master/xcloud-dopaas-umc/README.md)  Unified Monitoring and Operation Center(applications healthing, tracking, alarming, ELK log analysis, etc)
- [URM](../../blob/master/xcloud-dopaas-urm/README.md)  Unified Repository Management(source repo/build repo/image repo)
- [UCM](../../blob/master/xcloud-dopaas-ucm/README.md)  Unified Config Management(Online configuration of hot updates such as dataSource, redisClient, etc)
- [CMDB](../../blob/master/xcloud-dopaas-cmdb/README.md)  Configuration Management Database(hosts, applications, secretKeys, auditing, DNS zone resoluting, etc)
- [UDM](../../blob/master/xcloud-dopaas-udm/README.md)  Unified Document Management(Online APIs/swagger documents, etc)
- [LCDP](../../blob/master/xcloud-dopaas-lcdp/README_CN.md) Low Code Development Platform(Automatic generate of multi language/architecture complete projects (e.g springcloud/dubbo/golang/python/vue/angularjs, etc), WebIDE, and various developer tool chains, etc)
- [UDS](../../blob/master/xcloud-dopaas-uds/README.md)  Unified distributed scheduling control center, such as elasticjob/spark/flink task, etc.
- [UOS](../../blob/master/xcloud-dopaas-uos/README.md)  Unified object storage services, based on springcloud development support NativeFS, HDFS, Aliyun OSS, AWS S3, GlusterFS, etc
- [UIM](../../blob/master/xcloud-dopaas-uim/README.md)  Unified instant messaging service, convenient for project personnel to communicate in time, safe distribution of internal data.


### Other related apps and components documents
- [SHELL](xcloud-shell/README.md)            Shell Cli, adding a hbase-shell-like console to your app
- [IAM](xcloud-iam/README.md)                Unified identity and access management services, support SSO/CAS/oauth2/opensaml etc, It also supports multiple deployment modes(local/cluster/gateway)
- [Gateway](xcloud-gateway/README.md)        Enterprise microservice gateway based on spring cloud gateway, Can integrate with CI to realize Canary deployment.


### Communicate, feedback and contribute?
- Click add to group [![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
- ![q855349515](shots/q855349515.jpg)
- GitHub: https://github.com/wl4g/xcloud-dopaas
- OS China: https://gitee.com/wl4g/xcloud-dopaas
- No matter how strong a person's personal ability is, he can't defeat a team. He hopes that his brothers and sisters can support him and contribute some of his own code to improve it together (^_^).

[Share Code?](https://www.cnblogs.com/wenber/p/3630921.html)