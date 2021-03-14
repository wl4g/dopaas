## UMC - An open source unified monitoring center based on SpringBoot Cloud that supports real-time link tracking, real-time resource monitoring, and alarms.

[中文文档](README_CN.md)

[Client Docs](https://github.com/wl4g/umc-agent/blob/master/README.md)

#### 编译客户端GO
- 直接运行客户端
```
go run watch.go mynet.go
```
- 编译成window可执行
```
GOOS=windows GOARCH=amd64 go build watch.go mynet.go
```
- 编译成linux可执行
```
GOOS=linux GOARCH=amd64 go build watch.go mynet.go
```

#### 配置文件

- netCommand.txt

这个文件里配置的是查询网络信息所需要的命令，放go可执行文件同级目录
```
ss -n sport == #{port} |
awk '
BEGIN{up=0;down=0;n=0;c1=0;c2=0;c3=0;c4=0;c5=0;c6=0;}
{up+=$3};
{down+=$4};
{n+=1}
{if($0~"ESTAB") c1+=1};
{if($0~"CLOSE-WAIT") c2+=1};
{if($0~"TIME-WAIT") c3+=1};
{if($0~"CLOSE"&&!$0~"CLOSE-WAIT") c4+=1};
{if($0~"LISTEN") c5+=1};
{if($0~"CLOSING") c6+=1};
END {print up,down,n,c1,c2,c3,c4,c5,c6}
'
```

- conf.yml
```
#post uri
server-uri: http://10.0.0.26:14047/umc/basic

#physical
physical:
  #delay(ms)
  delay: 10000
  #network card
  net: en0
  gatherPort: 22,9092
```
解释：
```
server-uri:服务请求地址
delay:提交频率
net:网卡名，根据这个去找ip和mac，找不到会主动报错
gatherPort:网络信息采集的端口，逗号分割
```


#### 服务端
- 配置
```
opentsdb:
  url: http://10.0.0.57
  port: 4242
```
解释：
```
url:opentsdb服务请求地址
port:opentsdb服务请求端口
```


#### opentsdb部署相关
```
注：尝试过从官网下tar包，也尝试过从github上拉代码，都出现些许问题，自己编译有一定的风险
最终使用下载rpm包来安装
```
- 参考资料:

#### grafana安装相关
注意：
```
由于umc模块的前端嵌入grafana，但嵌入grafana有跨域问题，导致无法登录，所以暂时配置grafana允许匿名登录
修改/etc/grafana/grafana.ini
把[auth.anonymous]下的enabled注释解开并改为true
把allow_embedding的注释解开并改为true
参考：https://blog.csdn.net/weixin_41621706/article/details/100815603
```


