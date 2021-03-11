<!-- 接口基础信息 -->
<span class='apiNameClass'>${enterpriseApi.name}</span><br>
<span class='infoTitle'>接口ID:</span><span class='infoValue'>${enterpriseApi.id}</span><br>
<span class='infoTitle'>地址:</span><span class='infoValue'>${enterpriseApi.url}</span><br>
<span class='infoTitle'>类型:</span><span class='infoValue'>${enterpriseApi.method}</span>

<!-- 请求参数 -->
<span>请求参数</span>
<table>
    <tr>
        <th>名称</th>
        <th>必选</th>
        <th>类型</th>
        <th>初始值</th>
        <th>简介</th>
    </tr>
    <#if requestProperties?? && requestProperties?size gt 0>
        <@propertiesTree properties=requestProperties deep=0></@propertiesTree>
    </#if>
</table>

<!-- 响应参数 -->
<span>响应参数</span>
<table>
    <tr>
        <th>名称</th>
        <th>必选</th>
        <th>类型</th>
        <th>初始值</th>
        <th>简介</th>
    </tr>
    <#if responseProperties?? && responseProperties?size gt 0>
        <@propertiesTree properties=responseProperties deep=0></@propertiesTree>
    </#if>
</table>
