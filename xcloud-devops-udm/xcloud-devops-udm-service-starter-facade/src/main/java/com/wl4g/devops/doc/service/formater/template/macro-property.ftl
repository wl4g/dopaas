<#macro propertiesTree properties deep=0>
    <#if properties?? && properties?size gt 0>
        <#list properties as property>
            <tr>
                <td>
                   <span title="${property.name!}" style="margin-left: ${deep * 10}px;">${property.name!}</span>
                </td>
                <td>${property.required}</td>
                <td>${property.type}</td>
                <td>${property.value}</td>
                <td>${property.description}</td>
            </tr>
            <#if property.children?? && property.children?size gt 0>
                <@propertiesTree properties = property.children deep=deep+1/>
            </#if>
        </#list>
    </#if>
</#macro>