# ${organName?cap_first} ${projectName?cap_first}

#### Introduction
${projectDescription}


## Documentation
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

Ready to access API after startup, the baseURI is: [http://${devServiceHost}:28080/${entryAppName}](http://${devServiceHost}:${devServicePort}/${entryAppName})
> Note: before attempting to test access, make sure that the local hosts resolution has been added:
```
127.0.0.1  ${devServiceHost} # Backend service domain. (dev dnv, by default local)
127.0.0.1  ${devViewServiceHost} # Frontend service domain. (dev dnv, by default local)
127.0.0.1  ${devRedisHost} # Redis service domain. (dev env, by default local)
```


## Copyright and licensing
- Copyright (c) 2018-present, ${author}.
- ${copyright}


<p align="center">
</br>
${watermark}
</p>