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


<p align="center">
</br>
${watermark}
</p>