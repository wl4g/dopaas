<#macro class_annotation class_name author date>/**
    * ${class_name}
    *
    * @author ${author}
    * @Date ${date}
    */
</#macro>
<#--{包名}/{模块名}/{分层(dao,entity,service,web)}/{子模块名}-->
<#--package com.wl4g.devops.dts.codegen.service;-->
<#macro class_package package_name module_name demixing_package>${package_name}.${module_name}.${demixing_package}</#macro>
<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service" />

<#--import-->
import com.wl4g.components.data.page.PageModel;
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="bean" />.${className};

<@class_annotation class_name="${className}Service" author="${functionAuthor}" date="${aDate?iso_utc}" />
public interface ${className}Service {

    PageModel page(PageModel pm, String name);

    void save(${className} ${className?uncap_first});

    ${className} detail(Integer id);

    void del(Integer id);
}

