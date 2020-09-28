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


## 版权和许可
- Copyright (c) 2018-present, ${author}.
- ${copyright}


<p align="center">
</br>
${watermark}
</p>