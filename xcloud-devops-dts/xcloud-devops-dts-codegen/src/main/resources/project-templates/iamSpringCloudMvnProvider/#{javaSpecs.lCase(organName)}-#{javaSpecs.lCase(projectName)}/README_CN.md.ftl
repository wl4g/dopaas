<#assign topDomain = organName?lower_case + '.debug' />
<#assign subDomainOfBackend = projectName?lower_case + '-services' />
<#assign subDomain = projectName?lower_case />
<#assign serverName = projectName?lower_case + '-server' />
<#assign redisHost = 'redis.' + topDomain />
# ${organName?cap_first} ${projectName?cap_first}

## 简介
${projectDescription}


## 文档
- English version goes [here](README.md)


## 功能特性
<#if moduleMap??>
    <#list moduleMap?keys as moduleName>
- ${moduleName}
	</#list>
</#if>


## 快速开始

- 从源码构建：
```
mvn clean install -DskipTests -T 2C
```

启动后准备访问API, 其中baseURI为: [http://${subDomainOfBackend}.${topDomain}:28080/${serverName}](http://${subDomainOfBackend}.${topDomain}:28080/${serverName})
> 注：在尝试测试访问之前请确保已添加本地hosts解析：
```
127.0.0.1  ${subDomainOfBackend + '.' + topDomain} # 后端服务域名（dev环境，默认本机）
127.0.0.1  ${subDomain + '.' + topDomain} # 前端服务域名（dev环境，默认本机）
127.0.0.1  ${redisHost} # redis服务域名（dev环境，默认本机）
```


## 版权和许可
- Copyright (c) 2018-present, ${author}.
- ${copyright}


<p align="center">
</br>
${watermark}
</p>