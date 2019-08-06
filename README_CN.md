![DevSecOps](shots/logo.jpg)
## 一个轻量而全面的微服务DevSecOps解决方案.

![Build Status](https://travis-ci.org/wl4g/super-devops.svg)
![License](https://camo.githubusercontent.com/ce4fb5b3ec026da9d76d9de28d688d0a0d493949/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6c6963656e73652f73706f746966792f646f636b657266696c652d6d6176656e2e737667)


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
- QQ 群号： 群1(855349515)
- GitHub：https://github.com/wl4g/super-devops
- 开源中国：https://gitee.com/wl4g/super-devops
- 一个人的个人能力再强，也无法战胜一个团队，希望兄弟姐妹的支持，能够贡献出自己的部分代码，参与进来共同完善它(^_^)。

[如何共享代码](https://www.cnblogs.com/wenber/p/3630921.html)