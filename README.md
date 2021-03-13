## XCloud DevOps
![XCloud DevSecOps](shots/logo.jpg)

[中文文档](README_CN.md) | English version goes [here](README.md)

### One stop Devops solution SaaS platform is developed based on spring cloud / docker / istio, Main modules: continuous delivery of CI / CD (distributed compilation and deployment), Iam Certification Center, unified monitoring center, unified configuration center, unified resource management, task scheduling center, flexible scaling, shell tools, various tool chains (such as HBase / OSS operation and maintenance), document management, timely communication, lightweight risk control, private object storage, version control, etc

[![Build Status](https://travis-ci.org/wl4g/xcloud-devops.svg)](https://travis-ci.org/wl4g/xcloud-devops)
![License](https://img.shields.io/badge/license-Apache2.0+-green.svg)
[![Maven](https://img.shields.io/badge/Maven-3.5+-green.svg)](https://github.com/wl4g/xcloud-devops)
[![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)](https://github.com/wl4g/xcloud-devops)
[![MySQL](https://img.shields.io/badge/MySQL-5.6+-green.svg)](https://github.com/wl4g/xcloud-devops)
[![Redis](https://img.shields.io/badge/RedisCluster-3+-green.svg)](https://github.com/wl4g/xcloud-devops)
[![Kafka](https://img.shields.io/badge/Kafka-0.10.0+-green.svg)](https://github.com/wl4g/xcloud-devops)
[![Zookeeper](https://img.shields.io/badge/Zookeeper-3.4.6+-green.svg)](https://github.com/wl4g/xcloud-devops)
[![Docker-ce](https://img.shields.io/badge/DockerCE-18.06+-green.svg)](https://github.com/wl4g/xcloud-devops)
[![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
[![GithubStars](https://img.shields.io/github/stars/wl4g/xcloud-devops)](https://github.com/wl4g/xcloud-devops)
[![GiteeStars](https://gitee.com/wl4g/xcloud-devops/badge/star.svg)](https://gitee.com/wl4g/xcloud-devops)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-16+-green.svg)](https://gitee.com/wl4g/xcloud-devops)
[![CentOS](https://img.shields.io/badge/CentOS-6.5+-green.svg)](https://gitee.com/wl4g/xcloud-devops)


### One click automatic deployment:
> It is suitable for rapid deployment in server environment

```
bash -c "$(curl -L https://raw.githubusercontent.com/wl4g/xcloud-devops/master/script/deploy/deploy-boot.sh)"
# or
bash -c "$(curl -L https://gitee.com/wl4g/xcloud-devops/raw/master/script/deploy/deploy-boot.sh)"
```

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

git clone https://github.com/wl4g/xcloud-devops.git # Relatively new upstream (recommended)
# git clone https://gitee.com/wl4g/xcloud-devops.git # or
mvn -f xcloud-devops -U clean install -DskipTests -T 2C
```

- Frontend compiling
```
git clone https://github.com/wl4g/xcloud-devops-view.git # Relatively new upstream (recommended)
或者 git clone https://gitee.com/wl4g/xcloud-devops-view.git
npm run dev # Development debugging
npm run build # Production building
```

- Initial DB: first, prepare a MySQL5.6+ instance and create a database named `devops`(utf8/utf8)_bin), and then [Initial DB sql](../../../xcloud-devops-db). (Notes: the SQL script should correspond to the source code version. We will update it regularly. It is recommended to use the latest version)

- Configure local DNS: add local virtual domain name resolution （C:\Windows\System32\drivers\etc 或 vim /etc/hosts）：
```
# Notes: The default virtual domain names of various environment databases are different. In rare cases, it may not correspond to the document due to the change of version module. Please check the retention table (sys_cluster_config.extranet_base_uri) The configuration of hosts is as follows:

# dev:
127.0.0.1 wl4g.debug
# fat:
127.0.0.1 wl4g.fat devops.wl4g.fat iam.wl4g.fat erm.wl4g.fat ci.wl4g.fat dts.wl4g.fat vcs.wl4g.fat umc.wl4g.fat
# uat:
127.0.0.1 wl4g.uat devops.wl4g.uat iam.wl4g.uat erm.wl4g.uat ci.wl4g.uat dts.wl4g.uat vcs.wl4g.uat umc.wl4g.uat
# pro:
127.0.0.1 wl4g.com devops.wl4g.com iam.wl4g.com erm.wl4g.com ci.wl4g.com dts.wl4g.com vcs.wl4g.com umc.wl4g.com
```

- Quickly build a redis cluster/docker (optional)
> [https://github.com/wl4g/docker-redis-cluster](https://github.com/wl4g/docker-redis-cluster) 或者 [https://gitee.com/wl4g/docker-redis-cluster](https://github.com/wl4g/docker-redis-cluster)

- Browser Access (Chrome recommended)
> http://wl4g.debug &nbsp;&nbsp; Default account password: root/wl4g.com

- Deployed successful screenshot:
> Tips: the screenshot may be slightly different due to the version evolution.

![registered-eureka-apps](shots/registered-eureka-apps.png)
- [More shots](shots/)


### Submodule documents
- [UCI](xcloud-devops-uci/README.md)  Unified Continuous Integration Service(CI/CD)
- [UMC](xcloud-devops-umc/README.md)  Unified Monitoring and Operation Center(applications healthing, tracking, alarming, ELK log analysis, etc)
- [URM](xcloud-devops-urm/README.md)  Unified Repository Management(source repo/build repo/image repo)
- [UCM](xcloud-devops-ucm/README.md)  Unified Config Management(Online configuration of hot updates such as dataSource, redisClient, etc)
- [CMDB](xcloud-devops-cmdb/README.md)  Configuration Management Database(hosts, applications, secretKeys, auditing, DNS zone resoluting, etc)
- [UDM](xcloud-devops-udm/README.md)  Unified Document Management(Online APIs/swagger documents, etc)
- [UDC](xcloud-devops-dts/README.md)  Unified Developer Center(Devel tools, WebIDE, AutoGenerator, etc)

### Other related apps and components documents
- [DJOB](xcloud-djob/README_CN.md)           Based on spring cloud distributed scheduling platform, the default is based on elastic-job enhanced implementation
- [COSS](xcloud-coss/README_CN.md)           Based on the spring cloud composite object storage service, it supports NativeFS, HDFS, Aliyun OSS, AWS S3, GlusterFS, etc
- [SHELL](xcloud-shell/README.md)            Shell Cli, adding a hbase-shell-like console to your app
- [IAM](xcloud-iam/README.md)                Unified identity and access management services, support SSO/CAS/oauth2/opensaml etc, It also supports multiple deployment modes(local/cluster/gateway)
- [Gateway](xcloud-gateway/README.md)        Enterprise microservice gateway based on spring cloud gateway, Can integrate with CI to realize Canary deployment.
- [IM](xcloud-im/README.md)                  Instant messaging system, project personnel communicate in a timely manner, internal data distribution.


### 运行环境
- 基础环境依赖（必须）：JDK8+、Maven3.5+、MySQL5.6+
- 其他环境依赖（若需要）：Docker-ce18.06+、Kafka0.10.0+、Zookeeper3.4.6+


### Communicate, feedback and contribute?
- Click add to group [![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
- ![q855349515](shots/q855349515.jpg)
- GitHub: https://github.com/wl4g/xcloud-devops
- OS China: https://gitee.com/wl4g/xcloud-devops
- No matter how strong a person's personal ability is, he can't defeat a team. He hopes that his brothers and sisters can support him and contribute some of his own code to improve it together (^_^).

[Share Code?](https://www.cnblogs.com/wenber/p/3630921.html)