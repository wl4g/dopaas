# 关于集成 Sonatype Nexus OSS 私有仓库搭建部署资料

### 安装nexus(这里选用docker安装)

```
mkdir -p /mnt/disk1/nexus3
chmod 777 -R /mnt/disk1/nexus3
docker run -p 8081:8081 -d --name nexus3 -v /mnt/disk1/nexus3:/nexus-data sonatype/nexus3
docker logs -f --tail 10 nexus3
```
nexus3(docker)的文件存放路径是：/nexus-data/blobs

### 仓库类型
- `snapshots`  快照仓库，发布内部的SNAPSHOT版本包。
- `hosted`  宿主仓库，通常我们会部署自己的构件到这一类型的仓库，比如公司的第二方库。
- `proxy`  代理仓库，它们被用来代理上游远程公共仓库，如maven中央仓库。
- `group`  组仓库(相当于`proxy`+`hosted`)，用来合并多个hosted/proxy仓库，当你的项目希望在多个repository使用资源时就不需要多次引用了，只需要引用一个group即可。
- `releases`  发布仓库，release项目发布到远程仓库

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
