![DevSecOps](shots/logo.jpg)
## 一个轻量全面的微服务DevSecOps解决方案，CICD、统一监控、统一认证、配置中心、日志分析、弹性伸缩、Shell CLI、HBase运维工具、DOCS、VCS等

[![Build Status](https://travis-ci.org/wl4g/super-devops.svg)](https://travis-ci.org/wl4g/super-devops)
![License](https://img.shields.io/badge/license-Apache2.0+-green.svg)
[![Maven](https://img.shields.io/badge/Maven-3.5+-green.svg)](https://github.com/wl4g/super-devops)
[![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)](https://github.com/wl4g/super-devops)
[![MySQL](https://img.shields.io/badge/MySQL-5.6+-green.svg)](https://github.com/wl4g/super-devops)
[![Redis](https://img.shields.io/badge/RedisCluster-3+-green.svg)](https://github.com/wl4g/super-devops)
[![Kafka](https://img.shields.io/badge/Kafka-0.10.0+-green.svg)](https://github.com/wl4g/super-devops)
[![Zookeeper](https://img.shields.io/badge/Zookeeper-3.4.6+-green.svg)](https://github.com/wl4g/super-devops)
[![Docker-ce](https://img.shields.io/badge/DockerCE-18.06+-green.svg)](https://github.com/wl4g/super-devops)
[![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
[![GithubStars](https://img.shields.io/github/stars/wl4g/super-devops)](https://github.com/wl4g/super-devops)
[![GiteeStars](https://gitee.com/wl4g/super-devops/badge/star.svg)](https://gitee.com/wl4g/super-devops)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-16+-green.svg)](https://gitee.com/wl4g/super-devops)
[![CentOS](https://img.shields.io/badge/CentOS-6.5+-green.svg)](https://gitee.com/wl4g/super-devops)


English version goes [here](README.md)

### 运行时环境
- 基础环境依赖（必须）：JDK8+、Maven3.5+、MySQL5.6+
- 其他环境依赖（若需要）：Docker-ce18.06+、Kafka0.10.0+、Zookeeper3.4.6+

### 文档
- [CI](super-devops-ci/README_CN.md)            &nbsp;&nbsp;    持续集成部署模块（持续迭代）, CICD构建流等
- [ESM](super-devops-esm/README_CN.md)          &nbsp;&nbsp;    弹性伸缩管理, 集成k8s、docker，基于CPU、网络流量自动或手动容器伸缩的管理
- [SCM](super-devops-scm/README_CN.md)          &nbsp;&nbsp;    配置服务中心, 支持在线配置热更新如：DataSource、RedisClient等
- [SRM](super-devops-srm/README_CN.md)          &nbsp;&nbsp;    服务资源管理, 如，集成ELK日志系统等
- [SHELL](super-devops-shell/README_CN.md)      &nbsp;&nbsp;    Shell Cli, 给应用添加类似hbase-shell的控制台功能
- [DOC](super-devops-doc/README_CN.md)          &nbsp;&nbsp;    API文档服务, 在线API文档查阅
- [IAM](super-devops-iam/README_CN.md)          &nbsp;&nbsp;    统一身份识别与访问管理服务，支持SSO/CAS、oauth2、opensaml等
- [IM](super-devops-im/README_CN.md)            &nbsp;&nbsp;    即时通讯系统, 项目人员及时沟通，内部资料分发
- [UMC](super-devops-umc/README_CN.md)          &nbsp;&nbsp;    统一监控运维中心, 提供应用健康实时监控、实时追踪、实时告警等
- [VCS](super-devops-vcs/README_CN.md)          &nbsp;&nbsp;    版本控制服务, 软件源码、释放包版本管理


### 如何交流、反馈、参与贡献？
- QQ1(855349515)
- ![q855349515](shots/q855349515.jpg)
- GitHub：https://github.com/wl4g/super-devops
- 开源中国：https://gitee.com/wl4g/super-devops
- 一个人的个人能力再强，也无法战胜一个团队，希望兄弟姐妹的支持，能够贡献出自己的部分代码，参与进来共同完善它(^_^)。

[如何共享代码](https://www.cnblogs.com/wenber/p/3630921.html)