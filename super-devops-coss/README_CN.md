#### COSS(Composite Object Storage Service) 是一个基于SpringCloud复合对象存储服务，支持桥接到其他存储介质，如, hdfs、aliyun-oss、aws-s3、GlusterFS等。

##### 使用场景说明：
- 1，如果您只是用于demo及演示使用，只需使用默认本地（或NFS共享）文件系统，即不需要依赖部署任何coss-xx扩展模块；


- 2，coss-hdfs扩展模块，若您的使用场景更多倾向于归档存储、写多读少的一般场景，就可以使用它；


- 3，coss-glusterfs扩展模块，若您的场景有对POSIX系列标准文件接口依赖，例如：以spark-ml做分布式机器学习，
样本文件又比较大时，那么可以通过外部程序生成样本文件，再以coss API写入glusterfs，然后在spark程序从挂载的glusterfs目录直接读取样本文件即可；


- 4，coss-aliyunoss、coss-awss3等扩展模块，这些扩展都仅是桥接云厂商的OSS，具体使用场景可参考相应云厂商文档说明，集成于此的优势主要有，为企业提供统一基于devops对象/文件存储管理，如，初创项目到后期由于成本考虑需要更换云
厂商对象存储服务时，上层项目代码无需做任何更改。