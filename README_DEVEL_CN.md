# XCloud DoPaaS developers description.

### 1. Project codes

```
├── script # 开发、部署相关脚本
│   └── deploy # 一键部署脚本
├── thirdparty # 依赖的第三方中间件部署及相关资料
├── xcloud-dopaas-all-dependencies
├── xcloud-dopaas-all-starter # standalone模式的启动类
├── xcloud-dopaas-cmdb # CMDB管控中心，如主机、应用集群、密钥等配置管理
│   ├── xcloud-dopaas-cmdb-initializer # 应用安装部署模块(alpha)
│   ├── xcloud-dopaas-cmdb-service-data # 逻辑层微服务数据操作，如 dao类
│   ├── xcloud-dopaas-cmdb-service-facade # 逻辑层微服务spi接口，如 service接口类
│   ├── xcloud-dopaas-cmdb-service-starter-facade # 逻辑层逻辑层微服务spi接口实现，如 serviceImpl类
│   └── xcloud-dopaas-cmdb-service-starter-manager # web层对外接口启动类，如 controller类
├── xcloud-dopaas-common # 跨模块公共类，如 各种bean/utils/constants/config
├── xcloud-dopaas-uci # 统一构建流水线服务
│   ├── xcloud-dopaas-uci-cmdtools(alpha)
│   ├── xcloud-dopaas-uci-core
│   ├── xcloud-dopaas-uci-example
│   ├── xcloud-dopaas-uci-service-data # 集成构建中心DAO层
│   ├── xcloud-dopaas-uci-service-facade # 集成构建中心spi接口层
│   ├── xcloud-dopaas-uci-service-starter-analyzer # 集成构建中心代码扫描分析服务
│   ├── xcloud-dopaas-uci-service-starter-facade # 集成构建中心spi接口实现层
│   └── xcloud-dopaas-uci-service-starter-server # 集成构建中心服务端web层
├── xcloud-dopaas-ucm # 统一配置管理中心(alpha)
│   ├── xcloud-dopaas-ucm-client-c # 配置中心 C 客户端
│   ├── xcloud-dopaas-ucm-client-go # 配置中心 golang 客户端
│   ├── xcloud-dopaas-ucm-client-java # 配置中心普通 java 客户端
│   ├── xcloud-dopaas-ucm-client-springboot # 配置中心 springboot 客户端
│   ├── xcloud-dopaas-ucm-common # 配置中心公共模块
│   ├── xcloud-dopaas-ucm-config # 配置中心内部模块
│   ├── xcloud-dopaas-ucm-core # 配置中心服务端核心模块
│   ├── xcloud-dopaas-ucm-example # 配置中心示例应用客户端
│   └── xcloud-dopaas-ucm-server # 配置中心服务端web层，如 long-http watch接口
├── xcloud-dopaas-udc # 统一开发中心
│   ├── xcloud-dopaas-udc-codegen # 开发中心自动代码生成模块，支持多种语言(框架/模式)模版组合生成，如 dubbo/springcloud/golang/vue/angularjs/dao/service/controller等
│   ├── xcloud-dopaas-udc-service-facade # 开发中心spi接口层
│   ├── xcloud-dopaas-udc-service-starter-facade # 开发中心spi实现层
│   ├── xcloud-dopaas-udc-service-starter-manager # 开发中心web层
│   └── xcloud-dopaas-udc-tools # 开发和运维工具，如HBASE导入导出、批量添加代码版权、注释、代码量统计等
├── xcloud-dopaas-udm # 统一文档管理中心
│   ├── xcloud-dopaas-udm-plugin # 支持swagger静态(编译阶段)文档数据生成maven插件，与UCI集成，每次构建自动更新文档
│   ├── xcloud-dopaas-udm-service-data # 文档管理中心DAO层
│   ├── xcloud-dopaas-udm-service-facade # 文档管理中心spi接口层
│   ├── xcloud-dopaas-udm-service-starter-facade # 文档管理中心spi接口实现层
│   └── xcloud-dopaas-udm-service-starter-manager # 文档管理中心web接口层
├── xcloud-dopaas-umc # 统一监控中心
│   ├── xcloud-dopaas-umc-agent-go # 监控中心采集指标agent(golang版)，如Cpu/Mem/Disks等(alpha)
│   ├── xcloud-dopaas-umc-alarm # 监控中心告警处理模块(alpha)
│   ├── xcloud-dopaas-umc-client-springboot # 监控中心客户端，如自动配置springboot-admin/zipkin等
│   ├── xcloud-dopaas-umc-example # 监控中心示例应用客户端
│   ├── xcloud-dopaas-umc-service-data # 监控中心DAO层
│   ├── xcloud-dopaas-umc-service-facade # 监控中心管控服务spi接口层
│   ├── xcloud-dopaas-umc-service-starter-collector # 监控中心指标采集服务端
│   ├── xcloud-dopaas-umc-service-starter-facade # 监控中心管控服务spi接口实现，如 mysql操作
│   ├── xcloud-dopaas-umc-service-starter-manager # 监控中心管控web层
│   ├── xcloud-dopaas-umc-service-starter-tracker # 微服务链路追踪服务端，如 zipkin、jaeger等
│   ├── xcloud-dopaas-umc-store # 监控中心存储，如 指标采集服务端需存储(OpenTsdb/Cassandra等)(alpha)
│   └── xcloud-dopaas-umc-watch # 监控中心主动抓取监听模块，如 Iot设备在离线状态主动监测(alpha)
├── xcloud-dopaas-urm # 统一仓库管理中心，如 snaptype oss(git仓库、maven仓库、gradle仓库、image仓库、golang仓库等)
│   ├── xcloud-dopaas-urm-operator # 仓库操作sdk，如 uci模块拉取代码 git clone|pull|checkout等操作
│   ├── xcloud-dopaas-urm-service-data
│   ├── xcloud-dopaas-urm-service-facade
│   ├── xcloud-dopaas-urm-service-starter-facade
│   └── xcloud-dopaas-urm-service-starter-manager
└── xcloud-dopaas-view # 前端项目
```