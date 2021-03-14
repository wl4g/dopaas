/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 */
import iam from './iam/'
<#if moduleMap?exists>
    <#list moduleMap?keys as moduleName>
        <#list moduleMap[moduleName] as table>
import ${table.entityName?uncap_first} from './${moduleName?lower_case}/${table.entityName?uncap_first}.js'
        </#list>
    </#list>
</#if>

export default [
    {
        module: 'user',
        name: '用户管理',
        list: user
    }, {
        module: 'system',
        name: '系统设置',
        list: system
    }, {
        module: 'iam',
        name: '权限管理',
        list: iam
    },

<#if moduleMap?exists>
    <#list moduleMap?keys as moduleName>
     {
        module: '${moduleName?lower_case}',
        name: '权限管理',
        list: []<#list moduleMap[moduleName] as table>.concat(${table.entityName?uncap_first})</#list>
     },
    </#list>
</#if>


]
