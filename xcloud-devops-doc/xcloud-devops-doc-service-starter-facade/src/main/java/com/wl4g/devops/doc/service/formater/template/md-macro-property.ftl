<#macro propertiesTree properties deep=0>
    <#if properties?? && properties?size gt 0>
        <#list properties as property>
            | <#list 0..((deep?number)!0)*4 as i>&nbsp;</#list>${property.name!} | ${property.required} | ${property.type} | ${property.value} | ${property.description} |
            <#if property.children?? && property.children?size gt 0>
                <@propertiesTree properties = property.children deep=deep+1/>
            </#if>
        </#list>
    </#if>
</#macro>