<#assign topDomain = organName?lower_case + '.debug' />
<#assign subDomain = projectName?lower_case />
## Introduction
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
npm install
# Serve with hot reload at localhost:8090
npm run dev
# Build for production with minification
#npm run build
```

Ready to access page after startup：[http://${subDomain}.${topDomain}:38080](http://${subDomain}.${topDomain}:38080)
> Note: before attempting to test access, make sure that the local hosts resolution has been added:
```
127.0.0.1  ${subDomain + '.' + topDomain} # Frontend service domain. (dev dnv, by default local)
```

### Project describe:
- /src Main logic source code
- /build Package configuration code
- /node_modules Dependent package folder
- /static Some static files
- /config Configuration of multi running environment
- /config/index.js Configurable development environment port, proxy address, etc

[Backend project](../../../${vueSpecs.lCase(organName)}-${vueSpecs.lCase(projectName)})


