<#assign topDomain = organName?lower_case + '.debug' />
<#assign subDomainOfBackend = projectName?lower_case + '-services' />
<#assign subDomain = projectName?lower_case />
<#assign serverName = projectName?lower_case + '-server' />
<#assign redisHost = 'redis.' + topDomain />
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

Ready to access API after startup, the baseURI is: [http://${subDomainOfBackend}.${topDomain}:28080/${serverName}](http://${subDomainOfBackend}.${topDomain}:28080/${serverName})
> Note: before attempting to test access, make sure that the local hosts resolution has been added:
```
127.0.0.1  ${subDomainOfBackend + '.' + topDomain} # Backend service domain. (dev dnv, by default local)
127.0.0.1  ${subDomain + '.' + topDomain} # Frontend service domain. (dev dnv, by default local)
127.0.0.1  ${redisHost} # Redis service domain. (dev dnv, by default local)
```


## Copyright and licensing
- Copyright (c) 2018-present, ${author}.
- ${copyright}


<p align="center">
</br>
${watermark}
</p>