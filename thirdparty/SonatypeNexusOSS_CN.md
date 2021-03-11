# 关于集成 Sonatype Nexus OSS 私有仓库搭建部署资料

### 安装nexus(这里选用docker安装)

```
docker run -p 8081:8081 -d --name nexus sonatype/nexus3
```
nexus的文件存放地址是：/nexus-data/blobs
可以配置文件映射:
```
docker run -p 8081:8081 -d --name nexus -v /mnt/dist1/nexus/nexus-data:/nexus-data snoatype/nexus3
```

### 仓库类型说明
- hosted 类型的仓库，内部项目的发布仓库
- releases内部的模块中release模块的发布仓库
- snapshots发布内部的SNAPSHOT模块的仓库
- proxy 类型的仓库，从远程中央仓库中寻找数据的仓库
- group 类型的仓库，组仓库用来方便我们开发人员进行设置的仓库

### maven配置
#### proxy地址

```
https://maven.aliyun.com/repository/public
https://repo1.maven.org/maven2/
```
 
#### maven的setting.xml配置
```
<mirror>
        <id>maven-public</id>
        <mirrorOf>*</mirrorOf>
        <name>maven-public</name>
        <url>http://10.0.0.101:8081/repository/maven-public/</url>
</mirror>
```

### go配置
#### proxy地址
```
https://mirrors.aliyun.com/goproxy/
https://goproxy.io
```

#### go更改代理地址(通过添加环境变量)
```
export GO111MODULE=on
export GOPROXY=http://localhost:8081/repository/go-public/
```

### npm配置
#### proxy地址
```
http://registry.npm.taobao.org/
https://registry.npmjs.org/
```

#### go更改源

```
#查看本机镜像地址
npm config get registry

#设置源
npm config set registry http://localhost:8081/repository/npm-public/
或
npm install --registry=http://localhost:8081/repository/npm-public/

#还原
npm config set registry https://registry.npmjs.org/
```
