## XCloud DevOps
![XCloud DevSecOps](shots/logo.jpg)

[中文文档](README_CN.md) | English version goes [here](README.md)

### 一站式 DevOps 解决方案SaaS平台，基于SpringCloud/Docker/Istio开发, 主要模块：持续交付CI/CD(分布式编译部署)、IAM认证中心、统一监控中心、统一配置中心、统一资源管理、任务调度中心、弹性伸缩、shell工具、各种工具链(如HBase/OSS运维)、文档管理、及时通讯、轻量级风控、私有对象存储、版本控制等

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


### 一键自动部署：
> 适用于服务器环境快速部署

```
bash -c "$(curl -L https://raw.githubusercontent.com/wl4g/xcloud-devops/master/script/deploy/deploy-boot.sh)"
# 或者
bash -c "$(curl -L https://gitee.com/wl4g/xcloud-devops/raw/master/script/deploy/deploy-boot.sh)"
```

### 手动编译搭建：
> 适用于二次开发环境搭建

- 修改 .m2/settings.xml
```xml
<mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public<url>
</mirror>
```

- 后端编译
> 按项目依赖顺序进行编译, 其中 `mvn -P` 选项有：`springExecJar` (打包为单个可执行jar)、`mvnAssTar` (打包为通用软件发布包)，默认
```
git clone https://github.com/wl4g/xcloud-component.git # 上游较新（推荐）
或者 git clone https://gitee.com/wl4g/xcloud-component.git
mvn -f xcloud-component -U clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-iam.git # 上游较新（推荐）
或者 git clone https://gitee.com/wl4g/xcloud-iam.git
mvn -f xcloud-iam -U clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-devops.git # 上游较新（推荐）
或者 git clone https://gitee.com/wl4g/xcloud-devops.git
mvn -f xcloud-devops -U clean install -DskipTests -T 2C
```

- 前端编译
```
git clone https://github.com/wl4g/xcloud-devops-view.git # 上游较新（推荐）
或者 git clone https://gitee.com/wl4g/xcloud-devops-view.git
npm run dev # 开发调试
npm run build # 生产打包
```

- 初始数据库：首先准备一台MySQL5.6+实例，创建名为devops(utf8/utf8_bin)的库，再 [初始数据库](../../../xcloud-devops-db)。（注：sql脚本需与源码版本对应，我们会定期更新，建议都使用最新）

- 配置本地DNS：添加本地虚拟域名解析（C:\Windows\System32\drivers\etc 或 vim /etc/hosts）：
```
# 注：多种环境数据库默认配置的虚拟域名不同，极少情况可能由于版本模块变化导致与文档不对应，具体请检查保持表(sys_cluster_config.extranet_base_uri)配置对应即可，如下hosts配置供参考：
# dev:
127.0.0.1 wl4g.debug
# fat:
127.0.0.1 wl4g.fat devops.wl4g.fat iam.wl4g.fat erm.wl4g.fat ci.wl4g.fat dts.wl4g.fat vcs.wl4g.fat umc.wl4g.fat
# uat:
127.0.0.1 wl4g.uat devops.wl4g.uat iam.wl4g.uat erm.wl4g.uat ci.wl4g.uat dts.wl4g.uat vcs.wl4g.uat umc.wl4g.uat
# pro:
127.0.0.1 wl4g.com devops.wl4g.com iam.wl4g.com erm.wl4g.com ci.wl4g.com dts.wl4g.com vcs.wl4g.com umc.wl4g.com
```

- 快速搭建redis/docker集群(可选)
> [https://github.com/wl4g/docker-redis-cluster](https://github.com/wl4g/docker-redis-cluster) 或者 [https://gitee.com/wl4g/docker-redis-cluster](https://github.com/wl4g/docker-redis-cluster)

- 浏览器访问（建议使用Chrome）
> http://wl4g.debug &nbsp;&nbsp; 默认账号密码：root/wl4g.com

- 部署成功示例部分截图
> 提示：截图可能由于版本演变存在略微差异

![registered-eureka-apps](shots/registered-eureka-apps.png)
- [更多截图](shots/)


### 子模块文档
- [UCI](xcloud-devops-uci/README_CN.md)  (Unified Continuous Integration)统一持续集成交付服务, CI/CD等
- [UMC](xcloud-devops-umc/README_CN.md)  (Unified Monitoring and Operation Center)统一监控运维中心, 应用健康实时监控、实时追踪、实时告警、ELK日志分析等
- [URM](xcloud-devops-urm/README_CN.md)  (Unified Repository Management)统一仓库管理, 源码仓库、构建包仓库、镜像仓库等
- [UCM](xcloud-devops-ucm/README_CN.md)  (Unified Config Management)统一配置管理服务, 在线配置热更新如：DataSource、RedisClient等
- [CMDB](xcloud-devops-cmdb/README_CN.md)  (Configuration Management Database)统一资产管理, 主机、密钥、审计、DNS等基础设施资产管理等
- [UDM](xcloud-devops-udm/README_CN.md)  (Unified Document Management)统一文档管理, API/Swagger在线文档/Office/Pdf等
- [UDC](xcloud-devops-udc/README_CN.md)  (Unified Developer Center)统一开发中心, 各种开发者工具链、WebIDE、自动生成器等

### 其他相关应用/组件文档
- [DJOB](xcloud-djob/README_CN.md)                  基于SpringCloud分布式调度平台, 默认是基于ElasticJob增强实现
- [COSS](xcloud-coss/README_CN.md)					基于SpringCloud复合对象存储服, 支持 NativeFS, HDFS, Aliyun OSS, AWS S3, GlusterFS 等
- [SHELL](xcloud-devops-shell/README_CN.md)         Shell Cli, 给应用添加类似hbase-shell的控制台功能
- [IAM](xcloud-iam/README_CN.md)					统一身份识别与访问管理服务，支持SSO/CAS、oauth2、opensaml等，同时支持多种部署模式（local/cluster/gateway）
- [Gateway](xcloud-gateway/README_CN.md)			基于spring-cloud-gateway的企业级微服务网关, 可与CI整合实现金丝雀部署等等高级特性.
- [IM](xcloud-im/README_CN.md)						即时通讯系统, 项目人员及时沟通，内部资料分发


### 运行环境
- 基础环境依赖（必须）：JDK8+、Maven3.5+、MySQL5.6+
- 其他环境依赖（若需要）：Docker-ce18.06+、Kafka0.10.0+、Zookeeper3.4.6+


### 如何交流、反馈、参与贡献？
- 点击入群 [![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
- ![q855349515](shots/q855349515.jpg)
- GitHub：https://github.com/wl4g/xcloud-devops
- 开源中国：https://gitee.com/wl4g/xcloud-devops
- 一个人的个人能力再强，也无法战胜一个团队，希望兄弟姐妹的支持，能够贡献出自己的部分代码，参与进来共同完善它(^_^)。

[如何共享代码](https://www.cnblogs.com/wenber/p/3630921.html)
