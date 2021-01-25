### ${enterpriseApi.name}

- 接口ID: ${enterpriseApi.id}
- 地址: ${enterpriseApi.url}
- 类型: ${enterpriseApi.method}

#### 请求参数
|名称|必选|类型|初始值|简介|
| :---- | :----: | :----: | :----: | :----: |
<#if requestProperties?? && requestProperties?size gt 0>
    <@propertiesTree properties=requestProperties deep=0></@propertiesTree>
</#if>

#### 响应参数
|名称|必选|类型|初始值|简介|
| :---- | :----: | :----: | :----: | :----: |
<#if responseProperties?? && responseProperties?size gt 0>
    <@propertiesTree properties=responseProperties deep=0></@propertiesTree>
</#if>