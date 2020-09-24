/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 */
import user from './user/'
import system from './system/'
import iam from './iam/'
<#if moduleMap?exists>
    <#list moduleMap?keys as key>
        <#list moduleMap[key] as value>
import ${value?uncap_first} from './${key?lower_case}/${value?uncap_first}.js'
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
    <#list moduleMap?keys as key>
     {
        module: '${key?lower_case}',
        name: '权限管理',
        list: []<#list moduleMap[key] as value>.concat(${value?uncap_first})</#list>
     },
    </#list>
</#if>


]
