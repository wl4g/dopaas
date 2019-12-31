![DevSecOps](shots/logo.jpg)
## One stop micro service-oriented lightweight DevSecOps solution, supports CI/CD, unified monitoring, unified authentication, unified configuration, log analysis, elastic scaling, scheduling center, command-line services, HBase operation and maintenance tools, document management, version control, etc

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


[中文文档](README_CN.md)

### Runtime environment
- Basic environment dependencies (must): JDK8+、Maven3.5+、MySQL5.6+
- Other environment dependencies (if necessary): Docker-ce18.06+、Kafka0.10.0+、Zookeeper3.4.6+


### Quick start example:
For the sake of brevity, each service only deploys a single node to the same physical machine, which has been regarded as a pseudo cluster.

- step1, First, prepare a CentOS 6.5 + and MySQL 5.6 + instance, create a new database named Devops (utf8 / utf8_bin), and then use [initial SQL script] (dB /) to initialize it.（Note: this script corresponds to the code version, and we will update it regularly. Please use the latest one according to the named suffix date）
- step2, Configure local hosts virtual domain name resolution.（C:\Windows\System32\drivers\etc or vim /etc/hosts）：
```
10.0.0.160    wl4g.debug # Corresponding to app_cluster_config.extranet_base_uri
```


### Docs
- [CI](super-devops-ci/README.md)             &nbsp;&nbsp;    Continuous integration deployment(Continuous iteration), CICD build flow, etc.
- [ESM](super-devops-esm/README.md)           &nbsp;&nbsp;    Flexible scalability management, integrated k8s, docker, management based on CPU, network traffic automatic or manual container scaling.
- [SCM](super-devops-scm/README.md)           &nbsp;&nbsp;    Configure the service center to support online configuration of hot updates such as DataSource, RedisClient, etc.
- [SRM](super-devops-srm/README.md)           &nbsp;&nbsp;    Service resource management, such as integrated ELK log system, etc.
- [SHELL](super-devops-shell/README.md)       &nbsp;&nbsp;    Shell Cli, adding a hbase-shell-like console to your app
- [DOC](super-devops-doc/README.md)           &nbsp;&nbsp;    API documentation service, online API documentation
- [IAM](super-devops-iam/README.md)           &nbsp;&nbsp;    Unified identity and access management services, supporting SSO/CAS, oauth2, opensaml, etc.
- [IM](super-devops-im/README.md)             &nbsp;&nbsp;    Instant messaging system, project personnel communicate in a timely manner, internal data distribution.
- [UMC](super-devops-umc/README.md)           &nbsp;&nbsp;    Unified monitoring and operation center, providing real-time application health monitoring, real-time tracking, real-time alarms, etc.
- [VCS](super-devops-vcs/README.md)           &nbsp;&nbsp;    Version control service, software source code, release package version management.

### Communicate, feedback and contribute?
- Click add to group [![QQ1](https://img.shields.io/badge/QQ1-855349515-green.svg)](https://shang.qq.com/wpa/qunwpa?idkey=0343b06591d19188d86dc078912adfc5c40f023c8ec5a0d1eda5bdfc35ab40d0)
- ![q855349515](shots/q855349515.jpg)
- GitHub: https://github.com/wl4g/super-devops
- OS China: https://gitee.com/wl4g/super-devops
- No matter how strong a person's personal ability is, he can't defeat a team. He hopes that his brothers and sisters can support him and contribute some of his own code to improve it together (^_^).

[Share Code?](https://www.cnblogs.com/wenber/p/3630921.html)
