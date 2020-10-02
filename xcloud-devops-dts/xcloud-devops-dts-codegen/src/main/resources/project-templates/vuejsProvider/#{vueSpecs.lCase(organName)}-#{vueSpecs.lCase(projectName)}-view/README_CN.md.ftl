<#assign topDomain = organName?lower_case + '.debug' />
<#assign subDomain = projectName?lower_case />
<#assign serverName = projectName?lower_case + '-server' />
<#assign redisHost = 'redis.' + topDomain />
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
npm install
# 运行项目
npm run dev
# 项目打包
#npm run build
```

启动完成后开始访问：[http://${subDomain}.${topDomain}:38080](http://${subDomain}.${topDomain}:38080)
> 注：在尝试测试访问之前请确保已添加本地hosts解析：
```
127.0.0.1  ${topDomain}  ${subDomain +'.'+ topDomain}    ${redisHost}
```


### 项目结构:
- /src 主要编辑代码
- /build 打包配置代码
- /node_modules 依赖包文件夹
- /static 某些静态文件
- /config 多运行环境下的配置
- /config/index.js 可配置开发环境的端口,代理地址等

[后端项目](../../../${vueSpecs.lCase(organName)}-${vueSpecs.lCase(projectName)})


