# XCloud DoPaaS developers description.

### 1. Project codes

```
├── script # 开发、部署相关脚本
│   └── deploy # 一键部署脚本
├── thirdparty # 依赖的第三方中间件部署及相关资料
├── xcloud-dopaas-all-dependencies
├── xcloud-dopaas-all-starter # standalone模式的启动类
├── xcloud-dopaas-cmdb # CMDB管控中心，如主机、应用集群、密钥等配置管理
│   ├── xcloud-dopaas-cmdb-initializer
│   ├── xcloud-dopaas-cmdb-service-data # 逻辑层微服务数据操作，如 dao类
│   ├── xcloud-dopaas-cmdb-service-facade # 逻辑层微服务spi接口，如 service接口类
│   ├── xcloud-dopaas-cmdb-service-starter-facade # 逻辑层逻辑层微服务spi接口实现，如 serviceImpl类
│   └── xcloud-dopaas-cmdb-service-starter-manager # web层对外接口启动类，如 controller类
├── xcloud-dopaas-common # 跨模块公共类，如 各种bean、utils
├── xcloud-dopaas-uci # 统一构建流水线服务
│   ├── xcloud-dopaas-uci-cmdtools
│   ├── xcloud-dopaas-uci-core
│   ├── xcloud-dopaas-uci-example
│   ├── xcloud-dopaas-uci-service-data
│   ├── xcloud-dopaas-uci-service-facade
│   ├── xcloud-dopaas-uci-service-starter-analyzer
│   ├── xcloud-dopaas-uci-service-starter-facade
│   └── xcloud-dopaas-uci-service-starter-server
├── xcloud-dopaas-ucm # 统一配置管理中心
│   ├── xcloud-dopaas-ucm-client-c
│   ├── xcloud-dopaas-ucm-client-go
│   ├── xcloud-dopaas-ucm-client-java
│   ├── xcloud-dopaas-ucm-client-springboot
│   ├── xcloud-dopaas-ucm-common
│   ├── xcloud-dopaas-ucm-config
│   ├── xcloud-dopaas-ucm-core
│   ├── xcloud-dopaas-ucm-example
│   └── xcloud-dopaas-ucm-server
├── xcloud-dopaas-udc # 统一开发中心
│   ├── xcloud-dopaas-udc-codegen
│   ├── xcloud-dopaas-udc-service-facade
│   ├── xcloud-dopaas-udc-service-starter-facade
│   ├── xcloud-dopaas-udc-service-starter-manager
│   └── xcloud-dopaas-udc-tools
├── xcloud-dopaas-udm # 统一文档管理中心
│   ├── xcloud-dopaas-udm-plugin
│   ├── xcloud-dopaas-udm-service-data
│   ├── xcloud-dopaas-udm-service-facade
│   ├── xcloud-dopaas-udm-service-starter-facade
│   └── xcloud-dopaas-udm-service-starter-manager
├── xcloud-dopaas-umc # 统一监控中心
│   ├── xcloud-dopaas-umc-agent-go
│   ├── xcloud-dopaas-umc-alarm
│   ├── xcloud-dopaas-umc-client-springboot
│   ├── xcloud-dopaas-umc-example
│   ├── xcloud-dopaas-umc-service-data
│   ├── xcloud-dopaas-umc-service-facade # 监控中心管控服务spi接口
│   ├── xcloud-dopaas-umc-service-starter-collector # 监控中心指标采集服务端
│   ├── xcloud-dopaas-umc-service-starter-facade # 监控中心管控服务spi接口实现，如 mysql操作
│   ├── xcloud-dopaas-umc-service-starter-manager # 监控中心管控web服务
│   ├── xcloud-dopaas-umc-service-starter-tracker # 微服务链路追踪服务端，如 zipkin、jaeger等
│   ├── xcloud-dopaas-umc-store # 监控中心存储，如 指标采集服务端需存储(OpenTsdb/Cassandra等)
│   └── xcloud-dopaas-umc-watch # 监控中心主动抓取监听模块，如 Iot设备在离线状态主动监测
├── xcloud-dopaas-urm # 统一仓库管理中心，如 snaptype oss(git仓库、maven仓库、gradle仓库、image仓库、golang仓库等)
│   ├── xcloud-dopaas-urm-operator # 仓库操作sdk，如 uci模块拉取代码 git clone|pull|checkout等操作
│   ├── xcloud-dopaas-urm-service-data
│   ├── xcloud-dopaas-urm-service-facade
│   ├── xcloud-dopaas-urm-service-starter-facade
│   └── xcloud-dopaas-urm-service-starter-manager
└── xcloud-dopaas-view # 前端项目
```