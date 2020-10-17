# ${organName?cap_first} ${projectName?cap_first}

#### Introduction
${projectDescription}

- [中文文档](README_CN.md)


## Features
<#if moduleMap??>
    <#list moduleMap?keys as moduleName>
- ${moduleName}
	</#list>
</#if>


## Quick start
```
mvn clean install -DskipTests -T 2C
```

Ready to access API after startup, the baseURI is: [http://${devServiceHost}:${entryAppPort}/${entryAppName}](http://${devServiceHost}:${devServicePort}/${entryAppName})
> Note: before attempting to test access, make sure that the local hosts resolution has been added:
```
127.0.0.1  ${devServiceHost} # Backend service domain. (dev dnv, by default local)
127.0.0.1  ${devViewServiceHost} # Frontend service domain. (dev dnv, by default local)
127.0.0.1  ${devRedisHost} # Redis service domain. (dev env, by default local)
```


## API Documentation
<#if javaSpecs.isConf(extraOptions, "gen.swagger.ui", "bootstrapSwagger2")>
[http://${devServiceHost}:${entryAppPort}/${entryAppName}/doc.html](http://${devServiceHost}:${devServicePort}/${entryAppName}/doc.html)
<#elseif javaSpecs.isConf(extraOptions, "gen.swagger.ui", "officialOas")>
[http://${devServiceHost}:${entryAppPort}/${entryAppName}/swagger-ui/index.html](http://${devServiceHost}:${devServicePort}/${entryAppName}/swagger-ui/index.html)
</#if>


## Deployment
- [Configuration based on nginx deployment, please refer to](nginx/)


## Copyright and licensing
- Copyright (c) 2018-present, ${author}.
```
${copyright}
```


<p align="center">
</br>
${watermark}
</p>