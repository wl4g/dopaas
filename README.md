![SuperDevops](shots/logo.jpg)
## One stop Micoservice + DevoSecOps Cloud platform(SaaS) developed based on spring cloud. Its main function modules are: continuous delivery of CI/CD(Support distributed compilation, pptpvpn/OpenVPN/SSH tunnel deployment), certification center, monitoring center, configuration center, resource management center, scheduling center, elastic scaling, shell tools, various tool components (such as HBase/OSS operation and maintenance), document management, timely communication, lightweight risk control, private object storage, version control, etc

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


[中文文档](README_CN.md)

### Runtime environment
- Basic environment dependencies (must): JDK8+、Maven3.5+、MySQL5.6+
- Other environment dependencies (if necessary): Docker-ce18.06+、Kafka0.10.0+、Zookeeper3.4.6+


### Quick start:
```
bash -c "$(curl -L https://raw.githubusercontent.com/wl4g/xcloud-devops/master/script/deploy/deploy-boot.sh)"
# or:
bash -c "$(curl -L https://gitee.com/wl4g/xcloud-devops/raw/master/script/deploy/deploy-boot.sh)"
```

### Manual install example：
For the sake of brevity, each service only deploys a single node to the same physical machine, which has been regarded as a pseudo cluster.

- step1：Compile

```
cd xcloud-devops
# Packaged as a single executable jar
mvn -U clean install -DskipTests -T 2C -P springExecJar
# Packaged as general software release package (directory structure)
# mvn -U clean install -DskipTests -T 2C -P mvnAssTar
```
- step2, Initialize the db, First prepare a CentOS 6.5 + and MySQL 5.6 + instance, create a new database named Devops (utf8/utf8_bin), and then use [Initial SQL script](../../../xcloud-devops-data/tree/master/db) to initialize it.（Note: this script corresponds to the code version, and we will update it regularly. Please use the latest)
- step3, Configure hosts, Add local domain name resolution.（C:\Windows\System32\drivers\etc or vim /etc/hosts）：
```
10.0.0.160	wl4g.debug # Corresponding to sys_cluster_config.extranet_base_uri
```
- step4：[Optional] Quickly build a redis cluster (docker). The current version supports the coexistence of redis single point and cluster. In application-{env}.yml redis.nodes When the key is configured with a value, the redis stand-alone mode will be automatically checked.
```
mkdir -p /mnt/disk1/redis/
docker run -itd \
-p 16379:16379/tcp \
-p 16380:16380/tcp \
-p 16381:16381/tcp \
-p 17379:17379/tcp \
-p 17380:17380/tcp \
-p 17381:17381/tcp \
-p 6379:6379/tcp \
-p 6380:6380/tcp \
-p 6381:6381/tcp \
-p 7379:7379/tcp \
-p 7380:7380/tcp \
-p 7381:7381/tcp \
-v /mnt/disk1/redis/:/mnt/disk1/redis/ \
--privileged \
--name=redis_cluster \
wl4g/redis-cluster:latest /sbin/init -XlistenIp='127.0.0.1' -XredisPassword='zzx!@#$%'
```
Domestic friends, if need to speed up(Must>=1.10.0):
```
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://hjbu3ivg.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```
- 4.1 Change the image name (wl4g / redis-cluster: latest) to: registry.cn-shenzhen.aliyuncs.com/wl4g/redis-cluster:latest
- 4.2 If it is a Ali vpc machine, it will fly faster by walking the vpc intranet (Limited to South China 1): registry-vpc.cn-shenzhen.aliyuncs.com/wl4g/redis-cluster:latest


### Docs
- [CI](xcloud-devops-ci/README.md)                  Continuous integration deployment(Continuous iteration), CICD build flow, etc.
- [UMC](xcloud-devops-umc/README.md)                Unified monitoring and operation center, providing real-time application health monitoring, real-time tracking, real-time alarms, etc.
- [VCS](xcloud-devops-vcs/README.md)                Version control service, software source code, release package version management.
- [SCM](xcloud-devops-scm/README.md)                Configure the service center to support online configuration of hot updates such as DataSource, RedisClient, etc.
- [ERM](xcloud-devops-erm/README_CN.md)             Basic resource and environment management, such as elk log analysis, binary product repository, gateway, PrivateZone DNS resolution, etc
- [ESM](xcloud-devops-esm/README.md)                Flexible scalability management, integrated K8s, Docker, management based on CPU, network traffic automatic or manual container scaling.
- [DOC](xcloud-devops-doc/README.md)                API documentation service, online API documentation
- [DJOB](xcloud-djob/README_CN.md)           Based on spring cloud distributed scheduling platform, the default is based on elastic-job enhanced implementation
- [COSS](xcloud-coss/README_CN.md)           Based on the spring cloud composite object storage service, it supports NativeFS, HDFS, Aliyun OSS, AWS S3, GlusterFS, etc
- [SHELL](xcloud-shell/README.md)            Shell Cli, adding a hbase-shell-like console to your app
- [IAM](xcloud-iam/README.md)                Unified identity and access management services, support SSO/CAS/oauth2/opensaml etc, It also supports multiple deployment modes(local/cluster/gateway)
- [Gateway](xcloud-gateway/README.md)        Enterprise microservice gateway based on spring cloud gateway, Can integrate with CI to realize Canary deployment.
- [IM](xcloud-im/README.md)                  Instant messaging system, project personnel communicate in a timely manner, internal data distribution.

### Communicate, feedback and contribute?
- Click add to group [![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
- ![q855349515](shots/q855349515.jpg)
- GitHub: https://github.com/wl4g/xcloud-devops
- OS China: https://gitee.com/wl4g/xcloud-devops
- No matter how strong a person's personal ability is, he can't defeat a team. He hopes that his brothers and sisters can support him and contribute some of his own code to improve it together (^_^).

[Share Code?](https://www.cnblogs.com/wenber/p/3630921.html)


### FAQ
- maven mirror config
```xml
<mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public</url>
</mirror>
```