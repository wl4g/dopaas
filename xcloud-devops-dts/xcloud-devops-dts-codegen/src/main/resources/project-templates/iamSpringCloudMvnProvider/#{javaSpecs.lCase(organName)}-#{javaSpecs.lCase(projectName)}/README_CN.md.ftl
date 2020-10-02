<#assign topDomain = organName?lower_case + '.debug' />
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

启动后准备访问API, 其中baseURI为: [http://${subDomain}.${topDomain}:28080/${serverName}](http://${subDomain}.${topDomain}:28080/${serverName})
> 注：在尝试测试访问之前请确保已添加本地hosts解析：
```
127.0.0.1  ${topDomain}  ${subDomain +'.'+ topDomain}    ${redisHost}
```


## 版权和许可
- Copyright (c) 2018-present, ${author}.
- ${copyright}


<p align="center">
</br>
${watermark}
</p>