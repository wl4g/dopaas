<#macro propertiesTree properties deep=0>
    <#if properties?? && properties?size gt 0>
        <#list properties as property>
            <tr>
                <td>
                   <span title="${property.name!}" style="margin-left: ${deep * 10}px;">
                       ${property.name!}
                   </span>
                </td>
                <td>${property.required}</td>
                <td>${property.type}</td>
                <td>${property.value}</td>
                <td>${property.description}</td>
            </tr>
            <#if property.children?? && property.children?size gt 0>
                <@menuTree menus = property.children deep=deep+1/>
            </#if>
        </#list>
    </#if>
</#macro>

<!-- 接口基础信息 -->
<div class="baseInfo">
    <span class="apiNameClass">
        ${apiName}
    </span><br>

    <span class="infoTitle">接口ID:</span>
    <span class="infoValue">
        ${id}
    </span><br>

    <span class="infoTitle">地址:</span>
    <span class="infoValue">
        ${path}
    </span><br>

    <span class="infoTitle">类型:</span>
    <span class="infoValue">
        ${method}
    </span>
</div>

<!-- 请求参数 -->
<div>
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
</div>

<!-- 响应参数 -->
<div>
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
</div>

